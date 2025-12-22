package me.whereareiam.yuiverification.common.config.provider;

import jakarta.annotation.PostConstruct;
import me.whereareiam.yui.Registry;
import me.whereareiam.yui.Reloadable;
import me.whereareiam.yui.config.ConfigProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.nio.file.Path;

/**
 * Adapter-layer base that wires {@link ConfigProvider} into our reload registry
 * and exposes the resolved base path for subclasses.
 * <p>
 * By default uses the {@code pluginPath} base. Configs that need another base
 * (e.g. styles or languages) can override {@link #getBasePath()}.
 */
public abstract class DefaultConfigProvider<T> extends ConfigProvider<T> {
	@Autowired
	@Qualifier("pluginPath")
	private Path basePath;

	@Autowired
	private Registry<Reloadable> reloadables;

	@PostConstruct
	private void register() {
		reloadables.register(this);
	}

	protected Path getBasePath() {
		return basePath;
	}
}
