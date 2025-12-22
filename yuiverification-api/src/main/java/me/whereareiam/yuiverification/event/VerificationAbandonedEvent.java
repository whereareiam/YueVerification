package me.whereareiam.yuiverification.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.yui.model.fluctlight.Fluctlight;

/**
 * Event published when a user leaves the server during active verification.
 * <p>
 * This event is triggered when a user who was in the middle of the verification
 * process leaves the server, causing the verification to be cancelled.
 */
@Getter
@RequiredArgsConstructor
public class VerificationAbandonedEvent {
	/**
	 * The Fluctlight representing the user who abandoned verification.
	 */
	private final Fluctlight fluctlight;
	
	/**
	 * The name of the step the user was on when they left, or "Unknown" if unavailable.
	 */
	private final String currentStep;
}
