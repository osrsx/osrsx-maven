package io.osrsx.api

/** One node from a recursive widget-tree walk — a flattened, inspectable snapshot of a [Widget]. */
data class WidgetNode(
    val group: Int,
    val child: Int,
    val depth: Int,
    val type: Int,
    val text: String?,
    val name: String?,
    val actions: List<String>,
    val spriteId: Int,
    val itemId: Int,
    val hidden: Boolean,
    val hasListener: Boolean,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
)

/** Widget tree access + click interaction. */
interface Widgets {
    fun get(group: Int, child: Int): Widget?
    fun isVisible(group: Int, child: Int): Boolean
    fun getByText(text: String): Widget?
    fun interact(widget: Widget?): Boolean
    fun interact(group: Int, child: Int): Boolean
    fun interact(widget: Widget?, action: String, target: String? = null): Boolean

    /**
     * Drag-and-drop [from] onto [to] — the general drag-reorder primitive (bank/inventory rearrange,
     * or any drag UI). Presses a random point inside [from] and releases at a random point inside [to]
     * via the humanised held-button path (never a bare centre). False if either widget is missing or
     * hidden. The caller is responsible for both widgets being on-screen (e.g. not scrolled out).
     */
    fun drag(from: Widget?, to: Widget?): Boolean

    /**
     * Scroll [target] into [viewport] by mouse-wheel (general scrollable-list helper — bank, etc.).
     * Picks the wheel direction adaptively (reverses if the target moves the wrong way), so it works
     * regardless of a list's scroll-sign convention. True once [target]'s centre sits inside [viewport].
     * Leaves the cursor over [viewport]. No-op-true if already in view.
     */
    fun scrollIntoView(target: Widget?, viewport: Widget?): Boolean

    /**
     * Drag-and-drop [from] onto [to] when either may be scrolled off-screen: scrolls [from] into
     * [viewport], grabs it, scrolls [to] into view WHILE holding, then drops. The whole gesture is
     * serialised so the held item isn't disturbed. Random points inside each box (never centres).
     * For the common case where both ends are already visible, use [drag].
     */
    fun dragScroll(from: Widget?, to: Widget?, viewport: Widget?): Boolean

    /** Recursively walk a widget [group]'s whole tree (static + dynamic + nested children) into a flat
     *  list — for inspecting an interface's layout/actions when constants don't exist for a component. */
    fun dump(group: Int): List<WidgetNode>
    /** Every widget currently mounted under any root, recursively (visible or hidden). */
    fun all(): List<Widget>
    /** First mounted widget offering the menu [action] (case-insensitive, substring); null if none. */
    fun withAction(action: String): Widget?
    /**
     * First VISIBLE widget whose display name equals [name] (colour tags stripped, case-insensitive)
     * AND whose menu actions contain [action] (tags stripped, substring, case-insensitive). Either
     * criterion may be null to skip it (both null finds nothing). Unlike [withAction] this filters
     * to visible widgets — the lookup for on-screen controls like spells, prayers or GE buttons.
     */
    fun find(name: String?, action: String?): Widget?
}
