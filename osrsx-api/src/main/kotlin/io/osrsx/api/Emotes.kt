package io.osrsx.api

/**
 * Performs in-game emotes from the Emotes sidebar tab. An emote is executed by opening the tab, scrolling
 * its button into view and clicking it. Emotes are matched by SPRITE id (the robust key — a locked emote
 * shows a different sprite, so a sprite match naturally rejects one you can't perform) or by display name.
 *
 * Tab-switching is tick-based, so (DreamBot semantics) the first call may only open the tab / scroll and
 * return false — call again from `onLoop` until it returns true.
 */
interface Emotes {
    /** True when the Emotes interface (its list container) is loaded and visible. */
    fun isOpen(): Boolean

    /** Perform the emote with display name [name] (e.g. "Dance", "Bow"), case-insensitive. */
    fun perform(name: String): Boolean

    /** Perform the emote whose button uses [spriteId] — mirrors how Quest Helper locates an emote. */
    fun performBySprite(spriteId: Int): Boolean

    /** True when an emote named [name] is present in the tab right now (unlocked/performable). */
    fun canPerform(name: String): Boolean

    /** Display names of the emotes currently in the tab. */
    fun available(): List<String>
}
