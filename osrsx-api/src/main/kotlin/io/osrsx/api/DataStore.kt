package io.osrsx.api

/**
 * Per-account persistent key-value storage for plugins, backed by a SQLite file under the (launcher-
 * virtualised) `user.home` — so each account keeps its OWN data, isolated from other accounts, and it
 * survives restarts. This is for *mutable, account-specific* state (bank snapshots, progress counters,
 * learned positions, last-known anything) — NOT the shared read-only reference data in `osrsx.db`.
 *
 * Values are plain strings; serialize richer data yourself (JSON/CSV). Keys are grouped by [namespace]
 * so plugins don't collide — namespace by your plugin id (e.g. `"miner.state"`). Every write stamps an
 * update time readable via [updatedAt], so callers can reason about staleness.
 *
 * All methods are no-ops / empty / 0 if the store can't be opened (it never throws), so a plugin that
 * persists state still runs where the disk isn't writable.
 */
interface DataStore {
    /** Store [value] under ([namespace], [key]), overwriting any previous value and stamping the time. */
    fun put(namespace: String, key: String, value: String)

    /** The stored value for ([namespace], [key]), or null if unset. */
    fun get(namespace: String, key: String): String?

    /** Delete ([namespace], [key]). */
    fun remove(namespace: String, key: String)

    /** All keys present in [namespace]. */
    fun keys(namespace: String): List<String>

    /** All key→value pairs in [namespace]. */
    fun entries(namespace: String): Map<String, String>

    /** Delete every key in [namespace]. */
    fun clear(namespace: String)

    /** Epoch-millis of the last [put] to ([namespace], [key]), or 0 if absent. */
    fun updatedAt(namespace: String, key: String): Long
}
