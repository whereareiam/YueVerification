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

		Audit.log(AuditTypes.VERIFICATION_STEP_COMPLETED)
				.withLocalizedEmbed(locale -> new net.dv8tion.jda.api.EmbedBuilder()
						.setTitle(Translatable.text("plugin.yuiverification.audit.step.completed.title").resolve(locale))
						.setDescription(Translatable.text("plugin.yuiverification.audit.step.completed.description")
								.with("mention", context.getFluctlight().getAsMention())
								.resolve(locale))
						.addField(Translatable.text("plugin.yuiverification.audit.step.completed.fields.target").resolve(locale), context.getFluctlight().getAsMention(), true)
						.addField(Translatable.text("plugin.yuiverification.audit.step.completed.fields.stepName").resolve(locale), event.getStepName(), true)
						.setTimestamp(Instant.now())
						.build())
				.send();
	}
}
