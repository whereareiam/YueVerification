package me.whereareiam.yuiverification.common.audit;

import lombok.RequiredArgsConstructor;
import me.whereareiam.yui.util.Audit;
import me.whereareiam.yui.util.translation.Translatable;
import me.whereareiam.yuiverification.AuditTypes;
import me.whereareiam.yuiverification.event.VerificationCompletedEvent;
import me.whereareiam.yuiverification.model.VerificationContext;
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

		String title = Translatable.text("plugin.yuiverification.audit.completed.title").resolveDefault();
		String description = Translatable.text("plugin.yuiverification.audit.completed.description")
				.with("mention", context.getFluctlight().getAsMention())
				.resolveDefault();
		String targetField = Translatable.text("plugin.yuiverification.audit.completed.fields.target").resolveDefault();
		String durationField = Translatable.text("plugin.yuiverification.audit.completed.fields.duration").resolveDefault();
		String methodField = Translatable.text("plugin.yuiverification.audit.completed.fields.method").resolveDefault();

		Audit.log(AuditTypes.VERIFICATION_COMPLETED)
				.withEmbed(embed -> embed
						.setTitle(title)
						.setDescription(description)
						.addField(targetField, context.getFluctlight().getAsMention(), true)
						.addField(durationField, formatDuration(duration), true)
						.addField(methodField, method, true)
						.setTimestamp(Instant.now()))
				.send();
	}

	private String determineMethod(VerificationContext context) {
		ChannelType channelType = context.getConversation().getChannel().getType();
		return channelType == ChannelType.PRIVATE ? "Direct Message" : "Temporary Channel";
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
