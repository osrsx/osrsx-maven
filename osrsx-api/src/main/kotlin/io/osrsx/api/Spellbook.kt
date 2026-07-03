package io.osrsx.api

/** The four spellbooks, by the value of the spellbook varbit (4070). Returned by [Magic]. */
enum class Spellbook(val varbitValue: Int) {
    NORMAL(0), ANCIENT(1), LUNAR(2), ARCEUUS(3);

    companion object {
        fun fromVarbit(v: Int): Spellbook = entries.firstOrNull { it.varbitValue == v } ?: NORMAL
    }
}
