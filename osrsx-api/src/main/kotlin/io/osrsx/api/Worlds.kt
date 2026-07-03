package io.osrsx.api

/**
 * A world from the live OSRS world list — enough to filter before a hop. [normal] flags a plain world safe
 * to bot on (not PvP / high-risk / total-level / tournament / etc.).
 */
data class WorldInfo(
    val id: Int,
    val players: Int,
    /** RuneLite's geographic region code (US/UK/Germany/Australia). */
    val location: Int,
    val members: Boolean,
    val pvp: Boolean,
    val highRisk: Boolean,
    /** Raw world-type names (e.g. "MEMBERS", "SKILL_TOTAL") for finer filtering. */
    val types: Set<String>,
) {
    /** A plain world to bot on — none of the special/restricted types. */
    val normal: Boolean get() = types.none { it in SPECIAL }

    companion object {
        val SPECIAL = setOf(
            "PVP", "HIGH_RISK", "BOUNTY", "PVP_ARENA", "SKILL_TOTAL", "LAST_MAN_STANDING", "TOURNAMENT",
            "DEADMAN", "SEASONAL", "FRESH_START_WORLD", "QUEST_SPEEDRUNNING", "NOSAVE_MODE", "BETA_WORLD",
        )
    }
}

/**
 * World hopping — switch the live game to another world (e.g. when the resource you want isn't present on the
 * current world right now). Hops use the in-game world switcher (open switcher → `hopToWorld`), so they work
 * while logged in; on the login screen they change the world directly. A hop blocks until it completes or
 * times out.
 */
interface Worlds {
    /** The current world id. */
    fun current(): Int

    /** The OSRS world list (empty until it has loaded). */
    fun list(): List<WorldInfo>

    /** Hop to [worldId]. Blocks until the hop lands (or times out). False if the world is unknown or it failed. */
    fun hop(worldId: Int): Boolean

    /** Hop to a random NORMAL world of the SAME membership as the current one, not the current world — the
     *  common bot hop. False if none qualify or the hop failed. */
    fun hopRandom(): Boolean

    /** As [hopRandom], but only worlds with at most [maxPlayers] population. */
    fun hopRandom(maxPlayers: Int): Boolean
}
