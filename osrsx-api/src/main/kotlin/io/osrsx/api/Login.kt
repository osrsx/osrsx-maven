package io.osrsx.api

/** Unattended login flow. */
interface Login {
    fun isLoggedIn(): Boolean
    /** Advance the login flow one step; true once logged in. */
    fun login(): Boolean
}
