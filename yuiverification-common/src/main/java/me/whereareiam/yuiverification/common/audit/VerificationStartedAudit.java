package me.whereareiam.yuiverification.common.audit;

import lombok.RequiredArgsConstructor;
import me.whereareiam.yui.util.Audit;
import me.whereareiam.yui.util.translation.Translatable;
import me.whereareiam.yuiverification.AuditTypes;
import me.whereareiam.yuiverification.event.VerificationStartedEvent;
import me.whereareiam.yuiverification.model.VerificationContext;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class VerificationStartedAudit {
	@EventListener
	public void onVerificationStarted(VerificationStartedEvent event) {
		VerificationContext context = event.getContext();
		String method = determineMethod(context);

		if (event.isManual()) {
			String title = Translatable.text("plugin.yuiverification.audit.started.manual.title").resolveDefault();
			String description = Translatable.text("plugin.yuiverification.audit.started.manual.description")
					.with("mention", context.getFluctlight().getAsMention())
					.resolveDefault();
			String targetField = Translatable.text("plugin.yuiverification.audit.started.manual.fields.target").resolveDefault();
			String initiatorField = Translatable.text("plugin.yuiverification.audit.started.manual.fields.initiator").resolveDefault();
			String methodField = Translatable.text("plugin.yuiverification.audit.started.manual.fields.method").resolveDefault();

			Audit.log(AuditTypes.VERIFICATION_STARTED_MANUAL)
					.withEmbed(embed -> embed
							.setTitle(title)
							.setDescription(description)
							.addField(targetField, context.getFluctlight().getAsMention(), true)
							.addField(initiatorField, event.getInitiatorId() != null ? "<@" + event.getInitiatorId() + ">" : "System", true)
							.addField(methodField, method, true)
							.setTimestamp(Instant.now()))
					.send();
			return;
		}

		String title = Translatable.text("plugin.yuiverification.audit.started.auto.title").resolveDefault();
		String description = Translatable.text("plugin.yuiverification.audit.started.auto.description")
				.with("mention", context.getFluctlight().getAsMention())
				.resolveDefault();
		String targetField = Translatable.text("plugin.yuiverification.audit.started.auto.fields.target").resolveDefault();
		String methodField = Translatable.text("plugin.yuiverification.audit.started.auto.fields.method").resolveDefault();

		Audit.log(AuditTypes.VERIFICATION_STARTED)
				.withEmbed(embed -> embed
						.setTitle(title)
						.setDescription(description)
						.addField(targetField, context.getFluctlight().getAsMention(), true)
						.addField(methodField, method, true)
						.setTimestamp(Instant.now()))
				.send();
	}

	private String determineMethod(VerificationContext context) {
		ChannelType channelType = context.getConversation().getChannel().getType();
		return channelType == ChannelType.PRIVATE ? "Direct Message" : "Temporary Channel";
	}
}
