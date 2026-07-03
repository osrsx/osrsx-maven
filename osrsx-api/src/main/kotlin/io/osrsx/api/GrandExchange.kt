package io.osrsx.api

/** A read-only snapshot of one Grand Exchange slot (0..7). */
interface GeOffer {
    val slot: Int
    val state: GrandExchangeOfferState
    val itemId: Int
    val itemName: String?
    /** Offered price per item (gp). */
    val price: Int
    /** Total quantity the offer is buying/selling. */
    val totalQuantity: Int
    /** Items bought/sold so far. */
    val quantityFilled: Int
    /** gp spent (buy) or earned (sell) so far. */
    val spent: Int

    fun isEmpty(): Boolean
    /** BUYING or SELLING — the offer is live and not yet fully filled. */
    fun inProgress(): Boolean
    /** BOUGHT or SOLD — the offer fully completed. */
    fun isComplete(): Boolean
    /** CANCELLED_BUY or CANCELLED_SELL — the offer was aborted. */
    fun isCancelled(): Boolean
    /** There are items and/or coins in this slot waiting to be collected. */
    fun isCollectable(): Boolean
}

/**
 * The Grand Exchange — read the 8 offer slots and place / collect / abort offers. Mirrors DreamBot's
 * `GrandExchange`. Price helpers (`*AtMarket`, `*Instant`) use the live Wiki market value (see [Prices]):
 * "market" places at the guide price; "instant" crosses the spread (over-buy / under-cut) so the offer
 * fills immediately. Explicit `buy`/`sell` overloads take an exact gp price per item.
 */
interface GrandExchange {
    // ---- interface state ----
    fun isOpen(): Boolean
    /** Open the GE by talking to the nearest Grand Exchange Clerk in the loaded scene. Call until [isOpen]. */
    fun open(): Boolean

    /**
     * Walk to the nearest Grand Exchange (via the web walker) and open it. Loop-driven: call each loop
     * until it returns true (GE open). Unlike [open] — which only works when a clerk is already in the
     * scene — this navigates there first, targeting the CLERK NPC (the GE booth objects are catalogued on
     * the wrong plane, so don't route to them). Prefer this over a manual walk to a "Grand Exchange booth".
     */
    fun openNearest(): Boolean
    fun close(): Boolean

    // ---- offer slots (read) ----
    /** All 8 slots, indexed by slot number (0..7). */
    fun offers(): List<GeOffer>
    fun offer(slot: Int): GeOffer?
    /** Lowest-numbered empty slot, or -1 if all 8 are in use. */
    fun firstEmptySlot(): Int
    fun emptySlots(): Int
    fun isFull(): Boolean
    /** True when any slot has items/coins waiting to be collected. */
    fun isReadyToCollect(): Boolean

    // ---- guide price ----
    fun guidePrice(id: Int): Int
    fun guidePrice(name: String): Int

    // ---- buy ----
    fun buy(id: Int, quantity: Int, price: Int): Boolean
    fun buy(name: String, quantity: Int, price: Int): Boolean
    /** Buy at the live guide/market price. */
    fun buyAtMarket(name: String, quantity: Int): Boolean
    /** Buy well above market so the offer fills immediately. */
    fun buyInstant(name: String, quantity: Int): Boolean

    // ---- sell ----
    fun sell(id: Int, quantity: Int, price: Int): Boolean
    fun sell(name: String, quantity: Int, price: Int): Boolean
    /** Sell at the live guide/market price. */
    fun sellAtMarket(name: String, quantity: Int): Boolean
    /** Sell well below market so the offer fills immediately. */
    fun sellInstant(name: String, quantity: Int): Boolean

    // ---- collect / abort ----
    /** Collect everything (items + coins) from all completed/cancelled slots to the inventory. */
    fun collectToInventory(): Boolean
    /** Collect everything to the bank. */
    fun collectToBank(): Boolean
    /** Abort the offer in [slot] (0..7). Items/coins remain to be collected. */
    fun abort(slot: Int): Boolean
    /** Abort the active offer for [name]. */
    fun abort(name: String): Boolean
}
