package io.osrsx.api

/** Ground items in the loaded scene. */
interface GroundItems {
    fun all(): List<GroundItem>
    fun all(predicate: (GroundItem) -> Boolean): List<GroundItem>
    fun closest(predicate: (GroundItem) -> Boolean): GroundItem?
    fun closest(name: String): GroundItem?
    fun closest(vararg ids: Int): GroundItem?

    // ---- ref-based queries (the world sibling of [Container]'s ref access) ----

    /** Every ground item matching [ref] (id/name/alternates/category). */
    fun all(ref: ItemRef): List<GroundItem>
    /** The nearest ground item matching [ref], or null. */
    fun closest(ref: ItemRef): GroundItem?
    /** Whether enough loot matching [spec]'s ref lies on the ground (summed quantity ≥ [ItemSpec.qty]); the
     *  equip flag is meaningless for the floor and ignored. Mirrors [Container.satisfies]. */
    fun satisfies(spec: ItemSpec): Boolean
}
