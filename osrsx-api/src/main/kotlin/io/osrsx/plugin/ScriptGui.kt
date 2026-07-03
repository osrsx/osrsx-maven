package io.osrsx.plugin

/**
 * The `gui` object a plugin's [HasPaint.onPaint] receives — a thin, engine-agnostic facade over the
 * current interactive-UI frame so a panel is authored in osrsx terms (`gui.text(...)`, `gui.button(...)`)
 * rather than against a concrete UI toolkit. The engine supplies the backing implementation.
 *
 * Stateful widgets take the current value and return the new one (immediate-mode style): read the
 * returned value and store it back yourself.
 */
interface ScriptGui {
    fun text(s: String)
    fun textColored(r: Float, g: Float, b: Float, a: Float, s: String)
    fun separator()
    fun sameLine()
    fun spacing()

    /** @return true on the frame the button is clicked. */
    fun button(label: String): Boolean

    /** @return the new checkbox state (pass the current value in). */
    fun checkbox(label: String, value: Boolean): Boolean

    /** @return the new slider value (pass the current value in). */
    fun sliderInt(label: String, value: Int, min: Int, max: Int): Int

    /** @return the new integer (pass the current value in). */
    fun inputInt(label: String, value: Int): Int

    /** @return the new text (pass the current value in). */
    fun inputText(label: String, value: String, maxLen: Int = 256): String

    /**
     * A searchable item picker (GE-search-style fuzzy match, item sprite + name + category rows). [id] is a
     * stable widget key. @return the selected item id (`< 0` = none); pass the current selection in. The
     * default is a no-op (returns [selected]) for engine impls without a graphical surface.
     */
    fun itemPicker(id: String, selected: Int): Int = selected

    /**
     * A searchable NPC picker (fuzzy match, NPC model thumbnail + name + combat/actions rows). [id] is a
     * stable widget key. @return the selected NPC id (`< 0` = none); pass the current selection in. The
     * default is a no-op (returns [selected]).
     */
    fun npcPicker(id: String, selected: Int): Int = selected

    /** Initial-only window position (the user can move it after). */
    fun setNextWindowPos(x: Float, y: Float)

    /** Initial-only window size (the user can resize after). */
    fun setNextWindowSize(w: Float, h: Float)
}
