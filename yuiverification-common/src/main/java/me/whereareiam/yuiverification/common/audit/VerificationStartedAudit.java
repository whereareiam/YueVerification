package me.whereareiam.yuiverification.common.audit;

import lombok.RequiredArgsConstructor;
import me.whereareiam.yui.util.Audit;
import me.whereareiam.yui.util.translation.Translatable;
import me.whereareiam.yuiverification.AuditTypes;
import me.whereareiam.yuiverification.event.VerificationStartedEvent;
import me.whereareiam.yuiverification.model.VerificationContext;
import net.dv8tion.jda.api.interactions.DiscordLocale;
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
			Audit.log(AuditTypes.VERIFICATION_STARTED_MANUAL)
					.withLocalizedEmbed(locale -> buildManualEmbed(locale, context, method, event.getInitiatorId()))
					.send();
			return;
		}

		Audit.log(AuditTypes.VERIFICATION_STARTED)
				.withLocalizedEmbed(locale -> buildAutoEmbed(locale, context, method))
				.send();
	}

	private String determineMethod(VerificationContext context) {
		ChannelType channelType = context.getConversation().getChannel().getType();
		return channelType == ChannelType.PRIVATE ? "Direct Message" : "Temporary Channel";
	}

	private net.dv8tion.jda.api.entities.MessageEmbed buildManualEmbed(DiscordLocale locale, VerificationContext context, String method, Long initiatorId) {
		String title = Translatable.text("plugin.yuiverification.audit.started.manual.title").resolve(locale);
		String description = Translatable.text("plugin.yuiverification.audit.started.manual.description")
				.with("mention", context.getFluctlight().getAsMention())
				.resolve(locale);
		String targetField = Translatable.text("plugin.yuiverification.audit.started.manual.fields.target").resolve(locale);
		String initiatorField = Translatable.text("plugin.yuiverification.audit.started.manual.fields.initiator").resolve(locale);
		String methodField = Translatable.text("plugin.yuiverification.audit.started.manual.fields.method").resolve(locale);

		return new net.dv8tion.jda.api.EmbedBuilder()
				.setTitle(title)
				.setDescription(description)
				.addField(targetField, context.getFluctlight().getAsMention(), true)
				.addField(initiatorField, initiatorId != null ? "<@" + initiatorId + ">" : "System", true)
				.addField(methodField, method, true)
				.setTimestamp(Instant.now())
				.build();
	}

	private net.dv8tion.jda.api.entities.MessageEmbed buildAutoEmbed(DiscordLocale locale, VerificationContext context, String method) {
		String title = Translatable.text("plugin.yuiverification.audit.started.auto.title").resolve(locale);
		String description = Translatable.text("plugin.yuiverification.audit.started.auto.description")
				.with("mention", context.getFluctlight().getAsMention())
				.resolve(locale);
		String targetField = Translatable.text("plugin.yuiverification.audit.started.auto.fields.target").resolve(locale);
		String methodField = Translatable.text("plugin.yuiverification.audit.started.auto.fields.method").resolve(locale);

		return new net.dv8tion.jda.api.EmbedBuilder()
				.setTitle(title)
				.setDescription(description)
				.addField(targetField, context.getFluctlight().getAsMention(), true)
				.addField(methodField, method, true)
				.setTimestamp(Instant.now())
				.build();
	}
}
