package io.osrsx.api

/**
 * Register/unregister overlays — the plugin-facing handle to the game's overlay system. Overlays added
 * here are drawn by the fork's own `OverlayRenderer` onto the game's Graphics2D buffer and composited
 * over the scene by the GPU plugin (the same path RuneLite plugins use). This is the sanctioned way for
 * plugins to draw in-world or on the HUD; ImGui is reserved for the editor UI/panels.
 *
 * The parameter is typed [Any] deliberately: an overlay is a RuneLite `Overlay` instance (first-party
 * and engine plugins subclass it directly), which the api boundary must not name — so no net.runelite
 * type leaks onto the SDK signature. The engine adapts/validates the value; a non-overlay is rejected
 * (returns false).
 */
interface Overlays {
    /** Register [overlay] (a RuneLite `Overlay`); returns true if it was added (false if already present,
     *  unavailable, or not a valid overlay). */
    fun add(overlay: Any): Boolean
    /** Unregister [overlay]; returns true if it was removed. */
    fun remove(overlay: Any): Boolean
}
