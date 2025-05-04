package me.whereareiam.yuiverification.api.model.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationSettings {
	private boolean scanOnStartup;
	private int channelTimeout;

	private String verifiedRoleId;
	private String rulesChannelId;
}
