package io.osrsx.api

/**
 * Static item reference data — currently the broad bank CATEGORY of an item, sourced from the OSRS Wiki
 * and baked into osrsx.db (see io.osrsx.data.ItemCategoryDb / osrsx-tools/python/gen_item_categories.py). Lets a
 * plugin sort/file items by what they actually are instead of guessing from the name.
 *
 * Categories are the nine broad bank buckets: "Weapons", "Armour", "Jewellery", "Magic & Ranged",
 * "Consumables", "Herblore", "Resources", "Tools", "Misc". Anything not catalogued returns "Misc".
 */
interface ItemCatalog {
    /** Broad bank category for item [id]; "Misc" if the item isn't catalogued. */
    fun category(id: Int): String

    /** Broad bank category for an item by name (resolved to an id via the GE mapping); "Misc" if unknown. */
    fun category(name: String): String

    /** Canonical display name for item [id] (from its item definition), or null if the id is unknown. Lets a
     *  plugin label items by id without reaching through to the engine's `getItemDefinition`. */
    fun name(id: Int): String?
}
