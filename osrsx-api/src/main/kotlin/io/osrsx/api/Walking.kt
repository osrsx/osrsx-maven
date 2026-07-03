package io.osrsx.api


/**
 * Local (in-scene) walking and run management. Cross-region travel is [WebWalker].
 * The lower-level engine helpers (overlay path projection, etc.) live on the concrete
 * implementation; this is the plugin-facing surface.
 */
interface Walking {
    fun runEnergy(): Int
    fun isRunEnabled(): Boolean
    fun toggleRun(): Boolean
    fun enableRunIf(threshold: Int = 30): Boolean
    fun manageRun()
    fun isInScene(dest: Tile): Boolean
    fun walkStep(dest: Tile): Boolean
    /** Like [walkStep] but FORCES a fresh click even if already walking — for a redirect that must override an
     *  in-progress walk *now* (a boss dodge interrupting a reposition); normal cadence would swallow it. */
    fun walkStep(dest: Tile, force: Boolean): Boolean
    /** Like [walkStep] but, when [leftClickOnly] is true, NEVER falls back to a right-click → "Walk here" menu:
     *  if the viewport tile under the cursor isn't a left-click "Walk here" (an entity/object is on top), it goes
     *  STRAIGHT to a minimap left-click instead. The right-click menu costs ~700ms to open — fatal mid boss-dodge —
     *  so a dodge/reposition wants a guaranteed instant left-click (viewport if clean, else minimap). */
    fun walkStep(dest: Tile, force: Boolean, leftClickOnly: Boolean): Boolean
    fun walkTowards(target: Tile): Boolean
    fun localDistance(dest: Tile): Int
    fun canReachToInteract(obj: Tile): Boolean
    /** The tiles you could stand on to INTERACT with the object at [obj] — its walkable neighbours with NO
     *  wall on the edge toward it (so the wrong, walled-off side is excluded). Empty if [obj] isn't in the
     *  loaded scene. Pair with a reachability check to route to the correct usable side (e.g. INTO a building
     *  rather than up against its outer wall). */
    fun interactTiles(obj: Tile): List<Tile>
    fun reachableNear(dest: Tile): Tile
    /** Is [tile] standable right now (not a wall/blocked object) per the live collision map? False if it's
     *  outside the loaded scene. A fast, path-free check — e.g. to pick a safe dodge tile that isn't a wall. */
    fun isWalkable(tile: Tile): Boolean
    /** RuneLite's authoritative walk TARGET — the tile the client is currently moving toward (the yellow
     *  destination tile), or null when standing still. This is the real in-game destination regardless of who
     *  issued the move, so it's the reliable "are we moving, and where to" for dodge logic. */
    fun destination(): Tile?
    /** The most OPEN standable tile within [radius] of [center] — the one with the most walkable neighbors
     *  (tie-broken toward the centre), i.e. room to dodge in every direction. Collision-only, single scan;
     *  null if none in scene. Use it to reposition off a wall/corner before a boss fight. */
    fun openestTile(center: Tile, radius: Int): Tile?
    /** How many of [tile]'s 8 neighbours are walkable (0..8) — a cheap "room to move/dodge from here" measure. */
    fun opennessAt(tile: Tile): Int
    /** The openest tile REACHABLE (wall-aware, via the local pathfinder) from [from] within [radius] — the
     *  safest place to stand in a fight: maximum room to dodge AND actually walkable-to (unlike [openestTile],
     *  which is collision-only and can pick a tile across a wall). Tie-broken toward [from]; null if none.
     *  Stand here proactively so a dodge always has somewhere to go. */
    fun safestReachableTile(from: Tile, radius: Int): Tile?
    /** Every tile within [radius] of [center] paired with its step-openness (0..8), or **-1 if blocked** — one
     *  collision scan, for a debug overlay that visualizes exactly what the walker sees (blocked vs open room). */
    fun opennessGrid(center: Tile, radius: Int): List<Pair<Tile, Int>>
    /** Flood-fill the ENCLOSED area reachable from [from] via wall-aware steps (bounded by [maxRadius]) — every
     *  tile inside the same closed-off region, e.g. a boss pen. Scan an arena ONCE with this and confine all
     *  positioning to the returned set, so tiles across a wall (outside the pen) are never considered. */
    fun enclosedArea(from: Tile, maxRadius: Int): List<Tile>
    fun localTilePath(dest: Tile): List<Tile>?
    fun pathSteps(target: Tile): Int

    /**
     * Of [tiles], those within [maxSteps] ON-FOOT steps of [from] over the WHOLE-MAP collision map (no
     * transports) — so a water/wall barrier excludes a tile that is near in a straight line but a long walk
     * away (a tree across a river). Unlike the scene helpers this works OFF-SCENE (it reads the global
     * collision data), so a plugin can judge whether catalogued resource tiles it has never had in view form
     * a real, walkable cluster. An object/blocked tile counts as reached once a tile adjacent to it is reached
     * (you stand beside it to interact). Empty when [from]'s region has no collision data.
     */
    fun walkReachable(from: Tile, tiles: Collection<Tile>, maxSteps: Int): List<Tile>
}
