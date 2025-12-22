package me.whereareiam.yuiverification.common.config.provider;

import me.whereareiam.configura.Config;
import me.whereareiam.yui.command.DefinitionProvider;
import me.whereareiam.yui.model.command.CommandDefinition;
import me.whereareiam.yui.type.Source;
import me.whereareiam.yuiverification.common.config.template.VerificationCommandsTemplate;
import me.whereareiam.yuiverification.model.config.VerificationCommands;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class VerificationCommandsProvider extends DefaultConfigProvider<VerificationCommands> implements DefinitionProvider {
	@Override
	protected VerificationCommands load() {
		return Config.update(getBasePath().resolve("commands"), VerificationCommands.class);
	}

	@Override
	protected void registerTemplate() {
		Config.registerTemplate(VerificationCommandsTemplate.class);
	}

	@Override
	public Class<VerificationCommands> getObjectType() {
		return VerificationCommands.class;
	}

	@Override
	public @NotNull String id() {
		return "yuiverification";
	}

	@Override
	public @NotNull Source source() {
		return Source.EXTERNAL;
	}

	@Override
	public @NotNull Map<String, CommandDefinition> definitions() {
		return this.get().getCommands();
	}
}
