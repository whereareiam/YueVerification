package me.whereareiam.yueverification.common;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.whereareiam.yue.api.input.TemporaryChannelService;
import me.whereareiam.yue.api.model.ChannelDecoration;
import me.whereareiam.yue.api.model.profile.UserProfile;
import me.whereareiam.yue.api.output.provider.Provider;
import me.whereareiam.yue.api.util.Translatable;
import me.whereareiam.yue.api.util.Users;
import me.whereareiam.yueverification.api.VerificationStep;
import me.whereareiam.yueverification.api.model.VerificationContext;
import me.whereareiam.yueverification.api.model.config.VerificationSettings;
import me.whereareiam.yueverification.api.service.VerificationService;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultVerificationService implements VerificationService {
	private final Provider<VerificationSettings> settings;
	private final TemporaryChannelService temporaryChannelService;
	private final List<VerificationStep> steps;

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
						.name(Translatable.forUser("plugin.yueverification.channel.name", userId, Users.getUsername(userId)))
						.description(Translatable.of("plugin.yueverification.channel.description", userId))
						.message(Translatable.of("plugin.yueverification.channel.message", userId))
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

		for (VerificationStep step : steps)
			chain = chain.thenCompose(_ -> step.execute(ctx));

		return chain.thenApply(_ -> ctx);
	}
}
