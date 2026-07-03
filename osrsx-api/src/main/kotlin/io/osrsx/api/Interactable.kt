package io.osrsx.api

import java.awt.Shape

/**
 * Anything a plugin can click in the world or on the screen. The SDK-facing surface of the engine's
 * interaction wrappers (the concrete impls live in `io.osrsx.wrappers` inside the client and drive the
 * virtual mouse / camera / menu selection). No `net.runelite.*` on this boundary — positions are exposed
 * as [Tile] via [SceneEntity.tile]; on-screen geometry uses the JDK's [java.awt.Shape].
 */
interface Interactable {
    /** Display name (e.g. the NPC/object/item name), or null when unknown / off-scene. */
    fun name(): String?

    /** The on-screen clickable region (convex hull / clickbox), or null if off-screen. */
    fun clickbox(): Shape?

    /** Move the mouse over this entity without clicking. */
    fun hover(): Boolean

    /**
     * Interact with this entity. With [action] null, performs the default left-click; a non-null [action]
     * (e.g. "Attack", "Bank") selects that menu option.
     */
    fun interact(action: String? = null): Boolean
}

/**
 * A world entity (NPC, object, player, ground item) — an [Interactable] with a world position and
 * distance, plus verified/multi-option interaction helpers. Concrete impls are the engine's
 * `io.osrsx.wrappers.Entity` subclasses.
 */
interface SceneEntity : Interactable {
    /** World tile of this entity, or null if not in the scene. */
    fun tile(): Tile?

    /** Chebyshev distance (tiles) from the local player; [Int.MAX_VALUE] if either is off-scene. */
    fun distance(): Int

    /**
     * A fast, VERIFIED default left-click: hover, confirm the live top menu entry really is [action] on
     * [target] (defaults to this entity's name), then left-click. No right-click fallback — returns false
     * if it can't confirm, so a tight loop can just retry next tick.
     */
    fun leftClickIfDefault(action: String, target: String? = null): Boolean

    /**
     * Right-click once and select the FIRST of [actions] (in preference order) present in the live context
     * menu — for entities whose composition actions are empty but whose live menu carries the real options.
     */
    fun interactAny(actions: List<String>, target: String? = null): Boolean
}
