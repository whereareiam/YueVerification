package me.whereareiam.yuiverification.model.config;

import lombok.Getter;
import lombok.Setter;
import me.whereareiam.yui.model.command.CommandDefinition;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class VerificationCommands {
	private Map<String, CommandDefinition> commands = new HashMap<>();
}
