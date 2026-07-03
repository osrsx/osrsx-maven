package io.osrsx.config

/**
 * The value type of a [ConfigItem] — drives both persistence parsing and the auto-generated UI widget.
 *
 * The picker types all persist as a plain [String] (so a plugin reads them exactly like [STRING]) but
 * render as searchable, sprite/model-backed pickers instead of a raw text box:
 *  - [ITEM] / [NPC] / [OBJECT]              — a single item / NPC / scene-object **name**.
 *  - [ITEM_LIST] / [NPC_LIST] / [OBJECT_LIST] — a **comma-separated** name list (multi-select).
 */
enum class ConfigType { BOOL, INT, STRING, ENUM, ITEM, NPC, OBJECT, ITEM_LIST, NPC_LIST, OBJECT_LIST }

/**
 * One per-plugin setting: its storage [key], display [name]/[description], [type], [default], and
 * (for INT) a slider [min]/[max], or (for ENUM) the allowed [options]. A plugin declares its settings as
 * the list of these it registers (via [PluginConfig] delegates) — the analogue of a RuneLite
 * `@ConfigItem`, but as data so the same schema drives the config panel and persistence.
 */
data class ConfigItem(
    val key: String,
    val name: String,
    val type: ConfigType,
    val default: Any?,
    val description: String = "",
    val min: Int = 0,
    val max: Int = 0,
    val options: List<String> = emptyList(),
    /** Optional sub-section this item is grouped under in the config UI ("" = top level). */
    val section: String = "",
    /**
     * For a picker type ([ITEM]/[NPC]/[OBJECT] and their lists): restrict the choices to entries whose name
     * contains any of these terms (case-insensitive). Empty = the whole catalog. E.g. a Woodcutter tree
     * setting can be confined to `["tree","oak","willow","yew",…]`.
     */
    val filter: List<String> = emptyList(),
    /** For a picker type: show the (filtered) list when the search box is empty, instead of recents — so a
     *  small filtered set (e.g. the handful of tree objects) can be browsed without typing. */
    val browse: Boolean = false,
    /** For a picker type: collapse entries that share a name to a single result. Useful for objects, where
     *  many ids share one name (all the "Rocks"/"Basalt") and the value is stored by name anyway. */
    val distinct: Boolean = false,
)
