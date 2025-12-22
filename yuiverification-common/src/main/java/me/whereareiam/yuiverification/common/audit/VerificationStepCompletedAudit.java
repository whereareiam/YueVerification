package me.whereareiam.yuiverification.common.audit;

import lombok.RequiredArgsConstructor;
import me.whereareiam.yui.util.Audit;
import me.whereareiam.yui.util.translation.Translatable;
import me.whereareiam.yuiverification.AuditTypes;
import me.whereareiam.yuiverification.event.VerificationStepCompletedEvent;
import me.whereareiam.yuiverification.model.VerificationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class VerificationStepCompletedAudit {
	@EventListener
	public void onVerificationStepCompleted(VerificationStepCompletedEvent event) {
		VerificationContext context = event.getContext();

		String title = Translatable.text("plugin.yuiverification.audit.step.completed.title").resolveDefault();
		String description = Translatable.text("plugin.yuiverification.audit.step.completed.description")
				.with("mention", context.getFluctlight().getAsMention())
				.resolveDefault();
		String targetField = Translatable.text("plugin.yuiverification.audit.step.completed.fields.target").resolveDefault();
		String stepNameField = Translatable.text("plugin.yuiverification.audit.step.completed.fields.stepName").resolveDefault();

		Audit.log(AuditTypes.VERIFICATION_STEP_COMPLETED)
				.withEmbed(embed -> embed
						.setTitle(title)
						.setDescription(description)
						.addField(targetField, context.getFluctlight().getAsMention(), true)
						.addField(stepNameField, event.getStepName(), true)
						.setTimestamp(Instant.now()))
				.send();
	}
}
