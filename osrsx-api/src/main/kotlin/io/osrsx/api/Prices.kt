package io.osrsx.api

/**
 * Live Grand Exchange item prices from the OSRS Wiki real-time prices API
 * (`https://prices.runescape.wiki/api/v1/osrs`). Lets plugins value items — e.g. compute GP/hr.
 *
 * Prices are fetched in the background and cached (a few minutes TTL); lookups never block the caller —
 * they return the cached value, or 0 until the first fetch lands. Item names are resolved via the wiki
 * item mapping (case-insensitive).
 */
interface Prices {
    /** Guide price for [itemId] (the mid of instant-buy/instant-sell), or 0 if unknown/not yet loaded. */
    fun price(itemId: Int): Int
    /** Guide price for the item named [name] (case-insensitive), or 0 if unknown/not yet loaded. */
    fun price(name: String): Int
    /** Instant-buy (high) price for [itemId], or 0. */
    fun high(itemId: Int): Int
    /** Instant-sell (low) price for [itemId], or 0. */
    fun low(itemId: Int): Int
    /** The wiki item id for [name] (case-insensitive), or -1 if unknown/not yet loaded. */
    fun idOf(name: String): Int

    /** Recent (~last hour) traded volume (buys+sells) for [itemId], or 0. The liquidity gate for a market
     *  scan — a wide spread on a near-zero-volume item is a trap (it never fills). */
    fun volume(itemId: Int): Int

    /** Every item that currently has price data: id + latest instant-buy/sell + recent volume, in ONE pass
     *  (so a scanner can sweep the whole ~4k-item market without thousands of point lookups). */
    fun all(): List<PriceTick>

    /** Historical price/volume series for [itemId] at [timestep] ("5m"/"1h"/"6h"/"24h"), oldest→newest (up to
     *  ~365 points), for learning an item's normal price band. Cache-first + background-refreshed like the
     *  live prices: returns the cached series (empty until the first fetch lands), never blocks the caller. */
    fun history(itemId: Int, timestep: String): List<PricePoint>
}

/** A one-pass market snapshot row: latest instant-buy ([high]) / instant-sell ([low]) + recent [volume]. */
data class PriceTick(val id: Int, val high: Int, val low: Int, val volume: Int)

/** One point of an item's price history: averaged buy/sell and traded volumes over the timestep window. */
data class PricePoint(val timestamp: Long, val avgHigh: Int, val avgLow: Int, val highVolume: Int, val lowVolume: Int)
