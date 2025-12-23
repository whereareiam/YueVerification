package me.whereareiam.yuiverification.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.whereareiam.yui.conversation.Conversation;
import me.whereareiam.yui.model.fluctlight.Fluctlight;
import net.dv8tion.jda.api.entities.Message;

import java.util.concurrent.CompletableFuture;

/**
 * A mutable context object propagated through the whole verification pipeline.
 * Steps can read and update the message as they need (e.g. {@code ctx.getMessage()
 * .editMessage("new content").queue()}).
 */
@Getter
@RequiredArgsConstructor
public class VerificationContext {
	private final Fluctlight fluctlight;
	private final Conversation conversation;

	@Setter
	private Message message;

	@Setter
	private boolean completed = false;

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
