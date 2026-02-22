package me.whereareiam.yuiverification.common.audit;

import lombok.RequiredArgsConstructor;
import me.whereareiam.yui.event.journey.step.JourneyStepCompletedEvent;
import me.whereareiam.yui.util.Audit;
import me.whereareiam.yui.util.translation.Translatable;
import me.whereareiam.yuiverification.AuditTypes;
import me.whereareiam.yuiverification.model.VerificationState;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class VerificationStepCompletedAudit {
	@EventListener
	public void onJourneyStepCompleted(JourneyStepCompletedEvent event) {
		if (!"verification".equals(event.getSession().getJourneyId()))
			return;

		VerificationState state = event.getSession().getState(VerificationState.class);
		Audit.log(AuditTypes.VERIFICATION_STEP_COMPLETED)
				.withLocalizedEmbed(locale -> new net.dv8tion.jda.api.EmbedBuilder()
						.setTitle(Translatable.text("plugin.yuiverification.audit.journey.completed.title").resolve(locale))
						.setDescription(Translatable.text("plugin.yuiverification.audit.journey.completed.description")
								.with("mention", state.getFluctlight().getAsMention())
								.resolve(locale))
						.addField(Translatable.text("plugin.yuiverification.audit.journey.completed.fields.target").resolve(locale), state.getFluctlight().getAsMention(), true)
						.addField(Translatable.text("plugin.yuiverification.audit.journey.completed.fields.stepName").resolve(locale), event.getStepId(), true)
						.setTimestamp(Instant.now())
						.build())
				.send();
	}
}
