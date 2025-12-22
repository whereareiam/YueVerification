package me.whereareiam.yuiverification;

public final class AuditTypes {
	// INFO severity
	public static final String VERIFICATION_STARTED = "verification_started";
	public static final String VERIFICATION_STARTED_MANUAL = "verification_started_manual";
	public static final String VERIFICATION_COMPLETED = "verification_completed";
	public static final String VERIFICATION_STEP_COMPLETED = "verification_step_completed";

	// WARNING severity
	public static final String VERIFICATION_TIMEOUT = "verification_timeout";
	public static final String VERIFICATION_ABANDONED = "verification_abandoned";

	// ERROR severity
	public static final String VERIFICATION_FAILED = "verification_failed";
}
