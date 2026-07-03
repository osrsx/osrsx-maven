package io.osrsx.api


/**
 * Picks the best *usable* tool for a skill given three live constraints — the player's level, what's
 * already in the bank/inventory/equipment, and what the bank's coins can afford on the GE.
 *
 * Catalogued skills are those with a single primary tool that has a meaningful "best" — an effectiveness
 * LADDER (Woodcutting axes, Mining pickaxes, Fishing harpoons, Farming secateurs, Hunter butterfly nets)
 * or a single canonical required tool (Firemaking tinderbox, Smithing hammer, Fletching knife). For the
 * harpoon line, [best] models only that tiered tool — other fishing tools (net, rod, lobster pot) are
 * method-specific and not a "best" decision. Skills with no single primary tool (Cooking, Crafting,
 * Runecraft, Agility, …) aren't modelled; [tiers] returns empty and [best] returns null for them.
 *
 * Item ids and prices are resolved live via [Prices] (the OSRS Wiki mapping) — nothing is hardcoded
 * beyond the tier table itself, so the API works offline for the owned-tool case and only needs the
 * price feed for the "what can I afford" case.
 */
interface Toolbelt {
    /**
     * The best tool for [skill] the player can use right now, considering tools already owned AND tools
     * the bank's coins could buy on the GE (whichever yields the highest tier). Null if no catalogued
     * tier is both level-appropriate and obtainable (owned or affordable). Equivalent to
     * `best(skill, allowBuy = true)`.
     */
    fun best(skill: Skill): ToolChoice?

    /**
     * As [best], but with [allowBuy] = false it considers ONLY tools already in equipment/inventory/bank
     * (never a GE purchase) — use this to decide what to withdraw without spending coins. With
     * [allowBuy] = true a buyable higher tier can win over a lower tier you already own.
     */
    fun best(skill: Skill, allowBuy: Boolean): ToolChoice?

    /** Every catalogued tool tier for [skill], best-first (highest tier first); empty if not modelled. */
    fun tiers(skill: Skill): List<ToolTier>

    // ---- worn equipment (weapons / shields / armour) ----

    /**
     * The best worn item for an equipment [slot] in a combat [style] the player can use right now —
     * same owned-or-affordable logic as [best], but over a material ladder (bronze→dragon, leather→black
     * d'hide, robes…) gated by the slot's governing skill (Attack for weapons, Defence for armour,
     * Ranged/Magic for their gear). Null if no tier is level-appropriate and obtainable, or the
     * slot/style pair isn't modelled.
     */
    fun bestGear(slot: EquipSlot, style: CombatStyle): GearChoice?
    fun bestGear(slot: EquipSlot, style: CombatStyle, allowBuy: Boolean): GearChoice?

    /** The best obtainable item for every modelled slot of [style] — a whole loadout in one call. */
    fun bestLoadout(style: CombatStyle): Map<EquipSlot, GearChoice>
    fun bestLoadout(style: CombatStyle, allowBuy: Boolean): Map<EquipSlot, GearChoice>

    /** Every catalogued tier for ([slot], [style]), best-first; empty if not modelled. */
    fun gearTiers(slot: EquipSlot, style: CombatStyle): List<GearTier>

    // ---- skilling outfits & loadouts ----

    /**
     * The skilling outfit pieces for [skill] (Graceful for Agility, Lumberjack for Woodcutting, Prospector
     * for Mining, …), each with how to obtain it ([Acquisition]) — where to buy it, the currency and cost,
     * or which minigame drops it. Empty if the skill has no modelled outfit.
     */
    fun bestOutfit(skill: Skill): List<SkillItem>

    /**
     * A full skilling loadout for [skill]: the best tool (in the WEAPON slot) PLUS every outfit piece,
     * keyed by [EquipSlot], each carrying its [Acquisition]. The one-call "best gear for this skilling
     * task" — already owned where possible, otherwise telling you exactly where and with what to get it.
     */
    fun bestSkillingLoadout(skill: Skill): Map<EquipSlot, SkillItem>
}

/** How to obtain an item right now. */
enum class AcquireVia {
    /** Already owned (equipped/inventory/bank/cache). */
    OWNED,
    /** Buy on the Grand Exchange for coins. */
    BUY_GE,
    /** Buy from a reward shop for a non-GP currency (Marks of grace, Golden nuggets, Abyssal pearls, …). */
    BUY_SHOP,
    /** A random reward earned by playing a minigame/activity (no direct purchase). */
    MINIGAME,
    /** A monster/content drop. */
    DROP,
}

/**
 * How to obtain a specific item: the method ([via]), where to go ([sourceName] + [location]), what it
 * costs ([cost] of [currency] — `currency` is "Coins" for GE, a reward-shop currency for [BUY_SHOP], or
 * "" when not purchasable), and whether you can pay that from the bank right now ([affordable], always
 * true when [OWNED]). For point-based shops (Tithe Farm points, Foundry reputation) the currency isn't a
 * bankable item, so [affordable] is false and [cost]/[currency] are informational — go earn them.
 */
data class Acquisition(
    val via: AcquireVia,
    val sourceName: String?,
    val location: Tile?,
    val currency: String,
    val cost: Int,
    val affordable: Boolean,
) {
    val owned: Boolean get() = via == AcquireVia.OWNED
}

/**
 * One recommended item for a skilling loadout — what to wear/wield in [slot] for [skill], the level it
 * needs, and how to get it ([acquisition]).
 */
data class SkillItem(
    val item: String,
    val slot: EquipSlot,
    val skill: Skill,
    val levelReq: Int,
    val acquisition: Acquisition,
)

/** Anything with a name and a level requirement — the common shape the selection logic ranks over. */
interface LevelledItem {
    val name: String
    val levelReq: Int
}

/** A worn-equipment slot (the paperdoll positions). */
enum class EquipSlot { HEAD, CAPE, AMULET, AMMO, WEAPON, BODY, SHIELD, LEGS, HANDS, FEET, RING }

/** Combat style — selects which material ladder a slot uses and which skill gates it. */
enum class CombatStyle { MELEE, RANGED, MAGIC }

/**
 * One rung of a skill's tool ladder — static reference data (no live state). [rank] orders tiers by
 * effectiveness (higher = faster); [levelReq] is the SKILL level needed to use the tool for gathering.
 *
 * Note: wielding the metal tools also needs an Attack level (e.g. 60 Attack for a dragon axe), and a
 * few top tiers are quest-locked (crystal needs Song of the Elves). Those gates are NOT checked here —
 * an axe only needs to be in the inventory to chop, so [levelReq] (the skill level) is what gates use.
 */
data class ToolTier(
    val skill: Skill,
    /** Canonical in-game item name, e.g. "Rune axe" / "Dragon pickaxe". */
    override val name: String,
    /** Skill level required to use the tool for gathering. */
    override val levelReq: Int,
    /** Effectiveness order within the skill — higher is better/faster. */
    val rank: Int,
) : LevelledItem

/**
 * One rung of an equipment ladder (e.g. "Rune platebody"): the [slot] it fills, the [style] it belongs
 * to, the [skill] + [levelReq] that gate wearing/wielding it, and its effectiveness [rank] (higher =
 * better). Static reference data. Quest gates on a few top tiers (e.g. dragon scimitar needs Monkey
 * Madness) are NOT enforced — [levelReq] is the stat gate only.
 */
data class GearTier(
    val slot: EquipSlot,
    val style: CombatStyle,
    override val name: String,
    /** Skill that gates this item — Attack (weapons), Defence (armour), Ranged/Magic for their gear. */
    val skill: Skill,
    override val levelReq: Int,
    val rank: Int,
) : LevelledItem

/**
 * Where the chosen tool comes from. [EQUIPPED]/[INVENTORY]/[BANK] are owned and confirmed live;
 * [BANK_CACHE] is owned per the last persisted bank snapshot (believed-present, worth verifying when you
 * reach the bank); [BUYABLE] needs a GE purchase. All but [BUYABLE] are already-owned (no coins needed).
 */
enum class ToolSource { EQUIPPED, INVENTORY, BANK, BANK_CACHE, BUYABLE }

/**
 * The resolved best tool for a skill: which [tier], where it [source]s from, its GE item [id], and the
 * GE [price] you'd pay if you have to buy it (0 when already owned).
 */
data class ToolChoice(
    val tier: ToolTier,
    val source: ToolSource,
    /** GE item id (for withdraw/buy), or -1 if the price mapping hasn't loaded yet. */
    val id: Int,
    /** Coins this would cost on the GE; 0 when [source] is already-owned (EQUIPPED/INVENTORY/BANK). */
    val price: Int,
) {
    /** Convenience: the tool's item name. */
    val name: String get() = tier.name
    /** Convenience: the skill level the tool requires. */
    val levelReq: Int get() = tier.levelReq
    /** True when the tool is already owned (no GE purchase needed). */
    val owned: Boolean get() = source != ToolSource.BUYABLE

    /** True when the tool must be bought on the GE (not already owned). */
    fun isBuyable(): Boolean = source == ToolSource.BUYABLE
}

/**
 * The resolved best worn item for a slot: which [tier], where it [source]s from, its GE item [id], and
 * the GE [price] you'd pay if you have to buy it (0 when already owned).
 */
data class GearChoice(
    val tier: GearTier,
    val source: ToolSource,
    val id: Int,
    val price: Int,
) {
    val name: String get() = tier.name
    val slot: EquipSlot get() = tier.slot
    val levelReq: Int get() = tier.levelReq
    val owned: Boolean get() = source != ToolSource.BUYABLE
    /** True when the item must be bought on the GE (not already owned). */
    fun isBuyable(): Boolean = source == ToolSource.BUYABLE
}
