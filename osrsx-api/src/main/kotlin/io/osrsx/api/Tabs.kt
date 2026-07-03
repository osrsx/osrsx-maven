package io.osrsx.api

/** The sidebar tabs. */
interface Tabs {
    fun current(): Tab?
    fun isOpen(tab: Tab): Boolean
    fun open(tab: Tab): Boolean
}
