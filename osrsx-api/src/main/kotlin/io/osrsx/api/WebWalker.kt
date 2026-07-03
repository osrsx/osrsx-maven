package io.osrsx.api


/**
 * Whole-map web walker: plan + follow a tile-level route using transports/teleports as needed.
 *
 * Drive it by calling [walkTo] each loop until [arrived]. The walk is fully observable: register a
 * [WalkListener] (or pass one to [walkTo]) to receive every [WalkEvent] — started, path planned,
 * transport taken, stuck, unreachable, bank detour, arrived, failed — and/or poll [state] at any time
 * from any thread. Behaviour is shaped by a [WalkConfig] (transport gating, run policy, stamina,
 * pathfinding timeout, bank runs, …) set via [configure] or per call. Fairy rings, charters, teleports
 * and other transports are handled internally by the walker — there is no separate fairy-ring API.
 */
interface WebWalker {
    /** Advance toward [dest] using the active [config]; call repeatedly from the plugin loop until [arrived]. */
    fun walkTo(dest: Tile): Boolean
    /** Advance toward [dest] under [config] (becomes the active config). */
    fun walkTo(dest: Tile, config: WalkConfig): Boolean
    /** True once standing exactly on [dest] (or beside it when the tile itself is unstandable). */
    fun arrived(dest: Tile): Boolean

    /** Set the active walk configuration (clears any cached plan so the new rules take effect). */
    fun configure(config: WalkConfig)
    /** The active walk configuration. */
    fun config(): WalkConfig

    /** Register a listener notified of every [WalkEvent]; returns it so it can be [removeListener]'d. */
    fun addListener(listener: WalkListener): WalkListener
    fun removeListener(listener: WalkListener)

    /** An immutable snapshot of the walker's current state — safe to read from any thread. */
    fun state(): WalkState

    /**
     * The nearest bank you can ACTUALLY reach (the tile to stand on to use it), or null if none is
     * routable. Reachability is judged by global pathfinding to the bank's customer side — a banker's
     * interior tile, or a booth's stand ring — so a wall-mounted booth you can only touch from the wrong
     * side (e.g. the Cooks' Guild from outside its gated door) is correctly skipped. Walk to this with
     * [walkTo], then open the bank. Prefer this over a raw nearest-"Bank booth" object lookup.
     */
    fun nearestBank(): Tile?
}
