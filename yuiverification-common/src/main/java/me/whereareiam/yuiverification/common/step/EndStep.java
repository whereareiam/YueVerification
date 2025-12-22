package me.whereareiam.yuiverification.common.step;

import lombok.AllArgsConstructor;
import me.whereareiam.yui.service.ConversationService;
import me.whereareiam.yui.util.style.StyleKit;
import me.whereareiam.yui.util.translation.Translatable;
import me.whereareiam.yuiverification.VerificationStep;
import me.whereareiam.yuiverification.VerificationStepRegistry;
import me.whereareiam.yuiverification.model.VerificationContext;
import me.whereareiam.yuiverification.model.config.VerificationSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@AllArgsConstructor
public class EndStep implements VerificationStep {
	private final VerificationSettings settings;
	private final ConversationService conversationService;

	@Autowired
	private void register(VerificationStepRegistry registry) {
		registry.register(this);
	}

	@Override
	public CompletableFuture<Void> onStepStarted(VerificationContext context) {
		CompletableFuture<Void> future = context.start();

		context.getFluctlight().addAllowedRole(Long.parseLong(settings.getVerifiedRoleId()));
		context.setCompleted(true);

		EmbedBuilder embed = buildEmbed(context);

		context.getMessage()
				.editMessageEmbeds(embed.build())
				.setComponents()
				.queue(context::setMessage);

		context.next();

		long closeDelay = settings.getConversation().getCloseDelay() != null ?
				settings.getConversation().getCloseDelay().getSeconds() : 0;
		conversationService.close(context.getConversation(), closeDelay);

		return future;
	}

	private EmbedBuilder buildEmbed(VerificationContext context) {
		return StyleKit.embeds()
				.success()
				.setTitle(Translatable.text("plugin.yuiverification.steps.end.title").resolve(context.getFluctlight()))
				.setDescription(Translatable.text("plugin.yuiverification.steps.end.description")
						.with("mention", context.getFluctlight().getAsMention())
						.with("rulesChannel", "<#" + settings.getRulesChannelId() + ">")
						.resolve(context.getFluctlight()));
	}
}