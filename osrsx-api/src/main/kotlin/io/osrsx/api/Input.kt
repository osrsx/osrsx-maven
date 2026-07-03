package io.osrsx.api

/**
 * Plugin-facing control over the hard game-input lock. While locked, ALL physical user mouse/keyboard
 * input to the game is dropped for as long as the plugin holds it — so paced automation (e.g. dropping
 * an inventory one tick at a time) can't be disrupted by the user moving the cursor mid-action.
 *
 * The hold is owned by the calling plugin and automatically released when the plugin stops, so a forgotten
 * [unlock] (or a crash) can never leave the user permanently locked out. Typical use: gate it behind a
 * config option and call [lock] in `onStart` / [unlock] in `onStop`.
 */
interface Input {
    /** Engage the hard input lock for this plugin (idempotent). */
    fun lock()

    /** Release this plugin's hold on the input lock (idempotent). */
    fun unlock()

    /** Whether the hard input lock is currently engaged by any plugin. */
    fun isLocked(): Boolean
}
