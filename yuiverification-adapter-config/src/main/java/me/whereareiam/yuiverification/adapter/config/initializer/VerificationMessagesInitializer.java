package me.whereareiam.yuiverification.adapter.config.initializer;

import me.whereareiam.yui.api.output.config.ConfigurationLoader;
import me.whereareiam.yuiverification.api.model.config.VerificationMessages;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class VerificationMessagesInitializer {
	@Autowired
	public VerificationMessagesInitializer(@Qualifier("pluginLanguagesPath") Path pluginLanguagesPath, ConfigurationLoader configLoader) {
		configLoader.load(pluginLanguagesPath.resolve(DiscordLocale.ENGLISH_US.getLocale()), VerificationMessages.class);
	}
}
