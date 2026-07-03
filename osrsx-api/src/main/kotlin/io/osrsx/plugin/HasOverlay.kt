package io.osrsx.plugin

/**
 * A plugin capability: draw a **floating in-game overlay** each UI frame. Unlike a RuneLite overlay (a
 * read-only data box) this is a lightweight, ImGui-backed window rendered over the game — translucent,
 * with a RuneLite-style border — that is **interactive** (buttons, inputs, pickers all work) and
 * **repositionable by alt+dragging** it anywhere, the way RuneLite lets you move overlays.
 *
 * The engine owns the window chrome (background, border, alt+drag, input capture); the plugin just draws
 * its content through the same [ScriptGui] a docked panel uses. Contrast with [HasPaint] (a docked editor
 * panel) and `HasPanel` (a static RuneLite data box) — pick the one that fits.
 *
 * ```
 * class Miner : Plugin(), HasOverlay {
 *     override fun overlayTitle() = "Miner"
 *     override fun onOverlay(gui: ScriptGui) {
 *         gui.text("Ore mined: $mined")
 *         if (gui.button("Reset")) mined = 0
 *     }
 * }
 * ```
 */
interface HasOverlay {
    /** Render this plugin's overlay content for the current frame, into the engine-managed overlay window. */
    fun onOverlay(gui: ScriptGui)

    /** Whether to draw the overlay this frame (dynamic visibility). Default true. */
    fun overlayVisible(): Boolean = true

    /** Optional accent-coloured header shown at the top of the overlay; null = no header. */
    fun overlayTitle(): String? = null
}
