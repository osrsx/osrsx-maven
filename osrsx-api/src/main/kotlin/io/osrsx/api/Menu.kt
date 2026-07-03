package io.osrsx.api

/**
 * Dynamic menu-entry swapping — make a chosen right-click [action] the DEFAULT left-click for matching
 * entries (e.g. set "Drop" as the default for a log so a single left-click drops it, instead of a
 * right-click + "Drop" select).
 *
 * It only reorders the live menu's PRIORITY (on the fork's `PostMenuSort`); the action that actually
 * fires when clicked is the real one the client built — nothing is injected or fabricated. Rules stay
 * active until [remove]d or [reset]; a plugin should clear its rules when it stops.
 */
interface Menu {
    /**
     * Make [action] the default left-click for entries whose target contains [target] (case-insensitive;
     * null matches any target). [action] also matches dynamic ops like "Pay-toll(10gp)" via the prefix.
     * Returns a rule id for [remove].
     */
    fun setDefault(action: String, target: String? = null): Int

    /** Remove a single swap rule by its [id]. */
    fun remove(id: Int)

    /** Clear every swap rule (restores vanilla left-click defaults). */
    fun reset()
}
