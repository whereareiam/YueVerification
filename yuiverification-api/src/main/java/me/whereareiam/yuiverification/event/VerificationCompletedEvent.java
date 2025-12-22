package me.whereareiam.yuiverification.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.yuiverification.model.VerificationContext;

import java.time.Instant;

/**
 * Event published when a user successfully completes all verification steps.
 * <p>
 * This event is triggered after the final step completes and the user
 * has been granted the verified role.
 */
@Getter
@RequiredArgsConstructor
public class VerificationCompletedEvent {
	/**
	 * The verification context containing user and conversation information.
	 */
	private final VerificationContext context;
	
	/**
	 * The timestamp when verification was started, used to calculate duration.
	 */
	private final Instant startTime;
}
