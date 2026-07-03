package io.osrsx.api

/**
 * Account-wide break coordination, published once to the [ServiceRegistry]. Every plugin consults the
 * SAME manager before acting ([mayAct]), so breaks apply to the whole account instead of each plugin
 * scheduling its own — the coordination the antiban work in task 8 builds on. A plugin can publish its
 * own [BreakManager] to override the default policy.
 */
interface BreakManager {
    /** True while a break is active — plugins should idle (do nothing) until it ends. */
    fun onBreak(): Boolean

    /** Convenience for `!onBreak()` — the guard a plugin puts at the top of its loop. */
    fun mayAct(): Boolean = !onBreak()

    /** Milliseconds until the next scheduled break starts, or 0 while already on break. */
    fun timeUntilBreakMs(): Long

    /** Force a break now for [durationMs]. */
    fun startBreak(durationMs: Long)

    /** End any active break immediately and reschedule the next one. */
    fun endBreak()
}
