package me.whereareiam.yuiverification.common.config.template;

import me.whereareiam.configura.TemplateProvider;
import me.whereareiam.yui.type.ConversationType;
import me.whereareiam.yui.model.type.Duration;
import me.whereareiam.yuiverification.model.config.VerificationSettings;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class VerificationSettingsTemplate implements TemplateProvider<VerificationSettings> {
	@Override
	public VerificationSettings supply(VerificationSettings settings) {
		// Triggers
		VerificationSettings.Triggers triggers = new VerificationSettings.Triggers();
		triggers.setOnStartup(true);
		triggers.setOnJoin(true);
		triggers.setOnClear(true);
		settings.setTriggers(triggers);

		// Conversation settings
		VerificationSettings.ConversationSettings conversation = new VerificationSettings.ConversationSettings();
		conversation.setPreferredModes(Arrays.asList(
				ConversationType.PRIVATE_MESSAGE,
				ConversationType.TEMPORARY_CHANNEL
		));
		conversation.setCloseDelay(Duration.parse("30s"));
		settings.setConversation(conversation);

		// Timeout settings
		VerificationSettings.TimeoutSettings timeout = new VerificationSettings.TimeoutSettings();
		timeout.setEnabled(true);
		timeout.setDuration(Duration.parse("1d"));
		settings.setTimeout(timeout);

		// Role and channel IDs
		settings.setVerifiedRoleId("SET_VERIFIED_ROLE_ID");
		settings.setRulesChannelId("SET_RULES_CHANNEL_ID");

		return settings;
	}
}
