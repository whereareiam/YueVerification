package me.whereareiam.yuiverification.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.yuiverification.model.VerificationContext;

/**
 * Event published when a user completes an individual verification step.
 * <p>
 * This event is triggered after each step (e.g., Welcome, AdditionalLanguage)
 * is successfully completed by the user.
 */
@Getter
@RequiredArgsConstructor
public class VerificationStepCompletedEvent {
	/**
	 * The verification context containing user and conversation information.
	 */
	private final VerificationContext context;
	
	/**
	 * The name of the completed step (typically the class simple name).
	 */
	private final String stepName;
}
