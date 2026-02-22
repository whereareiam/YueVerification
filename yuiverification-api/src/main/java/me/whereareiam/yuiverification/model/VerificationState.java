package me.whereareiam.yuiverification.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.whereareiam.yui.conversation.Conversation;
import me.whereareiam.yui.model.fluctlight.Fluctlight;
import net.dv8tion.jda.api.entities.Message;

@Getter
@RequiredArgsConstructor
public class VerificationState {
	private final Fluctlight fluctlight;
	private final Conversation conversation;
	private final Long initiatorId;
	private final boolean manual;

	@Setter
	private Message message;

	@Setter
	private boolean completed;
}
