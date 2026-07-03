package io.osrsx.config

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Base class a Java/Kotlin plugin extends to declare its settings, RuneLite-style but as Kotlin
 * property delegates. Each `xItem(...)` registers a [ConfigItem] (so the engine can render + persist
 * it) AND returns a delegate that reads/writes the live, persisted value:
 *
 * ```kotlin
 * class WoodcutterConfig : PluginConfig("woodcutter") {
 *     var dropLogs by boolItem("dropLogs", "Drop logs when full", default = true)
 *     var radius   by intItem("radius", "Search radius", default = 10, min = 1, max = 30)
 *     var target   by stringItem("target", "Tree name", default = "Tree")
 * }
 * ```
 *
 * Reading `config.dropLogs` returns the stored value (or the default); assigning persists it. The
 * [group] namespaces the keys on disk (`<group>.properties`).
 */
abstract class PluginConfig(val group: String) {
    private val _items = mutableListOf<ConfigItem>()

    /** Every declared setting, in declaration order (used by the config panel). */
    val items: List<ConfigItem> get() = _items

    private val _sectionHeights = LinkedHashMap<String, Float>()

    /** Per-section fixed pixel height (scrollable); a section absent here renders its full height. */
    val sectionHeights: Map<String, Float> get() = _sectionHeights

    /** Give a sub-[section] a fixed pixel [height] so its box scrolls; omit for a full-height section. */
    protected fun sectionHeight(section: String, height: Float) { _sectionHeights[section] = height }

    /** Bound by the plugin manager once the config group's [ConfigStore] exists. */
    var store: ConfigStore? = null

    private inner class Delegate<T>(private val item: ConfigItem) : ReadWriteProperty<Any?, T> {
        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Any?, property: KProperty<*>): T = (store?.get(item) ?: item.default) as T
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) { store?.set(item, value) }
    }

    private fun <T> register(item: ConfigItem): ReadWriteProperty<Any?, T> {
        _items += item
        return Delegate(item)
    }

    protected fun boolItem(key: String, name: String, default: Boolean, description: String = "", section: String = ""): ReadWriteProperty<Any?, Boolean> =
        register(ConfigItem(key, name, ConfigType.BOOL, default, description, section = section))

    protected fun intItem(key: String, name: String, default: Int, min: Int = 0, max: Int = 0, description: String = "", section: String = ""): ReadWriteProperty<Any?, Int> =
        register(ConfigItem(key, name, ConfigType.INT, default, description, min, max, section = section))

    protected fun stringItem(key: String, name: String, default: String, description: String = "", section: String = ""): ReadWriteProperty<Any?, String> =
        register(ConfigItem(key, name, ConfigType.STRING, default, description, section = section))

    protected fun enumItem(key: String, name: String, default: String, options: List<String>, description: String = "", section: String = ""): ReadWriteProperty<Any?, String> =
        register(ConfigItem(key, name, ConfigType.ENUM, default, description, options = options, section = section))

    /**
     * An item setting: reads/writes an item **name** [String] exactly like [stringItem] (so plugin code is
     * unchanged), but renders as the searchable item picker (sprite + name) instead of a text box. [filter]
     * confines the choices (name substrings); [browse] shows that filtered set when the box is empty.
     */
    protected fun itemItem(key: String, name: String, default: String = "", description: String = "", section: String = "", filter: List<String> = emptyList(), browse: Boolean = false, distinct: Boolean = false): ReadWriteProperty<Any?, String> =
        register(ConfigItem(key, name, ConfigType.ITEM, default, description, section = section, filter = filter, browse = browse, distinct = distinct))

    /** An NPC setting: an NPC **name** [String] (like [stringItem]), rendered as the NPC picker. */
    protected fun npcItem(key: String, name: String, default: String = "", description: String = "", section: String = "", filter: List<String> = emptyList(), browse: Boolean = false, distinct: Boolean = false): ReadWriteProperty<Any?, String> =
        register(ConfigItem(key, name, ConfigType.NPC, default, description, section = section, filter = filter, browse = browse, distinct = distinct))

    /** A scene-object setting: an object **name** [String], rendered as the object picker (model + name). */
    protected fun objectItem(key: String, name: String, default: String = "", description: String = "", section: String = "", filter: List<String> = emptyList(), browse: Boolean = false, distinct: Boolean = false): ReadWriteProperty<Any?, String> =
        register(ConfigItem(key, name, ConfigType.OBJECT, default, description, section = section, filter = filter, browse = browse, distinct = distinct))

    /**
     * A multi-item setting: a **comma-separated** list of item names as one [String] (so existing
     * comma-separated string configs migrate unchanged), rendered as a multi-select item picker with
     * removable chips. Read/split it the same way as before (`value.split(",")`).
     */
    protected fun itemListItem(key: String, name: String, default: String = "", description: String = "", section: String = "", filter: List<String> = emptyList(), browse: Boolean = false, distinct: Boolean = false): ReadWriteProperty<Any?, String> =
        register(ConfigItem(key, name, ConfigType.ITEM_LIST, default, description, section = section, filter = filter, browse = browse, distinct = distinct))

    /** A multi-NPC setting: a comma-separated list of NPC names, rendered as a multi-select NPC picker. */
    protected fun npcListItem(key: String, name: String, default: String = "", description: String = "", section: String = "", filter: List<String> = emptyList(), browse: Boolean = false, distinct: Boolean = false): ReadWriteProperty<Any?, String> =
        register(ConfigItem(key, name, ConfigType.NPC_LIST, default, description, section = section, filter = filter, browse = browse, distinct = distinct))

    /** A multi-object setting: a comma-separated list of object names, rendered as a multi-select object picker. */
    protected fun objectListItem(key: String, name: String, default: String = "", description: String = "", section: String = "", filter: List<String> = emptyList(), browse: Boolean = false, distinct: Boolean = false): ReadWriteProperty<Any?, String> =
        register(ConfigItem(key, name, ConfigType.OBJECT_LIST, default, description, section = section, filter = filter, browse = browse, distinct = distinct))
}
