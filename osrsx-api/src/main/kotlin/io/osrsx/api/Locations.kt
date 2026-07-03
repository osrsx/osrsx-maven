package io.osrsx.api

/** World-wide object/NPC location lookup (backed by the offline SQLite store). */
interface Locations {
    fun all(name: String): List<Tile>
    fun nearest(name: String): Tile?
    fun allObjects(name: String): List<Tile>
    fun allObjects(id: Int): List<Tile>
    fun nearestObject(name: String): Tile?
    fun nearestObject(id: Int): Tile?
    fun allNpcs(name: String): List<Tile>
    fun allNpcs(id: Int): List<Tile>
    fun nearestNpc(name: String): Tile?
    fun nearestNpc(id: Int): Tile?

    /**
     * The nearest tile of object [name] that sits in a CLUSTER — at least [minCount] of that object within
     * [radius] tiles — so a skiller travels to a real patch (a forest, a mining cluster) rather than a lone
     * catalogued object that merely happens to be closest (e.g. an ornamental tree by the GE). Falls back
     * to the nearest single object when no cluster qualifies.
     */
    fun nearestCluster(name: String, minCount: Int, radius: Int): Tile?
}
