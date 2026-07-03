package io.osrsx.api

/**
 * A shared, typed registry that lets plugins expose capabilities to one another (the analogue of
 * RuneLite's Guice services, but plugin-scoped). One plugin **publishes** a service — by interface type
 * or by name — and any other plugin **looks it up** and calls it.
 *
 * Lookups (`get`/`getAll`) search across every plugin's publications, so this is the inter-plugin
 * communication seam. A plugin's publications are automatically removed when it stops (the manager
 * clears them), so a stale service never lingers after its owner is disabled.
 *
 * Each plugin receives a view of the same underlying registry; publishing records the owning plugin.
 */
interface ServiceRegistry {
    /** Publish [service] under interface [type]; replaces this plugin's previous service of that type. */
    fun <T : Any> publish(type: Class<T>, service: T)

    /**
     * Publish [service] under interface [type], tagged with a strict SemVer [version] so consumers can
     * refuse an incompatible provider via [get] with a `minVersion`. Replaces this plugin's previous
     * service of that type.
     */
    fun <T : Any> publish(type: Class<T>, service: T, version: String)

    /** The first published service assignable to [type], or null. */
    fun <T : Any> get(type: Class<T>): T?

    /**
     * The first published service assignable to [type] whose declared version satisfies [minVersion] —
     * same SemVer major and at least as new (`>= minVersion`). A provider published without a version,
     * or one that is older / a different major, is skipped. Returns null if none qualifies. Lets a
     * consumer refuse a stale provider instead of binding to an incompatible interface.
     */
    fun <T : Any> get(type: Class<T>, minVersion: String): T?

    /** Every published service assignable to [type] (across all plugins). */
    fun <T : Any> getAll(type: Class<T>): List<T>

    /**
     * Register [callback] to run whenever a service assignable to [type] is published — **including any
     * already published** at registration time. Makes cross-plugin binding order-independent: it fires
     * regardless of whether the provider plugin started before or after this one. The callback is scoped
     * to the subscribing plugin and dropped when that plugin is disabled.
     */
    fun <T : Any> onPublish(type: Class<T>, callback: (T) -> Unit)

    /**
     * A future that resolves with the first service assignable to [type] — immediately if one is already
     * published, otherwise when one is next published. The order-independent counterpart to [get] (which
     * returns null if the provider hasn't started yet). The pending await is scoped to the subscribing
     * plugin and dropped when that plugin is disabled.
     */
    fun <T : Any> getOrAwait(type: Class<T>): java.util.concurrent.CompletableFuture<T>

    /** Publish [service] under a string [name] — for name-keyed (cross-plugin) lookups. */
    fun publish(name: String, service: Any)

    /** The service published under [name], or null. */
    fun get(name: String): Any?
}

/** Reified sugar for [ServiceRegistry.get] — `services.get<PluginManager>()` over the `::class.java` form. */
inline fun <reified T : Any> ServiceRegistry.get(): T? = get(T::class.java)

/** Reified sugar for [ServiceRegistry.getAll]. */
inline fun <reified T : Any> ServiceRegistry.getAll(): List<T> = getAll(T::class.java)

/** Reified sugar for [ServiceRegistry.publish] by type — `services.publish(myService)`. */
inline fun <reified T : Any> ServiceRegistry.publish(service: T): Unit = publish(T::class.java, service)

/** Reified sugar for the versioned [ServiceRegistry.publish] — `services.publish(myService, "1.2.0")`. */
inline fun <reified T : Any> ServiceRegistry.publish(service: T, version: String): Unit =
    publish(T::class.java, service, version)

/** Reified sugar for the version-gated [ServiceRegistry.get] — `services.get<BankingService>("1.0.0")`. */
inline fun <reified T : Any> ServiceRegistry.get(minVersion: String): T? = get(T::class.java, minVersion)

/** Reified sugar for [ServiceRegistry.onPublish] — `services.onPublish<BankingService> { … }`. */
inline fun <reified T : Any> ServiceRegistry.onPublish(noinline callback: (T) -> Unit): Unit =
    onPublish(T::class.java, callback)

/** Reified sugar for [ServiceRegistry.getOrAwait] — `services.getOrAwait<BankingService>()`. */
inline fun <reified T : Any> ServiceRegistry.getOrAwait(): java.util.concurrent.CompletableFuture<T> =
    getOrAwait(T::class.java)
