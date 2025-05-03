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

		// Steps → Welcome
		VerificationMessages.Steps.Welcome welcome = new VerificationMessages.Steps.Welcome();
		welcome.setTitle("Welcome to the Discord-Server");
		welcome.setDescription(List.of(
				"Welcome {0}, before you can start chatting and having fun on our Discord server, you need to take a few steps.",
				"",
				"Please select your primary language by clicking one of the buttons below."
		));

		VerificationMessages.Steps steps = new VerificationMessages.Steps();
		steps.setWelcome(welcome);
		messages.setSteps(steps);

		return messages;
	}
}