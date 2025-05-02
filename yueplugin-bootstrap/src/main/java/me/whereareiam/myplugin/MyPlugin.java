package me.whereareiam.myplugin;

import me.whereareiam.myplugin.adapter.config.template.PluginCommandsTemplate;
import me.whereareiam.yue.api.model.config.Commands;
import me.whereareiam.yue.api.output.config.ConfigurationLoader;
import me.whereareiam.yue.api.output.plugin.YuePlugin;
import me.whereareiam.yue.api.output.service.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;

import java.nio.file.Path;

public class MyPlugin implements YuePlugin {
	private final ApplicationContext ctx;
	private final ConfigurationLoader configLoader;
	private final CommandService commandService;
	private final Path pluginPath;

	@Autowired
	public MyPlugin(
			ApplicationContext ctx,
			ConfigurationLoader configLoader,
			CommandService commandService,
			@Qualifier("pluginPath") Path pluginPath
	) {
		this.ctx = ctx;
		this.configLoader = configLoader;
		this.commandService = commandService;
		this.pluginPath = pluginPath;
	}

	@Override
	public void onLoad() {
		commandService.register(
				ctx,
				configLoader.load(pluginPath.resolve("commands"), Commands.class, new PluginCommandsTemplate())
		);
	}

	@Override
	public void onUnload() {
		commandService.unregister("example");
	}
}
