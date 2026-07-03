package io.osrsx.api

import kotlin.math.abs

/**
 * A world tile — the SDK-owned replacement for `net.runelite.api.coords.WorldPoint` on the plugin
 * boundary. A plain immutable (x, y, plane) value with no engine or RuneLite coupling, so the SDK stays
 * free of `net.runelite.*` (the clean-boundary goal — see docs/PLUGIN_SDK_PHASE0.md).
 *
 * The engine converts between [Tile] and its internal `WorldPoint` at the API boundary (an engine-side
 * `WorldPoint.toTile()` / `Tile.toWorldPoint()`); plugins never see `WorldPoint`.
 *
 * @property x east–west world coordinate
 * @property y north–south world coordinate
 * @property plane floor / height level (0 = ground)
 */
data class Tile(val x: Int, val y: Int, val plane: Int = 0) {

    /** This tile shifted by [dx]/[dy] (and optionally a new [plane]). */
    fun translate(dx: Int, dy: Int, plane: Int = this.plane): Tile = Tile(x + dx, y + dy, plane)

    /**
     * Chebyshev (king-move) distance in tiles to [other] — OSRS's notion of "distance", where a diagonal
     * step counts as one. Returns [Int.MAX_VALUE] when the tiles are on different planes.
     */
    fun distanceTo(other: Tile): Int =
        if (plane != other.plane) Int.MAX_VALUE else maxOf(abs(x - other.x), abs(y - other.y))

    /** Signed east–west delta to [other] (`other.x - x`): positive = [other] is east. */
    fun dx(other: Tile): Int = other.x - x

    /** Signed north–south delta to [other] (`other.y - y`): positive = [other] is north. */
    fun dy(other: Tile): Int = other.y - y

    /** This tile shifted by a `(dx, dy)` delta on the same plane — `tile + (1 to 0)` steps one tile east. */
    operator fun plus(delta: Pair<Int, Int>): Tile = Tile(x + delta.first, y + delta.second, plane)

    /** This tile shifted by the negation of a `(dx, dy)` delta on the same plane. */
    operator fun minus(delta: Pair<Int, Int>): Tile = Tile(x - delta.first, y - delta.second, plane)

    /** The 8 king-move neighbours (N, NE, E, SE, S, SW, W, NW) on this plane. */
    fun neighbors(): List<Tile> = buildList(8) {
        for (ddy in 1 downTo -1) for (ddx in -1..1) if (ddx != 0 || ddy != 0) add(Tile(x + ddx, y + ddy, plane))
    }

    /**
     * Every tile within Chebyshev [radius] of this one — the `(2·radius + 1)²` square block centred here
     * (this tile included), row-major from the south-west corner. [radius] 0 returns just this tile.
     */
    fun within(radius: Int): List<Tile> = buildList((2 * radius + 1) * (2 * radius + 1)) {
        for (ddy in -radius..radius) for (ddx in -radius..radius) add(Tile(x + ddx, y + ddy, plane))
    }

    /** The tile midway to [other] (integer midpoint), keeping this tile's plane. */
    fun midpoint(other: Tile): Tile = Tile((x + other.x) / 2, (y + other.y) / 2, plane)

    /** OSRS map-region id containing this tile: `(regionColumn shl 8) or regionRow` where a region is 64×64. */
    val regionId: Int get() = ((x shr 6) shl 8) or (y shr 6)

    /** This tile's east–west offset (0..63) WITHIN its 64×64 region. */
    val regionX: Int get() = x and 0x3F

    /** This tile's north–south offset (0..63) WITHIN its 64×64 region. */
    val regionY: Int get() = y and 0x3F

    /**
     * The king-move step [Direction] toward [other] (by the sign of the x/y deltas), or null when [other]
     * is this exact tile or on a different plane. E.g. any tile to the north-east returns [Direction.NE].
     */
    fun directionTo(other: Tile): Direction? {
        if (plane != other.plane) return null
        val sx = Integer.signum(other.x - x)
        val sy = Integer.signum(other.y - y)
        return Direction.of(sx, sy)
    }

    /**
     * The straight tile line from this tile to [other] (inclusive of both), by integer Bresenham on this
     * plane. Cross-plane just returns `[this, other]`. Handy for line-of-sight sweeps and telegraph lanes.
     */
    fun lineTo(other: Tile): List<Tile> {
        if (plane != other.plane) return listOf(this, other)
        val out = ArrayList<Tile>()
        var cx = x; var cy = y
        val ddx = abs(other.x - x); val ddy = abs(other.y - y)
        val sx = if (x < other.x) 1 else -1
        val sy = if (y < other.y) 1 else -1
        var err = ddx - ddy
        while (true) {
            out.add(Tile(cx, cy, plane))
            if (cx == other.x && cy == other.y) break
            val e2 = 2 * err
            if (e2 > -ddy) { err -= ddy; cx += sx }
            if (e2 < ddx) { err += ddx; cy += sy }
        }
        return out
    }

    override fun toString(): String = "Tile($x, $y, $plane)"
}

/**
 * An 8-way king-move compass direction on the tile grid (north = +y, east = +x) — the SDK-owned form used
 * by [Tile.directionTo], so plugins reason about heading without any `net.runelite.*` type.
 */
enum class Direction(val dx: Int, val dy: Int) {
    N(0, 1), NE(1, 1), E(1, 0), SE(1, -1), S(0, -1), SW(-1, -1), W(-1, 0), NW(-1, 1);

    companion object {
        /** The direction for a pair of unit signs (-1, 0, +1), or null for `(0, 0)`. */
        fun of(sx: Int, sy: Int): Direction? = entries.firstOrNull { it.dx == sx && it.dy == sy }
    }
}
