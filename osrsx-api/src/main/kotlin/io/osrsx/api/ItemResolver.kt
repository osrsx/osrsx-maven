package io.osrsx.api

/**
 * Collapses an [ItemRef] to a concrete id / candidate ids / live [Item] against the live game data —
 * [Prices] for name→id and [ItemCatalog] for categories. This is the ONE place name→id conversion lives,
 * replacing the `prices().idOf(name)` calls that used to be copied across the boss/quest/GE/toolbelt code.
 */
class ItemResolver(private val prices: Prices, private val catalog: ItemCatalog) {

    /** A single canonical id for [ref] — for a GE buy / bank withdraw / price lookup — or -1 if unresolved.
     *  [ItemRef.AnyOf] yields its first (preferred) id; [ItemRef.ByCategory] has no single id (returns -1). */
    fun idOf(ref: ItemRef): Int = when (ref) {
        is ItemRef.ById -> ref.id
        is ItemRef.ByName -> prices.idOf(ref.name)
        is ItemRef.AnyOf -> ref.ids.firstOrNull() ?: -1
        is ItemRef.ByCategory -> -1
    }

    /** Every candidate id for [ref] (for a "have I got any of these?" scan): [ItemRef.AnyOf] → all,
     *  [ItemRef.ByName] → the one resolved id (empty if unknown), [ItemRef.ByCategory] → none. */
    fun idsOf(ref: ItemRef): List<Int> = when (ref) {
        is ItemRef.ById -> listOf(ref.id)
        is ItemRef.ByName -> prices.idOf(ref.name).takeIf { it >= 0 }?.let { listOf(it) } ?: emptyList()
        is ItemRef.AnyOf -> ref.ids
        is ItemRef.ByCategory -> emptyList()
    }

    /** Does the live [item] satisfy [ref]? (Category matches consult the [catalog].) */
    fun matches(ref: ItemRef, item: Item): Boolean = ref.matches(item, catalog)
}
