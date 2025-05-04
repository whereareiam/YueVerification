package me.whereareiam.yuiverification.api;

import java.util.List;

public interface VerificationStepRegistry {
	/**
	 * Registers a newly created step instance.
	 */
	void register(VerificationStep step);

	/**
	 * Returns an immutable, ordered snapshot of all known steps.
	 */
	List<VerificationStep> getSteps();
}