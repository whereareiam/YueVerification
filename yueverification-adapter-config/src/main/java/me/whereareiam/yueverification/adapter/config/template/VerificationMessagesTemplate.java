package me.whereareiam.yueverification.adapter.config.template;

import me.whereareiam.yue.api.output.config.DefaultConfig;
import me.whereareiam.yueverification.api.model.config.VerificationMessages;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VerificationMessagesTemplate implements DefaultConfig<VerificationMessages> {

	@Override
	public VerificationMessages getDefault() {
		VerificationMessages messages = new VerificationMessages();

		// Channel
		VerificationMessages.Channel channel = new VerificationMessages.Channel();
		channel.setName("verification-{0}");
		channel.setDescription("Temporary verification channel");
		channel.setMessage("A temporary verification channel was created for you. Please go through all the steps and verify yourself in the message that will appear in the channel.");
		messages.setChannel(channel);

		// Steps
		VerificationMessages.Steps steps = new VerificationMessages.Steps();
		// Steps → Welcome
		VerificationMessages.Steps.Welcome welcome = new VerificationMessages.Steps.Welcome();
		welcome.setTitle("Welcome to the Discord-Server");
		welcome.setDescription(List.of(
				"Welcome {0}, before you can start chatting and having fun on our Discord server, you need to take a few steps.",
				"",
				"Please select your primary language by clicking one of the buttons below."
		));
		steps.setWelcome(welcome);

		// Steps → Additional Language
		VerificationMessages.Steps.AdditionalLanguage additionalLanguage = new VerificationMessages.Steps.AdditionalLanguage();
		additionalLanguage.setTitle("Select Additional Languages & Channels");
		additionalLanguage.setDescription(List.of(
				"Choose any additional languages you understand. These will be used for translations if your primary language is not available.",
				"",
				"For each selected language, a dedicated chat channel will be added, allowing you to connect with others in that language.",
				"Your primary language will also have its own channel automatically."
		));
		steps.setAdditionalLanguage(additionalLanguage);

		// Steps → End
		VerificationMessages.Steps.End end = new VerificationMessages.Steps.End();
		end.setTitle("You're All Set!");
		end.setDescription(List.of(
				"Congratulations {0}, you have completed the verification process!",
				"",
				"You can now join the conversation and chat with others on our Discord server.",
				"Before you start, please make sure you have read and understood our rules in the {1} channel.",
				"",
				"Enjoy your time here!"
		));
		steps.setEnd(end);

		messages.setSteps(steps);

		return messages;
	}
}