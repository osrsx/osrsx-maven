package io.osrsx.api

/**
 * The sidebar tabs. Each carries the index reported by the "open tab" client var (VarClientInt 171)
 * and, where one exists, the menu action of its tab stone. Part of the public plugin API surface
 * (returned by [Tabs]); the implementation lives in `io.osrsx.methods.TabsImpl`, which owns the
 * fixed-/resizable-layout widget mapping (kept engine-side so this enum stays free of any client type).
 *
 * NOTE: the var index mapping is the long-standing OSRS order; verify against the live client when
 * wiring up an end-to-end run (it shifts occasionally with interface updates).
 */
enum class Tab(val index: Int, val action: String = "") {
    COMBAT(0, "Combat Options"),
    STATS(1, "Skills"),
    QUESTS(2),
    INVENTORY(3, "Inventory"),
    EQUIPMENT(4, "Worn Equipment"),
    PRAYER(5, "Prayer"),
    MAGIC(6, "Magic"),
    FRIENDS_CHAT(7),
    FRIENDS(8, "Friends List"),
    IGNORES(9, "Ignore List"),
    LOGOUT(10, "Logout"),
    OPTIONS(11, "Settings"),
    EMOTES(12, "Emotes"),
    MUSIC(13, "Music Player"),
}
