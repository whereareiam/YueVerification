package me.whereareiam.yuiverification.common.command;

import lombok.AllArgsConstructor;
import me.whereareiam.yui.annotation.command.Argument;
import me.whereareiam.yui.annotation.command.Command;
import me.whereareiam.yui.annotation.command.Definition;
import me.whereareiam.yui.command.Interaction;
import me.whereareiam.yui.model.fluctlight.Fluctlight;
import me.whereareiam.yui.util.style.StyleKit;
import me.whereareiam.yui.util.translation.Translatable;
import me.whereareiam.yuiverification.VerificationService;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class VerifyCommand {
	private final VerificationService verificationService;

	@Definition("verify")
	@Command("verify <user>")
	public void onCommand(
			Interaction interaction,
			@Argument("user") Fluctlight target
	) {
		Fluctlight executor = interaction.fluctlight();

		verificationService.verifyManual(target, executor.getId());

		interaction.replyCallback()
				.replyEmbeds(
						StyleKit.embeds()
								.success()
								.setTitle(Translatable.text("plugin.yuiverification.command.verify.success.title").resolve(executor))
								.setDescription(Translatable.text("plugin.yuiverification.command.verify.success.description")
										.with("user", target.getAsMention())
										.resolve(executor))
								.build()
				).setEphemeral(true)
				.queue();
	}
}
