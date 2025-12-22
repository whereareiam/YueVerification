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
import me.whereareiam.yuiverification.model.config.VerificationMessages;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class VerifyCommand {
	private final VerificationService verificationService;
	private final ObjectProvider<VerificationMessages> messages;

	@Definition("verify")
	@Command("verify (user)")
	public void onCommand(
			Interaction interaction,
			@Argument("user") Fluctlight target
	) {
		Fluctlight executor = interaction.fluctlight();
		VerificationMessages msgs = messages.getObject();

		verificationService.verifyManual(target, executor.getId());

		VerificationMessages.Command.Verify.Success success = msgs.getCommand().getVerify().getSuccess();
		interaction.replyCallback().replyEmbeds(
				StyleKit.embeds()
						.success()
						.setTitle(success.getTitle())
						.setDescription(Translatable.text(String.join("\n", success.getDescription()))
								.with("user", target.getAsMention())
								.resolve(executor))
						.build()
		).setEphemeral(true).queue();
	}
}
