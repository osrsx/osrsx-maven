package io.osrsx.api

/**
 * A player skill — the SDK-owned replacement for `net.runelite.api.Skill` on the plugin boundary, so the
 * SDK carries no `net.runelite.*` dependency (see docs/PLUGIN_SDK_PHASE0.md).
 *
 * Members mirror the game's skills 1:1 by name, so the engine maps to/from its internal `Skill` enum by
 * [name] at the boundary. [OVERALL] is the aggregate (total level / total xp), not a trainable skill.
 */
enum class Skill {
    ATTACK,
    DEFENCE,
    STRENGTH,
    HITPOINTS,
    RANGED,
    PRAYER,
    MAGIC,
    COOKING,
    WOODCUTTING,
    FLETCHING,
    FISHING,
    FIREMAKING,
    CRAFTING,
    SMITHING,
    MINING,
    HERBLORE,
    AGILITY,
    THIEVING,
    SLAYER,
    FARMING,
    RUNECRAFT,
    HUNTER,
    CONSTRUCTION,
    SAILING,
    OVERALL,
}
