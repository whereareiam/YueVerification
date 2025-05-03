package me.whereareiam.yueverification.adapter.config.template;

import me.whereareiam.yueverification.api.model.config.VerificationSettings;
import me.whereareiam.yue.api.output.config.DefaultConfig;
import org.springframework.stereotype.Component;

@Component
public class VerificationSettingsTemplate implements DefaultConfig<VerificationSettings> {
	@Override
	public VerificationSettings getDefault() {
		VerificationSettings settings = new VerificationSettings();

		// Default values
		settings.setScanOnStartup(true);
		settings.setVerifiedRoleId("SET_VERIFIED_ROLE_ID");
		settings.setRulesChannelId("SET_RULES_CHANNEL_ID");

		return settings;
	}
}
