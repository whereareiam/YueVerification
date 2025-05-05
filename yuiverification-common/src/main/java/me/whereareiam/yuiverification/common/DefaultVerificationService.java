package me.whereareiam.yuiverification.common;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.whereareiam.yui.api.input.TemporaryChannelService;
import me.whereareiam.yui.api.model.channel.ChannelDecoration;
import me.whereareiam.yui.api.model.profile.UserProfile;
import me.whereareiam.yui.api.output.provider.Provider;
import me.whereareiam.yui.api.util.Translatable;
import me.whereareiam.yui.api.util.Users;
import me.whereareiam.yuiverification.api.VerificationStep;
import me.whereareiam.yuiverification.api.VerificationStepRegistry;
import me.whereareiam.yuiverification.api.model.VerificationContext;
import me.whereareiam.yuiverification.api.model.config.VerificationSettings;
import me.whereareiam.yuiverification.api.service.VerificationService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultVerificationService implements VerificationService {
	private final Provider<VerificationSettings> settings;
	private final TemporaryChannelService temporaryChannelService;
	private final VerificationStepRegistry stepRegistry;
	private final JDA jda;

	@Override
	public void verify() {
		jda.getGuilds().getFirst().getMembers().stream()
				.filter(member -> !member.getUser().isBot())
				.forEach(member ->
						verify(member.getIdLong())
				);
	}

	@Override
	public void verify(long userId) {
		VerificationSettings settings = this.settings.get();

		boolean hasRole = Users.get(userId)
				.map(UserProfile::getRoles)
				.map(arr -> Arrays.stream(arr).anyMatch(id -> id == Long.parseLong(settings.getVerifiedRoleId())))
				.orElse(false);

		if (hasRole)
			return;

		Optional<TextChannel> existing = temporaryChannelService.findByUser(userId);
		CompletableFuture<TextChannel> channelFuture = existing
				.map(CompletableFuture::completedFuture)
				.orElseGet(() -> temporaryChannelService.create(Collections.singleton(userId), ChannelDecoration.builder()
						.name(Translatable.forUser("plugin.yuiverification.channel.name", userId, Users.getUsername(userId)))
						.description(Translatable.of("plugin.yuiverification.channel.description", userId))
						.message(Translatable.of("plugin.yuiverification.channel.message", userId))
						.mention(true)
						.build()
				));

		channelFuture
				.thenCompose(channel -> executeStepsSequentially(new VerificationContext(userId, channel)))
				.exceptionally(throwable -> {
					log.error("Verification pipeline failed for user {}", userId, throwable);
					return null;
				});
	}

	private CompletableFuture<VerificationContext> executeStepsSequentially(VerificationContext ctx) {
		CompletableFuture<Void> chain = CompletableFuture.completedFuture(null);

		for (VerificationStep step : stepRegistry.getSteps())
			chain = chain.thenCompose(_ -> step.execute(ctx));

		return chain.thenApply(_ -> ctx);
	}
}
