package io.osrsx.api


/** Magic / spell casting. */
interface Magic {
    fun spellbook(): Spellbook
    fun spellWidget(spell: String): Widget?
    fun canCast(spell: String): Boolean
    fun cast(spell: String): Boolean
}
