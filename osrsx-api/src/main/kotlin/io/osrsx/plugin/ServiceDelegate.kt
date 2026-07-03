package io.osrsx.plugin

import io.osrsx.api.PluginApi
import io.osrsx.api.ServiceRegistry
import io.osrsx.api.onPublish
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Property delegates that bind a plugin field to a [ServiceRegistry] capability, order-independently:
 *
 * ```
 * class MyPlugin : Plugin() {
 *     private val bank by service<BankingService>()          // BankingService? — null until published
 *     private val breaks by requireService<BreakManager>()   // BreakManager  — throws if never published
 * }
 * ```
 *
 * On first read the binding looks the service up AND subscribes to future publications (via
 * [ServiceRegistry.onPublish]), so it resolves whether the provider plugin started before or after this one
 * — no start-order coupling, no re-lookup per access. Reads defer to first access, so the delegate is safe
 * as a field initializer even though `ctx` isn't wired until after construction.
 */
class ServiceBinding<T : Any>(private val type: Class<T>) {
    @Volatile private var cached: T? = null
    @Volatile private var bound = false

    val typeName: String get() = type.simpleName

    fun get(api: PluginApi): T? {
        bind(api.services)
        return cached
    }

    @Synchronized
    private fun bind(registry: ServiceRegistry) {
        if (bound) return
        cached = registry.get(type)
        registry.onPublish(type) { cached = it } // reactive: latch a later (or replacement) publication
        bound = true
    }
}

/** Nullable service delegate — resolves order-independently, null until a provider publishes. */
class OptionalService<T : Any>(private val binding: ServiceBinding<T>) : ReadOnlyProperty<PluginApi, T?> {
    override fun getValue(thisRef: PluginApi, property: KProperty<*>): T? = binding.get(thisRef)
}

/** Non-null service delegate — throws on read if no matching service has been published. */
class RequiredService<T : Any>(private val binding: ServiceBinding<T>) : ReadOnlyProperty<PluginApi, T> {
    override fun getValue(thisRef: PluginApi, property: KProperty<*>): T = binding.get(thisRef)
        ?: error("Required service ${binding.typeName} requested by '${property.name}' but none is published")
}

/** Reactive, order-independent service delegate — `val bank by service<BankingService>()` (null until published). */
inline fun <reified T : Any> service(): OptionalService<T> = OptionalService(ServiceBinding(T::class.java))

/**
 * Like [service] but non-null: reading it throws if no matching service is published. Use for a capability
 * the plugin can't run without (e.g. one published at bootstrap). Prefer [service] otherwise.
 */
inline fun <reified T : Any> requireService(): RequiredService<T> = RequiredService(ServiceBinding(T::class.java))
