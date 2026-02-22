package me.whereareiam.yuiverification.common.audit;

import lombok.RequiredArgsConstructor;
import me.whereareiam.yui.event.journey.session.JourneySessionStartedEvent;
import me.whereareiam.yui.util.Audit;
import me.whereareiam.yui.util.translation.Translatable;
import me.whereareiam.yuiverification.AuditTypes;
import me.whereareiam.yuiverification.model.VerificationState;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class VerificationStartedAudit {
	@EventListener
	public void onJourneyStarted(JourneySessionStartedEvent event) {
		if (!"verification".equals(event.getSession().getJourneyId()))
			return;

		VerificationState state = event.getSession().getState(VerificationState.class);
		String method = determineMethod(state);
		if (state.isManual()) {
			Audit.log(AuditTypes.VERIFICATION_STARTED_MANUAL)
					.withLocalizedEmbed(locale -> buildManualEmbed(locale, state, method, state.getInitiatorId()))
					.send();
			return;
		}

		Audit.log(AuditTypes.VERIFICATION_STARTED)
				.withLocalizedEmbed(locale -> buildAutoEmbed(locale, state, method))
				.send();
	}

	private String determineMethod(VerificationState state) {
		ChannelType channelType = state.getConversation().getChannel().getType();
		return channelType == ChannelType.PRIVATE ? "Direct Message" : "Temporary Channel";
	}

	private MessageEmbed buildManualEmbed(DiscordLocale locale, VerificationState state, String method, Long initiatorId) {
		String title = Translatable.text("plugin.yuiverification.audit.started.manual.title").resolve(locale);
		String description = Translatable.text("plugin.yuiverification.audit.started.manual.description")
				.with("mention", state.getFluctlight().getAsMention())
				.resolve(locale);
		String targetField = Translatable.text("plugin.yuiverification.audit.started.manual.fields.target").resolve(locale);
		String initiatorField = Translatable.text("plugin.yuiverification.audit.started.manual.fields.initiator").resolve(locale);
		String methodField = Translatable.text("plugin.yuiverification.audit.started.manual.fields.method").resolve(locale);

		return new EmbedBuilder()
				.setTitle(title)
				.setDescription(description)
				.addField(targetField, state.getFluctlight().getAsMention(), true)
				.addField(initiatorField, initiatorId != null ? "<@" + initiatorId + ">" : "System", true)
				.addField(methodField, method, true)
				.setTimestamp(Instant.now())
				.build();
	}

	private MessageEmbed buildAutoEmbed(DiscordLocale locale, VerificationState state, String method) {
		String title = Translatable.text("plugin.yuiverification.audit.started.auto.title").resolve(locale);
		String description = Translatable.text("plugin.yuiverification.audit.started.auto.description")
				.with("mention", state.getFluctlight().getAsMention())
				.resolve(locale);
		String targetField = Translatable.text("plugin.yuiverification.audit.started.auto.fields.target").resolve(locale);
		String methodField = Translatable.text("plugin.yuiverification.audit.started.auto.fields.method").resolve(locale);

		return new EmbedBuilder()
				.setTitle(title)
				.setDescription(description)
				.addField(targetField, state.getFluctlight().getAsMention(), true)
				.addField(methodField, method, true)
				.setTimestamp(Instant.now())
				.build();
	}
}
