package io.osrsx.api

/**
 * A prayer — the SDK-owned replacement for `net.runelite.api.Prayer` on the plugin boundary, so the SDK
 * carries no `net.runelite.*` dependency (see docs/PLUGIN_SDK_PHASE0.md).
 *
 * Members mirror the game's prayers 1:1 by name (standard prayerbook first, then the Ruinous Powers,
 * prefixed `RP_`), so the engine maps to/from its internal `Prayer` enum by [name] at the boundary.
 */
enum class Prayer {
    // ---- standard prayerbook ----
    THICK_SKIN,
    BURST_OF_STRENGTH,
    CLARITY_OF_THOUGHT,
    SHARP_EYE,
    MYSTIC_WILL,
    ROCK_SKIN,
    SUPERHUMAN_STRENGTH,
    IMPROVED_REFLEXES,
    RAPID_RESTORE,
    RAPID_HEAL,
    PROTECT_ITEM,
    HAWK_EYE,
    MYSTIC_LORE,
    STEEL_SKIN,
    ULTIMATE_STRENGTH,
    INCREDIBLE_REFLEXES,
    PROTECT_FROM_MAGIC,
    PROTECT_FROM_MISSILES,
    PROTECT_FROM_MELEE,
    EAGLE_EYE,
    MYSTIC_MIGHT,
    RETRIBUTION,
    REDEMPTION,
    SMITE,
    CHIVALRY,
    DEADEYE,
    MYSTIC_VIGOUR,
    PIETY,
    PRESERVE,
    RIGOUR,
    AUGURY,

    // ---- Ruinous Powers (ancient prayerbook) ----
    RP_REJUVENATION,
    RP_ANCIENT_STRENGTH,
    RP_ANCIENT_SIGHT,
    RP_ANCIENT_WILL,
    RP_PROTECT_ITEM,
    RP_RUINOUS_GRACE,
    RP_DAMPEN_MAGIC,
    RP_DAMPEN_RANGED,
    RP_DAMPEN_MELEE,
    RP_TRINITAS,
    RP_BERSERKER,
    RP_PURGE,
    RP_METABOLISE,
    RP_REBUKE,
    RP_VINDICATION,
    RP_DECIMATE,
    RP_ANNIHILATE,
    RP_VAPORISE,
    RP_FUMUS_VOW,
    RP_UMBRA_VOW,
    RP_CRUORS_VOW,
    RP_GLACIES_VOW,
    RP_WRATH,
    RP_INTENSIFY,
}
