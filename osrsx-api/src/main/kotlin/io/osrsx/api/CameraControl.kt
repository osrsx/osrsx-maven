package io.osrsx.api

/** The segmentable abilities of the background camera humanizer. A plugin can [CameraControl.block] any
 *  subset at any time, so it keeps the parts it wants (e.g. gentle zoom) and silences the rest (e.g. no
 *  rotation/glances while it drives a fight). */
enum class CameraAction {
    /** Yaw turns — facing the travel heading, facing an NPC, idle re-aims. */
    ROTATE,
    /** Pitch tilts — easing toward the adaptive/overhead angle. */
    PITCH,
    /** Mouse-wheel zoom management (toward the preferred walking width). */
    ZOOM,
    /** Idle look-arounds while standing still (the "keep the scene alive" glances). */
    GLANCE,
    /** The occlusion recovery nudge when the avatar is hidden behind scenery. */
    REVEAL,
}

/**
 * Per-plugin control over what the background [io.osrsx.methods.CameraHumanizer] is allowed to do, so
 * concurrent plugins can **partition** the camera: allow some actions, block others, at different times.
 *
 * The humanizer only ever animates the camera when the player is otherwise idle (or web-walking) — but that
 * can still collide with a plugin's own timing (grabbing the mouse mid-eat during a boss fight). This lets a
 * plugin declare, for as long as it needs, "don't rotate/zoom/glance right now" — or the blunt [hold] ("don't
 * touch the camera at all").
 *
 * Leases are **keyed by the calling plugin's loop thread** (no argument to pass, like [Coordination]) and
 * **auto-expire** after a TTL, refreshed on each call — so a crashed or stopped plugin never leaves the
 * camera frozen; its restrictions lapse on their own. Restrictions from multiple plugins **compose** (the
 * union of everything blocked), so the humanizer does an action only if EVERY plugin currently permits it.
 * To force a specific framing, block the relevant actions here and drive [Camera] directly — the humanizer
 * won't fight your explicit rotate/zoom while it's blocked.
 */
interface CameraControl {
    /** Block [actions] (refresh the lease for [ttlMs]). Call each loop while the restriction should hold. */
    fun block(actions: Set<CameraAction>, ttlMs: Long = DEFAULT_TTL_MS)

    /** Restrict the humanizer to ONLY [actions] — i.e. block the complement. */
    fun allowOnly(actions: Set<CameraAction>, ttlMs: Long = DEFAULT_TTL_MS)

    /** Block EVERYTHING — the humanizer won't touch the camera at all. The common case for a boss fight:
     *  call it each loop while you own the fight so it can't grab the mouse mid-eat/attack. */
    fun hold(ttlMs: Long = DEFAULT_TTL_MS)

    /** Drop THIS plugin's camera restrictions immediately (don't wait for the TTL). Call on stop. */
    fun release()

    /** True if [action] is currently permitted after ALL plugins' active restrictions. */
    fun allows(action: CameraAction): Boolean

    /** True if the camera is currently fully held (every action blocked by someone). */
    fun isHeld(): Boolean

    companion object {
        /** Default lease lifetime. Comfortably longer than a plugin loop so a per-loop refresh keeps it live,
         *  short enough that a dead plugin's hold lapses quickly. */
        const val DEFAULT_TTL_MS = 5_000L
    }
}
