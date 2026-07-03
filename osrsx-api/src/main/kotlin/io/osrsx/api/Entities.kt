package io.osrsx.api

/**
 * The plugin-facing wrapper interfaces for live scene entities. The engine supplies the concrete impls
 * (`io.osrsx.wrappers.Npc` / `Player` / `SceneObject` / `GroundItem`), which read RuneLite state on the
 * client thread; plugins only ever see these interfaces, so the SDK stays free of `net.runelite.*`.
 */

/**
 * The combat style an NPC's overhead PROTECTION prayer defends against — the SDK-owned form of the
 * engine's overhead-icon read, so plugins never touch `net.runelite.api.HeadIcon`. A boss flips this
 * mid-fight; read it to attack the style it ISN'T protecting.
 */
enum class Overhead { MELEE, RANGED, MAGIC }

/** A live NPC. */
interface Npc : SceneEntity {
    val id: Int
    /** The NPC's menu actions (e.g. `["Talk-to", "Travel"]`). */
    fun actions(): List<String>
    /** The combat style this NPC's overhead PROTECTION prayer is defending against (its overhead icon), or
     *  null when it shows no protection icon. SDK-typed ([Overhead]) — no `net.runelite.*` on the boundary. */
    fun overheadStyle(): Overhead?
    /** Current animation id, or -1. */
    val animation: Int
    /** Current spot-anim graphic id, or -1 — a telegraph signal for boss specials. */
    val graphic: Int
    /** Facing angle in JAU (0..2047; 0 = south, 512 = west, 1024 = north, 1536 = east). */
    val orientation: Int
    /** Tile footprint size (1 = 1×1, 3 = 3×3, …); [tile] is the SW corner of that footprint. */
    val size: Int
    /** Overhead spoken text (e.g. a boss "snorts" before a special), or null. */
    val overheadText: String?
    val healthRatio: Int
    val isDead: Boolean
    /** Name of whoever this NPC is currently interacting with, or null. */
    val interactingName: String?
    /** True when this NPC is currently interacting with (targeting) the local player. */
    val isInteractingWithMe: Boolean
    /** Health as a percentage, or -1 when no health bar is shown. */
    val healthPercent: Int
}

/** A live player (other players or the local player). */
interface Player : SceneEntity {
    val combatLevel: Int
    val animation: Int
    /** Name of whoever this player is currently interacting with, or null. */
    val interactingName: String?
    /** True when this player is walking/running (its pose animation differs from its idle pose). */
    val isMoving: Boolean
}

/** A live scene object — game objects (trees, banks) and wall/decorative/ground objects (gates, doors). */
interface SceneObject : SceneEntity {
    val id: Int
    /** The object's live menu actions (e.g. `["Open"]` or `["Open", "Pay-toll(10gp)"]`). */
    fun actions(): List<String>
}

/** A live item on the ground; [id]/[quantity] come from [HasItemRef]. */
interface GroundItem : SceneEntity, HasItemRef

// ---- convenience extensions (pure; no net.runelite) ----

/** Chebyshev tiles from this entity to [other], or [Int.MAX_VALUE] if this entity has no tile. */
fun SceneEntity.distanceTo(other: Tile): Int = tile()?.distanceTo(other) ?: Int.MAX_VALUE

/** Chebyshev tiles between two entities, or [Int.MAX_VALUE] if either has no tile. */
fun SceneEntity.distanceTo(other: SceneEntity): Int =
    other.tile()?.let { distanceTo(it) } ?: Int.MAX_VALUE

/** Chebyshev tiles from this entity to the local player — a readable alias for [SceneEntity.distance]. */
val SceneEntity.distanceToPlayer: Int get() = distance()

/**
 * Interact with this entity, or run [fallback] if it's gone (null receiver) — kills the
 * `val n = npcs.closest(...); if (n != null) n.interact(a) else …` boilerplate:
 * `npcs.closest("Goblin").interactOr("Attack") { walkToSpot() }`. Returns the interaction result
 * (always false when the entity was null).
 */
fun SceneEntity?.interactOr(action: String, fallback: () -> Unit): Boolean {
    if (this == null) {
        fallback()
        return false
    }
    return interact(action)
}
