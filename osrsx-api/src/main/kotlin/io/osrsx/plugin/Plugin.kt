package io.osrsx.plugin

import io.osrsx.api.PluginApi
import io.osrsx.api.PluginContext
import io.osrsx.config.PluginConfig

/**
 * The single, first-class automation unit — what used to be split into "scripts" and "plugins". A
 * plugin can do anything from a tiny background utility (auto-loot in a radius) to a full quest/skill
 * bot; many run concurrently. Author in Kotlin/Java by extending this and annotating with
 * [PluginDescriptor].
 *
 * Lifecycle, driven by the [PluginManager]:
 *  - [onStart] when enabled, [onStop] when disabled,
 *  - [onLoop] repeatedly on the plugin's own thread (return ms until the next call; negative = no loop).
 *
 * [ctx] (the game API) and [config] are wired before [onStart].
 *
 * UI is opt-in via capability interfaces, kept off this base so it carries no engine/UI types:
 *  - implement [HasPaint] to draw an interactive docked panel (ImGui) each frame,
 *  - implement [HasOverlay] to draw a floating, translucent, alt-draggable **interactive** overlay over
 *    the game (ImGui) — the lightweight replacement for RuneLite overlays,
 *  - implement [HasPanel] to draw a read-only RuneLite data box (the engine registers/removes it for you).
 */
abstract class Plugin : PluginApi {
    /**
     * The game API surface. Assigned by the host's plugin manager before [onStart] — never read it
     * before then, and plugin code should never assign it (the setter exists only for the host, which
     * lives in a separate module). [PluginApi] surfaces each sub-API as an unprefixed `val`
     * (`skills`, `npcs`, …) that delegates here.
     */
    override lateinit var ctx: PluginContext

    /** This plugin's settings, or null if it has none. Override to declare configuration. */
    open fun config(): PluginConfig? = null

    open fun onStart() {}
    open fun onStop() {}

    /**
     * Called when one of this plugin's settings changes ([key] is the changed [io.osrsx.config.ConfigItem]
     * key). Runs on the thread that made the change (often the UI thread) — keep it quick. Override only
     * for settings that need real setup/teardown; most can just read the live config value when needed.
     */
    open fun onConfigChanged(key: String) {}

    /** Background work; return ms until the next call, or a negative value to not loop at all. */
    open fun onLoop(): Long = NO_LOOP

    companion object {
        const val NO_LOOP = -1L
    }
}
