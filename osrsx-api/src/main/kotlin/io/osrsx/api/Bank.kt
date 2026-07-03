package io.osrsx.api


/** The bank: container reads plus open/withdraw/deposit interaction. */
interface Bank : Container {
    fun isOpen(): Boolean
    fun open(): Boolean

    /**
     * Walk to the nearest bank you can ACTUALLY reach (via the web walker) and open it. Loop-driven: call
     * each loop until it returns true (bank open). Unlike [open] — which only opens a bank already in the
     * loaded scene — this navigates there first, using reachability-aware selection ([WebWalker.nearestBank])
     * so it won't get stuck routing to a wall-mounted booth it can't bank across (e.g. the Cooks' Guild from
     * outside its gate). Falls back to opening an in-scene bank if the walker isn't available.
     */
    fun openNearest(): Boolean
    fun close(): Boolean
    fun withdraw(name: String, amount: Int): Boolean
    fun withdraw(id: Int, amount: Int, target: String? = null): Boolean
    /** Withdraw [amount] (<=0 = All) of [name] as NOTES ([noted] true) or items — collapses an unstackable
     *  pile into one stack (e.g. so a whole leather lot sells in a single GE offer). Toggles mode only if needed. */
    fun withdraw(name: String, amount: Int, noted: Boolean): Boolean
    /** Deposit [amount] of [name] from the inventory into the open bank. [amount] `<= 0` means ALL —
     *  the same sentinel [withdraw] uses (0 = All) — which fires the "Deposit-All" menu action rather than
     *  typing a quantity. See [depositAll] for the explicit convenience. */
    fun deposit(name: String, amount: Int): Boolean

    /** Deposit ALL of [name] from the inventory (the "Deposit-All" action). Convenience for
     *  `deposit(name, 0)`, mirroring `withdraw(name, 0)`. */
    fun depositAll(name: String): Boolean = deposit(name, 0)
    fun depositInventory(): Boolean
    fun depositEquipment(): Boolean

    /** True while the bank is in Insert rearrange mode (a drag shifts items rather than swapping). */
    fun isInsertMode(): Boolean
    /** Put the bank into Insert (true) or Swap (false) rearrange mode; toggles only if needed. */
    fun setInsertMode(insert: Boolean): Boolean
    /** The bank tab currently shown (0 = "View all items"). */
    fun viewedTab(): Int
    /** Switch to "View all items" — required before moving items by global slot (a tab view shows a subset). */
    fun viewAllItems(): Boolean
    /** Number of items in bank [tab] (1..9). In the all view, tabs occupy the leading slots in tab order,
     *  so these counts give each tab's slot range (and thus which tab any item is in). */
    fun tabSize(tab: Int): Int

    /** The on-screen widget currently holding item [id] (located by item id, never a slot index), or null
     *  if it isn't in the bank. Drag THIS — it's always the right item regardless of scroll position. */
    fun itemWidget(id: Int): Widget?
    /** True when item [id]'s widget centre lies inside the visible bank viewport (not scrolled out). */
    fun isItemVisible(id: Int): Boolean
    /** Deterministically scroll item [id] to the viewport centre by dragging the scrollbar thumb (content-
     *  space math — no wheel heuristics). Returns true only once the item is genuinely visible, so a caller
     *  can drag it without ever grabbing empty space or the wrong row. */
    fun bringItemIntoView(id: Int): Boolean
}
