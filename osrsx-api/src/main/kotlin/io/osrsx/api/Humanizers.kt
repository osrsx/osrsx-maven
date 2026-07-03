package io.osrsx.api

/**
 * Background-humanizer partitioning a plugin may need while it drives the cursor itself — e.g. a boss
 * plugin whose hover-wander owns the mouse for the whole fight silences the generic idle-mouse drift so
 * it can't pull the cursor off the dodge tile. An engine-adjacent, first-party surface (like
 * [CameraControl]).
 */
interface Humanizers {
    /** Enable/disable the idle-mouse humanizer. Returns the PREVIOUS state so a plugin can restore it on stop. */
    fun setIdleMouse(enabled: Boolean): Boolean

    /** Whether the idle-mouse humanizer is currently enabled. */
    fun idleMouseEnabled(): Boolean
}
