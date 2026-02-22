package me.whereareiam.yuiverification.common.audit;

import lombok.RequiredArgsConstructor;
import me.whereareiam.yui.event.journey.session.JourneySessionTimeoutEvent;
import me.whereareiam.yui.util.Audit;
import me.whereareiam.yui.util.style.StyleKit;
import me.whereareiam.yui.util.translation.Translatable;
import me.whereareiam.yuiverification.AuditTypes;
import me.whereareiam.yuiverification.model.VerificationState;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class VerificationTimeoutAudit {
	@EventListener
	public void onJourneyTimedOut(JourneySessionTimeoutEvent event) {
		if (!"verification".equals(event.getSession().getJourneyId()))
			return;

		VerificationState state = event.getSession().getState(VerificationState.class);
		Duration timeLimit = Duration.ofSeconds(event.getTimeoutSeconds());
		Duration timeSpent = event.getElapsed();

		Audit.log(AuditTypes.VERIFICATION_TIMEOUT)
				.withLocalizedEmbed(locale -> StyleKit.embeds().warning()
						.setTitle(Translatable.text("plugin.yuiverification.audit.timeout.title").resolve(locale))
						.setDescription(Translatable.text("plugin.yuiverification.audit.timeout.description")
								.with("mention", state.getFluctlight().getAsMention())
								.resolve(locale))
						.addField(Translatable.text("plugin.yuiverification.audit.timeout.fields.target").resolve(locale), state.getFluctlight().getAsMention(), true)
						.addField(Translatable.text("plugin.yuiverification.audit.timeout.fields.timeLimit").resolve(locale), formatDuration(timeLimit), true)
						.addField(Translatable.text("plugin.yuiverification.audit.timeout.fields.timeSpent").resolve(locale), formatDuration(timeSpent), true)
						.setTimestamp(Instant.now())
						.build())
				.send();
	}

	private String formatDuration(Duration duration) {
		long seconds = duration.getSeconds();
		long minutes = seconds / 60;
		long remainingSeconds = seconds % 60;
		if (minutes > 0)
			return String.format("%dm %ds", minutes, remainingSeconds);
		return String.format("%ds", remainingSeconds);
	}
}
