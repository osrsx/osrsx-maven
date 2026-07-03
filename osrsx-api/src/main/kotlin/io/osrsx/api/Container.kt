package io.osrsx.api

/** Common read surface over an item container (inventory, equipment, bank). */
interface Container {
    fun items(): List<Item>
    fun items(predicate: (Item) -> Boolean): List<Item>
    /** Distinct item names present in this container — a plain-strings view for "keep these,
     *  do something with the rest" logic without iterating item objects. */
    fun names(): List<String>
    fun getItem(predicate: (Item) -> Boolean): Item?
    fun getItem(name: String): Item?
    fun getItem(id: Int): Item?
    /** `operator` so `995 in inventory` works; equivalent to `count(id) > 0`. */
    operator fun contains(id: Int): Boolean
    /** `operator` so `"Shark" in inventory` works; equivalent to `count(name) > 0`. */
    operator fun contains(name: String): Boolean
    fun containsAll(vararg names: String): Boolean
    fun count(id: Int): Int
    fun count(name: String): Int
    fun occupiedSlots(): Int
    fun isEmpty(): Boolean

    // ---- ref-based access (the [ItemRef] unification over the name/id overloads above) ----

    /** The first held item matching [ref] (id/name/alternates/category), or null. */
    fun getItem(ref: ItemRef): Item?
    /** Whether any held item matches [ref]. `operator` so `ref in inventory` works. */
    operator fun contains(ref: ItemRef): Boolean
    /** Total quantity of items matching [ref] across stacks. */
    fun count(ref: ItemRef): Int

    /**
     * Whether this container satisfies [spec] — at least [ItemSpec.qty] of its ref. When [ItemSpec.equip] is
     * set the item must be WORN, so ONLY the equipment container can satisfy it (a copy sitting in the
     * inventory does not) — the generalized fix for the "must-be-equipped item counts while merely carried"
     * bug. Ask `inventory().satisfies(spec) || equipment().satisfies(spec)` for "held or worn".
     */
    fun satisfies(spec: ItemSpec): Boolean
}
