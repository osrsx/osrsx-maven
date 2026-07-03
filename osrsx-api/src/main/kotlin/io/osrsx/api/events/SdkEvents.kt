package io.osrsx.api.events

import io.osrsx.api.Npc
import io.osrsx.api.SceneEntity
import io.osrsx.api.Skill

/**
 * First-class, SDK-owned game events. Plugins subscribe to THESE (via `events.subscribe<GameTick>{}`,
 * `on<GameTick>{}`, or `@Subscribe`) instead of the raw RuneLite event classes, so a plugin's event handlers
 * carry no engine dependency — the engine adapts RuneLite events into these at the boundary.
 * Every payload is an SDK type ([SceneEntity]/[Npc]/[Skill]/enum) or a primitive.
 *
 * Handlers for these run on the client thread as the event fires (keep them quick and non-blocking).
 */

/** A game tick (~600ms). [tick] is the client tick count. */
data class GameTick(val tick: Int)

/** A client frame tick (every rendered frame — higher frequency than [GameTick]). */
class ClientTick

/** The client's high-level state changed (login screen, loading, logged in, hopping, …). */
data class GameStateChanged(val state: GameStateType)

/** SDK mirror of the client's high-level game state — no engine GameState on the boundary. */
enum class GameStateType { STARTING, LOGIN_SCREEN, AUTHENTICATOR, LOADING, LOGGED_IN, CONNECTION_LOST, HOPPING, UNKNOWN }

/** XP gained (or a level change) in a skill — fired as a stat updates. */
data class XpGained(val skill: Skill, val xp: Int, val level: Int)

/** An actor's animation changed. [source] is the NPC/Player now playing [animation] (-1 = idle). */
data class AnimationChanged(val source: SceneEntity, val animation: Int)

/** An actor started or stopped interacting with (targeting) another. [target] is null when it stopped. */
data class InteractingChanged(val source: SceneEntity, val target: SceneEntity?)

/** A hitsplat landed on [target]. [mine] is true when it's damage I dealt/received; [type] is the raw id. */
data class HitsplatApplied(val target: SceneEntity, val amount: Int, val mine: Boolean, val type: Int)

/** A chat message. [type] is the RuneLite message-type name (e.g. "GAMEMESSAGE", "PUBLICCHAT"). */
data class ChatMessage(val type: String, val sender: String?, val message: String)

/** A menu option was clicked. Pure strings/id — no engine MenuEntry on the boundary. */
data class MenuOptionClicked(val option: String, val target: String, val id: Int)

/** A varbit or varp changed. [varbitId] is -1 for a varp change; [varpId] is -1 for a varbit change. */
data class VarbitChanged(val varbitId: Int, val varpId: Int, val value: Int)

/** An item container changed (inventory, bank, equipment, …). [containerId] identifies which one. */
data class ItemContainerChanged(val containerId: Int)

/** An NPC spawned into the loaded scene. */
data class NpcSpawned(val npc: Npc)

/** An NPC despawned from the loaded scene. */
data class NpcDespawned(val npc: Npc)
