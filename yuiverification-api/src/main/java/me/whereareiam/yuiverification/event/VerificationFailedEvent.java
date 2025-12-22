package me.whereareiam.yuiverification.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.yui.model.fluctlight.Fluctlight;

/**
 * Event published when the verification pipeline fails with an exception.
 * <p>
 * This event is triggered when an unexpected error occurs during verification,
 * such as conversation creation failure or step execution errors.
 */
@Getter
@RequiredArgsConstructor
public class VerificationFailedEvent {
	/**
	 * The Fluctlight representing the user whose verification failed.
	 */
	private final Fluctlight fluctlight;
	
	/**
	 * The error message describing what went wrong.
	 */
	private final String errorMessage;
}
