package io.osrsx.api

/**
 * Authoring sugar over [PluginContext]: one trivial `val` per accessor so a plugin body reads
 * `skills.real(Skill.HITPOINTS)` / `npcs.closest("Goblin")` instead of `ctx.skills().…` / `ctx.npcs().…`.
 *
 * These are **interface default getters**, deliberately not extension `val`s, so they are:
 *  - always in scope in a [Plugin] subclass with no import;
 *  - Java-visible (`getSkills()`, `getNpcs()`, …) — the SDK supports Java plugins, which can't see Kotlin
 *    extensions;
 *  - zero-burden — trivial default bodies, nothing to implement.
 *
 * [ctx] stays public, so anything not surfaced here (or any Java author) still calls `ctx.foo()`.
 * Purely additive — every getter delegates straight to the matching [PluginContext] accessor.
 *
 * A drift-guard test asserts every [PluginContext] accessor has a matching getter here, so a new
 * accessor added without sugar fails CI (the codebase's "no hand-maintained lists" convention).
 */
interface PluginApi {
    /** The game API surface every getter below delegates to. */
    val ctx: PluginContext

    // ---- scene queries ----
    val players: Players get() = ctx.players()
    val npcs: Npcs get() = ctx.npcs()
    val objects: GameObjects get() = ctx.objects()
    val groundItems: GroundItems get() = ctx.groundItems()

    // ---- containers ----
    val inventory: Inventory get() = ctx.inventory()
    val equipment: Equipment get() = ctx.equipment()
    val bank: Bank get() = ctx.bank()

    // ---- player state ----
    val skills: Skills get() = ctx.skills()
    val prayers: Prayers get() = ctx.prayers()
    val magic: Magic get() = ctx.magic()
    val combat: Combat get() = ctx.combat()
    val emotes: Emotes get() = ctx.emotes()
    val minigames: Minigames get() = ctx.minigames()

    // ---- interaction / interface state ----
    val widgets: Widgets get() = ctx.widgets()
    val tabs: Tabs get() = ctx.tabs()
    val dialogues: Dialogues get() = ctx.dialogues()

    // ---- movement ----
    val walking: Walking get() = ctx.walking()
    val webWalking: WebWalker get() = ctx.webWalking()
    val camera: Camera get() = ctx.camera()
    val locations: Locations get() = ctx.locations()

    // ---- session ----
    val login: Login get() = ctx.login()

    // ---- platform ----
    val overlays: Overlays get() = ctx.overlays()
    val events: Events get() = ctx.events()
    val services: ServiceRegistry get() = ctx.services()
    val coordination: Coordination get() = ctx.coordination()
    val cameraControl: CameraControl get() = ctx.cameraControl()
    val menu: Menu get() = ctx.menu()
    val prices: Prices get() = ctx.prices()
    val items: ItemCatalog get() = ctx.items()
    val itemResolver: ItemResolver get() = ctx.itemResolver()
    val toolbelt: Toolbelt get() = ctx.toolbelt()
    val drops: Drops get() = ctx.drops()
    val worlds: Worlds get() = ctx.worlds()
    val bankCache: BankCache get() = ctx.bankCache()
    val dataStore: DataStore get() = ctx.dataStore()
    val input: Input get() = ctx.input()
    val humanizers: Humanizers get() = ctx.humanizers()
    val grandExchange: GrandExchange get() = ctx.grandExchange()

    // ---- convenience shorthands ----
    /** The local player, or null when not logged in / not yet loaded. */
    val me: Player? get() = ctx.players().localPlayer()

    /** The local player's tile, or null when not logged in / not yet loaded. */
    val myTile: Tile? get() = me?.tile()

    /**
     * A per-plugin scoped logger (tag = this plugin's class name): `log.i("chopping oak")` instead of the
     * engine `Log.i("tag", …)`. In-client the lines flow to stdout, the Logs panel, the debug stream and
     * the log file — everywhere engine logs go. See [io.osrsx.plugin.PluginLog].
     */
    val log: io.osrsx.plugin.PluginLog get() = io.osrsx.plugin.PluginLog(this.javaClass.simpleName)
}

/**
 * Terse event subscription inside a plugin body — `on<GameTick> { … }` instead of
 * `events.subscribe<GameTick> { … }`. Returns the [Subscription] to unsubscribe manually; for handlers
 * that should live for the plugin's whole lifetime, prefer an `@Subscribe`-annotated method (auto-removed
 * on stop).
 */
inline fun <reified T : Any> PluginApi.on(crossinline handler: (T) -> Unit): Subscription =
    events.subscribe(T::class.java) { handler(it) }
