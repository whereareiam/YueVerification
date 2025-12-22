package me.whereareiam.yuiverification.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.yuiverification.model.VerificationContext;

/**
 * Event published when a user begins the verification process.
 * <p>
 * This event is triggered when:
 * <ul>
 *   <li>A user joins the server and automatic verification starts</li>
 *   <li>Staff manually initiates verification via command</li>
 * </ul>
 */
@Getter
@RequiredArgsConstructor
public class VerificationStartedEvent {
	/**
	 * The verification context containing user and conversation information.
	 */
	private final VerificationContext context;
	
	/**
	 * Whether this verification was manually initiated by staff.
	 */
	private final boolean manual;
	
	/**
	 * The ID of the staff member who initiated verification, or null if automatic.
	 */
	private final Long initiatorId;
}
