package me.whereareiam.yuiverification.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.yui.model.fluctlight.Fluctlight;

import java.time.Duration;

/**
 * Event published when a user fails to complete verification within the configured time limit.
 * <p>
 * This event is triggered when the verification timeout expires and the user
 * is about to be kicked from the server.
 */
@Getter
@RequiredArgsConstructor
public class VerificationTimeoutEvent {
	/**
	 * The Fluctlight representing the user who timed out.
	 */
	private final Fluctlight fluctlight;
	
	/**
	 * The configured time limit for verification.
	 */
	private final Duration timeLimit;
	
	/**
	 * The actual time spent before timeout occurred.
	 */
	private final Duration timeSpent;
}
