package io.osrsx.plugin

import io.osrsx.api.BreakManager
import io.osrsx.api.PluginApi
import io.osrsx.api.get
import kotlin.random.Random

/**
 * One opt-in antiban behavior. [maybeIdle] runs at the top of a plugin's loop, before it acts: return a
 * number of milliseconds to idle *instead* of acting (a break / fatigue pause / micro-hesitation), or null
 * to let the plugin proceed. Behaviors are pure and composable — the engine already humanizes the cursor
 * and camera in the background; these govern loop-level *timing*.
 */
fun interface AntibanBehavior {
    fun maybeIdle(api: PluginApi): Long?
}

/**
 * A composable set of [AntibanBehavior]s a plugin HOLDS and consults each loop, instead of re-implementing
 * antiban timing per plugin:
 *
 * ```
 * class MyPlugin : Plugin() {
 *     private val antiban = Antiban(this, Breaks(), Fatigue(), MicroPause())
 *     override fun onLoop(): Long {
 *         antiban.idle()?.let { return it }   // on break / fatigued / hesitating → idle
 *         … act …
 *     }
 * }
 * ```
 *
 * Behaviors are consulted in order; the first that returns a delay wins. Coordinated behaviors ([Breaks])
 * go through the shared [BreakManager] service, so breaks are account-wide, not per-plugin.
 */
class Antiban(private val api: PluginApi, private vararg val behaviors: AntibanBehavior) {
    /** The idle delay the first triggering behavior wants, or null to proceed with the plugin's action. */
    fun idle(): Long? {
        for (behavior in behaviors) behavior.maybeIdle(api)?.let { return it }
        return null
    }
}

private fun rand(range: LongRange): Long = Random.nextLong(range.first, range.last + 1)

/**
 * Respect account-wide breaks: idle while the shared [BreakManager] says we're on break. No-op if no
 * `BreakManager` is published. This is the behavior that makes breaks apply to the whole account.
 */
class Breaks(private val onBreakDelayMs: LongRange = 2_000L..5_000L) : AntibanBehavior {
    override fun maybeIdle(api: PluginApi): Long? {
        val breaks = api.services.get<BreakManager>() ?: return null
        return if (breaks.onBreak()) rand(onBreakDelayMs) else null
    }
}

/**
 * Simulated fatigue: an occasional micro-pause whose likelihood grows with session length, so a long
 * session drifts slower — the way a tiring human does. [sessionStartMs] defaults to construction time.
 */
class Fatigue(
    private val sessionStartMs: Long = System.currentTimeMillis(),
    private val baseChance: Double = 0.02,
    private val pauseMs: LongRange = 800L..3_000L,
    private val now: () -> Long = System::currentTimeMillis,
) : AntibanBehavior {
    override fun maybeIdle(api: PluginApi): Long? {
        val hours = (now() - sessionStartMs).coerceAtLeast(0) / 3_600_000.0
        val chance = (baseChance * (1.0 + hours)).coerceAtMost(0.5)
        return if (Random.nextDouble() < chance) rand(pauseMs) else null
    }
}

/** A small random hesitation before acting, [chance] of the time — the human "not a metronome" jitter. */
class MicroPause(
    private val chance: Double = 0.03,
    private val pauseMs: LongRange = 300L..1_200L,
) : AntibanBehavior {
    override fun maybeIdle(api: PluginApi): Long? =
        if (Random.nextDouble() < chance) rand(pauseMs) else null
}
