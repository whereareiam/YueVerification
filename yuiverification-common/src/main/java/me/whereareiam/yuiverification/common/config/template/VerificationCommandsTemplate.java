package me.whereareiam.yuiverification.common.config.template;

import me.whereareiam.configura.TemplateProvider;
import me.whereareiam.yui.model.command.CommandCooldown;
import me.whereareiam.yui.model.command.CommandDefinition;
import me.whereareiam.yui.model.requirement.Requirements;
import me.whereareiam.yui.model.requirement.type.RoleRequirement;
import me.whereareiam.yui.type.CommandCategory;
import me.whereareiam.yui.type.requirement.RequirementOperator;
import me.whereareiam.yuiverification.model.config.VerificationCommands;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class VerificationCommandsTemplate implements TemplateProvider<VerificationCommands> {
	@Override
	public VerificationCommands supply(VerificationCommands commands) {
		// Create requirements for verify command
		Requirements verifyRequirements = new Requirements();
		verifyRequirements.setOperator(RequirementOperator.AND);

		RoleRequirement roleRequirement = new RoleRequirement();
		roleRequirement.setRoles(List.of("EXAMPLE"));
		roleRequirement.setRoleMatchBy("NAME");
		verifyRequirements.getGroups().put("ROLE", roleRequirement);

		CommandDefinition verify = new CommandDefinition(
				true,
				List.of("verify"),
				"translate(plugin.yuiverification.command.verify.description)",
				"translate(plugin.yuiverification.command.verify.example)",
				"{command} {alias} (user)",
				Map.of(
						"user", "translate(plugin.yuiverification.command.verify.variables.user)"
				),
				CommandCategory.ADMINISTRATION,
				new CommandCooldown(false, 10, ""),
				verifyRequirements
		);

		commands.getCommands().put("verify", verify);

		return commands;
	}
}
