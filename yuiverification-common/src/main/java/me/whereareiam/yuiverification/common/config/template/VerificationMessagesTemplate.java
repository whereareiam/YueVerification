package me.whereareiam.yuiverification.common.config.template;

import me.whereareiam.yui.localization.format.FileFormat;
import me.whereareiam.yui.localization.format.FileFormats;
import me.whereareiam.yui.localization.provider.LocalizationProvider;
import me.whereareiam.yuiverification.model.config.VerificationMessages;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VerificationMessagesTemplate implements LocalizationProvider<VerificationMessages> {
	@Override
	public Class<VerificationMessages> getModelClass() {
		return VerificationMessages.class;
	}

	@Override
	public FileFormat getFormat() {
		return FileFormats.LOCALE;
	}

	@Override
	public VerificationMessages supply(VerificationMessages messages) {
		// Channel
		VerificationMessages.Channel channel = new VerificationMessages.Channel();
		channel.setName("verification-<p:username>");
		channel.setDescription("Temporary verification channel");
		channel.setMessage("A temporary verification channel was created for you. Please go through all the steps and verify yourself in the message that will appear in the channel.");
		messages.setChannel(channel);

		// Steps
		VerificationMessages.Steps steps = new VerificationMessages.Steps();
		// Steps → Welcome
		VerificationMessages.Steps.Welcome welcome = new VerificationMessages.Steps.Welcome();
		welcome.setTitle("Welcome to the Discord-Server");
		welcome.setDescription(List.of("Welcome <p:mention>, before you can start chatting and having fun on our Discord server, you need to take a few steps.", "", "Please select your primary language by clicking one of the buttons below."));
		steps.setWelcome(welcome);

		// Steps → Additional Language
		VerificationMessages.Steps.AdditionalLanguage additionalLanguage = new VerificationMessages.Steps.AdditionalLanguage();
		additionalLanguage.setTitle("Select Additional Languages & Channels");
		additionalLanguage.setDescription(List.of("Choose any additional languages you understand. These will be used for translations if your primary language is not available.", "", "For each selected language, a dedicated chat channel will be added, allowing you to connect with others in that language.", "Your primary language will also have its own channel automatically."));
		steps.setAdditionalLanguage(additionalLanguage);

		// Steps → End
		VerificationMessages.Steps.End end = new VerificationMessages.Steps.End();
		end.setTitle("You're All Set!");
		end.setDescription(List.of("Congratulations <p:mention>, you have completed the verification process!", "", "You can now join the conversation and chat with others on our Discord server.", "Before you start, please make sure you have read and understood our rules in the <p:rulesChannel> channel.", "", "Enjoy your time here!"));
		steps.setEnd(end);

		messages.setSteps(steps);

		// Timeout
		VerificationMessages.Timeout timeout = new VerificationMessages.Timeout();
		timeout.setKickReason("Failed to complete verification within the time limit");
		messages.setTimeout(timeout);

		// Command
		VerificationMessages.Command command = new VerificationMessages.Command();
		VerificationMessages.Command.Verify verify = new VerificationMessages.Command.Verify();
		verify.setDescription("Manually start verification process for a user");
		verify.setExample("/yui verify @user");

		VerificationMessages.Command.Verify.Variables variables = new VerificationMessages.Command.Verify.Variables();
		variables.setUser("The Discord user to verify");
		verify.setVariables(variables);

		VerificationMessages.Command.Verify.Success success = new VerificationMessages.Command.Verify.Success();
		success.setTitle("Verification Started");
		success.setDescription(List.of(
				"Successfully started verification process for {user}."
		));
		verify.setSuccess(success);

		VerificationMessages.Command.Verify.Error error = new VerificationMessages.Command.Verify.Error();
		VerificationMessages.Command.Verify.Error.NotFound notFound = new VerificationMessages.Command.Verify.Error.NotFound();
		notFound.setTitle("User Not Found");
		notFound.setDescription(List.of(
				"Could not find user {user} in the system."
		));
		error.setNotFound(notFound);
		verify.setError(error);

		command.setVerify(verify);
		messages.setCommand(command);

		return messages;
	}
}