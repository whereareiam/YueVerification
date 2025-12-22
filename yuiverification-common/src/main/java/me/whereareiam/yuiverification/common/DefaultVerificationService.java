package me.whereareiam.yuiverification.common;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.whereareiam.yui.fluctlight.FluctlightService;
import me.whereareiam.yui.model.conversation.ConversationConfig;
import me.whereareiam.yui.model.fluctlight.Fluctlight;
import me.whereareiam.yui.service.ConversationService;
import me.whereareiam.yui.util.translation.Translatable;
import me.whereareiam.yuiverification.VerificationService;
import me.whereareiam.yuiverification.VerificationStep;
import me.whereareiam.yuiverification.VerificationStepRegistry;
import me.whereareiam.yuiverification.model.VerificationContext;
import me.whereareiam.yuiverification.model.config.VerificationMessages;
import me.whereareiam.yuiverification.model.config.VerificationSettings;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultVerificationService implements VerificationService {
	private final ObjectProvider<VerificationSettings> settings;
	private final ObjectProvider<VerificationMessages> messages;
	private final ConversationService conversationService;
	private final VerificationStepRegistry stepRegistry;
	private final FluctlightService fluctlightService;
	private final JDA jda;

	private final Map<Long, VerificationContext> activeVerifications = new ConcurrentHashMap<>();

	private final ScheduledExecutorService scheduler =
			Executors.newSingleThreadScheduledExecutor(r -> {
				Thread t = new Thread(r, "verification-timeout");
				t.setDaemon(true);
				return t;
			});

	@Override
	public void verify() {
		jda.getGuilds().getFirst().getMembers().stream()
				.filter(member -> !member.getUser().isBot())
				.forEach(member -> {
					fluctlightService.get(member.getIdLong()).ifPresent(this::verify);
				});
	}

	@Override
	public void verify(Fluctlight fluctlight) {
		VerificationSettings config = this.settings.getObject();

		boolean hasRole = fluctlight.getAllowedRoles() != null &&
				Arrays.stream(fluctlight.getAllowedRoles())
						.anyMatch(id -> id == Long.parseLong(config.getVerifiedRoleId()));

		if (hasRole)
			return;

		long userId = fluctlight.getId();

		ConversationConfig conversationConfig = ConversationConfig.builder()
				.preferPrivateMessage(config.getConversation().isPreferPrivateMessage())
				.allowTemporaryChannel(config.getConversation().isAllowTemporaryChannel())
				.channelName(Translatable.text("plugin.yuiverification.channel.name")
						.with("username", fluctlight.getName())
						.resolve(fluctlight))
				.channelDescription(Translatable.text("plugin.yuiverification.channel.description").resolve(fluctlight))
				.initialMessage(Translatable.text("plugin.yuiverification.channel.message").resolve(fluctlight))
				.mentionUsers(true)
				.closeDelaySeconds(config.getConversation().getCloseDelay() != null ?
						config.getConversation().getCloseDelay().getSeconds() : null)
				.build();

		conversationService.create(Collections.singleton(userId), "verification", conversationConfig)
				.thenCompose(conversation -> {
					VerificationContext ctx = new VerificationContext(fluctlight, conversation);

					if (config.getTimeout().isEnabled()) {
						scheduleTimeout(ctx, config);
					}

					return executeStepsSequentially(ctx);
				})
				.exceptionally(throwable -> {
					log.error("Verification pipeline failed for user {}", userId, throwable);
					return null;
				});
	}

	private void scheduleTimeout(VerificationContext ctx, VerificationSettings config) {
		long timeoutSeconds = config.getTimeout().getDuration().getSeconds();

		scheduler.schedule(() -> {
			if (!ctx.isCompleted() && config.getTimeout().isEnabled()) {
				log.info("User {} failed to complete verification within {} - kicking", 
						ctx.getFluctlight().getId(), config.getTimeout().getDuration());
				kickUser(ctx.getFluctlight().getId());
			}
		}, timeoutSeconds, TimeUnit.SECONDS);
	}

	private void kickUser(long userId) {
		Guild guild = jda.getGuilds().getFirst();
		VerificationMessages msgs = this.messages.getObject();

		guild.retrieveMemberById(userId).queue(
				member -> {
					String reason = msgs.getTimeout().getKickReason();
					member.kick().reason(reason).queue(
							_ -> {
								log.info("Kicked user {} for verification timeout: {}", userId, reason);
								// Notify steps of cancellation and close conversation
								conversationService.findByUser(userId, "verification")
										.ifPresent(conv -> {
											fluctlightService.get(userId).ifPresent(fluctlight -> {
												VerificationContext ctx = new VerificationContext(fluctlight, conv);
												cancelVerification(ctx);
											});
										});
							},
							error -> log.error("Failed to kick user {} for verification timeout", userId, error)
					);
				},
				_ -> log.warn("Could not retrieve member {} to kick for verification timeout", userId)
		);
	}

	private void cancelVerification(VerificationContext context) {
		// Notify all steps that verification was cancelled
		for (VerificationStep step : stepRegistry.getSteps())
			step.onVerificationCancelled(context);
		
		// Close conversation
		conversationService.close(context.getConversation(), 0);
	}

	public void handleUserLeave(long userId) {
		VerificationContext ctx = activeVerifications.remove(userId);
		if (ctx != null) {
			cancelVerification(ctx);
		}
	}

	public void cancelAllVerifications() {
		log.info("[YuiVerification]: Cancelling {} active verifications", activeVerifications.size());
		activeVerifications.values().forEach(this::cancelVerification);
		activeVerifications.clear();
	}

	private CompletableFuture<VerificationContext> executeStepsSequentially(VerificationContext ctx) {
		CompletableFuture<Void> chain = CompletableFuture.completedFuture(null);

		for (VerificationStep step : stepRegistry.getSteps()) {
			chain = chain.thenCompose(_ -> step.onStepStarted(ctx))
					.thenRun(() -> step.onStepCompleted(ctx));
		}

		return chain.thenApply(_ -> {
			// Notify all steps that verification completed
			for (VerificationStep step : stepRegistry.getSteps())
				step.onVerificationCompleted(ctx);
			return ctx;
		});
	}
}
