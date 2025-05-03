package me.whereareiam.yueverification.adapter.config.provider;

import jakarta.annotation.PostConstruct;
import me.whereareiam.yue.api.input.Registry;
import me.whereareiam.yue.api.output.Reloadable;
import me.whereareiam.yue.api.output.config.ConfigurationLoader;
import me.whereareiam.yue.api.output.provider.Provider;
import me.whereareiam.yueverification.api.model.config.VerificationSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class VerificationSettingsProvider implements Provider<VerificationSettings>, Reloadable {
	private final Path pluginPath;
	private final ConfigurationLoader configLoader;

	private VerificationSettings settings;

	@Autowired
	public VerificationSettingsProvider(
			@Qualifier("pluginPath") Path pluginPath,
			ConfigurationLoader configLoader,
			Registry<Reloadable> registry
	) {
		this.pluginPath = pluginPath;
		this.configLoader = configLoader;

		registry.register(this);
	}

	@PostConstruct
	public void init() {
		load();
	}

	public VerificationSettings get() {
		if (settings == null) {
			load();
		}
		return settings;
	}

	@Override
	public void reload() {
		load();
	}

	private void load() {
		settings = configLoader.load(pluginPath.resolve("settings"), VerificationSettings.class);
	}
}