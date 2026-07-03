package io.osrsx.api

/**
 * Minigame-group teleports. The standard-spellbook "Minigame Teleport" spell opens the Grouping teleport
 * list; picking a row teleports you to that minigame's lobby. Shared ~20-minute cooldown across all of them.
 *
 * Opening the list and teleporting are tick-based, so (DreamBot semantics, like [Magic.cast]) the first call
 * may only open the list and return false — call again from `onLoop` until it returns true.
 */
interface Minigames {
    /** True when the Grouping minigame-teleport list is open. */
    fun isOpen(): Boolean

    /** Cast the "Minigame Teleport" spell to open the list (does not teleport on its own). */
    fun open(): Boolean

    /** Teleport to [minigame] (e.g. "Castle Wars"): opens the list if needed, then clicks its row. */
    fun teleport(minigame: String): Boolean

    /** True when [minigame] is present (and selectable) in the list right now. */
    fun canTeleport(minigame: String): Boolean

    /** Display names of the minigames currently in the list. */
    fun available(): List<String>
}
