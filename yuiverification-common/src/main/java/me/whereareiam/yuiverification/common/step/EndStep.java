package me.whereareiam.yuiverification.common.step;

import lombok.AllArgsConstructor;
import me.whereareiam.yui.api.input.TemporaryChannelService;
import me.whereareiam.yui.api.output.service.UserProfileService;
import me.whereareiam.yui.api.style.StyleKit;
import me.whereareiam.yui.api.util.Translatable;
import me.whereareiam.yui.api.util.Users;
import me.whereareiam.yuiverification.api.VerificationStep;
import me.whereareiam.yuiverification.api.VerificationStepRegistry;
import me.whereareiam.yuiverification.api.model.VerificationContext;
import me.whereareiam.yuiverification.api.model.config.VerificationSettings;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@AllArgsConstructor
public class EndStep implements VerificationStep {
	private final VerificationSettings settings;
	private final TemporaryChannelService temporaryChannelService;
	private final UserProfileService userProfileService;

	@Autowired
	private void register(VerificationStepRegistry registry) {
		registry.register(this);
	}

	@Override
	public CompletableFuture<Void> execute(VerificationContext context) {
		CompletableFuture<Void> future = context.start();

		userProfileService.addRole(context.getUserId(), Long.parseLong(settings.getVerifiedRoleId()));
		MessageEmbed content = buildContent(context.getUserId());

		context.getMessage()
				.editMessageEmbeds(content)
				.setComponents()
				.queue(context::setMessage);

		context.next();
		temporaryChannelService.close(context.getChannel(), settings.getChannelTimeout());

		return future;
	}

	private MessageEmbed buildContent(long userId) {
		return StyleKit.embeds()
				.success()
				.setTitle(Translatable.of("plugin.yuiverification.steps.end.title", userId))
				.setDescription(Translatable.forUser(
						"plugin.yuiverification.steps.end.description",
						userId,
						Users.getMention(userId),
						"<#" + settings.getRulesChannelId() + ">"
				)).build();
	}
}