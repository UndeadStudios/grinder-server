package com.grinder.game.content.skill.skillable.impl.herblore

import com.grinder.util.ItemID

/**
 * Represents an unfinished potion.
 *
 * @author Professor Oak
 */
enum class PotionUnfinishedType(
    val potionUnfId: Int,
    val requiredHerbId: Int,
    val requiredVialId: Int = 227,
    val requiredHerbloreLevel: Int)
{
    // #Herblore
    GUAM_POTION(
        potionUnfId = ItemID.GUAM_POTION_UNF_,
        requiredHerbId = ItemID.GUAM_LEAF,
        requiredHerbloreLevel = 1
    ),
    MARRENTILL_POTION(
        potionUnfId = ItemID.MARRENTILL_POTION_UNF_,
        requiredHerbId = ItemID.MARRENTILL,
        requiredHerbloreLevel = 5
    ),
    TARROMIN_POTION(
        potionUnfId = ItemID.TARROMIN_POTION_UNF_,
        requiredHerbId = ItemID.TARROMIN,
        requiredHerbloreLevel = 12
    ),
    HARRALANDER_POTION(
        potionUnfId = ItemID.HARRALANDER_POTION_UNF_,
        requiredHerbId = ItemID.HARRALANDER,
        requiredHerbloreLevel = 22
    ),
    RANARR_POTION(
        potionUnfId = ItemID.RANARR_POTION_UNF_,
        requiredHerbId = ItemID.RANARR_WEED,
        requiredHerbloreLevel = 30
    ),
    TOADFLAX_POTION(
        potionUnfId = ItemID.TOADFLAX_POTION_UNF_,
        requiredHerbId = ItemID.TOADFLAX,
        requiredHerbloreLevel = 34
    ),
    SPIRIT_WEED_POTION(
        potionUnfId = 12181,
        requiredHerbId = 12172,
        requiredHerbloreLevel = 40
    ),
    IRIT_POTION(
        potionUnfId = ItemID.IRIT_POTION_UNF_,
        requiredHerbId = ItemID.IRIT_LEAF,
        requiredHerbloreLevel = 45
    ),
    WERGALI_POTION(
        potionUnfId = 14856,
        requiredHerbId = 14854,
        requiredHerbloreLevel = 1
    ),
    AVANTOE_POTION(
        potionUnfId = ItemID.AVANTOE_POTION_UNF_,
        requiredHerbId = ItemID.AVANTOE,
        requiredHerbloreLevel = 50
    ),
    KWUARM_POTION(
        potionUnfId = ItemID.KWUARM_POTION_UNF_,
        requiredHerbId = ItemID.KWUARM,
        requiredHerbloreLevel = 55
    ),
    SNAPDRAGON_POTION(
        potionUnfId = ItemID.SNAPDRAGON_POTION_UNF_,
        requiredHerbId = ItemID.SNAPDRAGON,
        requiredHerbloreLevel = 63
    ),
    CADANTINE_POTION(
        potionUnfId = ItemID.CADANTINE_POTION_UNF_,
        requiredHerbId = ItemID.CADANTINE,
        requiredHerbloreLevel = 66
    ),
    LANTADYME(
        potionUnfId = ItemID.LANTADYME_POTION_UNF_,
        requiredHerbId = ItemID.LANTADYME,
        requiredHerbloreLevel = 69
    ),
    DWARF_WEED_POTION(
        potionUnfId = ItemID.DWARF_WEED_POTION_UNF_,
        requiredHerbId = ItemID.DWARF_WEED,
        requiredHerbloreLevel = 72
    ),
    TORSTOL_POTION(
        potionUnfId = ItemID.TORSTOL_POTION_UNF_,
        requiredHerbId = ItemID.TORSTOL,
        requiredHerbloreLevel = 78
    ),
    CADANTINE_BLOOD_POTION(
        potionUnfId = ItemID.CADANTINE_BLOOD_POTION_UNF,
        requiredHerbId = ItemID.CADANTINE,
        requiredVialId = 22446,
        requiredHerbloreLevel = 80
    );
}