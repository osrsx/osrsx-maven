package io.osrsx.api


/** Prayer activation. */
interface Prayers {
    fun isActive(prayer: Prayer): Boolean
    fun activate(prayer: Prayer): Boolean
    fun deactivate(prayer: Prayer): Boolean
    fun toggle(prayer: Prayer): Boolean
    fun setActive(vararg prayers: Prayer): Boolean

    /** Whether the quick-prayer preset is currently active (reads the quick-prayer varbit). */
    fun quickActive(): Boolean
    /** Toggle the quick-prayer preset to [on] by clicking the minimap orb — the fast path for flicking. */
    fun quick(on: Boolean): Boolean
}
