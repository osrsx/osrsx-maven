package io.osrsx.plugin

/**
 * The drawing surface a plugin's [HasPanel.onPanel] receives to render a read-only data box. A thin
 * builder: append a centred [title], key/value [line]s, or plain [text]; the engine draws the recorded
 * box as a draggable/snappable overlay (the same stats box every RuneLite plugin uses). Only records —
 * it does no drawing itself.
 *
 * Colours are 0xAARRGGBB longs (so a plugin can pass `0xFF55FF55` without colour juggling and without
 * overflowing a 32-bit int). All builders return `this` so calls can be chained.
 */
interface PanelBuilder {
    /** A centred title line (white). */
    fun title(text: String): PanelBuilder
    /** A centred title line in [argb] (0xAARRGGBB). */
    fun title(text: String, argb: Long): PanelBuilder
    /** A key/value row: [left] on the left, [right] right-aligned (both white). */
    fun line(left: String, right: String): PanelBuilder
    /** A key/value row with explicit [leftArgb]/[rightArgb] colours (0xAARRGGBB). */
    fun line(left: String, right: String, leftArgb: Long, rightArgb: Long): PanelBuilder
    /** A single left-aligned line of text (white). */
    fun text(s: String): PanelBuilder
    /** A single left-aligned line of text in [argb] (0xAARRGGBB). */
    fun text(s: String, argb: Long): PanelBuilder
    /** A blank spacer line. */
    fun blank(): PanelBuilder
    /** Alias for [blank]. */
    fun separator(): PanelBuilder
    /** Set the panel's fixed width in pixels. Widen it for long values to avoid wrapping. */
    fun width(px: Int): PanelBuilder
    /** Override the panel background colour (0xAARRGGBB). */
    fun background(argb: Long): PanelBuilder

    companion object {
        // Handy ARGB presets for status text.
        const val WHITE = 0xFFFFFFFFL
        const val DIM = 0xFFB0B0B8L
        const val GOOD = 0xFF4CD964L
        const val BAD = 0xFFFF5B5BL
        const val ACCENT = 0xFF5B8CFFL
    }
}
