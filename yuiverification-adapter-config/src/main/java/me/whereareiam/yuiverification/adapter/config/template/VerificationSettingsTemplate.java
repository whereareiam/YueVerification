package me.whereareiam.yuiverification.adapter.config.template;

import me.whereareiam.yui.api.output.config.DefaultConfig;
import me.whereareiam.yuiverification.api.model.config.VerificationSettings;
import org.springframework.stereotype.Component;

@Component
public class VerificationSettingsTemplate implements DefaultConfig<VerificationSettings> {
	@Override
	public VerificationSettings getDefault() {
		VerificationSettings settings = new VerificationSettings();

		// Default values
		settings.setScanOnStartup(true);
		settings.setChannelTimeout(120);

		settings.setVerifiedRoleId("SET_VERIFIED_ROLE_ID");
		settings.setRulesChannelId("SET_RULES_CHANNEL_ID");

		return settings;
	}
}
