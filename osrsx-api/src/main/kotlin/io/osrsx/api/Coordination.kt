package io.osrsx.api

/**
 * A cooperative, priority-aware **task queue** over the single shared avatar, so several plugins can run
 * together by taking TURNS rather than fighting over where to walk.
 *
 * The model: a plugin that has work and needs the avatar [request]s it; the director grants control to the
 * highest-priority requester (ties rotate fairly). A plugin only acts while [granted]; otherwise it waits
 * its turn. A holder that sees [shouldYield] (a strictly-higher-priority plugin is now waiting) wraps up and
 * [release]s, letting the higher one in — then re-requests to get its turn back. A plugin with no current
 * work [release]s so the next in line runs.
 *
 * Two usage styles, both first-class:
 *  - **Active participant** (e.g. the Swing Trader): `if hasWork then request(); if granted() then act end
 *    else release() end` — takes explicit turns.
 *  - **Background activity** (e.g. a skiller, the Hide Tanner): just `if shouldYield() then return wait end`
 *    at the top of the loop — it acts by default and steps aside whenever a higher-priority plugin requests.
 *
 * Requests carry the caller's loop-thread priority (from its `@PluginDescriptor`) and AUTO-EXPIRE
 * after a short TTL (refreshed each [request]), so a crashed or idle holder never deadlocks the queue —
 * everyone else resumes the moment it stops requesting. Identity is the calling plugin's loop thread, so no
 * arguments are needed.
 */
interface Coordination {
    /** Enqueue THIS plugin for avatar control at its priority; refreshes the request TTL. Call each loop while
     *  you have work and need the avatar. */
    fun request()

    /** Stop requesting and drop any grant this plugin holds — call when you've no work, or to step aside. */
    fun release()

    /** True if THIS plugin currently holds the grant (it's the highest-priority active requester). Act only
     *  while this is true. */
    fun granted(): Boolean

    /** True if a strictly-higher-priority plugin is requesting control — a holder should wrap up and [release];
     *  a background activity should pause this loop. */
    fun shouldYield(): Boolean

    /** The loop-thread name of the plugin currently holding the avatar grant, or null if it's free. */
    fun holder(): String?
}
