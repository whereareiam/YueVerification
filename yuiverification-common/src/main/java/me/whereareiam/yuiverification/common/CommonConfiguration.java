package me.whereareiam.yuiverification.common;

import me.whereareiam.yui.Constants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class CommonConfiguration {
	@Bean
	@Qualifier("pluginLanguagesPath")
	public Path pluginLanguagesPath(@Qualifier("pluginPath") Path pluginPath) {
		Path languagesPath = pluginPath.resolve(Constants.Structure.languagesDir);

		if (!languagesPath.toFile().exists()) {
			boolean created = languagesPath.toFile().mkdirs();
			if (!created) throw new RuntimeException("Failed to create plugin languages directory");
		}

		return languagesPath;
	}
}
