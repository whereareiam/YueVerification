package me.whereareiam.yuiverification.common.audit;

import lombok.RequiredArgsConstructor;
import me.whereareiam.yui.util.Audit;
import me.whereareiam.yui.util.translation.Translatable;
import me.whereareiam.yuiverification.AuditTypes;
import me.whereareiam.yuiverification.event.VerificationCompletedEvent;
import me.whereareiam.yuiverification.model.VerificationContext;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.entities.channel.ChannelType;
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
		String method = determineMethod(context);

		Audit.log(AuditTypes.VERIFICATION_COMPLETED)
				.withLocalizedEmbed(locale -> buildEmbed(locale, context, duration, method))
				.send();
	}

	private String determineMethod(VerificationContext context) {
		ChannelType channelType = context.getConversation().getChannel().getType();
		return channelType == ChannelType.PRIVATE ? "Direct Message" : "Temporary Channel";
	}

	private net.dv8tion.jda.api.entities.MessageEmbed buildEmbed(DiscordLocale locale, VerificationContext context, Duration duration, String method) {
		return new net.dv8tion.jda.api.EmbedBuilder()
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
		} else {
			return String.format("%ds", remainingSeconds);
		}
	}
}
