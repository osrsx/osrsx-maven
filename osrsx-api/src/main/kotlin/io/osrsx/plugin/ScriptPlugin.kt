package io.osrsx.plugin

/** Thrown internally to unwind a running [ScriptPlugin.run] when the plugin stops. Authors don't catch it. */
class ScriptStopped internal constructor() : RuntimeException()

/**
 * A [Plugin] written as a **linear script** rather than a re-entrant `onLoop` state machine: override
 * [run] and write the automation top-to-bottom, blocking with [sleep] / [waitUntil]. The body runs once on
 * its own thread; a plugin stop unwinds it cleanly (a mid-wait `sleep`/`waitUntil` throws to exit).
 *
 * ```
 * @PluginDescriptor(name = "Chopper", …)
 * class Chopper : ScriptPlugin() {
 *     override fun run() {
 *         while (!isStopping) {
 *             if (inventory.isFull) { inventory.dropAll("Logs"); continue }
 *             val tree = objects.closest("Tree", "Chop down") ?: run { sleep(800); continue }
 *             tree.interact("Chop down")
 *             waitUntil(5_000) { me?.animation != -1 }              // started chopping
 *             waitUntil(30_000) { me?.animation == -1 || inventory.isFull } // done / full
 *         }
 *     }
 * }
 * ```
 *
 * This is the sequential counterpart to the [Task]/[TaskScript] DSL — pick whichever reads best for the
 * plugin. Each game-API call marshals to the client thread itself (as it does from `onLoop`), so the
 * script body needs no special threading. Note the script runs at the default engine-lease priority.
 */
abstract class ScriptPlugin : Plugin() {

    @Volatile private var scriptThread: Thread? = null
    @Volatile private var stopping = false

    /** The linear automation. Runs once on its own thread; block with [sleep] / [waitUntil]. Returning ends it. */
    abstract fun run()

    /** Optional setup/teardown around the script, run on the caller's start/stop thread (not the script thread). */
    open fun onScriptStart() {}
    open fun onScriptStop() {}

    final override fun onStart() {
        stopping = false
        onScriptStart()
        scriptThread = Thread({
            try {
                run()
            } catch (_: ScriptStopped) {
                // clean stop — the plugin was disabled mid-script
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            } catch (e: Throwable) {
                log.e("script crashed", e)
            }
        }, "osrsx-script-${javaClass.simpleName}").apply { isDaemon = true; start() }
    }

    final override fun onStop() {
        stopping = true
        scriptThread?.let { it.interrupt(); runCatching { it.join(2_000) } }
        scriptThread = null
        onScriptStop()
    }

    /** The sequential model drives everything from [run]; there is no re-entrant loop. */
    final override fun onLoop(): Long = NO_LOOP

    /** True once the plugin is stopping — check it in your own long loops to exit cooperatively. */
    protected val isStopping: Boolean get() = stopping || Thread.currentThread().isInterrupted

    /** Sleep [ms] (interruptibly); unwinds the script cleanly if the plugin stops mid-sleep. */
    protected fun sleep(ms: Long) {
        checkStop()
        if (ms <= 0) return
        try {
            Thread.sleep(ms)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw ScriptStopped()
        }
    }

    /**
     * Block until [condition] is true (→ true) or [timeoutMs] elapses (→ false), polling every [pollMs].
     * Unwinds the script cleanly if the plugin stops while waiting.
     */
    protected fun waitUntil(timeoutMs: Long = 10_000, pollMs: Long = 50, condition: () -> Boolean): Boolean {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            checkStop()
            if (condition()) return true
            sleep(pollMs)
        }
        return condition() // one final check at the deadline
    }

    private fun checkStop() {
        if (isStopping) throw ScriptStopped()
    }
}
