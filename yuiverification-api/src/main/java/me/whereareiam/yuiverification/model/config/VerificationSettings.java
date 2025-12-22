package me.whereareiam.yuiverification.model.config;

import lombok.Getter;
import lombok.Setter;
import me.whereareiam.yui.model.type.Duration;

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
		private boolean preferPrivateMessage;
		private boolean allowTemporaryChannel;
		private Duration closeDelay;
	}

	@Getter
	@Setter
	public static class TimeoutSettings {
		private boolean enabled;
		private Duration duration;
	}
}
