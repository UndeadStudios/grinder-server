package com.grinder.game.content.skill.skillable.impl.herblore

import com.grinder.util.ItemID

/**
 * Represents a finished potion.
 *
 * @author Professor Oak
 */
enum class PotionFinishedType(
    val finishedPotion: Int,
    val unfinishedPotion: Int,
    val itemNeeded: Int,
    val levelReq: Int,
    val expGained: Int
) {
    // #Herblore
    IMP_REPLLENT(ItemID.IMP_REPELLENT, ItemID.ANCHOVY_OIL, ItemID.ASSORTED_FLOWERS, 1, 5),
    ATTACK_POTION(ItemID.ATTACK_POTION_3_, ItemID.GUAM_POTION_UNF_, ItemID.EYE_OF_NEWT, 1, 25),
    ANTIPOISON(ItemID.ANTIPOISON_3_, ItemID.MARRENTILL_POTION_UNF_, ItemID.UNICORN_HORN_DUST, 5, 38),
    RELICYMS_BALM(ItemID.RELICYMS_BALM_3_, ItemID.ROGUES_PURSE, ItemID.SNAKE_WEED, 8, 40),
    STRENGTH_POTION(ItemID.STRENGTH_POTION_3_, ItemID.TARROMIN_POTION_UNF_, ItemID.LIMPWURT_ROOT, 12, 50),
    SERUM_207(ItemID.SERUM_207_3_, ItemID.TARROMIN_POTION_UNF_, ItemID.ASHES, 15, 50),

    UNFINISHED_HERB_TEA_HARRALANDER(ItemID.HERB_TEA_MIX_3, ItemID.CUP_OF_HOT_WATER, ItemID.HARRALANDER, 18, 14),
    UNFINISHED_HERB_TEA_GUAM(ItemID.HERB_TEA_MIX_5, ItemID.CUP_OF_HOT_WATER, ItemID.GUAM_LEAF, 18, 14),
    UNFINISHED_HERB_TEA_MARRENTILL(ItemID.HERB_TEA_MIX_7, ItemID.CUP_OF_HOT_WATER, ItemID.MARRENTILL, 18, 14),
    UNFINISHED_HERB_TEA_HARRALANDER_MARRENTILL(ItemID.RUINED_HERB_TEA, ItemID.HERB_TEA_MIX_3, ItemID.MARRENTILL, 18, 14),
    UNFINISHED_HERB_TEA_MARRENTILL_HARRALANDER(ItemID.RUINED_HERB_TEA, ItemID.HERB_TEA_MIX_7, ItemID.HARRALANDER, 18, 14),
    UNFINISHED_HERB_TEA_GUAM_GUAM(ItemID.RUINED_HERB_TEA, ItemID.HERB_TEA_MIX_5, ItemID.GUAM_LEAF, 18, 0),
    UNFINISHED_HERB_TEA_MARRENTILL_MARRENTILL(ItemID.RUINED_HERB_TEA, ItemID.HERB_TEA_MIX_7, ItemID.MARRENTILL, 18, 0),
    UNFINISHED_HERB_TEA_HARRALANDER_HARRALANDER(ItemID.RUINED_HERB_TEA, ItemID.HERB_TEA_MIX_3, ItemID.HARRALANDER, 18, 0),
    //UNFINISHED_HERB_TEA_GUAM_GUAM_MARRENTILL(ItemID.RUINED_HERB_TEA, ItemID.HERB_TEA_MIX_11, ItemID.MARRENTILL, 18, 14),
    UNFINISHED_HERB_TEA_GUAM_HARRALANDER(ItemID.HERB_TEA_MIX_9, ItemID.HERB_TEA_MIX_3, ItemID.GUAM_LEAF, 18, 14),
    UNFINISHED_HERB_TEA_HARRALANDER_GUAM(ItemID.HERB_TEA_MIX_9, ItemID.HERB_TEA_MIX_5, ItemID.HARRALANDER, 18, 14),
    UNFINISHED_HERB_TEA_HARRALANDER_GUAM_GUAM(ItemID.HERB_TEA_MIX_11, ItemID.HERB_TEA_MIX_9, ItemID.GUAM_LEAF, 18, 14),
    GUTHIX_TEA(ItemID.GUTHIX_REST_4_, ItemID.HERB_TEA_MIX_11, ItemID.MARRENTILL, 18, 14),

    COMPOST_POTION(ItemID.COMPOST_POTION_3_, ItemID.HARRALANDER_POTION_UNF_, 21622, 22, 60),
    RESTORE_POTION(ItemID.RESTORE_POTION_3_, ItemID.HARRALANDER_POTION_UNF_, ItemID.RED_SPIDERS_EGGS, 22, 63),
    GUTHIX_BALANCE_4(ItemID.GUTHIX_BALANCE_4_, ItemID.GUTHIX_BALANCE_UNF_, ItemID.SILVER_DUST, 22, 25),
    GUTHIX_BALANCE_3(ItemID.GUTHIX_BALANCE_3_, ItemID.GUTHIX_BALANCE_UNF_3, ItemID.SILVER_DUST, 22, 25),
    GUTHIX_BALANCE_2(ItemID.GUTHIX_BALANCE_2_, ItemID.GUTHIX_BALANCE_UNF_5, ItemID.SILVER_DUST, 22, 25),
    GUTHIX_BALANCE_1(ItemID.GUTHIX_BALANCE_1_, ItemID.GUTHIX_BALANCE_UNF_7, ItemID.SILVER_DUST, 22, 25),
    BLAMISH_OIL(ItemID.BLAMISH_OIL, ItemID.HARRALANDER_POTION_UNF_, ItemID.BLAMISH_SNAIL_SLIME, 25, 80),
    ENERGY_POTION(ItemID.ENERGY_POTION_3_, ItemID.HARRALANDER_POTION_UNF_, ItemID.CHOCOLATE_DUST, 26, 68),
    DEFENCE_POTION(ItemID.DEFENCE_POTION_3_, ItemID.RANARR_POTION_UNF_, ItemID.WHITE_BERRIES, 30, 75),
    AGILITY_POTION(ItemID.AGILITY_POTION_3_, ItemID.TOADFLAX_POTION_UNF_, ItemID.TOADS_LEGS, 34, 80),
    COMBAT_POTION(ItemID.COMBAT_POTION_3_, ItemID.HARRALANDER_POTION_UNF_, ItemID.GOAT_HORN_DUST, 36, 84),
    PRAYER_POTION(ItemID.PRAYER_POTION_3_, ItemID.RANARR_POTION_UNF_, ItemID.SNAPE_GRASS, 38, 88),
    SUPER_ATTACK(ItemID.SUPER_ATTACK_3_, ItemID.IRIT_POTION_UNF_, ItemID.EYE_OF_NEWT, 45, 100),
    SUPER_ANTIPOISON(ItemID.SUPERANTIPOISON_3_, ItemID.IRIT_POTION_UNF_, ItemID.UNICORN_HORN_DUST, 48, 106),
    FISHING_POTION(ItemID.FISHING_POTION_3_, ItemID.AVANTOE_POTION_UNF_, ItemID.SNAPE_GRASS, 50, 112),
    SUPER_ENERGY(ItemID.SUPER_ENERGY_3_, ItemID.AVANTOE_POTION_UNF_, ItemID.MORT_MYRE_FUNGUS, 52, 118),
    HUNTER_POTION(ItemID.HUNTER_POTION_3_, ItemID.AVANTOE_POTION_UNF_, ItemID.KEBBIT_TEETH_DUST, 53, 120),
    SUPER_STRENGTH(ItemID.SUPER_STRENGTH_3_, ItemID.KWUARM_POTION_UNF_, ItemID.LIMPWURT_ROOT, 55, 130),
    WEAPON_POISON(ItemID.WEAPON_POISON, ItemID.KWUARM_POTION_UNF_, ItemID.DRAGON_SCALE_DUST, 60, 138),
    SUPER_RESTORE(ItemID.SUPER_RESTORE_3_, ItemID.SNAPDRAGON_POTION_UNF_, ItemID.RED_SPIDERS_EGGS, 63, 143),
    MIXTURE_STEP_ONE_4(ItemID.MIXTURE__STEP_1_4_, ItemID.SUPER_RESTORE_4_, ItemID.UNICORN_HORN_DUST, 65, 57),
    MIXTURE_STEP_ONE_3(ItemID.MIXTURE__STEP_1_3_, ItemID.SUPER_RESTORE_3_, ItemID.UNICORN_HORN_DUST, 65, 57),
    MIXTURE_STEP_ONE_2(ItemID.MIXTURE__STEP_1_2_, ItemID.SUPER_RESTORE_2_, ItemID.UNICORN_HORN_DUST, 65, 57),
    MIXTURE_STEP_ONE_1(ItemID.MIXTURE__STEP_1_1_, ItemID.SUPER_RESTORE_1_, ItemID.UNICORN_HORN_DUST, 65, 57),
    MIXTURE_STEP_TWO_4(ItemID.MIXTURE__STEP_2_4_, ItemID.MIXTURE__STEP_1_4_, ItemID.SNAKE_WEED, 65, 31),
    MIXTURE_STEP_TWO_3(ItemID.MIXTURE__STEP_2_3_, ItemID.MIXTURE__STEP_1_3_, ItemID.SNAKE_WEED, 65, 31),
    MIXTURE_STEP_TWO_2(ItemID.MIXTURE__STEP_2_2_, ItemID.MIXTURE__STEP_1_2_, ItemID.SNAKE_WEED, 65, 31),
    MIXTURE_STEP_TWO_1(ItemID.MIXTURE__STEP_2_1_, ItemID.MIXTURE__STEP_1_1_, ItemID.SNAKE_WEED, 65, 31),
    SANFEW_SERUM_4(ItemID.SANFEW_SERUM_4_, ItemID.MIXTURE__STEP_2_4_, ItemID.NAIL_BEAST_NAILS, 65, 36),
    SANFEW_SERUM_3(ItemID.SANFEW_SERUM_3_, ItemID.MIXTURE__STEP_2_3_, ItemID.NAIL_BEAST_NAILS, 65, 36),
    SANFEW_SERUM_2(ItemID.SANFEW_SERUM_2_, ItemID.MIXTURE__STEP_2_2_, ItemID.NAIL_BEAST_NAILS, 65, 36),
    SANFEW_SERUM_1(ItemID.SANFEW_SERUM_1_, ItemID.MIXTURE__STEP_2_1_, ItemID.NAIL_BEAST_NAILS, 65, 36),
    SUPER_DEFENCE(ItemID.SUPER_DEFENCE_3_, ItemID.CADANTINE_POTION_UNF_, ItemID.WHITE_BERRIES, 66, 150),
    ANTIPOISON_PLUS(ItemID.ANTIDOTE_PLUS_3_, ItemID.COCONUT_MILK, ItemID.YEW_ROOTS, 68, 155),
    ANTIFIRE(ItemID.ANTIFIRE_POTION_3_, ItemID.LANTADYME_POTION_UNF_, ItemID.DRAGON_SCALE_DUST, 69, 158),
    DIVINE_SUPER_ATTACK_4(ItemID.DIVINE_SUPER_ATTACK_4, ItemID.SUPER_ATTACK_4_, ItemID.CRYSTAL_DUST, 70, 2),
    DIVINE_SUPER_ATTACK_3(ItemID.DIVINE_SUPER_ATTACK_3, ItemID.SUPER_ATTACK_3_, ItemID.CRYSTAL_DUST, 70, 2),
    DIVINE_SUPER_ATTACK_2(ItemID.DIVINE_SUPER_ATTACK_2, ItemID.SUPER_ATTACK_2_, ItemID.CRYSTAL_DUST, 70, 2),
    DIVINE_SUPER_ATTACK_1(ItemID.DIVINE_SUPER_ATTACK_1, ItemID.SUPER_ATTACK_1_, ItemID.CRYSTAL_DUST, 70, 2),
    DIVINE_SUPER_DEFENCE_4(ItemID.DIVINE_SUPER_DEFENCE_4, ItemID.SUPER_DEFENCE_4_, ItemID.CRYSTAL_DUST, 70, 2),
    DIVINE_SUPER_DEFENCE_3(ItemID.DIVINE_SUPER_DEFENCE_3, ItemID.SUPER_DEFENCE_3_, ItemID.CRYSTAL_DUST, 70, 2),
    DIVINE_SUPER_DEFENCE_2(ItemID.DIVINE_SUPER_DEFENCE_2, ItemID.SUPER_DEFENCE_2_, ItemID.CRYSTAL_DUST, 70, 2),
    DIVINE_SUPER_DEFENCE_1(ItemID.DIVINE_SUPER_DEFENCE_1, ItemID.SUPER_DEFENCE_1_, ItemID.CRYSTAL_DUST, 70, 2),
    DIVINE_SUPER_STRENGTH_4(ItemID.DIVINE_SUPER_STRENGTH_4, ItemID.SUPER_STRENGTH_4_, ItemID.CRYSTAL_DUST, 70, 2),
    DIVINE_SUPER_STRENGTH_3(ItemID.DIVINE_SUPER_STRENGTH_3, ItemID.SUPER_STRENGTH_3_, ItemID.CRYSTAL_DUST, 70, 2),
    DIVINE_SUPER_STRENGTH_2(ItemID.DIVINE_SUPER_STRENGTH_2, ItemID.SUPER_STRENGTH_2_, ItemID.CRYSTAL_DUST, 70, 2),
    DIVINE_SUPER_STRENGTH_1(ItemID.DIVINE_SUPER_STRENGTH_1, ItemID.SUPER_STRENGTH_1_, ItemID.CRYSTAL_DUST, 70, 2),
    RANGING_POTION(ItemID.RANGING_POTION_3_, ItemID.DWARF_WEED_POTION_UNF_, ItemID.WINE_OF_ZAMORAK, 72, 163),
    WEAPON_POISON_PLUS(ItemID.WEAPON_POISON_PLUS_, ItemID.COCONUT_MILK, ItemID.RED_SPIDERS_EGGS, 73, 145), //add primary ingredient
    DIVINE_RANGING_4(ItemID.DIVINE_RANGING_4, ItemID.RANGING_POTION_4_, ItemID.CRYSTAL_DUST, 74, 2),
    DIVINE_RANGING_3(ItemID.DIVINE_RANGING_3, ItemID.RANGING_POTION_3_, ItemID.CRYSTAL_DUST, 74, 2),
    DIVINE_RANGING_2(ItemID.DIVINE_RANGING_2, ItemID.RANGING_POTION_2_, ItemID.CRYSTAL_DUST, 74, 2),
    DIVINE_RANGING_1(ItemID.DIVINE_RANGING_1, ItemID.RANGING_POTION_1_, ItemID.CRYSTAL_DUST, 74, 2),
    MAGIC_POTION(ItemID.MAGIC_POTION_3_, ItemID.LANTADYME_POTION_UNF_, ItemID.POTATO_CACTUS, 76, 173),
    STAMINA_POTION_4(ItemID.STAMINA_POTION_4_, ItemID.SUPER_ENERGY_4_, ItemID.AMYLASE_CRYSTAL, 77, 102), // TODO: https://www.youtube.com/watch?v=9GiLzilluXk&t=424s (like video and wiki)
    ZAMORAK_BREW(ItemID.ZAMORAK_BREW_3_, ItemID.TORSTOL_POTION_UNF_, ItemID.JANGERBERRIES, 78, 175),
    DIVINE_MAGIC_4(ItemID.DIVINE_MAGIC_4, ItemID.MAGIC_POTION_4_, ItemID.CRYSTAL_DUST, 78, 2),
    DIVINE_MAGIC_3(ItemID.DIVINE_MAGIC_3, ItemID.MAGIC_POTION_3_, ItemID.CRYSTAL_DUST, 78, 2),
    DIVINE_MAGIC_2(ItemID.DIVINE_MAGIC_2, ItemID.MAGIC_POTION_2_, ItemID.CRYSTAL_DUST, 78, 2),
    DIVINE_MAGIC_1(ItemID.DIVINE_MAGIC_1, ItemID.MAGIC_POTION_1_, ItemID.CRYSTAL_DUST, 78, 2),
    ANTIDOTE(ItemID.ANTIDOTE_PLUS_PLUS_4_, ItemID.COCONUT_MILK, ItemID.IRIT_LEAF, 79, 177),
    ANTIDOTE2(ItemID.ANTIDOTE_PLUS_PLUS_4_, ItemID.COCONUT_MILK, ItemID.MAGIC_ROOTS, 79, 177),
    BASTION_POTION(ItemID.BASTION_3, ItemID.CADANTINE_BLOOD_POTION_UNF, ItemID.WINE_OF_ZAMORAK, 80, 155),
    BATTLEMAGE_POTION(ItemID.BATTLEMAGE_3, ItemID.CADANTINE_BLOOD_POTION_UNF, ItemID.POTATO_CACTUS, 80, 155),
    SARADOMIN_BREW(ItemID.SARADOMIN_BREW_3_, ItemID.TOADFLAX_POTION_UNF_, ItemID.CRUSHED_NEST, 81, 180),
    WEAPON_POISON_PLUSPLUS(ItemID.WEAPON_POISON_PLUS_PLUS_, ItemID.COCONUT_MILK, ItemID.POISON_IVY_BERRIES, 82, 168), //add primary ingredient
    EXTENDED_ANTIFIRE(ItemID.EXTENDED_ANTIFIRE_4_, ItemID.ANTIFIRE_POTION_3_, ItemID.LAVA_SCALE_SHARD, 85, 210),
    DIVINE_BASTION_4(ItemID.DIVINE_BASTION_4, ItemID.BASTION_4, ItemID.CRYSTAL_DUST, 86, 2),
    DIVINE_BASTION_3(ItemID.DIVINE_BASTION_3, ItemID.BASTION_3, ItemID.CRYSTAL_DUST, 86, 2),
    DIVINE_BASTION_2(ItemID.DIVINE_BASTION_2, ItemID.BASTION_2, ItemID.CRYSTAL_DUST, 86, 2),
    DIVINE_BASTION_1(ItemID.DIVINE_BASTION_1, ItemID.BASTION_1, ItemID.CRYSTAL_DUST, 86, 2),
    DIVINE_BATTLEMAGE_4(ItemID.DIVINE_BATTLEMAGE_4, ItemID.BATTLEMAGE_4, ItemID.CRYSTAL_DUST, 86, 2),
    DIVINE_BATTLEMAGE_3(ItemID.DIVINE_BATTLEMAGE_3, ItemID.BATTLEMAGE_3, ItemID.CRYSTAL_DUST, 86, 2),
    DIVINE_BATTLEMAGE_2(ItemID.DIVINE_BATTLEMAGE_2, ItemID.BATTLEMAGE_2, ItemID.CRYSTAL_DUST, 86, 2),
    DIVINE_BATTLEMAGE_1(ItemID.DIVINE_BATTLEMAGE_1, ItemID.BATTLEMAGE_1, ItemID.CRYSTAL_DUST, 86, 2),
    SUPER_ANTIFIRE_POTION(21981, ItemID.ANTIFIRE_POTION_3_, 21975, 92, 130),
    DIVINE_SUPER_COMBAT_4(ItemID.DIVINE_SUPER_COMBAT_4, ItemID.SUPER_COMBAT_POTION_4_, ItemID.CRYSTAL_DUST, 97, 2),
    DIVINE_SUPER_COMBAT_3(ItemID.DIVINE_SUPER_COMBAT_3, ItemID.SUPER_COMBAT_POTION_3_, ItemID.CRYSTAL_DUST, 97, 2),
    DIVINE_SUPER_COMBAT_2(ItemID.DIVINE_SUPER_COMBAT_2, ItemID.SUPER_COMBAT_POTION_2_, ItemID.CRYSTAL_DUST, 97, 2),
    DIVINE_SUPER_COMBAT_1(ItemID.DIVINE_SUPER_COMBAT_1, ItemID.SUPER_COMBAT_POTION_1_, ItemID.CRYSTAL_DUST, 97, 2),
    EXTENDED_ANTIFIRE_4(ItemID.EXTENDED_SUPER_ANTIFIRE_POTION_4, ItemID.SUPER_ANTIFIRE_POTION_4, ItemID.LAVA_SCALE_SHARD, 98, 2),
    EXTENDED_ANTIFIRE_3(ItemID.EXTENDED_SUPER_ANTIFIRE_POTION_3, ItemID.SUPER_ANTIFIRE_POTION_3, ItemID.LAVA_SCALE_SHARD, 98, 2),
    EXTENDED_ANTIFIRE_2(ItemID.EXTENDED_SUPER_ANTIFIRE_POTION_2, ItemID.SUPER_ANTIFIRE_POTION_2, ItemID.LAVA_SCALE_SHARD, 98, 2),
    EXTENDED_ANTIFIRE_1(ItemID.EXTENDED_SUPER_ANTIFIRE_POTION_1, ItemID.SUPER_ANTIFIRE_POTION_1, ItemID.LAVA_SCALE_SHARD, 98, 2)


    // TODO: Herblore: Barbarian mixes potion making
    // TODO: Herbloie: Swamp tars making
    ;
}