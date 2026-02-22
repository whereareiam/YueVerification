package me.whereareiam.yuiverification.common;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.whereareiam.yui.conversation.ConversationService;
import me.whereareiam.yui.event.journey.JourneyCancelledEvent;
import me.whereareiam.yui.event.journey.JourneyFailedEvent;
import me.whereareiam.yui.event.journey.session.JourneySessionTimeoutEvent;
import me.whereareiam.yui.fluctlight.FluctlightService;
import me.whereareiam.yui.journey.JourneyService;
import me.whereareiam.yui.journey.JourneyKeys;
import me.whereareiam.yui.model.ConversationConfig;
import me.whereareiam.yui.model.fluctlight.Fluctlight;
import me.whereareiam.yui.model.journey.session.JourneySession;
import me.whereareiam.yui.model.journey.session.JourneySessionRequest;
import me.whereareiam.yui.type.journey.JourneyStatus;
import me.whereareiam.yui.util.translation.Translatable;
import me.whereareiam.yuiverification.VerificationService;
import me.whereareiam.yuiverification.model.VerificationState;
import me.whereareiam.yuiverification.model.config.VerificationSettings;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultVerificationService implements VerificationService {
	private static final Set<JourneyStatus> ACTIVE_STATUSES = EnumSet.of(JourneyStatus.RUNNING, JourneyStatus.WAITING);

	private final ObjectProvider<VerificationSettings> settings;
	private final ConversationService conversationService;
	private final JourneyService journeyService;
	private final FluctlightService fluctlightService;
	private final JDA jda;

	@Override
	public void verify() {
		jda.getGuilds().getFirst().getMembers().stream()
				.filter(member -> !member.getUser().isBot())
				.forEach(member -> fluctlightService.get(member.getIdLong()).ifPresent(this::verify));
	}

	@Override
	public void verify(Fluctlight fluctlight) {
		startVerification(fluctlight, false, null);
	}

	@Override
	public void verifyManual(Fluctlight fluctlight, long initiatorId) {
		startVerification(fluctlight, true, initiatorId);
	}

	private void startVerification(Fluctlight fluctlight, boolean isManual, Long initiatorId) {
		VerificationSettings config = settings.getObject();

		boolean hasRole = fluctlight.getAllowedRoles() != null
				&& Arrays.stream(fluctlight.getAllowedRoles())
				.anyMatch(id -> id == Long.parseLong(config.getVerifiedRoleId()));
		if (hasRole)
			return;

		long userId = fluctlight.getId();
		if (journeyService.find("verification", userId, ACTIVE_STATUSES, VerificationState.class).isPresent())
			return;

		ConversationConfig conversationConfig = ConversationConfig.builder()
				.preferredModes(config.getConversation().getPreferredModes())
				.channelName(Translatable.text("plugin.yuiverification.channel.name")
						.with("username", fluctlight.getName())
						.resolve(fluctlight))
				.channelDescription(Translatable.text("plugin.yuiverification.channel.description").resolve(fluctlight))
				.privateInitialMessage(Translatable.text("plugin.yuiverification.privateMessage.message").resolve(fluctlight))
				.channelInitialMessage(Translatable.text("plugin.yuiverification.channel.message").resolve(fluctlight))
				.mentionUsers(true)
				.closeDelaySeconds(config.getConversation().getCloseDelay() != null
						? config.getConversation().getCloseDelay().getSeconds()
						: null)
				.build();

		conversationService.create(Collections.singleton(userId), "verification", conversationConfig)
				.thenAccept(conversation -> {
					VerificationState state = new VerificationState(fluctlight, conversation, initiatorId, isManual);
					Long timeoutSeconds = null;
					if (config.getTimeout().isEnabled() && config.getTimeout().getDuration() != null)
						timeoutSeconds = config.getTimeout().getDuration().getSeconds();

					JourneySessionRequest.Builder<VerificationState> requestBuilder =
							JourneySessionRequest.builder("verification", userId, VerificationState.class, state);
					if (timeoutSeconds != null)
						requestBuilder.attribute(JourneyKeys.TIMEOUT_SECONDS, timeoutSeconds);

					JourneySessionRequest<VerificationState> request = requestBuilder.build();

					journeyService.start(request);
				})
				.exceptionally(throwable -> {
					log.error("Verification journey failed to start for user {}", userId, throwable);
					return null;
				});
	}

	public void handleUserLeave(long userId) {
		journeyService.find("verification", userId, ACTIVE_STATUSES, VerificationState.class)
				.ifPresent(session -> journeyService.cancel(session.getId()));
	}

	public void cancelAllVerifications() {
		Collection<JourneySession<?>> sessions = journeyService.findAll("verification", ACTIVE_STATUSES);
		log.info("[YuiVerification]: Cancelling {} active verifications", sessions.size());
		sessions.forEach(session -> journeyService.cancel(session.getId()));
	}

	@EventListener
	public void onJourneyCancelled(JourneyCancelledEvent event) {
		if (!"verification".equals(event.getSession().getJourneyId()))
			return;

		closeVerificationConversation(event.getSession());
	}

	@EventListener
	public void onJourneyFailed(JourneyFailedEvent event) {
		if (!"verification".equals(event.getSession().getJourneyId()))
			return;

		closeVerificationConversation(event.getSession());
	}

	@EventListener
	public void onJourneyTimedOut(JourneySessionTimeoutEvent event) {
		if (!"verification".equals(event.getSession().getJourneyId()))
			return;

		kickUser(event.getSession().getParticipantId());
	}

	private void closeVerificationConversation(JourneySession<?> session) {
		VerificationState state = session.getState(VerificationState.class);
		if (state.getConversation() != null)
			conversationService.close(state.getConversation(), 0);
	}

	private void kickUser(long userId) {
		fluctlightService.get(userId).ifPresent(fluctlight -> {
			Guild guild = jda.getGuilds().getFirst();
			guild.retrieveMemberById(userId).queue(
					member -> {
						String reason = Translatable.text("plugin.yuiverification.kick.reason").resolve(fluctlight);
						member.kick().reason(reason).queue(
								_ -> log.info("Kicked user {} for verification timeout: {}", userId, reason),
								error -> log.error("Failed to kick user {} for verification timeout", userId, error)
						);
					},
					_ -> log.warn("Could not retrieve member {} to kick for verification timeout", userId)
			);
		});
	}
}
