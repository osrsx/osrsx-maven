package io.osrsx.api

/**
 * One bus for both **RuneLite game events** and **custom plugin events**.
 *
 * Game events: subscribe by their RuneLite type (`net.runelite.api.events.*`); handlers run
 * **synchronously on the client thread** as the event fires, so they can safely read widgets and mutate
 * the menu (e.g. add a right-click action on `MenuOpened`). Keep them quick and non-blocking.
 *
 * Custom events let plugins talk to each other: post a typed object (received by every subscriber of an
 * assignable type) or a named payload (a name-keyed channel). A custom-event handler runs on the
 * **posting** thread, so treat it like any cross-thread callback.
 */
interface Events {
    /** Register [handler] for events of [type] (a game-event class or a custom type); returns a [Subscription]. */
    fun <T : Any> subscribe(type: Class<T>, handler: EventHandler<T>): Subscription

    /** Register [handler] for custom events posted under [name]; returns a [Subscription]. */
    fun subscribe(name: String, handler: EventHandler<Any>): Subscription

    /** Post a custom [event] object to every subscriber of an assignable type. */
    fun post(event: Any)

    /** Post a custom [payload] to every subscriber of the channel [name]. */
    fun post(name: String, payload: Any)
}

/**
 * Reified sugar for [Events.subscribe] — `events.subscribe<GameTick> { … }` instead of
 * `events.subscribe(GameTick::class.java) { … }`. The event type comes from the type argument, so the
 * `::class.java` boilerplate disappears at every call site.
 */
inline fun <reified T : Any> Events.subscribe(crossinline handler: (T) -> Unit): Subscription =
    subscribe(T::class.java) { handler(it) }

/** A single-method handler so callers can pass a lambda. */
fun interface EventHandler<T> {
    fun handle(event: T)
}

/** Handle returned by [Events.subscribe]; call [unsubscribe] to stop receiving events. */
fun interface Subscription {
    fun unsubscribe()
}
