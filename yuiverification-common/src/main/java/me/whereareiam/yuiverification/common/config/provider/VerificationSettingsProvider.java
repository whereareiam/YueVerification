package me.whereareiam.yuiverification.common.config.provider;

import me.whereareiam.configura.Config;
import me.whereareiam.yui.Reloadable;
import me.whereareiam.yuiverification.common.config.template.VerificationSettingsTemplate;
import me.whereareiam.yuiverification.model.config.VerificationSettings;
import org.springframework.stereotype.Component;

@Component
public class VerificationSettingsProvider extends DefaultConfigProvider<VerificationSettings> implements Reloadable {
	@Override
	protected VerificationSettings load() {
		return Config.update(getBasePath().resolve("settings"), VerificationSettings.class);
	}

	@Override
	protected void registerTemplate() {
		Config.registerTemplate(VerificationSettingsTemplate.class);
	}

	@Override
	public Class<VerificationSettings> getObjectType() {
		return VerificationSettings.class;
	}
}