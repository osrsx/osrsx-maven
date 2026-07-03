package io.osrsx.api

/**
 * The outcome of an interaction, with a failure reason — a richer alternative to the bare `Boolean` that
 * [Interactable.interact] returns. Chain a follow-up wait/branch instead of hand-rolling a poll:
 *
 * ```
 * npc.attempt("Attack")
 *     .andWaitFor { me.inCombat }
 *     .orElse { reason -> log.w(reason) }
 * ```
 *
 * Pure (no engine): [andWaitFor] polls on the calling plugin thread. The plain-`Boolean` path is kept too
 * — `npc.interact("Attack").andWaitFor { … }` works via the [Boolean.andWaitFor] extension — so nothing
 * that relies on the existing `Boolean` return breaks.
 */
sealed interface InteractionResult {
    /** True for [Success]. */
    val succeeded: Boolean

    /** The interaction (and any chained wait) succeeded. */
    object Success : InteractionResult {
        override val succeeded: Boolean get() = true
    }

    /** The interaction (or a chained wait) failed; [reason] says why (for logging / branching). */
    data class Failure(val reason: String) : InteractionResult {
        override val succeeded: Boolean get() = false
    }

    /**
     * If this is [Success], poll [condition] until it's true (→ stays [Success]) or [timeoutMs] elapses
     * (→ [Failure]). If already [Failure], short-circuits (condition is not polled). Blocks the calling
     * plugin thread between polls; a thread interrupt (plugin stop) ends the wait as a [Failure].
     */
    fun andWaitFor(timeoutMs: Long = 5_000, pollMs: Long = 50, condition: () -> Boolean): InteractionResult {
        if (this !is Success) return this
        return if (pollUntil(timeoutMs, pollMs, condition)) Success
        else Failure("timed out after ${timeoutMs}ms waiting for the follow-up condition")
    }

    /** Run [handler] with the reason when this is a [Failure] (e.g. to log it); returns this for chaining. */
    fun orElse(handler: (String) -> Unit): InteractionResult {
        if (this is Failure) handler(reason)
        return this
    }
}

/** Wrap this boolean interaction result into an [InteractionResult] (with a generic failure reason). */
fun Boolean.asInteraction(action: String? = null): InteractionResult =
    if (this) InteractionResult.Success
    else InteractionResult.Failure("interaction${action?.let { " '$it'" } ?: ""} did not start")

/**
 * Attempt an interaction and get a chainable [InteractionResult] (with a failure reason) instead of a bare
 * boolean: `npc.attempt("Attack").andWaitFor { me.inCombat }`.
 */
fun Interactable.attempt(action: String? = null): InteractionResult = interact(action).asInteraction(action)

/**
 * Chain a poll onto a boolean interaction: returns true only if the interaction succeeded AND [condition]
 * becomes true within [timeoutMs]. Replaces the interact-then-`Wait.until` boilerplate directly on the
 * existing `Boolean` API: `npc.interact("Attack").andWaitFor { me.inCombat }`. A short-circuit on false
 * means [condition] is never polled when the interaction didn't start.
 */
fun Boolean.andWaitFor(timeoutMs: Long = 5_000, pollMs: Long = 50, condition: () -> Boolean): Boolean =
    this && pollUntil(timeoutMs, pollMs, condition)

/** Poll [condition] every [pollMs] until true or [timeoutMs] elapses; false on timeout or interrupt. */
private fun pollUntil(timeoutMs: Long, pollMs: Long, condition: () -> Boolean): Boolean {
    val deadline = System.currentTimeMillis() + timeoutMs
    while (System.currentTimeMillis() < deadline) {
        if (condition()) return true
        try {
            Thread.sleep(pollMs)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            return false
        }
    }
    return condition() // one final check at the deadline
}
