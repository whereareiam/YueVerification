package me.whereareiam.yueverification.api.model.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationSettings {
	private boolean scanOnStartup;

	private String verifiedRoleId;
	private String rulesChannelId;
}
