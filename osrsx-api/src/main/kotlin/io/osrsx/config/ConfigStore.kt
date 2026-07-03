package io.osrsx.config

/**
 * A typed get/set view over one plugin's config [group] — the persistence seam a [PluginConfig] writes
 * through. Declared in the SDK (plugins see it via `PluginConfig.store`); the client supplies the
 * concrete, file-backed implementation (`io.osrsx.config.ConfigStoreImpl`, backed by `ConfigManager`).
 */
interface ConfigStore {
    /** The config group this view is scoped to (namespaces the keys on disk). */
    val group: String

    /** The stored value for [item], parsed to its [ConfigItem.type], or [ConfigItem.default] when unset. */
    fun get(item: ConfigItem): Any?

    /** Persist [value] for [item]. */
    fun set(item: ConfigItem, value: Any?)
}
