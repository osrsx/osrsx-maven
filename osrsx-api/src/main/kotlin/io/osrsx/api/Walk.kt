package io.osrsx.api


/**
 * Observability + configuration types for the [WebWalker]. A walk emits a stream of [WalkEvent]s to
 * registered [WalkListener]s and keeps a pollable [WalkState] snapshot; [WalkConfig] shapes how the
 * route is planned and executed.
 */

/** Coarse phase of an in-progress web walk (pollable via [WebWalker.state]). */
enum class WalkPhase { IDLE, PLANNING, WALKING, TAKING_TRANSPORT, BANKING, STUCK, ARRIVED, FAILED }

/**
 * The family a transport edge belongs to — used for config gating and event reporting. Covers every
 * transport category present in the osrsx dataset (the source TSVs: agility shortcuts, boats/canoes,
 * charter ships, fairy rings, gnome gliders, hot-air balloons, magic carpets, mushtrees, minecarts,
 * quetzals, spirit trees, teleport items/spells, wilderness obelisks, …). [TRAVERSAL] (doors, gates,
 * stairs, ladders, tunnels) and [AGILITY_SHORTCUT] are "on-foot" — they are kept out of [FAST_TRAVEL]
 * so a walk-only configuration still opens doors and uses shortcuts. [OTHER] is any hub-travel edge we
 * couldn't classify yet.
 */
enum class TransportKind {
    TRAVERSAL,
    AGILITY_SHORTCUT,
    BOAT,
    CHARTER_SHIP,
    TELEPORT_ITEM,
    TELEPORT_SPELL,
    FAIRY_RING,
    SPIRIT_TREE,
    GNOME_GLIDER,
    HOT_AIR_BALLOON,
    MAGIC_CARPET,
    MUSHTREE,
    MINECART,
    QUETZAL,
    WILDERNESS_OBELISK,
    MINIGAME_TELEPORT,
    OTHER;

    companion object {
        /** Every non-on-foot family — everything except [TRAVERSAL] and [AGILITY_SHORTCUT]. */
        val FAST_TRAVEL: Set<TransportKind> get() = entries.toSet() - TRAVERSAL - AGILITY_SHORTCUT
        /** Teleport families (item tabs/jewellery + spells + minigame-group teleports). */
        val TELEPORTS: Set<TransportKind> get() = setOf(TELEPORT_ITEM, TELEPORT_SPELL, MINIGAME_TELEPORT)
    }
}

/** A transport/teleport on a planned or taken route, in API-level terms (no engine internals leaked). */
data class WalkTransport(
    val name: String,
    val from: Tile?,
    val to: Tile,
    val kind: TransportKind,
)

/**
 * Something that happened during a walk. Delivered to every registered [WalkListener] and reflected in
 * [WebWalker.state]. All the walker's former diagnostics (selected path, took transport, unreachable,
 * stuck, bank detour, …) surface here.
 */
sealed interface WalkEvent {
    /** The destination this walk is heading to. */
    val dest: Tile

    /** A walk to [dest] began from [from]. */
    data class Started(override val dest: Tile, val from: Tile) : WalkEvent
    /** A route was planned: [walkLegs] walking legs and the ordered [transports] it will take. */
    data class PathPlanned(override val dest: Tile, val walkLegs: Int, val transports: List<WalkTransport>) : WalkEvent
    /** Now heading to the next local waypoint [target]. */
    data class Stepping(override val dest: Tile, val target: Tile) : WalkEvent
    /** A transport/teleport was taken. */
    data class TransportTaken(override val dest: Tile, val transport: WalkTransport) : WalkEvent
    /** Movement stalled at [at] ([reason] describes why — e.g. a blocked path or unreachable waypoint). */
    data class Stuck(override val dest: Tile, val at: Tile, val reason: String) : WalkEvent
    /** No route to [dest] could be found ([diagnosis] from the pathfinder). */
    data class Unreachable(override val dest: Tile, val diagnosis: String) : WalkEvent
    /** Detouring to [bank] to obtain [needs] (items/coins a transport on the route requires). */
    data class BankDetour(override val dest: Tile, val bank: Tile, val needs: List<String>) : WalkEvent
    /** A generic diagnostic message (kept for parity with the debug logging). */
    data class Info(override val dest: Tile, val message: String) : WalkEvent
    /** Arrived at [dest]. */
    data class Arrived(override val dest: Tile) : WalkEvent
    /** The walk gave up ([reason] explains — e.g. a non-bankable requirement, or bank runs disabled). */
    data class Failed(override val dest: Tile, val reason: String) : WalkEvent
}

/** Receives [WalkEvent]s. Functional so callers can pass a lambda. */
fun interface WalkListener {
    fun onEvent(event: WalkEvent)
}

/** An immutable snapshot of the walker's state — safe to read from any thread, any time. */
data class WalkState(
    val phase: WalkPhase,
    val destination: Tile?,
    /** The local waypoint currently being walked to, if any. */
    val target: Tile?,
    /** Transports the active plan will take (in order). */
    val plannedTransports: List<WalkTransport>,
    /** Number of walking legs in the active plan (a route can have legs but no transports). */
    val plannedLegs: Int,
    /** The most recently taken transport, if any. */
    val lastTransport: WalkTransport?,
    /** The most recent event. */
    val lastEvent: WalkEvent?,
    /** A short human-readable status line. */
    val message: String,
) {
    companion object {
        val IDLE = WalkState(WalkPhase.IDLE, null, null, emptyList(), 0, null, null, "idle")
    }
}

/** When the walker should turn run on. */
sealed interface RunPolicy {
    /** The default humanised model: drain to ~0, re-enable at a varied energy, occasional early stop. */
    object Humanized : RunPolicy
    /** Run whenever any energy is available. */
    object Always : RunPolicy
    /** Never run. */
    object Never : RunPolicy
    /** Enable run once energy is at least [percent]. */
    data class AboveEnergy(val percent: Int) : RunPolicy
}

/**
 * Highly customizable configuration for a web walk. Defaults reproduce today's behaviour: every
 * transport allowed, humanised run, bank detours on, GE buying off, no stamina potions.
 *
 * Transport gating is layered. A transport is allowed when:
 *  - its name is not in [blacklistedTransports],
 *  - its [TransportKind] is not in [disabledKinds] (TRAVERSAL is always allowed so basic walking never
 *    breaks — putting it in [disabledKinds] has no effect),
 *  - and — if [allowedTransports] is non-empty — its name is whitelisted (the whitelist applies only to
 *    non-TRAVERSAL transports).
 *
 * Family-level control is via [disabledKinds], e.g. `disabledKinds = setOf(TransportKind.GNOME_GLIDER)`
 * to ban gliders, or [WalkConfig.WALK_ONLY] to ban all fast travel. Helper sets like
 * [TransportKind.FAST_TRAVEL] and [TransportKind.TELEPORTS] make common cases terse.
 */
data class WalkConfig(
    /** Whole transport families to exclude (see [TransportKind]). TRAVERSAL is always kept. */
    val disabledKinds: Set<TransportKind> = emptySet(),
    /** Specific transport names never used. */
    val blacklistedTransports: Set<String> = emptySet(),
    /** If non-empty, the ONLY non-traversal transports allowed (by name). */
    val allowedTransports: Set<String> = emptySet(),
    /** Detour to a bank to withdraw items/coins a transport on the route needs. */
    val allowBankRuns: Boolean = true,
    /** Buy missing items at the Grand Exchange (reserved — not yet implemented; emits an info event). */
    val allowGrandExchange: Boolean = false,
    /** Drink stamina/energy potions when run energy is low. */
    val useStaminaPotions: Boolean = false,
    /** Run-energy percent at/below which a stamina/energy potion is drunk (when [useStaminaPotions]). */
    val staminaThreshold: Int = 20,
    /** When to enable run. */
    val runPolicy: RunPolicy = RunPolicy.Humanized,
    /** Per-search pathfinding budget (ms). */
    val pathfindTimeoutMs: Long = 5000,
    /** If the exact destination tile is unreachable (walled off), accept arriving at the nearest reachable
     *  tile within this many tiles instead of failing. 0 = require the exact tile (today's behaviour). */
    val arriveRadius: Int = 0,
) {
    /** True when [kind] is permitted (TRAVERSAL is always permitted). */
    fun allows(kind: TransportKind): Boolean = kind == TransportKind.TRAVERSAL || kind !in disabledKinds

    companion object {
        val DEFAULT = WalkConfig()
        /** Walk on foot only — no fast travel (doors/gates/stairs + agility shortcuts still used). */
        val WALK_ONLY = WalkConfig(disabledKinds = TransportKind.FAST_TRAVEL)
        /** No teleport items or spells (boats, fairy rings, etc. still allowed). */
        val NO_TELEPORTS = WalkConfig(disabledKinds = TransportKind.TELEPORTS)
    }
}
