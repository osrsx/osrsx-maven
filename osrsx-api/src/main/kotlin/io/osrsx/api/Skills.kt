package io.osrsx.api


/** Skill levels and experience. */
interface Skills {
    fun boosted(skill: Skill): Int
    fun real(skill: Skill): Int
    fun experience(skill: Skill): Int
    fun experienceToNextLevel(skill: Skill): Int
    fun totalLevel(): Int

    /** Current hitpoints as a percentage (0-100) of your max HP: `boosted(HITPOINTS) * 100 / real(HITPOINTS)`.
     *  Returns 100 when max HP is unreadable (guards divide-by-zero) so a "below X% → eat" check never fires
     *  spuriously. The canonical accessor for eat-at logic — don't hand-derive HP% at the call site. */
    fun hitpointsPercent(): Int
}
