package com.grinder.util.tools

import com.grinder.game.GameConstants
import com.grinder.game.definition.ObjectDefinition
import com.grinder.game.definition.loader.impl.ItemDefinitionLoader
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerLoading
import com.grinder.game.entity.agent.player.PlayerSaving
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import com.grinder.util.Misc
import java.nio.file.Paths

object ServerItemWiper {

    @JvmStatic
    fun main(args: Array<String>) {

        // Required or else random errors will happen you cant debug when loading farming patches
        // and when deleting items from the inventories.
        ObjectDefinition.init()
        ItemDefinitionLoader().load()


        val itemsToReduce: List<Pair<Int, List<Int>>> = listOf(
            // DivideBy, itemIds
            Pair(10_000_000, listOf(
                ItemID.MITHRIL_SEEDS,)),
            Pair(20, listOf(
                ItemID.COPPER_ORE,
                ItemID.COPPER_ORE_2,
                ItemID.TIN_ORE,
                ItemID.TIN_ORE_2,
                ItemID.IRON_ORE,
                ItemID.IRON_ORE_2,
                ItemID.COAL,
                ItemID.COAL_2,

                ItemID.RAW_KARAMBWANJI,
                ItemID.BLUE_DRAGON_SCALE,
                ItemID.LIMPWURT_ROOT,
                ItemID.LIMPWURT_ROOT_2,
                ItemID.UNICORN_HORN,
                ItemID.UNICORN_HORN_2,
                ItemID.UNICORN_HORN_DUST,
                ItemID.UNICORN_HORN_DUST_2,
                ItemID.WHITE_BERRIES,
                ItemID.WHITE_BERRIES_2,
                ItemID.POISON_IVY_BERRIES,
                ItemID.POISON_IVY_BERRIES_2,
                ItemID.POTATO_CACTUS,
                ItemID.POTATO_CACTUS_2,
                ItemID.GOAT_HORN_DUST,
                ItemID.GOAT_HORN_DUST_2,
                ItemID.ARROW_SHAFT,
                ItemID.HEADLESS_ARROW,
                ItemID.RUNITE_BOLTS_UNF_,
                ItemID.ADAMANT_BOLTS_UNF_,
                ItemID.EYE_OF_NEWT,
                ItemID.EYE_OF_NEWT_2,



                ItemID.RUNE_ESSENCE,
                ItemID.RUNE_ESSENCE_2,
                ItemID.PURE_ESSENCE,
                ItemID.PURE_ESSENCE_2,
                ItemID.GOLD_ORE,
                ItemID.GOLD_ORE_2,
                ItemID.SILVER_ORE,
                ItemID.SILVER_ORE_2,
                ItemID.MITHRIL_ORE,
                ItemID.MITHRIL_ORE_2,
                ItemID.ADAMANTITE_ORE,
                ItemID.ADAMANTITE_ORE_2,
                ItemID.RUNITE_ORE,
                ItemID.RUNITE_ORE_2,
                ItemID.BRONZE_BAR,
                ItemID.BRONZE_BAR_2,
                ItemID.IRON_BAR,
                ItemID.IRON_BAR_2,
                ItemID.STEEL_BAR,
                ItemID.STEEL_BAR_2,
                ItemID.GOLD_BAR,
                ItemID.GOLD_BAR_2,
                ItemID.SILVER_BAR,
                ItemID.SILVER_BAR_2,
                ItemID.MITHRIL_BAR,
                ItemID.MITHRIL_BAR_2,
                ItemID.ADAMANTITE_BAR,
                ItemID.ADAMANTITE_BAR_2,
                ItemID.RUNITE_BAR,
                ItemID.RUNITE_BAR_2,
                ItemID.FEATHER,
                ItemID.THREAD,
                ItemID.NEEDLE,
                ItemID.FISHING_BAIT,
                ItemID.WOOL,
                ItemID.WOOL_2,
                ItemID.BALL_OF_WOOL,
                ItemID.BALL_OF_WOOL_2,
                ItemID.SOFT_CLAY,
                ItemID.SOFT_CLAY_2,
                ItemID.JADE,
                ItemID.JADE_2,
                ItemID.OPAL,
                ItemID.OPAL_2,
                ItemID.RED_TOPAZ,
                ItemID.RED_TOPAZ_2,
                ItemID.SAPPHIRE,
                ItemID.SAPPHIRE_2,
                ItemID.RUBY,
                ItemID.RUBY_2,
                ItemID.EMERALD,
                ItemID.EMERALD_2,
                ItemID.DIAMOND,
                ItemID.DIAMOND_2,
                ItemID.DRAGONSTONE,
                ItemID.DRAGONSTONE_2,
                ItemID.UNCUT_JADE,
                ItemID.UNCUT_JADE_2,
                ItemID.UNCUT_OPAL,
                ItemID.UNCUT_OPAL_2,
                ItemID.UNCUT_RED_TOPAZ,
                ItemID.UNCUT_RED_TOPAZ_2,
                ItemID.UNCUT_SAPPHIRE,
                ItemID.UNCUT_SAPPHIRE_2,
                ItemID.UNCUT_RUBY,
                ItemID.UNCUT_RUBY_2,
                ItemID.UNCUT_EMERALD,
                ItemID.UNCUT_EMERALD_2,
                ItemID.UNCUT_DIAMOND,
                ItemID.UNCUT_DIAMOND_2,
                ItemID.UNCUT_DRAGONSTONE,
                ItemID.UNCUT_DRAGONSTONE_2,
                ItemID.RAW_SHRIMPS,
                ItemID.RAW_SHRIMPS_2,
                ItemID.RAW_ANCHOVIES,
                ItemID.RAW_ANCHOVIES_2,
                ItemID.RAW_SARDINE,
                ItemID.RAW_SARDINE_2,
                ItemID.RAW_HERRING,
                ItemID.RAW_HERRING_2,
                ItemID.RAW_TROUT,
                ItemID.RAW_TROUT_2,
                ItemID.RAW_COD,
                ItemID.RAW_COD_2,
                ItemID.RAW_SALMON,
                ItemID.RAW_SALMON_2,
                ItemID.RAW_BASS,
                ItemID.RAW_BASS_2,
                ItemID.RAW_TUNA,
                ItemID.RAW_TUNA_2,
                ItemID.RAW_LOBSTER,
                ItemID.RAW_LOBSTER_2,
                ItemID.RAW_SWORDFISH,
                ItemID.RAW_SWORDFISH_2,
                ItemID.RAW_MONKFISH,
                ItemID.RAW_MONKFISH_2,
                ItemID.RAW_SEA_TURTLE,
                ItemID.RAW_SEA_TURTLE_2,
                ItemID.RAW_SHARK,
                ItemID.RAW_SHARK_2,
                ItemID.RAW_KARAMBWAN,
                ItemID.RAW_KARAMBWAN_2,
                ItemID.RAW_ANGLERFISH,
                ItemID.RAW_ANGLERFISH_2,
                ItemID.COOKED_KARAMBWAN,
                ItemID.CAKE,
                ItemID.CAKE_2,
                ItemID.CHOCOLATE_CAKE,
                ItemID.CHOCOLATE_CAKE_2,
                ItemID.MANTA_RAY,
                ItemID.MANTA_RAY_2,
                ItemID.SHRIMPS,
                ItemID.SHRIMPS_2,
                ItemID.ANCHOVIES,
                ItemID.ANCHOVIES_2,
                ItemID.SARDINE,
                ItemID.SARDINE_2,
                ItemID.HERRING,
                ItemID.HERRING_2,
                ItemID.TROUT,
                ItemID.TROUT_2,
                ItemID.COD,
                ItemID.COD_2,
                ItemID.SALMON,
                ItemID.SALMON_2,
                ItemID.BASS,
                ItemID.BASS_2,
                ItemID.TUNA,
                ItemID.TUNA_2,
                ItemID.LOBSTER,
                ItemID.LOBSTER_2,
                ItemID.SWORDFISH,
                ItemID.SWORDFISH_2,
                ItemID.MONKFISH,
                ItemID.MONKFISH_2,
                ItemID.SEA_TURTLE,
                ItemID.SEA_TURTLE_2,
                ItemID.SHARK,
                ItemID.SHARK_2,
                ItemID.ANGLERFISH,
                ItemID.ANGLERFISH_2,
                ItemID.COOKED_KARAMBWAN,
                ItemID.MANTA_RAY,
                ItemID.MANTA_RAY_2,
                ItemID.AIR_RUNE,
                ItemID.WATER_RUNE,
                ItemID.EARTH_RUNE,
                ItemID.FIRE_RUNE,
                ItemID.BODY_RUNE,
                ItemID.MIND_RUNE,
                ItemID.CHAOS_RUNE,
                ItemID.DEATH_RUNE,
                ItemID.NATURE_RUNE,
                ItemID.LAW_RUNE,
                ItemID.BLOOD_RUNE,
                ItemID.ASTRAL_RUNE,
                ItemID.COSMIC_RUNE,
                ItemID.SOUL_RUNE,
                ItemID.WRATH_RUNE,
                ItemID.MUD_RUNE,
                ItemID.DUST_RUNE,
                ItemID.COWHIDE,
                ItemID.COWHIDE_2,
                ItemID.SNAKESKIN,
                ItemID.SNAKESKIN_2,
                ItemID.GREEN_DRAGONHIDE,
                ItemID.GREEN_DRAGONHIDE_2,
                ItemID.BLUE_DRAGONHIDE,
                ItemID.BLUE_DRAGONHIDE_2,
                ItemID.RED_DRAGONHIDE,
                ItemID.RED_DRAGONHIDE_2,
                ItemID.BLACK_DRAGONHIDE,
                ItemID.BLACK_DRAGONHIDE_2,
                ItemID.GUAM_SEED,
                ItemID.MARRENTILL_SEED,
                ItemID.TARROMIN_SEED,
                ItemID.HARRALANDER_SEED,
                ItemID.RANARR_SEED,
                ItemID.TOADFLAX_SEED,
                ItemID.IRIT_SEED,
                ItemID.KWUARM_SEED,
                ItemID.SNAPDRAGON_SEED,
                ItemID.CADANTINE_SEED,
                ItemID.LANTADYME_SEED,
                ItemID.DWARF_WEED_SEED,
                ItemID.TORSTOL_SEED,
                ItemID.AVANTOE_SEED,


                ItemID.GRIMY_GUAM_LEAF,
                ItemID.GRIMY_MARRENTILL,
                ItemID.GRIMY_TARROMIN,
                ItemID.GRIMY_HARRALANDER,
                ItemID.GRIMY_RANARR_WEED,
                ItemID.GRIMY_TOADFLAX,
                ItemID.GRIMY_IRIT_LEAF,
                ItemID.GRIMY_KWUARM,
                ItemID.GRIMY_SNAPDRAGON,
                ItemID.GRIMY_CADANTINE,
                ItemID.GRIMY_LANTADYME,
                ItemID.GRIMY_DWARF_WEED,
                ItemID.GRIMY_TORSTOL,
                ItemID.GRIMY_AVANTOE,



                ItemID.GUAM_LEAF,
                ItemID.MARRENTILL,
                ItemID.TARROMIN,
                ItemID.HARRALANDER,
                ItemID.RANARR_WEED,
                ItemID.TOADFLAX,
                ItemID.IRIT_LEAF,
                ItemID.KWUARM,
                ItemID.SNAPDRAGON,
                ItemID.CADANTINE,
                ItemID.LANTADYME,
                ItemID.DWARF_WEED,
                ItemID.TORSTOL,
                ItemID.AVANTOE,


                ItemID.GUAM_POTION_UNF_,
                ItemID.GUAM_POTION_UNF_2,
                ItemID.MARRENTILL_POTION_UNF_,
                ItemID.MARRENTILL_POTION_UNF_2,
                ItemID.TARROMIN_POTION_UNF_,
                ItemID.TARROMIN_POTION_UNF_2,
                ItemID.HARRALANDER_POTION_UNF_,
                ItemID.HARRALANDER_POTION_UNF_2,
                ItemID.RANARR_POTION_UNF_,
                ItemID.RANARR_POTION_UNF_2,
                ItemID.TOADFLAX_POTION_UNF_,
                ItemID.TOADFLAX_POTION_UNF_2,
                ItemID.IRIT_POTION_UNF_,
                ItemID.IRIT_POTION_UNF_2,
                ItemID.KWUARM_POTION_UNF_,
                ItemID.KWUARM_POTION_UNF_2,
                ItemID.SNAPDRAGON_POTION_UNF_,
                ItemID.SNAPDRAGON_POTION_UNF_2,
                ItemID.CADANTINE_POTION_UNF_,
                ItemID.CADANTINE_POTION_UNF_2,
                ItemID.LANTADYME_POTION_UNF_,
                ItemID.LANTADYME_POTION_UNF_2,
                ItemID.DWARF_WEED_POTION_UNF_,
                ItemID.DWARF_WEED_POTION_UNF_2,
                ItemID.TORSTOL_POTION_UNF_,
                ItemID.TORSTOL_POTION_UNF_2,
                ItemID.AVANTOE_POTION_UNF_,
                ItemID.AVANTOE_POTION_UNF_2,
                ItemID.FLAX,
                ItemID.FLAX_2,
                ItemID.BOW_STRING,
                ItemID.BOW_STRING_2,
                ItemID.MAPLE_SHORTBOW_U_,
                ItemID.MAPLE_SHORTBOW_U_2,
                ItemID.YEW_SHORTBOW_U_,
                ItemID.YEW_SHORTBOW_U_2,
                ItemID.MAGIC_SHORTBOW_U_,
                ItemID.MAGIC_SHORTBOW_U_2,
                ItemID.MAGIC_LONGBOW_U_,
                ItemID.MAGIC_LONGBOW_U_2,
                ItemID.LOGS,
                ItemID.LOGS_2,
                ItemID.OAK_LOGS,
                ItemID.OAK_LOGS_2,
                ItemID.WILLOW_LOGS,
                ItemID.WILLOW_LOGS_2,
                ItemID.MAPLE_LOGS,
                ItemID.MAPLE_LOGS_2,
                ItemID.YEW_LOGS,
                ItemID.YEW_LOGS_2,
                ItemID.MAGIC_LOGS,
                ItemID.MAGIC_LOGS_2,

                ItemID.RUNE_ARROW,
                ItemID.DRAGON_ARROW,
                ItemID.RUNITE_BOLTS,
                ItemID.ADAMANT_BOLTS,
                ItemID.BONE_BOLTS,


                ItemID.SUPER_ATTACK_3_,
                ItemID.SUPER_ATTACK_2_,
                ItemID.SUPER_ATTACK_4_,
                ItemID.SUPER_ATTACK_4_2,

                ItemID.SUPER_STRENGTH_3_,
                ItemID.SUPER_STRENGTH_2_,
                ItemID.SUPER_STRENGTH_4_,
                ItemID.SUPER_STRENGTH_4_2,

                ItemID.SUPER_DEFENCE_3_,
                ItemID.SUPER_DEFENCE_2_,
                ItemID.SUPER_DEFENCE_4_,
                ItemID.SUPER_DEFENCE_4_2,

                ItemID.MAGIC_POTION_3_,
                ItemID.MAGIC_POTION_3_2,
                ItemID.MAGIC_POTION_4_,
                ItemID.MAGIC_POTION_4_2,

                ItemID.RANGING_POTION_3_,
                ItemID.RANGING_POTION_3_2,
                ItemID.RANGING_POTION_4_,
                ItemID.RANGING_POTION_4_2,

                ItemID.AGILITY_POTION_3_,
                ItemID.AGILITY_POTION_3_2,
                ItemID.AGILITY_POTION_4_,
                ItemID.AGILITY_POTION_4_2,

                ItemID.SUPER_COMBAT_POTION_3_,
                ItemID.SUPER_COMBAT_POTION_3_2,
                ItemID.SUPER_COMBAT_POTION_4_,
                ItemID.SUPER_COMBAT_POTION_4_2,


                ItemID.ANTIPOISON_3_,
                ItemID.ANTIPOISON_3_2,
                ItemID.ANTIPOISON_4_,
                ItemID.ANTIPOISON_4_2,

                ItemID.SUPERANTIPOISON_3_,
                ItemID.SUPERANTIPOISON_3_2,
                ItemID.SUPERANTIPOISON_4_,
                ItemID.SUPERANTIPOISON_4_2,

                ItemID.ANTIDOTE_PLUS_PLUS_3_,
                ItemID.ANTIDOTE_PLUS_PLUS_3_2,
                ItemID.ANTIDOTE_PLUS_PLUS_4_,
                ItemID.ANTIDOTE_PLUS_PLUS_4_2,

                ItemID.SUPER_ANTIFIRE_POTION_3,
                ItemID.SUPER_ANTIFIRE_POTION_4,

                ItemID.PRAYER_POTION_3_,
                ItemID.PRAYER_POTION_3_2,
                ItemID.PRAYER_POTION_4_,
                ItemID.PRAYER_POTION_4_2,

                ItemID.SUPER_RESTORE_3_,
                ItemID.SUPER_RESTORE_2_,
                ItemID.SUPER_RESTORE_4_,
                ItemID.SUPER_RESTORE_4_2,

                ItemID.SARADOMIN_BREW_3_,
                ItemID.SARADOMIN_BREW_3_2,
                ItemID.SARADOMIN_BREW_4_,
                ItemID.SARADOMIN_BREW_4_2,

                ItemID.BASTION_3,
                ItemID.BASTION_4,

                ItemID.BATTLEMAGE_3,
                ItemID.BATTLEMAGE_4,)),
            Pair(10, listOf(        ItemID.HELM_OF_NEITIZNOT,
                ItemID.HELM_OF_NEITIZNOT,
                ItemID.DRAGON_SCIMITAR,
                ItemID.DRAGON_SCIMITAR_2,
                ItemID.DRAGON_DEFENDER,
                ItemID.VOID_MAGE_HELM,
                ItemID.VOID_RANGER_HELM,
                ItemID.VOID_MELEE_HELM,
                ItemID.VOID_KNIGHT_GLOVES,
                ItemID.VOID_KNIGHT_TOP,
                ItemID.VOID_KNIGHT_ROBE,
                ItemID.ELITE_VOID_ROBE,
                ItemID.ELITE_VOID_TOP,
                ItemID.BARROWS_GLOVES,
                ItemID.DRAGON_GLOVES,
                ItemID.BANDOS_CHESTPLATE,
                ItemID.BANDOS_CHESTPLATE_2,
                ItemID.BANDOS_TASSETS,
                ItemID.BANDOS_TASSETS_2,
                ItemID.ARMADYL_HELMET,
                ItemID.ARMADYL_HELMET_2,
                ItemID.ARMADYL_CHAINSKIRT,
                ItemID.ARMADYL_CHAINSKIRT_2,
                ItemID.ARMADYL_CHESTPLATE,
                ItemID.ARMADYL_CHESTPLATE_2,
                ItemID.FIGHTER_TORSO,
                ItemID.RUNE_CROSSBOW,
                ItemID.RUNE_CROSSBOW_2,
                ItemID.DRAGON_BOOTS,
                ItemID.DRAGON_BOOTS_2,
                ItemID.AMULET_OF_FURY,
                ItemID.AMULET_OF_FURY_2,
                ItemID.GRANITE_MAUL,
                ItemID.GRANITE_MAUL_2,
                ItemID.ABYSSAL_WHIP,
                ItemID.ABYSSAL_WHIP_2,
                ItemID.ANCIENT_STAFF,
                ItemID.ANCIENT_STAFF_2,
                ItemID.MAGIC_SHORTBOW,
                ItemID.MAGIC_SHORTBOW_2,
                ItemID.GHOSTLY_HOOD,
                ItemID.GHOSTLY_ROBE,
                ItemID.GHOSTLY_GLOVES,
                ItemID.GHOSTLY_CLOAK,
                ItemID.MYSTIC_HAT,
                ItemID.MYSTIC_ROBE_BOTTOM,
                ItemID.MYSTIC_ROBE_TOP,
                ItemID.MYSTIC_GLOVES,
                ItemID.MYSTIC_BOOTS,
                15160, // Dgs
                ItemID.DRAGONFIRE_SHIELD_2,
                ItemID.DRAGONFIRE_WARD,
                ItemID.ANCIENT_WYVERN_SHIELD,
                ItemID._3RD_AGE_AMULET,
                22542,
                22547,
                22552)),
            Pair(2, listOf(
                ItemID.DRAGON_AXE,
                ItemID.DRAGON_PICKAXE,
                ItemID.RUNE_AXE,
                ItemID.RUNE_PICKAXE,
                ItemID.PRIMORDIAL_BOOTS,
                ItemID.PRIMORDIAL_BOOTS_2,
                ItemID.PEGASIAN_BOOTS,
                ItemID.PEGASIAN_BOOTS_2,
                ItemID.ETERNAL_BOOTS,
                ItemID.ETERNAL_BOOTS_2,
                15155, // Dragon whip
                22557,
                ItemID.DARK_BOW,
                ItemID.DARK_BOW_2,
                ItemID.BLOOD_MONEY,
                ItemID.COINS,
                ItemID.PLATINUM_TOKEN,
                ItemID.AMULET_OF_ETERNAL_GLORY,
                ItemID.AMULET_OF_ETERNAL_GLORY_2,
                ItemID.DRACONIC_VISAGE,
                22006,
                ItemID.DRAGON_PLATELEGS,
                ItemID.DRAGON_CLAWS,
                ItemID.ANCESTRAL_HAT,
                ItemID.ANCESTRAL_ROBE_BOTTOM,
                ItemID.ANCESTRAL_ROBE_TOP,
                ItemID.ARMADYL_GODSWORD,
                ItemID.BANDOS_GODSWORD,
                ItemID.SARADOMIN_GODSWORD,
                ItemID.ELYSIAN_SPIRIT_SHIELD,
                ItemID.ARCANE_SPIRIT_SHIELD,
                ItemID.SPECTRAL_SPIRIT_SHIELD,
                ItemID.CORRUPTED_HELM,
                ItemID.CORRUPTED_PLATEBODY,
                ItemID.CORRUPTED_PLATELEGS,
                ItemID.CORRUPTED_KITESHIELD,
                ItemID.CORRUPTED_PLATESKIRT,
                ItemID._3RD_AGE_RANGE_TOP,
                ItemID._3RD_AGE_RANGE_LEGS,
                ItemID._3RD_AGE_RANGE_COIF,
                ItemID._3RD_AGE_VAMBRACES,
                ItemID._3RD_AGE_ROBE_TOP,
                ItemID._3RD_AGE_ROBE,
                ItemID._3RD_AGE_MAGE_HAT,
                ItemID._3RD_AGE_PLATELEGS,
                ItemID._3RD_AGE_PLATEBODY,
                ItemID._3RD_AGE_FULL_HELMET,
                ItemID._3RD_AGE_KITESHIELD,
                ItemID._3RD_AGE_WAND,
                ItemID._3RD_AGE_CLOAK,
                ItemID._3RD_AGE_LONGSWORD,
                ItemID._3RD_AGE_AXE,
                ItemID._3RD_AGE_PICKAXE,

                ItemID.DRAGON_FULL_HELM)),
            Pair(0, listOf(
                ItemID.ATTACK_HOOD,
                ItemID.STRENGTH_HOOD,
                ItemID.DEFENCE_HOOD,
                ItemID.PRAYER_HOOD,
                ItemID.MAGIC_HOOD,
                ItemID.RANGING_HOOD,
                ItemID.RUNECRAFTING_HOOD,
                ItemID.HITPOINTS_HOOD,
                ItemID.AGILITY_HOOD,
                ItemID.HERBLORE_HOOD,
                ItemID.THIEVING_HOOD,
                ItemID.CRAFTING_HOOD,
                ItemID.FLETCHING_HOOD,
                ItemID.SLAYER_HOOD,
                ItemID.HUNTER_HOOD,
                ItemID.MINING_HOOD,
                ItemID.SMITHING_HOOD,
                ItemID.FISHING_HOOD,
                ItemID.COOKING_HOOD,
                ItemID.FIREMAKING_HOOD,
                ItemID.WOODCUTTING_HOOD,
                ItemID.FARMING_HOOD,


                ItemID.ATTACK_CAPE,
                ItemID.STRENGTH_CAPE,
                ItemID.DEFENCE_CAPE,
                ItemID.PRAYER_CAPE,
                ItemID.MAGIC_CAPE,
                ItemID.RANGING_CAPE,
                ItemID.RUNECRAFT_CAPE,
                ItemID.HITPOINTS_CAPE,
                ItemID.AGILITY_CAPE,
                ItemID.HERBLORE_CAPE,
                ItemID.THIEVING_CAPE,
                ItemID.CRAFTING_CAPE,
                ItemID.FLETCHING_CAPE,
                ItemID.SLAYER_CAPE,
                ItemID.HUNTER_CAPE,
                ItemID.MINING_CAPE,
                ItemID.SMITHING_CAPE,
                ItemID.FISHING_CAPE,
                ItemID.COOKING_CAPE,
                ItemID.FIREMAKING_CAPE,
                ItemID.WOODCUTTING_CAPE,
                ItemID.FARMING_CAPE,

                ItemID.ATTACK_CAPE_T_,
                ItemID.STRENGTH_CAPE_T_,
                ItemID.DEFENCE_CAPE_T_,
                ItemID.PRAYER_CAPE_T_,
                ItemID.MAGIC_CAPE_T_,
                ItemID.RANGING_CAPE_T_,
                ItemID.RUNECRAFT_CAPE_T_,
                ItemID.HITPOINTS_CAPE_T_,
                ItemID.AGILITY_CAPE_T_,
                ItemID.HERBLORE_CAPE_T_,
                ItemID.THIEVING_CAPE_T_,
                ItemID.CRAFTING_CAPE_T_,
                ItemID.FLETCHING_CAPE_T_,
                ItemID.SLAYER_CAPE_T_,
                ItemID.HUNTER_CAPE_T_,
                ItemID.MINING_CAPE_T_,
                ItemID.SMITHING_CAPE_T_,
                ItemID.FISHING_CAPE_T_,
                ItemID.COOKING_CAPE_T_,
                ItemID.FIREMAKING_CAPE_T_,
                ItemID.WOODCUT_CAPE_T_,
                ItemID.FARMING_CAPE_T_,
            ))
        )


        editPlayerFiles(itemsToReduce)

    }

    private fun editPlayerFiles(itemsToReduce: List<Pair<Int, List<Int>>>) {
        for (file in Paths.get(GameConstants.PLAYER_DIRECTORY).toFile().listFiles()) {

            if (file.extension != "json")
                continue

            if (!file.name.contains("Barry"))
                continue;

            val player = Player()
            player.username = file.nameWithoutExtension

            // Load the JSON file
            val result = PlayerLoading.getResult(player, false, true)

            if(result == 0) {
                println("Erroring on ${file.nameWithoutExtension}")
                continue
            }

            // Modify player
            // divide all the item ids by 10 in
            // inventory - bank - rune pouch - safe deposit - equipment

            val inventories = listOf(player.inventory, player.runePouch, player.safeDeposit, player.equipment, player.banks[0], player.banks[1], player.banks[2], player.banks[3], player.banks[4], player.banks[5], player.banks[6], player.banks[7], player.banks[8], player.banks[9])

            for(itemContainer in inventories) {
                if(itemContainer == null) {
                    continue
                }

                for(item in itemContainer.items) {

                    for(pair in itemsToReduce) {
                        val divideBy = pair.first
                        val itemsToDivide = pair.second
                        //println("Reducing ${itemContainer::class.simpleName} by $divideBy")
                        for(toReduce in itemsToDivide) {
                            if(item.id == toReduce) {
                                val beforeAmount = itemContainer.getAmount(item.id)
                                val slot = itemContainer.getSlot(item.id)
                                if(slot == -1) {
                                    continue
                                }
                                if(divideBy == 0) {
                                    itemContainer.delete(item, false)
                                } else {
                                    val newAmount = beforeAmount / divideBy
                                    itemContainer.set(slot, Item(item.id, newAmount))
                                }
                            }
                        }
                    }
                }
            }
            player.username = file.nameWithoutExtension
            println("Saving ${player.username} JSON")

            // Save JSON file again
            PlayerSaving.save(player, true)
        }
    }
}