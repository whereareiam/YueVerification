package me.whereareiam.yuiverification.common.audit;

import lombok.RequiredArgsConstructor;
import me.whereareiam.yui.type.AuditSeverity;
import me.whereareiam.yui.util.Audit;
import me.whereareiam.yui.util.translation.Translatable;
import me.whereareiam.yuiverification.AuditTypes;
import me.whereareiam.yuiverification.event.VerificationTimeoutEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class VerificationTimeoutAudit {
	@EventListener
	public void onVerificationTimeout(VerificationTimeoutEvent event) {
		String title = Translatable.text("plugin.yuiverification.audit.timeout.title").resolveDefault();
		String description = Translatable.text("plugin.yuiverification.audit.timeout.description")
				.with("mention", event.getFluctlight().getAsMention())
				.resolveDefault();
		String targetField = Translatable.text("plugin.yuiverification.audit.timeout.fields.target").resolveDefault();
		String timeLimitField = Translatable.text("plugin.yuiverification.audit.timeout.fields.timeLimit").resolveDefault();
		String timeSpentField = Translatable.text("plugin.yuiverification.audit.timeout.fields.timeSpent").resolveDefault();

		Audit.log(AuditTypes.VERIFICATION_TIMEOUT)
				.withSeverity(AuditSeverity.WARNING)
				.withEmbed(embed -> embed
						.setTitle(title)
						.setDescription(description)
						.addField(targetField, event.getFluctlight().getAsMention(), true)
						.addField(timeLimitField, formatDuration(event.getTimeLimit()), true)
						.addField(timeSpentField, formatDuration(event.getTimeSpent()), true)
						.setTimestamp(Instant.now()))
				.send();
	}

	private String formatDuration(Duration duration) {
		long seconds = duration.getSeconds();
		long minutes = seconds / 60;
		long remainingSeconds = seconds % 60;

		if (minutes > 0) {
			return String.format("%dm %ds", minutes, remainingSeconds);
		} else {
			return String.format("%ds", remainingSeconds);
		}
	}
}
