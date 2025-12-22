package me.whereareiam.yuiverification.common.audit;

import lombok.RequiredArgsConstructor;
import me.whereareiam.yui.type.AuditSeverity;
import me.whereareiam.yui.util.Audit;
import me.whereareiam.yui.util.translation.Translatable;
import me.whereareiam.yuiverification.AuditTypes;
import me.whereareiam.yuiverification.event.VerificationAbandonedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class VerificationAbandonedAudit {
	@EventListener
	public void onVerificationAbandoned(VerificationAbandonedEvent event) {
		String title = Translatable.text("plugin.yuiverification.audit.abandoned.title").resolveDefault();
		String description = Translatable.text("plugin.yuiverification.audit.abandoned.description")
				.with("mention", event.getFluctlight().getAsMention())
				.resolveDefault();
		String targetField = Translatable.text("plugin.yuiverification.audit.abandoned.fields.target").resolveDefault();
		String currentStepField = Translatable.text("plugin.yuiverification.audit.abandoned.fields.currentStep").resolveDefault();

		Audit.log(AuditTypes.VERIFICATION_ABANDONED)
				.withSeverity(AuditSeverity.WARNING)
				.withEmbed(embed -> embed
						.setTitle(title)
						.setDescription(description)
						.addField(targetField, event.getFluctlight().getAsMention(), true)
						.addField(currentStepField, event.getCurrentStep() != null ? event.getCurrentStep() : "Unknown", true)
						.setTimestamp(Instant.now()))
				.send();
	}
}
