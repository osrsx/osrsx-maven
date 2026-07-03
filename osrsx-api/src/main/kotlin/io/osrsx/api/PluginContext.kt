package io.osrsx.api

/**
 * The single entry point a plugin (Java/Kotlin) receives — the analogue of DreamBot's
 * `MethodProvider`, but as a clean interface that exposes every sub-API through an accessor. Program
 * against this and the sub-interfaces it returns; never reach into the engine internals.
 *
 * The implementation (`io.osrsx.methods.PluginContextImpl`) delegates to the engine's `MethodContext`.
 * Platform services (scheduler, event bus, service registry, per-plugin config, overlays) are added to
 * this interface as their subsystems land in later phases.
 */
interface PluginContext {
    // ---- scene queries ----
    fun players(): Players
    fun npcs(): Npcs
    fun objects(): GameObjects
    fun groundItems(): GroundItems

    // ---- containers ----
    fun inventory(): Inventory
    fun equipment(): Equipment
    fun bank(): Bank

    // ---- player state ----
    fun skills(): Skills
    fun prayers(): Prayers
    fun magic(): Magic
    fun combat(): Combat
    fun emotes(): Emotes
    fun minigames(): Minigames

    // ---- interaction / interface state ----
    fun widgets(): Widgets
    fun tabs(): Tabs
    fun dialogues(): Dialogues

    // ---- movement ----
    fun walking(): Walking
    fun webWalking(): WebWalker
    fun camera(): Camera
    fun locations(): Locations

    // ---- session ----
    fun login(): Login

    /** Register RuneLite overlays to draw in-world / on the HUD (see [Overlays]). */
    fun overlays(): Overlays

    /** Subscribe to game + custom events, and post custom events — see [Events]. */
    fun events(): Events

    /** Publish/consume capabilities to/from other plugins — see [ServiceRegistry]. */
    fun services(): ServiceRegistry

    /** Priority-aware avatar-control hand-off between concurrent plugins — see [Coordination]. */
    fun coordination(): Coordination

    /** Partition what the background camera humanizer may do (block rotation/zoom/glances, or [CameraControl.hold]
     *  it entirely) while this plugin drives — e.g. a boss plugin holding the camera so it can't grab the mouse
     *  mid-eat. See [CameraControl]. */
    fun cameraControl(): CameraControl

    /** Dynamically swap which right-click action is the default left-click — see [Menu]. */
    fun menu(): Menu

    /** Live GE item prices (OSRS Wiki) for valuing items / GP-per-hour — see [Prices]. */
    fun prices(): Prices

    /** Static item reference data — broad bank category per item — see [ItemCatalog]. */
    fun items(): ItemCatalog

    /** Resolve an [ItemRef] to a concrete id / live item (name→id, category) — see [ItemResolver]. First-party
     *  helper: callers usually go through the ref-aware [Container]/[Equipment]/[GroundItems] surfaces instead. */
    fun itemResolver(): ItemResolver

    /** Pick the best usable tool for a skill given level + bank contents + affordable coins — see [Toolbelt]. */
    fun toolbelt(): Toolbelt

    /** What a world object/NPC yields — a tree → its logs, a rock → its ore (OSRS Wiki data) — see [Drops]. */
    fun drops(): Drops

    /** World hopping — switch the live game to another world (e.g. a quieter one, or to refresh a resource). */
    fun worlds(): Worlds

    /** Persisted per-account bank snapshot, readable anywhere (auto-captured at the bank) — see [BankCache]. */
    fun bankCache(): BankCache

    /** Per-account persistent key-value storage for plugin state — see [DataStore]. */
    fun dataStore(): DataStore

    /** Lock out physical user input while this plugin drives the game — see [Input]. */
    fun input(): Input

    /** Toggle the engine's background humanizers (idle-mouse drift) while this plugin owns the cursor —
     *  see [Humanizers]. */
    fun humanizers(): Humanizers

    /** Read GE offer slots and place/collect/abort offers — see [GrandExchange]. */
    fun grandExchange(): GrandExchange
}
