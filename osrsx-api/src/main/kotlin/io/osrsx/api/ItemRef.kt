package io.osrsx.api

/**
 * How code SPECIFIES or REQUIRES an item — a description, NOT a live held instance (that's [Item] /
 * [io.osrsx.wrappers.GroundItem], which are the resolved forms; see [HasItemRef]). One shape unifies the
 * ~five ad-hoc item representations that used to coexist (bare name/id, quest `ItemNeed`, boss `GearItem`,
 * quest `EquipAction.Wear`, …): a container/loot query, a provisioning need, or an equip target is all just
 * an [ItemRef] (optionally wrapped in an [ItemSpec] to carry quantity + a must-be-worn flag).
 *
 * Use the [invoke] factories for the terse common cases — `ItemRef("Iron chainbody")` / `ItemRef(1113)` —
 * and the explicit subtypes for alternates ([AnyOf]) and tool categories ([ByCategory]).
 */
sealed interface ItemRef {
    /** An exact GE item id — the most precise form, and what a live [Item] resolves to. */
    data class ById(val id: Int) : ItemRef

    /** An item by display name (case-insensitive), resolved to an id via the GE mapping when needed. */
    data class ByName(val name: String) : ItemRef

    /** Any one of [ids] satisfies it — alternates (quest `allIds`, a transport `a||b||c` list). */
    data class AnyOf(val ids: List<Int>) : ItemRef

    /** Any item in a named CATEGORY (broad bank bucket via [ItemCatalog], e.g. "Tools"). */
    data class ByCategory(val category: String) : ItemRef

    companion object {
        /** Terse factory: `ItemRef("Coins")`. */
        operator fun invoke(name: String): ItemRef = ByName(name)

        /** Terse factory: `ItemRef(995)`. */
        operator fun invoke(id: Int): ItemRef = ById(id)
    }
}

/**
 * An item requirement: an [ItemRef] plus how many ([qty]) and whether it must be WORN ([equip]) rather than
 * merely carried. The equip flag is what [Container.satisfies] keys on to demand the equipment container —
 * the generalized fix for the "a must-be-equipped item still counts while it sits in the inventory" bug.
 */
data class ItemSpec(val ref: ItemRef, val qty: Int = 1, val equip: Boolean = false)

/**
 * A live, HELD item that satisfies an [ItemRef] by exact id — the resolved form of a spec. Both the
 * container [Item] and the world [io.osrsx.wrappers.GroundItem] implement it, so membership/identity scans
 * can talk about "a thing with an id and a quantity" uniformly instead of each re-deriving name/id logic.
 */
interface HasItemRef {
    val id: Int
    val quantity: Int
    /** The exact-id spec this live item satisfies. */
    val ref: ItemRef get() = ItemRef.ById(id)
}
