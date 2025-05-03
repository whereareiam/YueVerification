package me.whereareiam.yueverification.common.step;

import lombok.AllArgsConstructor;
import me.whereareiam.yue.api.style.StyleKit;
import me.whereareiam.yue.api.util.Translatable;
import me.whereareiam.yue.api.util.Users;
import me.whereareiam.yueverification.api.VerificationStep;
import me.whereareiam.yueverification.api.model.VerificationContext;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@AllArgsConstructor
@Order(Integer.MAX_VALUE)
public class EndStep implements VerificationStep {
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
				.primary()
				.setTitle(Translatable.forUser("plugin.yueverification.steps.welcome.title", userId, Users.getMention(userId)))
				.setDescription(Translatable.forUser("plugin.yueverification.steps.welcome.description", userId, Users.getMention(userId)))
				.build();
	}
}