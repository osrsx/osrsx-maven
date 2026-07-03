package io.osrsx.api

/**
 * The lifecycle state of one Grand Exchange offer slot — the SDK-owned replacement for
 * `net.runelite.api.GrandExchangeOfferState` on the plugin boundary, so the SDK carries no
 * `net.runelite.*` dependency (see docs/PLUGIN_SDK_PHASE0.md).
 *
 * Members mirror the game's states 1:1 by name, so the engine maps to/from its internal enum by [name]
 * at the boundary (see `io.osrsx.methods.GrandExchangeImpl`).
 */
enum class GrandExchangeOfferState {
    /** No offer in this slot. */
    EMPTY,
    /** A buy offer that was cancelled (items/coins may remain to collect). */
    CANCELLED_BUY,
    /** A sell offer that was cancelled (items/coins may remain to collect). */
    CANCELLED_SELL,
    /** A buy offer still in progress (not yet fully bought). */
    BUYING,
    /** A buy offer that fully completed. */
    BOUGHT,
    /** A sell offer still in progress (not yet fully sold). */
    SELLING,
    /** A sell offer that fully completed. */
    SOLD,
}
