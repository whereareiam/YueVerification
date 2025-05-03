package me.whereareiam.yueverification.api;

import me.whereareiam.yueverification.api.model.VerificationContext;

import java.util.concurrent.CompletableFuture;

/**
 * A single step in the verification pipeline.
 * <p>
 * Implementations must be Spring components so that they are discovered and injected
 * automatically. Steps are executed in the order provided by Spring
 * (use {@code @Order} if you need a strict order).
 */

public interface VerificationStep {
	/**
	 * Executes the step.
	 *
	 * @param context current verification context (never {@code null})
	 * @return a future that completes when the step has finished
	 */
	CompletableFuture<Void> execute(VerificationContext context);
}
