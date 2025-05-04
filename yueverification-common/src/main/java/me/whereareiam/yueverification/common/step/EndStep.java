package me.whereareiam.yueverification.common.step;

import lombok.AllArgsConstructor;
import me.whereareiam.yue.api.style.StyleKit;
import me.whereareiam.yue.api.util.Translatable;
import me.whereareiam.yue.api.util.Users;
import me.whereareiam.yueverification.api.VerificationStep;
import me.whereareiam.yueverification.api.model.VerificationContext;
import me.whereareiam.yueverification.api.model.config.VerificationSettings;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@AllArgsConstructor
public class EndStep implements VerificationStep {
	private final VerificationSettings settings;

	@Override
	public CompletableFuture<Void> execute(VerificationContext context) {
		CompletableFuture<Void> future = context.start();

		MessageEmbed content = buildContent(context.getUserId());

		context.getMessage()
				.editMessageEmbeds(content)
				.setComponents()
				.queue(context::setMessage);

		return future;
	}

	private MessageEmbed buildContent(long userId) {
		return StyleKit.embeds()
				.success()
				.setTitle(Translatable.of("plugin.yueverification.steps.end.title", userId))
				.setDescription(Translatable.forUser(
						"plugin.yueverification.steps.end.description",
						userId,
						Users.getMention(userId),
						"<#" + settings.getRulesChannelId() + ">"
				)).build();
	}
}