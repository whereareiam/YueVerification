package me.whereareiam.yuiverification.common.audit;

import lombok.RequiredArgsConstructor;
import me.whereareiam.yui.type.AuditSeverity;
import me.whereareiam.yui.util.Audit;
import me.whereareiam.yui.util.translation.Translatable;
import me.whereareiam.yuiverification.AuditTypes;
import me.whereareiam.yuiverification.event.VerificationFailedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class VerificationFailedAudit {
	@EventListener
	public void onVerificationFailed(VerificationFailedEvent event) {
		String title = Translatable.text("plugin.yuiverification.audit.failed.title").resolveDefault();
		String description = Translatable.text("plugin.yuiverification.audit.failed.description")
				.with("mention", event.getFluctlight().getAsMention())
				.resolveDefault();
		String targetField = Translatable.text("plugin.yuiverification.audit.failed.fields.target").resolveDefault();
		String errorField = Translatable.text("plugin.yuiverification.audit.failed.fields.error").resolveDefault();

		Audit.log(AuditTypes.VERIFICATION_FAILED)
				.withSeverity(AuditSeverity.ERROR)
				.withEmbed(embed -> embed
						.setTitle(title)
						.setDescription(description)
						.addField(targetField, event.getFluctlight().getAsMention(), true)
						.addField(errorField, event.getErrorMessage() != null ? event.getErrorMessage() : "Unknown error", false)
						.setTimestamp(Instant.now()))
				.send();
	}
}
