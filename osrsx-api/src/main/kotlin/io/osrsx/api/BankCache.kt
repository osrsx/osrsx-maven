package io.osrsx.api

/**
 * A persisted, per-account snapshot of the player's bank — so "what's in my bank?" is answerable
 * anywhere on the map, not just while standing at an open bank. The snapshot is captured automatically
 * whenever the bank is open and its contents change (no caller effort), persisted via [DataStore] under
 * the account's `user.home`, and reloaded on the next session. Toolbelt and other helpers fall back to
 * this when the live bank container isn't loaded.
 *
 * Reads ([items]/[contains]/[count]/[coins]) reflect the most recent snapshot — possibly stale. Use
 * [capturedAt]/[ageMs]/[isStale] to judge freshness, and [ensureFresh] to walk to a bank and refresh.
 */
interface BankCache {
    /** The last-known bank contents; empty if nothing has ever been captured for this account. */
    fun items(): List<CachedItem>

    fun contains(id: Int): Boolean
    fun contains(name: String): Boolean
    /** Quantity of [id] in the snapshot (0 if absent). */
    fun count(id: Int): Int
    /** Total quantity matching [name] (case-insensitive) in the snapshot (0 if absent). */
    fun count(name: String): Int
    /** Coins (item 995) in the snapshot. */
    fun coins(): Int

    /** Epoch-millis the snapshot was captured, or 0 if there is none. */
    fun capturedAt(): Long
    /** Milliseconds since the snapshot was captured, or [Long.MAX_VALUE] if there is none. */
    fun ageMs(): Long
    /** True if there's no snapshot or it's older than [maxAgeMs]. */
    fun isStale(maxAgeMs: Long): Boolean
    /** True if no snapshot has been captured (or it captured an empty bank). */
    fun isEmpty(): Boolean

    /**
     * Whether the live bank has actually been opened/captured since the current login. A snapshot loaded
     * from disk is from a PREVIOUS session, so it stays unconfirmed until the bank is opened this session
     * (reset on logout/world-hop). Code that drives real actions on bank contents should confirm first —
     * [ensureFresh] re-opens the bank once per session for exactly this reason.
     */
    fun isConfirmed(): Boolean

    /** Capture the live bank into the cache right now. No-op returning false unless the bank is open. */
    fun snapshot(): Boolean

    /**
     * Bring the cache up to date by walking to the nearest reachable bank and opening it (the snapshot
     * then happens automatically). Loop-driven like the web walker: call every loop — it returns true
     * once the cache is fresh (bank reached + captured), false while still travelling/opening. If the
     * bank is already open it just snapshots. Returns true immediately only when the snapshot is BOTH
     * confirmed this session ([isConfirmed]) and not stale — so calling it at startup forces one real
     * bank visit per login rather than trusting a previous session's snapshot.
     */
    fun ensureFresh(): Boolean
    /** As [ensureFresh], but treats a snapshot younger than [maxAgeMs] as already fresh (no walk). */
    fun ensureFresh(maxAgeMs: Long): Boolean
}

/** One item in a [BankCache] snapshot. [name] is resolved from [id] via the item cache. */
data class CachedItem(val id: Int, val name: String, val quantity: Int)
