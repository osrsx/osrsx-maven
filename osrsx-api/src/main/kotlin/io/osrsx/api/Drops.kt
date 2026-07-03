package io.osrsx.api

/** One item a world object/NPC yields, and how often. [rarity] "Always" marks the guaranteed gathered
 *  product (a tree's logs, a rock's ore); other rows are conditional/rare (bird nests, gems, pets). */
data class Drop(
    val item: String,
    val quantity: String,
    val rarity: String,
    /** The gathering skill for a resource drop (Woodcutting/Mining/…), or "" for a non-skilling source. */
    val skill: String,
)

/**
 * What a world OBJECT or NPC yields — a tree → its logs, a rock → its ore, a fishing spot → its fish. Backed
 * by a table harvested from the OSRS Wiki (`osrsx-tools/python/gen_drops.py` → the `drops` table in
 * `osrsx.db`). The generic "what does this thing drop?" seam: a skiller uses it to auto-fill its product
 * config, but any plugin can ask.
 *
 * Lookups are by display name, case-insensitive. Everything returns empty/null when the source is unknown
 * or the data isn't present, so callers degrade gracefully. Covers BOTH monster/NPC loot tables and
 * gathering products (trees/rocks/fishing/hunter/thieving/farming/…).
 */
interface Drops {
    /** Everything [source] yields (empty if unknown). */
    fun of(source: String): List<Drop>

    /** The item name(s) [source] yields (empty if unknown). */
    fun produces(source: String): List<String>

    /** The single MAIN product of [source] — the guaranteed "Always" gathered resource — or null when
     *  unknown (e.g. a generic "Rocks" whose ore is ambiguous). */
    fun primary(source: String): String?
}
