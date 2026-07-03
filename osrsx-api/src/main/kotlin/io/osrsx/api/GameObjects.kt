package io.osrsx.api

/** Scene objects (game/wall/decorative/ground objects) in the loaded scene. */
interface GameObjects {
    fun all(): List<SceneObject>
    fun all(predicate: (SceneObject) -> Boolean): List<SceneObject>
    fun closest(predicate: (SceneObject) -> Boolean): SceneObject?
    fun closest(name: String): SceneObject?
    /** Nearest object named [name] that currently offers menu [action] — skips identically-named scenery
     *  (e.g. a decorative "Tree" with no "Chop down"). Both compared case-insensitively. */
    fun closest(name: String, action: String): SceneObject?
    fun closest(vararg ids: Int): SceneObject?
}
