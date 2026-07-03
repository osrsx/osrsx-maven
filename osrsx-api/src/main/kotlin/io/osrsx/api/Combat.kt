package io.osrsx.api

/** Combat helpers: attacking, in-combat state, special attack. */
interface Combat {
    fun attack(npc: Npc): Boolean
    fun isInCombat(): Boolean
    fun specialEnergy(): Int
    fun isSpecialEnabled(): Boolean
    fun toggleSpecial(): Boolean
    fun enableSpecialIf(minEnergy: Int = 50): Boolean
}
