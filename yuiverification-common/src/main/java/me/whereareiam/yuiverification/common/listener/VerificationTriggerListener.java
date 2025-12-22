package me.whereareiam.yuiverification.common.listener;

import lombok.AllArgsConstructor;
import me.whereareiam.yui.event.fluctlight.FluctlightClearedEvent;
import me.whereareiam.yui.event.fluctlight.FluctlightCreatedEvent;
import me.whereareiam.yuiverification.VerificationService;
import me.whereareiam.yuiverification.model.config.VerificationSettings;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class VerificationTriggerListener {
	private final VerificationService verificationService;
	private final VerificationSettings settings;

	@EventListener
	public void onFluctlightCreated(FluctlightCreatedEvent event) {
		if (settings.getTriggers().isOnJoin())
			verificationService.verify(event.getFluctlight());
	}

	@EventListener
	public void onFluctlightCleared(FluctlightClearedEvent event) {
		if (settings.getTriggers().isOnClear())
			verificationService.verify(event.getNewFluctlight());
	}
}
