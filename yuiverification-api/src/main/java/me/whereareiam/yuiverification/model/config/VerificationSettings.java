package me.whereareiam.yuiverification.model.config;

import lombok.Getter;
import lombok.Setter;
import me.whereareiam.yui.type.ConversationType;
import me.whereareiam.yui.model.type.Duration;

import java.util.List;

@Getter
@Setter
public class VerificationSettings {
	private Triggers triggers;
	private ConversationSettings conversation;
	private TimeoutSettings timeout;

	private String verifiedRoleId;
	private String rulesChannelId;

	@Getter
	@Setter
	public static class Triggers {
		private boolean onStartup;
		private boolean onJoin;
		private boolean onClear;
	}

	@Getter
	@Setter
	public static class ConversationSettings {
		private List<ConversationType> preferredModes;
		private Duration closeDelay;
	}

	@Getter
	@Setter
	public static class TimeoutSettings {
		private boolean enabled;
		private Duration duration;
	}
}
