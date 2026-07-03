package io.osrsx.testkit

import io.osrsx.api.Bank
import io.osrsx.api.BankCache
import io.osrsx.api.Camera
import io.osrsx.api.CameraControl
import io.osrsx.api.Combat
import io.osrsx.api.Coordination
import io.osrsx.api.DataStore
import io.osrsx.api.Dialogues
import io.osrsx.api.Drops
import io.osrsx.api.Emotes
import io.osrsx.api.Equipment
import io.osrsx.api.Events
import io.osrsx.api.GameObjects
import io.osrsx.api.GrandExchange
import io.osrsx.api.GroundItems
import io.osrsx.api.Humanizers
import io.osrsx.api.Input
import io.osrsx.api.Inventory
import io.osrsx.api.ItemCatalog
import io.osrsx.api.ItemResolver
import io.osrsx.api.Locations
import io.osrsx.api.Login
import io.osrsx.api.Magic
import io.osrsx.api.Menu
import io.osrsx.api.Minigames
import io.osrsx.api.Npcs
import io.osrsx.api.Overlays
import io.osrsx.api.Players
import io.osrsx.api.PluginContext
import io.osrsx.api.Prayers
import io.osrsx.api.Prices
import io.osrsx.api.ServiceRegistry
import io.osrsx.api.Skills
import io.osrsx.api.Tabs
import io.osrsx.api.Toolbelt
import io.osrsx.api.Walking
import io.osrsx.api.WebWalker
import io.osrsx.api.Widgets
import io.osrsx.api.Worlds
import org.mockito.kotlin.mock

/**
 * A headless, fully-mocked [PluginContext] for unit-testing plugins with no live client — the SDK's
 * plugin test harness. Every accessor returns a Mockito mock exposed as a public `val` you can stub, so
 * a plugin author drives their plugin's logic and asserts on the game API without a running game:
 *
 * ```
 * val ctx = TestContext()
 * whenever(ctx.skills.hitpointsPercent()).thenReturn(30)
 * whenever(ctx.inventory.contains("Cooked chicken")).thenReturn(true)
 *
 * val plugin = FighterPlugin().apply { ctx = this@run.ctx }   // ctx is public on Plugin
 * plugin.onLoop()
 *
 * verify(ctx.inventory).interact("Cooked chicken", "Eat")
 * ```
 *
 * The methods API was designed mockable behind `io.osrsx.game.GameClient`; this exposes that testability
 * to plugin authors. Add it to a plugin project as `testImplementation project(":osrsx-testkit")` (or the
 * published `io.osrsx:osrsx-testkit`). Each surface is a constructor parameter defaulting to a fresh mock,
 * so you can also inject a hand-written fake for any surface: `TestContext(inventory = MyFakeInventory())`.
 */
open class TestContext(
    val players: Players = mock(),
    val npcs: Npcs = mock(),
    val objects: GameObjects = mock(),
    val groundItems: GroundItems = mock(),
    val drops: Drops = mock(),
    val inventory: Inventory = mock(),
    val equipment: Equipment = mock(),
    val bank: Bank = mock(),
    val skills: Skills = mock(),
    val prayers: Prayers = mock(),
    val magic: Magic = mock(),
    val combat: Combat = mock(),
    val emotes: Emotes = mock(),
    val minigames: Minigames = mock(),
    val widgets: Widgets = mock(),
    val tabs: Tabs = mock(),
    val dialogues: Dialogues = mock(),
    val walking: Walking = mock(),
    val webWalking: WebWalker = mock(),
    val camera: Camera = mock(),
    val locations: Locations = mock(),
    val login: Login = mock(),
    val overlays: Overlays = mock(),
    val events: Events = mock(),
    val services: ServiceRegistry = mock(),
    val coordination: Coordination = mock(),
    val cameraControl: CameraControl = mock(),
    val menu: Menu = mock(),
    val prices: Prices = mock(),
    val items: ItemCatalog = mock(),
    val itemResolver: ItemResolver = mock(),
    val toolbelt: Toolbelt = mock(),
    val worlds: Worlds = mock(),
    val bankCache: BankCache = mock(),
    val dataStore: DataStore = mock(),
    val input: Input = mock(),
    val humanizers: Humanizers = mock(),
    val grandExchange: GrandExchange = mock(),
) : PluginContext {
    override fun players() = players
    override fun npcs() = npcs
    override fun objects() = objects
    override fun groundItems() = groundItems
    override fun drops() = drops
    override fun inventory() = inventory
    override fun equipment() = equipment
    override fun bank() = bank
    override fun skills() = skills
    override fun prayers() = prayers
    override fun magic() = magic
    override fun combat() = combat
    override fun emotes() = emotes
    override fun minigames() = minigames
    override fun widgets() = widgets
    override fun tabs() = tabs
    override fun dialogues() = dialogues
    override fun walking() = walking
    override fun webWalking() = webWalking
    override fun camera() = camera
    override fun locations() = locations
    override fun login() = login
    override fun overlays() = overlays
    override fun events() = events
    override fun services() = services
    override fun coordination() = coordination
    override fun cameraControl() = cameraControl
    override fun menu() = menu
    override fun prices() = prices
    override fun items() = items
    override fun itemResolver() = itemResolver
    override fun toolbelt() = toolbelt
    override fun worlds() = worlds
    override fun bankCache() = bankCache
    override fun dataStore() = dataStore
    override fun input() = input
    override fun humanizers() = humanizers
    override fun grandExchange() = grandExchange
}
