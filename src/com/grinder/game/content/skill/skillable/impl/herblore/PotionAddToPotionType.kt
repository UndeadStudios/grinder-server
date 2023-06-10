package com.grinder.game.content.skill.skillable.impl.herblore

import com.grinder.util.ItemID

/**
 * Represents an unfinished potion.
 *
 * @author Professor Oak
 */
enum class PotionAddToPotionType(
    val potionUnfId: Int,
    val requiredPotionId: Int,
    val requiredIngredientId: Int,
    val requiredHerbloreLevel: Int,
    val experience: Int)
{
    // #Herblore
    GUTHIX_BALANCE_4(
        potionUnfId = ItemID.GUTHIX_BALANCE_UNF_,
        requiredPotionId = ItemID.RESTORE_POTION_4_,
        requiredIngredientId = ItemID.GARLIC,
        requiredHerbloreLevel = 22,
        experience = 25
    ),
    GUTHIX_BALANCE_3(
    potionUnfId = ItemID.GUTHIX_BALANCE_UNF_3,
    requiredPotionId = ItemID.RESTORE_POTION_3_,
    requiredIngredientId = ItemID.GARLIC,
    requiredHerbloreLevel = 22,
    experience = 25
    ),
    GUTHIX_BALANCE_2(
    potionUnfId = ItemID.GUTHIX_BALANCE_UNF_5,
    requiredPotionId = ItemID.RESTORE_POTION_2_,
    requiredIngredientId = ItemID.GARLIC,
    requiredHerbloreLevel = 22,
    experience = 25
    ),
    GUTHIX_BALANCE_1(
    potionUnfId = ItemID.GUTHIX_BALANCE_UNF_7,
    requiredPotionId = ItemID.RESTORE_POTION_1_,
    requiredIngredientId = ItemID.GARLIC,
    requiredHerbloreLevel = 22,
    experience = 25
    );
}