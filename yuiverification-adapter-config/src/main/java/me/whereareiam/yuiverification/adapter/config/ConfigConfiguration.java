package me.whereareiam.yuiverification.adapter.config;

import me.whereareiam.yui.api.output.config.ConfigurationManager;
import me.whereareiam.yui.api.output.config.DefaultConfig;
import me.whereareiam.yuiverification.adapter.config.provider.VerificationSettingsProvider;
import me.whereareiam.yuiverification.api.model.config.VerificationMessages;
import me.whereareiam.yuiverification.api.model.config.VerificationSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class ConfigConfiguration {
	@Bean
	public VerificationSettings settings(VerificationSettingsProvider verificationSettingsProvider) {
		return verificationSettingsProvider.get();
	}

	@Bean
	@Qualifier("pluginLanguagesPath")
	public Path languagesPath(@Qualifier("pluginPath") Path pluginPath) {
		Path languagesPath = pluginPath.resolve("languages");

		if (!languagesPath.toFile().exists()) {
			boolean created = languagesPath.toFile().mkdirs();
			if (!created) throw new RuntimeException("Failed to create plugin languages directory");
		}

		return languagesPath;
	}

	@Autowired
	public void setTemplates(ApplicationContext ctx, ConfigurationManager configManager) {
		configManager.addTemplate(VerificationSettings.class, ctx.getBean("verificationSettingsTemplate", DefaultConfig.class));
		configManager.addTemplate(VerificationMessages.class, ctx.getBean("verificationMessagesTemplate", DefaultConfig.class));
	}
}
