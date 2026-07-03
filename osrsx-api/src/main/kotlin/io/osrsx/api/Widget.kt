package io.osrsx.api

import java.awt.Rectangle

/**
 * A thin, read-only handle to one on-screen interface component — the plugin-facing view of a game
 * widget. The engine adapts the underlying RuneLite widget behind this interface, so no net.runelite
 * type leaks across the api boundary; plugins program against this surface alone.
 *
 * Reads are LIVE (they reflect the widget's current state each access), so query them on demand rather
 * than caching. A read is only meaningful while the widget is still mounted; a stale handle returns
 * empty/default values instead of throwing.
 */
interface Widget {
    /** Packed component id (`group << 16 | child`), or -1 if unavailable. */
    val id: Int
    /** The component group (the high 16 bits of [id]). */
    val group: Int get() = if (id < 0) -1 else id ushr 16
    /** The child index within the group (the low 16 bits of [id]). */
    val child: Int get() = if (id < 0) -1 else id and 0xFFFF
    /** True when the widget is present but hidden (not drawn / not interactable). */
    val hidden: Boolean
    /** Canvas bounds of the widget, or null if it has no drawn area. */
    val bounds: Rectangle?
    /** Display text (colour tags included), or null. */
    val text: String?
    /** Display name (the right-click target text, colour tags included), or null. */
    val name: String?
    /** Menu actions offered by the widget (nulls/blanks removed). */
    val actions: List<String>
    /** Item id shown in the widget, or -1 if none. */
    val itemId: Int
    /** Item stack quantity shown in the widget, or 0 if none. */
    val itemQuantity: Int
    /** Sprite id drawn in the widget, or -1 if none. */
    val spriteId: Int
    /** Direct child components (dynamic + static), or empty. */
    val children: List<Widget>
}
