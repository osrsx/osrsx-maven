package io.osrsx.plugin

/**
 * A plugin capability: draw an interactive, docked panel each UI frame. A [Plugin] that implements this
 * gets its own docked window in the editor chrome; [onPaint] is called every frame with a [ScriptGui].
 * Reserved for panels that need interactive widgets (buttons, inputs) — for plain read-only data prefer
 * [HasPanel] (a draggable RuneLite data box) instead.
 */
interface HasPaint {
    /** Render this plugin's interactive panel for the current frame. */
    fun onPaint(gui: ScriptGui)

    /** Whether the panel should be drawn this frame (dynamic visibility toggle). Default true. */
    fun paints(): Boolean = true
}
