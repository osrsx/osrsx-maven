package io.osrsx.api

/** Worn equipment: container reads plus the shared equip/unequip primitive (the single "open the inventory
 *  tab → Wield/Wear/Equip" / "Worn tab → Remove" idiom, formerly re-implemented in the boss + quest code). */
interface Equipment : Container {
    /**
     * Equip the item matching [ref] from the inventory: no-op-true if it's already worn, false if it isn't
     * carried, otherwise open the inventory tab and Wield/Wear/Equip it (returns whether the click landed).
     */
    fun equip(ref: ItemRef): Boolean
    /** Equip the inventory item named [name] (ergonomic [equip] overload). */
    fun equip(name: String): Boolean
    /** Equip the inventory item with id [id] (ergonomic [equip] overload). */
    fun equip(id: Int): Boolean

    /**
     * Take off the worn item matching [ref]: false if it isn't worn, otherwise open the Worn Equipment tab,
     * find its slot and click "Remove" (returns whether the click landed).
     */
    fun unequip(ref: ItemRef): Boolean
    /** Unequip the worn item named [name] (ergonomic [unequip] overload). */
    fun unequip(name: String): Boolean
    /** Unequip the worn item with id [id] (ergonomic [unequip] overload). */
    fun unequip(id: Int): Boolean
}
