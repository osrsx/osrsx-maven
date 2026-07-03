package io.osrsx.api

/**
 * A stack of items in a container slot — the plugin-facing view of the engine's `io.osrsx.wrappers.Item`.
 * As a [HasItemRef] it is the resolved/live form of an [ItemRef] (its [ref] is an exact-id match), so
 * identity/membership scans share the same ref matching as specs. [id]/[quantity] come from [HasItemRef].
 */
interface Item : HasItemRef {
    /** Display name, resolved from the item definition, or null. */
    val name: String?

    /** Inventory menu actions (e.g. `["Eat", …]`, `["Wield", …]`), used to classify an item generically. */
    val actions: List<String>

    /**
     * A bank PLACEHOLDER — the greyed-out 0-stack slot kept after a withdraw-all. A distinct item id that
     * resolves to the real item's name, so `contains`/`count` must ignore it.
     */
    val isPlaceholder: Boolean

    /** The container slot this stack occupies, or -1 if not slot-bound. */
    val slot: Int

    /** True if [name] equals [target] (case-insensitive). */
    fun nameMatches(target: String): Boolean
}
