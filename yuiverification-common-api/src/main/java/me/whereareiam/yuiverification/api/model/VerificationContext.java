package me.whereareiam.yuiverification.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.concurrent.CompletableFuture;

/**
 * A mutable context object propagated through the whole verification pipeline.
 * Steps can read and update the message as they need (e.g. {@code ctx.getMessage()
 * .editMessage("new content").queue()}).
 */
@Getter
@RequiredArgsConstructor
public class VerificationContext {
	private final long userId;
	private final TextChannel channel;

	@Setter
	private Message message;

	private transient CompletableFuture<Void> step;

	public CompletableFuture<Void> start() {
		this.step = new CompletableFuture<>();
		return this.step;
	}

	public void next() {
		if (step != null && !step.isDone())
			step.complete(null);
	}
}
