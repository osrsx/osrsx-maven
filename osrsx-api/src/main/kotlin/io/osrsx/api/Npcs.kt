package io.osrsx.api

/** NPCs in the loaded scene. */
interface Npcs {
    fun all(): List<Npc>
    fun all(predicate: (Npc) -> Boolean): List<Npc>
    fun closest(predicate: (Npc) -> Boolean): Npc?
    fun closest(name: String): Npc?
    /** Nearest NPC named [name] that OFFERS [action] — e.g. a "Fishing spot" with a "Net" action, so a
     *  multi-type area (Catherby net + cage/harpoon spots) picks the one matching the chosen method. */
    fun closest(name: String, action: String): Npc?
    fun closest(vararg ids: Int): Npc?
}
