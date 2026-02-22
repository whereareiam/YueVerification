package me.whereareiam.yuiverification.common.audit;

import lombok.RequiredArgsConstructor;
import me.whereareiam.yui.event.journey.JourneyCancelledEvent;
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
public class VerificationAbandonedAudit {
	@EventListener
	public void onJourneyCancelled(JourneyCancelledEvent event) {
		if (!"verification".equals(event.getSession().getJourneyId()))
			return;

		VerificationState state = event.getSession().getState(VerificationState.class);
		Audit.log(AuditTypes.VERIFICATION_ABANDONED)
				.withLocalizedEmbed(locale -> StyleKit.embeds().warning()
						.setTitle(Translatable.text("plugin.yuiverification.audit.abandoned.title").resolve(locale))
						.setDescription(Translatable.text("plugin.yuiverification.audit.abandoned.description")
								.with("mention", state.getFluctlight().getAsMention())
								.resolve(locale))
						.addField(Translatable.text("plugin.yuiverification.audit.abandoned.fields.target").resolve(locale), state.getFluctlight().getAsMention(), true)
						.addField(Translatable.text("plugin.yuiverification.audit.abandoned.fields.currentStep").resolve(locale), "Unknown", true)
						.setTimestamp(Instant.now())
						.build())
				.send();
	}
}
