package me.whereareiam.yuiverification.common.audit;

import lombok.RequiredArgsConstructor;
import me.whereareiam.yui.event.journey.JourneyFailedEvent;
import me.whereareiam.yui.util.Audit;
import me.whereareiam.yui.util.style.StyleKit;
import me.whereareiam.yui.util.translation.Translatable;
import me.whereareiam.yuiverification.AuditTypes;
import me.whereareiam.yuiverification.model.VerificationState;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class VerificationFailedAudit {
	@EventListener
	public void onJourneyFailed(JourneyFailedEvent event) {
		if (!"verification".equals(event.getSession().getJourneyId()))
			return;

		VerificationState state = event.getSession().getState(VerificationState.class);
		Audit.log(AuditTypes.VERIFICATION_FAILED)
				.withLocalizedEmbed(locale -> StyleKit.embeds().error()
						.setTitle(Translatable.text("plugin.yuiverification.audit.failed.title").resolve(locale))
						.setDescription(Translatable.text("plugin.yuiverification.audit.failed.description")
								.with("mention", state.getFluctlight().getAsMention())
								.resolve(locale))
						.addField(Translatable.text("plugin.yuiverification.audit.failed.fields.target").resolve(locale), state.getFluctlight().getAsMention(), true)
						.addField(Translatable.text("plugin.yuiverification.audit.failed.fields.error").resolve(locale), "Not available", false)
						.setTimestamp(Instant.now())
						.build())
				.send();
	}
}
