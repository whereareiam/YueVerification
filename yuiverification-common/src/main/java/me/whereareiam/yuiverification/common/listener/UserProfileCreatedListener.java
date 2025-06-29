package me.whereareiam.yuiverification.common.listener;

import lombok.AllArgsConstructor;
import me.whereareiam.yui.api.event.user.UserProfileCreatedEvent;
import me.whereareiam.yuiverification.api.service.VerificationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserProfileCreatedListener {
	private final VerificationService verificationService;

	@EventListener
	public void onUserProfileCreatedEvent(UserProfileCreatedEvent event) {
		verificationService.verify(event.getUserProfile().getId());
	}
}
