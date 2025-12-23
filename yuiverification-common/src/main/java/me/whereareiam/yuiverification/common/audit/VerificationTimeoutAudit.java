package me.whereareiam.yuiverification.common.audit;

import lombok.RequiredArgsConstructor;
import me.whereareiam.yui.util.Audit;
import me.whereareiam.yui.util.style.StyleKit;
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
		Audit.log(AuditTypes.VERIFICATION_TIMEOUT)
				.withLocalizedEmbed(locale -> StyleKit.embeds().warning()
						.setTitle(Translatable.text("plugin.yuiverification.audit.timeout.title").resolve(locale))
						.setDescription(Translatable.text("plugin.yuiverification.audit.timeout.description")
								.with("mention", event.getFluctlight().getAsMention())
								.resolve(locale))
						.addField(Translatable.text("plugin.yuiverification.audit.timeout.fields.target").resolve(locale), event.getFluctlight().getAsMention(), true)
						.addField(Translatable.text("plugin.yuiverification.audit.timeout.fields.timeLimit").resolve(locale), formatDuration(event.getTimeLimit()), true)
						.addField(Translatable.text("plugin.yuiverification.audit.timeout.fields.timeSpent").resolve(locale), formatDuration(event.getTimeSpent()), true)
						.setTimestamp(Instant.now())
						.build())
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
