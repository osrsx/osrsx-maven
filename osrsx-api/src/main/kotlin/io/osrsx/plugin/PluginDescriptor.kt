package io.osrsx.plugin

/**
 * Required metadata for a Java/Kotlin [Plugin] — the analogue of RuneLite's `@PluginDescriptor`. The
 * [PluginManager] discovers in-tree and external-jar plugin classes by this annotation.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class PluginDescriptor(
    val name: String,
    val description: String = "",
    val author: String = "",
    val version: String = "1.0",
    val tags: Array<String> = [],
    /** Enable automatically on first discovery (otherwise the user enables it from the Plugin Manager). */
    val enabledByDefault: Boolean = false,
    /** Default scheduler priority for this plugin's actions (used by the Phase 3 control arbiter). */
    val defaultPriority: Int = 0,
)
