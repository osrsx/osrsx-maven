package io.osrsx.api

import io.osrsx.api.Interactable
import java.awt.Rectangle

/** The 28-slot player inventory plus click interaction. */
interface Inventory : Container {
    fun isFull(): Boolean
    fun emptySlotCount(): Int
    fun slotBounds(slot: Int): Rectangle?
    fun interact(item: Item, action: String? = null): Boolean
    fun interact(name: String, action: String? = null): Boolean
    fun drop(item: Item): Boolean
    fun drop(name: String): Boolean

    /**
     * Power-drop [count] items named [name] ([count] < 0 = all of them) using the menu swap (makes "Drop"
     * the left-click default), then a single quick left-click per slot — NO per-click confirmation wait
     * (one click is enough; OSRS leaves a dropped slot empty so positions stay stable). Each drop is paced
     * by a random [minPaceMs]..[maxPaceMs] ms. With [missPercent] (0..100) chance it "fumbles" a slot:
     * skips it, keeps dropping, then returns to click it [minRevisitMs]..[maxRevisitMs] ms later — like a
     * human mis-click they correct a moment later. Returns the number actually dropped.
     */
    fun drop(name: String, count: Int, minPaceMs: Int, maxPaceMs: Int, missPercent: Int, minRevisitMs: Int, maxRevisitMs: Int): Int
    fun use(item: Item): Boolean
    fun use(name: String): Boolean
    fun useOn(item: Item, target: Interactable): Boolean
    fun combine(a: Item, b: Item): Boolean
    fun combine(a: String, b: String): Boolean
}
