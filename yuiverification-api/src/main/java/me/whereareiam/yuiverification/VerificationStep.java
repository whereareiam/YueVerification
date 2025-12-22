package me.whereareiam.yuiverification;

import me.whereareiam.yuiverification.model.VerificationContext;

import java.util.concurrent.CompletableFuture;

/**
 * A single step in the verification pipeline.
 * <p>
 * Implementations must be Spring components so that they are discovered and injected
 * automatically. Steps are executed in the order provided by Spring
 * (use {@code @Order} if you need a strict order).
 * <p>
 * All methods are optional (default implementations provided). Implement only the
 * lifecycle hooks your step needs.
 */
@SuppressWarnings("unused")
public interface VerificationStep {
	/**
	 * Step should begin execution (user interaction, async).
	 * <p>
	 * This is where the step presents UI to the user and waits for their interaction.
	 * The returned future should complete when the user has finished interacting
	 * with this step (e.g., clicked a "Continue" button).
	 * <p>
	 * Default implementation immediately completes, meaning the step is skipped.
	 *
	 * @param context The verification context (never {@code null})
	 * @return A future that completes when the step is finished
	 */
	default CompletableFuture<Void> onStepStarted(VerificationContext context) {
		return CompletableFuture.completedFuture(null);
	}

	/**
	 * User successfully completed this specific step.
	 * <p>
	 * Called when the user finishes interacting with this step and moves to the next.
	 * Use this to clean up step-specific resources like cached contexts, pending messages,
	 * or temporary data associated with this user in this step.
	 * <p>
	 * Default implementation is a no-op.
	 *
	 * @param context The verification context (never {@code null})
	 */
	default void onStepCompleted(VerificationContext context) {
		// no-op
	}

	/**
	 * User successfully completed all verification steps.
	 * <p>
	 * Called after the final step completes, when the user has been granted
	 * the verified role. Use this for logging, analytics, sending congratulations
	 * messages, or any post-verification tasks.
	 * <p>
	 * Default implementation is a no-op.
	 *
	 * @param context The verification context (never {@code null})
	 */
	default void onVerificationCompleted(VerificationContext context) {
		// no-op
	}

	/**
	 * Verification was cancelled/aborted before completion.
	 * <p>
	 * Called when verification ends prematurely due to:
	 * <ul>
	 *   <li>User leaving the server</li>
	 *   <li>Timeout expiring and user being kicked</li>
	 *   <li>Manual cancellation by staff</li>
	 *   <li>Plugin being disabled/unloaded</li>
	 * </ul>
	 * <p>
	 * Use this to clean up resources such as cached contexts, pending messages,
	 * or open conversations.
	 * <p>
	 * Default implementation is a no-op.
	 *
	 * @param context The verification context (never {@code null})
	 */
	default void onVerificationCancelled(VerificationContext context) {
		// no-op
	}
}
