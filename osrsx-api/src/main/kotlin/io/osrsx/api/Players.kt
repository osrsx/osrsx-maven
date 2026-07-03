package io.osrsx.api

/**
 * The osrsx plugin API — clean, stable interfaces a plugin programs against, independent of the
 * concrete engine implementations in `io.osrsx.methods.*`. Every sub-API is reachable from
 * [PluginContext]; the implementations are the existing method classes, which now implement these
 * interfaces.
 *
 * These mirror DreamBot's accessor classes (`org.dreambot.api.methods.interactive.*`, etc.) but as
 * injectable interfaces so plugins, tests, and tooling never touch the engine internals directly.
 */

/** Players in the loaded scene + the local player. */
interface Players {
    fun localPlayer(): Player?
    fun all(): List<Player>
    fun all(predicate: (Player) -> Boolean): List<Player>
    fun closest(predicate: (Player) -> Boolean): Player?
    fun closest(name: String): Player?
}
