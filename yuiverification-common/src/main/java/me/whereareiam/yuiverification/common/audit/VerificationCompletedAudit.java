package me.whereareiam.yuiverification.common.audit;

import lombok.RequiredArgsConstructor;
import me.whereareiam.yui.event.journey.JourneyCompletedEvent;
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

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class VerificationCompletedAudit {
	@EventListener
	public void onJourneyCompleted(JourneyCompletedEvent event) {
		if (!"verification".equals(event.getSession().getJourneyId()))
			return;

		VerificationState state = event.getSession().getState(VerificationState.class);
		Duration duration = Duration.between(
				event.getSession().getLifecycle().getStartedAt(),
				event.getSession().getLifecycle().getUpdatedAt()
		);

		Audit.log(AuditTypes.VERIFICATION_COMPLETED)
				.withLocalizedEmbed(locale -> {
					String method = determineMethod(state, locale);
					return buildEmbed(locale, state, duration, method);
				})
				.send();

		Audit.log(AuditTypes.VERIFICATION_WELCOME)
				.withLocalizedEmbed(locale -> new EmbedBuilder()
						.setTitle(Translatable.text("plugin.yuiverification.audit.welcome.title").resolve(locale))
						.setDescription(Translatable.text("plugin.yuiverification.audit.welcome.description")
								.with("mention", state.getFluctlight().getAsMention())
								.resolve(locale))
						.setTimestamp(Instant.now())
						.build())
				.send();
	}

	private String determineMethod(VerificationState state, DiscordLocale locale) {
		ChannelType channelType = state.getConversation().getChannel().getType();
		return channelType == ChannelType.PRIVATE
				? Translatable.text("vocabulary.privateMessage").resolve(locale)
				: Translatable.text("vocabulary.temporaryChannel").resolve(locale);
	}

	private MessageEmbed buildEmbed(DiscordLocale locale, VerificationState state, Duration duration, String method) {
		return new EmbedBuilder()
				.setTitle(Translatable.text("plugin.yuiverification.audit.completed.title").resolve(locale))
				.setDescription(Translatable.text("plugin.yuiverification.audit.completed.description")
						.with("mention", state.getFluctlight().getAsMention())
						.resolve(locale))
				.addField(Translatable.text("plugin.yuiverification.audit.completed.fields.target").resolve(locale), state.getFluctlight().getAsMention(), true)
				.addField(Translatable.text("plugin.yuiverification.audit.completed.fields.duration").resolve(locale), formatDuration(duration), true)
				.addField(Translatable.text("plugin.yuiverification.audit.completed.fields.method").resolve(locale), method, true)
				.setTimestamp(Instant.now())
				.build();
	}

	private String formatDuration(Duration duration) {
		long seconds = duration.getSeconds();
		long minutes = seconds / 60;
		long remainingSeconds = seconds % 60;

		if (minutes > 0) return String.format("%dm %ds", minutes, remainingSeconds);
		return String.format("%ds", remainingSeconds);
	}
}
