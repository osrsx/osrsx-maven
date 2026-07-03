package io.osrsx.plugin

/**
 * Marks a [Plugin] method as an event handler. The framework auto-registers every `@Subscribe` method
 * when the plugin starts and unsubscribes them all when it stops — no manual [io.osrsx.api.Subscription]
 * bookkeeping, and no forgotten-unsubscribe leak.
 *
 * The method must take exactly ONE parameter whose type is the event to subscribe to — an SDK event
 * (`io.osrsx.api.events.*`) or a custom plugin event type. Handlers for game events run on the client
 * thread as the event fires (keep them quick).
 *
 * ```
 * @Subscribe fun onTick(e: GameTick) { … }
 * @Subscribe fun onHit(e: HitsplatApplied) { if (e.mine) … }
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Subscribe
