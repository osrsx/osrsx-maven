package io.osrsx.api

/** Camera control (rotation, zoom, drag-rotate). */
interface Camera {
    fun yaw(): Int
    fun pitch(): Int
    /** The INPUT-driven yaw (getCameraYawTarget), i.e. what our drags asked for — NOT the rendered [yaw],
     *  which the engine eases toward this and can override (collision, cutscenes). Control loops steer by
     *  this so they measure their own effect instead of chasing the engine (which caused the camera spaz). */
    fun yawTarget(): Int
    /** The INPUT-driven pitch (getCameraPitchTarget). See [yawTarget]: near walls/bank booths the engine
     *  forces the rendered [pitch] near-vertical while this stays where our input left it — steer by this. */
    fun pitchTarget(): Int
    /** True when the engine is currently overriding the camera — the rendered pitch has been pushed well
     *  away from [pitchTarget] (camera collision with scenery, or a cutscene). While true, drag-to-target
     *  corrections can't win, so background camera work stands its pitch down instead of fighting it. */
    fun engineOverriding(): Boolean
    fun scale(): Int
    fun rotateToYaw(yaw: Int)
    fun rotateToPitch(pitch: Int)
    fun rotateTo(yaw: Int, pitch: Int)
    fun yawForDirection(dx: Int, dy: Int): Int
    fun rotateToEntity(entity: SceneEntity): Boolean

    /** Project [tile]'s ground point to a canvas pixel, or null if it isn't on the loaded scene / screen. */
    fun toScreen(tile: Tile): java.awt.Point?

    /** The canvas polygon outlining [tile], or null if it isn't on the loaded scene / screen. */
    fun tilePoly(tile: Tile): java.awt.Polygon?
    /** Zoom by [notches] wheel steps. [acquireMs] > 0 lets a background driver wait that long for the mouse
     *  lock (a bounded fair-share acquire) so it isn't perpetually starved by the walker; 0 = non-blocking.
     *  Returns true iff the wheel gesture actually RAN (won the mouse lock); false if it was skipped because
     *  the lock was held — so a caller can tell "wheel rolled but hit its limit" apart from "never rolled". */
    fun zoom(notches: Int, acquireMs: Long = 0): Boolean
    /** Rotate by a real middle-mouse drag of ([dx],[dy]) screen px. [abort], if given, is re-checked inside
     *  the mouse lock right before the button-press AND on every step of the held drag — returning true
     *  cancels the drag cleanly (release without rotating). Background drivers pass it so a click/hold that
     *  landed in the lock-acquisition window aborts the drag instead of stealing the mouse mid-interaction.
     *  [acquireMs] > 0 waits that long for the mouse lock (bounded fair-share) so the background camera isn't
     *  perpetually out-raced by the walker; 0 = non-blocking (skip if the lock is held). */
    fun dragRotate(dx: Int, dy: Int, abort: (() -> Boolean)? = null, acquireMs: Long = 0)
}
