package me.whereareiam.yuiverification.common.journey;

import lombok.AllArgsConstructor;
import me.whereareiam.yui.annotation.journey.JourneyStep;
import me.whereareiam.yui.conversation.ConversationService;
import me.whereareiam.yui.journey.definition.group.JourneyStepDefinition;
import me.whereareiam.yui.model.journey.JourneyInstruction;
import me.whereareiam.yui.model.journey.JourneyStepContext;
import me.whereareiam.yui.util.style.StyleKit;
import me.whereareiam.yui.util.translation.Translatable;
import me.whereareiam.yuiverification.model.VerificationState;
import me.whereareiam.yuiverification.model.config.VerificationSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@JourneyStep(journeyId = "verification", stepId = "end", order = 999)
public class EndStep implements JourneyStepDefinition<VerificationState> {
	private final VerificationSettings settings;
	private final ConversationService conversationService;

	@Override
	public @NotNull JourneyInstruction onEnter(JourneyStepContext<VerificationState> context) {
		VerificationState state = context.state();
		state.getFluctlight().addAllowedRole(Long.parseLong(settings.getVerifiedRoleId()));
		state.setCompleted(true);

		if (state.getMessage() != null) {
			state.getMessage()
					.editMessageEmbeds(buildEmbed(state).build())
					.setComponents()
					.queue(state::setMessage, _ -> {
					});
		}

		long closeDelay = settings.getConversation().getCloseDelay() != null
				? settings.getConversation().getCloseDelay().getSeconds()
				: 0;
		conversationService.close(state.getConversation(), closeDelay);

		return JourneyInstruction.complete();
	}

	private EmbedBuilder buildEmbed(VerificationState state) {
		return StyleKit.embeds()
				.success()
				.setTitle(Translatable.text("plugin.yuiverification.steps.end.title").resolve(state.getFluctlight()))
				.setDescription(Translatable.text("plugin.yuiverification.steps.end.description")
						.with("mention", state.getFluctlight().getAsMention())
						.with("rulesChannel", "<#" + settings.getRulesChannelId() + ">")
						.resolve(state.getFluctlight()));
	}
}
