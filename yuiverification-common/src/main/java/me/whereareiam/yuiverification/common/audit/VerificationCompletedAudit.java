package me.whereareiam.yuiverification.common.audit;

import lombok.RequiredArgsConstructor;
import me.whereareiam.yui.util.Audit;
import me.whereareiam.yui.util.translation.Translatable;
import me.whereareiam.yuiverification.AuditTypes;
import me.whereareiam.yuiverification.event.VerificationCompletedEvent;
import me.whereareiam.yuiverification.model.VerificationContext;
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
	public void onVerificationCompleted(VerificationCompletedEvent event) {
		VerificationContext context = event.getContext();
		Duration duration = Duration.between(event.getStartTime(), Instant.now());

		Audit.log(AuditTypes.VERIFICATION_COMPLETED)
				.withLocalizedEmbed(locale -> {
					String method = determineMethod(context, locale);
					return buildEmbed(locale, context, duration, method);
				})
				.send();
	}

	@EventListener
	public void onWelcomeAudit(VerificationCompletedEvent event) {
		VerificationContext context = event.getContext();

		Audit.log(AuditTypes.VERIFICATION_WELCOME)
				.withLocalizedEmbed(locale -> new EmbedBuilder()
						.setTitle(Translatable.text("plugin.yuiverification.audit.welcome.title").resolve(locale))
						.setDescription(Translatable.text("plugin.yuiverification.audit.welcome.description")
								.with("mention", context.getFluctlight().getAsMention())
								.resolve(locale))
						.setTimestamp(Instant.now())
						.build())
				.send();
	}

	private String determineMethod(VerificationContext context, DiscordLocale locale) {
		ChannelType channelType = context.getConversation().getChannel().getType();
		return channelType == ChannelType.PRIVATE ?
				Translatable.text("vocabulary.privateMessage").resolve(locale) :
				Translatable.text("vocabulary.temporaryChannel").resolve(locale);
	}

	private MessageEmbed buildEmbed(DiscordLocale locale, VerificationContext context, Duration duration, String method) {
		return new EmbedBuilder()
				.setTitle(Translatable.text("plugin.yuiverification.audit.completed.title").resolve(locale))
				.setDescription(Translatable.text("plugin.yuiverification.audit.completed.description")
						.with("mention", context.getFluctlight().getAsMention())
						.resolve(locale))
				.addField(Translatable.text("plugin.yuiverification.audit.completed.fields.target").resolve(locale), context.getFluctlight().getAsMention(), true)
				.addField(Translatable.text("plugin.yuiverification.audit.completed.fields.duration").resolve(locale), formatDuration(duration), true)
				.addField(Translatable.text("plugin.yuiverification.audit.completed.fields.method").resolve(locale), method, true)
				.setTimestamp(Instant.now())
				.build();
	}

	private String formatDuration(Duration duration) {
		long seconds = duration.getSeconds();
		long minutes = seconds / 60;
		long remainingSeconds = seconds % 60;

		if (minutes > 0) {
			return String.format("%dm %ds", minutes, remainingSeconds);
		}

		return String.format("%ds", remainingSeconds);
	}
}
