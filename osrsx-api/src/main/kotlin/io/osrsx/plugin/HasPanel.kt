package io.osrsx.plugin

/**
 * A plugin capability: draw a read-only data box each frame. A [Plugin] that implements this has the
 * engine register/remove a draggable RuneLite data-box overlay for it on enable/disable — no manual
 * `ctx.overlays()` wiring needed. Append nothing to the [PanelBuilder] to hide the box for that frame.
 */
interface HasPanel {
    /** Render this plugin's data box into [panel] (called fresh each frame on the render thread — keep
     *  it read-only and quick). */
    fun onPanel(panel: PanelBuilder)
}
