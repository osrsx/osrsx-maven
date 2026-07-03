package io.osrsx.api

/** NPC/player dialogue handling. */
interface Dialogues {
    fun inDialogue(): Boolean
    fun canContinue(): Boolean
    fun continueDialogue(): Boolean
    fun continueWithSpace(): Boolean
    /** Advance a continue prompt the way a player does: Space nearly always, an occasional click. */
    fun continueAuto(): Boolean
    fun getOptions(): List<String>
    fun chooseOption(text: String): Boolean
    fun chooseOption(index: Int): Boolean
    /** The accept choice in an open confirmation prompt ("Yes." / "Ok" / "Pay …"), or null when the
     *  current options hold none. Question/title rows and negatives ("No.", "Cancel.") never match. */
    fun affirmativeOption(): String?
    /** Click the [affirmativeOption] of an open confirmation prompt. False when there is none. */
    fun confirm(): Boolean
    /** Advance a dialogue one step: continue if a continue prompt is up, else [confirm]. */
    fun advance(): Boolean

    /** True while the "make" / production interface ("How many would you like to make?") is open — the
     *  skill-multi widget that cook-all / fletch-all / craft-all use, distinct from an NPC continue prompt. */
    fun makeOpen(): Boolean

    /**
     * Pick a quantity on the open "make" / production interface and start making. [all] selects the "All"
     * preset (the maximum) — the common cook-all / fletch-all case; otherwise the exact [amount] is chosen
     * (its 1/5/10 preset, or the custom "X" prompt for other values). Produces the interface's first/default
     * item. Returns false when the make interface isn't open.
     */
    fun makeQuantity(all: Boolean = true, amount: Int = 0): Boolean
}
