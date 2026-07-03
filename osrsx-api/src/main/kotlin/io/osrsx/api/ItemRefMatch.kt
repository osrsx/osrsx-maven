package io.osrsx.api

/**
 * True if the live [item] satisfies [this] ref. Id/name/alternates are intrinsic; [ItemRef.ByCategory]
 * needs a [catalog] to classify the item (matches nothing when none is supplied). A single place for the
 * ref→item matching rule, shared by [Container] and [ItemResolver].
 *
 * Lives in the client (not the SDK module) because it depends on the engine-side [Item] wrapper and the
 * [ItemCatalog] surface; [ItemRef] itself is a pure SDK type.
 */
fun ItemRef.matches(item: Item, catalog: ItemCatalog? = null): Boolean = when (this) {
    is ItemRef.ById -> item.id == id
    is ItemRef.ByName -> item.name.equals(name, ignoreCase = true)
    is ItemRef.AnyOf -> item.id in ids
    is ItemRef.ByCategory -> catalog != null && catalog.category(item.id).equals(category, ignoreCase = true)
}
