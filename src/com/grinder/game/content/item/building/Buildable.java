package com.grinder.game.content.item.building;

import com.grinder.game.content.skill.SkillRequirement;
import com.grinder.game.model.Skill;
import com.grinder.util.ItemID;

/**
 * Handles the builds
 */
public enum Buildable {

    //THAMMARONS_SCEPTRE_U(new int[]{22552, 21804}, 22555, new SkillRequirement(Skill.CRAFTING, 68, 500), true),

    //CRAWS_BOW_U(new int[]{22547, 21804}, 22550, new SkillRequirement(Skill.CRAFTING, 68, 500), true),

    //VIGGORAS_CHAINMACE_U(new int[]{22542, 21804}, 22545, new SkillRequirement(Skill.CRAFTING, 68, 500), true),

    AVERNIC_DEFENDER(new int[]{12954, 22477}, 22322, new SkillRequirement(Skill.SMITHING, 85, 766), true),

    HEAVY_BALLISTA_PART1(new int[]{19589, 19592}, 19598, new SkillRequirement(Skill.FLETCHING, 72, 220)),
    HEAVY_BALLISTA_PART2(new int[]{19598, 19601}, 19607, new SkillRequirement(Skill.FLETCHING, 72, 220)),
    HEAVY_BALLISTA_FINISH(new int[]{19607, 19610}, 19481, new SkillRequirement(Skill.FLETCHING, 72, 220)),

    LIGHT_BALLISTA_PART1(new int[]{19586, 19592}, 19595, new SkillRequirement(Skill.FLETCHING, 47, 110)),
    LIGHT_BALLISTA_PART2(new int[]{19595, 19601}, 19604, new SkillRequirement(Skill.FLETCHING, 47, 110)),
    LIGHT_BALLISTA_FINISH(new int[]{19604, 19610}, 19478, new SkillRequirement(Skill.FLETCHING, 47, 110)),

    UNCUT_ZENYTE(new int[]{19529, 6573}, 19496, false),

    BRYOPHYTA_STAFF(new int[]{ItemID.BATTLESTAFF, 22372}, 22368, new SkillRequirement(Skill.CRAFTING, 62, 0), true),

    DEVOUT_BOOTS(new int[]{ItemID.HOLY_SANDALS, 22960}, 22954, true),

/*    STUDDED_CHAPS(new int[]{ItemID.LEATHER_CHAPS, ItemID.STEEL_STUDS}, ItemID.STUDDED_CHAPS, false),

    STUDDED_BODY(new int[]{ItemID.LEATHER_BODY, ItemID.STEEL_STUDS}, ItemID.STUDDED_BODY, false),*/

    BRIMSTONE_BOOTS(new int[]{23037, 22957}, 22951, true),

    AVA_ASSEMBLER(new int[]{21907, 10499}, 22109, true),




    HARMONISED_NIGHTMARE_STAFF(new int[]{24511, 24422}, 24423, true),
    VOLATILE_NIGHTMARE_STAFF(new int[]{24514, 24422}, 24424, true),
    ELDRITCH_NIGHTMARE_STAFF(new int[]{24517, 24422}, 24425, true),

    INFERNAL_AXE_UNCHARGED(new int[]{13242, 13233}, 13241, true),
    INFERNAL_AXE_UNCHARGED2(new int[]{13242, 6739}, 13241, true),

    INFERNAL_PICKAXE_UNCHARGED(new int[]{13244, 13233}, 13243, true),
    INFERNAL_PICKAXE_UNCHARGED2(new int[]{13244, 11920}, 13243, true),

    INFERNAL_HARPOON_UNCHARGED(new int[]{21033, 13233}, 21031, true),
    INFERNAL_HARPOON_UNCHARGED2(new int[]{21033, 21028}, 21031, true),

    STAFF_OF_LIGHT(new int[]{13256, 11791}, 22296, true),

    DRAGON_WHIP(new int[]{4151, 6640}, 15155, true),

    INFERNAL_WHIP(new int[]{15155, ItemID.INFERNAL_CAPE}, 15722, true),
    INFERNAL_WHIP_UNCHARGED(new int[]{15730, ItemID.INFERNAL_CAPE}, 15155, true),

    DRAGON_GODSWORD(new int[]{ItemID.ARMADYL_GODSWORD, 6640}, 15160, true),

    GUARDIAN_BOOTS(new int[]{21730, 11836}, 21733, true),

    HOLY_ORNAMENT_KIT(new int[]{22486, 25742}, 25738, true),
    SANGUINE_ORNAMENT_KIT(new int[]{22486, 25744}, 25741, true),

    RUNED_SCEPTRE(new int[]{9010, 9011}, 9012, null),

    SARADOMIN_SWORD_ENCHANTED(new int[]{11838, 12804}, 12809, true),

    CRYSTAL_KEY(new int[]{985, 987}, 989, null),

    STRANGE_SKULL(new int[]{9007, 9008}, 9009, null),

    SKULL_SCEPTRE(new int[]{9012, 9009}, 9013, null),

    TRIDENT_OF_SWAMP(new int[]{ItemID.UNCHARGED_TRIDENT, ItemID.MAGIC_FANG}, ItemID.UNCHARGED_TOXIC_TRIDENT, new SkillRequirement(Skill.CRAFTING, 59, 0)),

    UNCHARGED_TRIDENT_ENCHANCED(new int[]{ItemID.UNCHARGED_TRIDENT, ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE,
            ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE}, 22290, false),

    UNCHARGED_TRIDENT_ENCHANCE(new int[]{ItemID.UNCHARGED_TOXIC_TRIDENT, ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE,
            ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE}, 22290, false),

    UNCHARGED_TOXIC_TRIDENT_ENHANCE(new int[]{ItemID.UNCHARGED_TOXIC_TRIDENT, ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE,
            ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE, ItemID.KRAKEN_TENTACLE}, 22294, false),

    UNCHARGED_ENCHANCED_TRIDENT_UPGRADE(new int[]{22290, ItemID.MAGIC_FANG}, 22294, new SkillRequirement(Skill.CRAFTING, 59, 0)),

    TOXIC_STAFF_OF_THE_DEAD(new int[]{ItemID.STAFF_OF_THE_DEAD, ItemID.MAGIC_FANG}, 12902, new SkillRequirement(Skill.CRAFTING, 59, 0)),

    TOXIC_BLOWPIPE(new int[]{ItemBuilding.CHISEL, 12922}, 12924, new SkillRequirement(Skill.FLETCHING, 53, 0)),

    ODIUM_WARD_OR(new int[]{12802, 11926}, 12807, true),

    STAFF_OF_BALANCE(new int[]{ItemID.STAFF_OF_THE_DEAD, 24217}, 24144, true),

    MALEDICTION_WARD_OR(new int[]{12802, 11924}, 12806, true),

    RUNE_SCIMITAR_SARADOMIN(new int[]{1333, 23324}, 23332, false),
    RUNE_SCIMITAR_GUTHIX(new int[]{1333, 23321}, 23330, false),
    RUNE_SCIMITAR_ZAMORAK(new int[]{1333, 23327}, 23334, false),

    NEITZNOT_FACEGUARD(new int[]{10828, 24268}, 24271, false),

    STEAM_STAFF_OR(new int[]{11787, 12798}, 12795, true),

    MYSTIC_STEAM_STAFF_OR(new int[]{11789, 12798}, 12796, true),

    SLED(new int[]{4083, 4085}, 4084, true),

    MAX_FIRE_CAPE(new int[]{13281, 13342, 6570}, new int[]{13329, 13330}, true),

    MAX_AVA_CAPE(new int[]{13281, 13342, 10499}, new int[]{13337, 13338}, true),

    MAX_SARA_CAPE(new int[]{13281, 13342, 2412}, new int[]{13331, 13332}, true),

    MAX_ZAMORAK_CAPE(new int[]{13281, 13342, 2414}, new int[]{13333, 13334}, true),

    MAX_GUTHIX_CAPE(new int[]{13281, 13342, 2413}, new int[]{13335, 13336}, true),

    MAX_SARA_CAPE_IMBUED(new int[]{13342, 13281, 21791}, new int[]{21776, 21778}, true),

    MAX_ZAMORAK_CAPE_IMBUED(new int[]{13342, 13281, 21795}, new int[]{21780, 21782}, true),

    MAX_GUTHIX_CAPE_IMBUED(new int[]{13342, 13281, 21793}, new int[]{21784, 21786}, true),

    MAX_ARDY_CAPE(new int[]{13281, 13342, 13124}, new int[]{20760, 20764}, true),

    MAX_INFERNAL_CAPE(new int[]{13281, 13342, 21295}, new int[]{21285, 21282}, true),

    ASSEMBLER_MAX_CAPE(new int[]{22109, 13281, 13342}, new int[]{21898, 21900}, true),

    MYTHICAL_MAX_CAPE(new int[]{22114, 13281, 13342}, new int[]{24855, 24857}, true),

    DRAGON_SQ_SHIELD(new int[]{2366, 2368,}, 1187, true),

    DRAGON_FIRE_SHIELD(new int[]{1540, 11286}, 11284, new SkillRequirement(Skill.SMITHING, 90, 0), true),

    DRAGONFIRE_WARD(new int[]{1540, 22006}, 22003, new SkillRequirement(Skill.SMITHING, 90, 0), true),

    ANCIENT_WYVERN_SHIELD(new int[]{2890, 21637}, 21634, new SkillRequirement(Skill.SMITHING, 66, 0), true),

    BLESSED_SPIRIT_SHIELD(new int[]{12833, 12829}, 12831, new SkillRequirement(Skill.PRAYER, 85, 0), true),

    SPECTRAL_SPIRIT_SHIELD(new int[]{12831, 12823}, 12821, new SkillRequirement(Skill.PRAYER, 90, 0), true),

    ELYSIAN_SPIRIT_SHIELD(new int[]{12831, 12819}, 12817, new SkillRequirement(Skill.PRAYER, 90, 0), true),

    DIVINE_SPIRIT_SHIELD(new int[]{12831, 15802}, 15798, new SkillRequirement(Skill.PRAYER, 90, 0), true),

    ARCANE_SPIRIT_SHIELD(new int[]{12831, 12827}, 12825, new SkillRequirement(Skill.PRAYER, 90, 0), true),

    SERPENTINE_HELM(new int[]{ItemID.SERPENTINE_VISAGE, ItemBuilding.CHISEL}, ItemID.SERPENTINE_HELM_UNCHARGED_, new SkillRequirement(Skill.CRAFTING, 52, 0), false),

    MAGMA_HELM(new int[]{ItemID.MAGMA_MUTAGEN, ItemID.SERPENTINE_HELM_UNCHARGED_}, ItemID.MAGMA_HELM_UNCHARGED_, true),
    MAGMA_HELM_CHARGED(new int[]{ItemID.MAGMA_MUTAGEN, ItemID.SERPENTINE_HELM}, ItemID.MAGMA_HELM, true),

    TANZANITE_HELM(new int[]{ItemID.TANZANITE_MUTAGEN, ItemID.SERPENTINE_HELM_UNCHARGED_}, ItemID.TANZANITE_HELM_UNCHARGED_, true),
    TANZANITE_HELM_CHARGED(new int[]{ItemID.TANZANITE_MUTAGEN, ItemID.SERPENTINE_HELM}, ItemID.TANZANITE_HELM, true),

    GODSWORD_SHARD(new int[]{11818, 11820}, 11794),

    GODSWORD_BLADE(new int[]{11822, 11794}, 11798),

    BANDOS_GODSWORD(new int[]{11812, 11798}, 11804),

    SARADOMIN_GODSWORD(new int[]{11814, 11798}, 11806),

    ZAMORAK_GODSWORD(new int[]{11816, 11798}, 11808),

    ARMADYL_GODSWORD(new int[]{11810, 11798}, 11802),

    ANCIENT_GODSWORD(new int[]{11798, 26370}, 26233),

    COLOSSAL_POUCH(new int[]{ItemID.GIANT_POUCH, ItemID.MEDIUM_POUCH, ItemID.SMALL_POUCH, ItemID.LARGE_POUCH, ItemID.ABYSSAL_NEEDLE}, ItemID.COLOSSAL_POUCH, true),

    //BLOOD_FURY_AMULET(new int[]{ItemID.AMULET_OF_FURY, ItemID.BLOOD_SHARD}, ItemID.AMULET_OF_BLOOD_FURY),

    ZARYTE_CROSSBOW(new int[]{ItemID.ARMADYL_CROSSBOW, ItemID.NIHIL_HORN, ItemID.NIHIL_SHARD}, ItemID.ZARYTE_CROSSBOW, true),

    BANDOS_WHIP(new int[]{15797, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS}, 15751, new SkillRequirement(Skill.SMITHING, 90, 2250), true),
    ZAMORAK_WHIP(new int[]{15852, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS}, 15846, new SkillRequirement(Skill.SMITHING, 90, 2250), true),
    SARADOMIN_WHIP(new int[]{15853, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS}, 15848, new SkillRequirement(Skill.SMITHING, 90, 2250), true),
    GUTHIX_WHIP(new int[]{15854, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS}, 15850, new SkillRequirement(Skill.SMITHING, 90, 2250), true),



    TORVA_WHIP(new int[]{ItemID.TORVA_WHIP_DAMAGED, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.DRAGON_WHIP}, ItemID.TORVA_WHIP, new SkillRequirement(Skill.SMITHING, 90, 2250), true),
//    TORVA_FULL_HELMET(new int[]{ItemID.TORVA_FULL_HELM_DAMAGED, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS}, ItemID.TORVA_FULL_HELM, new SkillRequirement(Skill.SMITHING, 90, 2250), true),
//    TORVA_PLATEBODY(new int[]{ItemID.TORVA_PLATEBODY_DAMAGED, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS}, ItemID.TORVA_PLATEBODY, new SkillRequirement(Skill.SMITHING, 90, 2250), true),
//    TORVA_PLATELEGS(new int[]{ItemID.TORVA_PLATELEGS_DAMAGED, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS, ItemID.BANDOSIAN_COMPONENTS}, ItemID.TORVA_PLATELEGS, new SkillRequirement(Skill.SMITHING, 90, 2250), true),
//
//    PERNIX_COWL(new int[]{15895, 15898, 15898, 15898, 15898, 15898, 15898, 15898, 15898}, 15883, new SkillRequirement(Skill.SMITHING, 90, 2250), true),
//    PERNIX_BODY(new int[]{15896, 15898, 15898, 15898, 15898, 15898, 15898, 15898, 15898, 15898, 15898, 15898, 15898}, 15885, new SkillRequirement(Skill.SMITHING, 90, 2250), true),
//    PERNIX_CHAPS(new int[]{15897, 15898, 15898, 15898, 15898, 15898, 15898, 15898, 15898, 15898, 15898}, 15887, new SkillRequirement(Skill.SMITHING, 90, 2250), true),
//
//    VIRTUS_MASK(new int[]{15892, 15899, 15899, 15899, 15899, 15899, 15899, 15899, 15899}, 15877, new SkillRequirement(Skill.SMITHING, 90, 2250), true),
//    VIRTUS_TOP(new int[]{15893, 15899, 15899, 15899, 15899, 15899, 15899, 15899, 15899, 15899, 15899, 15899, 15899}, 15879, new SkillRequirement(Skill.SMITHING, 90, 2250), true),
//    VIRTUS_ROBE_BOTTOMS(new int[]{15894, 15899, 15899, 15899, 15899, 15899, 15899, 15899, 15899, 15899, 15899}, 15881, new SkillRequirement(Skill.SMITHING, 90, 2250), true),

    PEGASIAN_BOOTS(new int[]{13229, 2577}, 13237, new SkillRequirement(Skill.RUNECRAFTING, 60, 0), true),

    PRIMORDIAL_BOOTS(new int[]{13231, 11840}, 13239, new SkillRequirement(Skill.RUNECRAFTING, 60, 0), true),

    ETERNAL_BOOTS(new int[]{13227, 2579}, 13235, new SkillRequirement(Skill.RUNECRAFTING, 60, 0), true),

    INFERNAL_AXE(new int[]{6739, 13233}, 13241, new SkillRequirement(Skill.WOODCUTTING, 61, 200), true),

    INFERNAL_PICKAXE(new int[]{11920, 13233}, 13243, new SkillRequirement(Skill.SMITHING, 85, 200), true),

    INFERNAL_HARPOON(new int[]{13233, 21028}, 21031, new SkillRequirement(Skill.FISHING, 75, 200), true),

    VOLCANIC_WHIP(new int[]{4151, 12771}, 12773, true),

    KODAI_WAND(new int[]{21043, 6914}, 21006, true),

    DRAGON_HUNTER_LANCE(new int[]{22966, 11889}, 22978, true),

    // Cleaning items from poison with cleaning cloth

    BRONZE_DAGGER_P_PLUS_PLUS(new int[]{ItemID.BRONZE_DAGGER_P_PLUS_PLUS_, ItemID.CLEANING_CLOTH}, ItemID.BRONZE_DAGGER, true),
    BRONZE_DAGGER_P_PLUS_(new int[]{ItemID.BRONZE_DAGGER_P_PLUS_, ItemID.CLEANING_CLOTH}, ItemID.BRONZE_DAGGER, true),
    BRONZE_DAGGER_P(new int[]{ItemID.BRONZE_DAGGER_P_, ItemID.CLEANING_CLOTH}, ItemID.BRONZE_DAGGER, true),

    IRON_DAGGER_P_PLUS_PLUS(new int[]{ItemID.IRON_DAGGER_P_PLUS_PLUS_, ItemID.CLEANING_CLOTH}, ItemID.IRON_DAGGER, true),
    IRON_DAGGER_P_PLUS_(new int[]{ItemID.IRON_DAGGER_P_PLUS_, ItemID.CLEANING_CLOTH}, ItemID.IRON_DAGGER, true),
    IRON_DAGGER_P(new int[]{ItemID.IRON_DAGGER_P_, ItemID.CLEANING_CLOTH}, ItemID.IRON_DAGGER, true),

    STEEL_DAGGER_P_PLUS_PLUS(new int[]{ItemID.STEEL_DAGGER_P_PLUS_PLUS_, ItemID.CLEANING_CLOTH}, ItemID.STEEL_DAGGER, true),
    STEEL_DAGGER_P_PLUS_(new int[]{ItemID.STEEL_DAGGER_P_PLUS_, ItemID.CLEANING_CLOTH}, ItemID.STEEL_DAGGER, true),
    STEEL_DAGGER_P(new int[]{ItemID.STEEL_DAGGER_P_, ItemID.CLEANING_CLOTH}, ItemID.STEEL_DAGGER, true),

    MITHRIL_DAGGER_P_PLUS_PLUS(new int[]{ItemID.MITHRIL_DAGGER_P_PLUS_PLUS_, ItemID.CLEANING_CLOTH}, ItemID.MITHRIL_DAGGER, true),
    MITHRIL_DAGGER_P_PLUS_(new int[]{ItemID.MITHRIL_DAGGER_P_PLUS_, ItemID.CLEANING_CLOTH}, ItemID.MITHRIL_DAGGER, true),
    MITHRIL_DAGGER_P(new int[]{ItemID.MITHRIL_DAGGER_P_, ItemID.CLEANING_CLOTH}, ItemID.MITHRIL_DAGGER, true),

    BLACK_DAGGER_P_PLUS_PLUS(new int[]{ItemID.BLACK_DAGGER_P_PLUS_PLUS_, ItemID.CLEANING_CLOTH}, ItemID.BLACK_DAGGER, true),
    BLACK_DAGGER_P_PLUS_(new int[]{ItemID.BLACK_DAGGER_P_PLUS_, ItemID.CLEANING_CLOTH}, ItemID.BLACK_DAGGER, true),
    BLACK_DAGGER_P(new int[]{ItemID.BLACK_DAGGER_P_, ItemID.CLEANING_CLOTH}, ItemID.BLACK_DAGGER, true),

    ADAMANT_DAGGER_P_PLUS_PLUS(new int[]{ItemID.ADAMANT_DAGGER_P_PLUS_PLUS_, ItemID.CLEANING_CLOTH}, ItemID.ADAMANT_DAGGER, true),
    ADAMANT_DAGGER_P_PLUS_(new int[]{ItemID.ADAMANT_DAGGER_P_PLUS_, ItemID.CLEANING_CLOTH}, ItemID.ADAMANT_DAGGER, true),
    ADAMANT_DAGGER_P(new int[]{ItemID.ADAMANT_DAGGER_P_, ItemID.CLEANING_CLOTH}, ItemID.ADAMANT_DAGGER, true),

    RUNE_DAGGER_P_PLUS_PLUS(new int[]{ItemID.RUNE_DAGGER_P_PLUS_PLUS_, ItemID.CLEANING_CLOTH}, ItemID.RUNE_DAGGER, true),
    RUNE_DAGGER_P_PLUS_(new int[]{ItemID.RUNE_DAGGER_P_PLUS_, ItemID.CLEANING_CLOTH}, ItemID.RUNE_DAGGER, true),
    RUNE_DAGGER_P(new int[]{ItemID.RUNE_DAGGER_P_, ItemID.CLEANING_CLOTH}, ItemID.RUNE_DAGGER, true),

    DRAGON_DAGGER_P_PLUS_PLUS(new int[]{ItemID.DRAGON_DAGGER_P_PLUS_PLUS_, ItemID.CLEANING_CLOTH}, ItemID.DRAGON_DAGGER, true),
    DRAGON_DAGGER_P_PLUS_(new int[]{ItemID.DRAGON_DAGGER_P_PLUS_, ItemID.CLEANING_CLOTH}, ItemID.DRAGON_DAGGER, true),
    DRAGON_DAGGER_P(new int[]{ItemID.DRAGON_DAGGER_P_, ItemID.CLEANING_CLOTH}, ItemID.DRAGON_DAGGER, true),

    ABYSSAL_DAGGER_P_PLUS_PLUS_(new int[]{ItemID.ABYSSAL_DAGGER_P_PLUS_PLUS_, ItemID.CLEANING_CLOTH}, ItemID.ABYSSAL_DAGGER, true),
    ABYSSAL_DAGGER_P_PLUS_(new int[]{ItemID.ABYSSAL_DAGGER_P_PLUS_, ItemID.CLEANING_CLOTH}, ItemID.ABYSSAL_DAGGER, true),
    ABYSSAL_DAGGER_P(new int[]{ItemID.ABYSSAL_DAGGER_P_, ItemID.CLEANING_CLOTH}, ItemID.ABYSSAL_DAGGER, true),

    BONE_DAGGER_P_PLUS_PLUS(new int[]{ItemID.BONE_DAGGER_P_PLUS_PLUS_, ItemID.CLEANING_CLOTH}, ItemID.BONE_DAGGER, true),
    BONE_DAGGER_P_PLUS_(new int[]{ItemID.BONE_DAGGER_P_PLUS_, ItemID.CLEANING_CLOTH}, ItemID.BONE_DAGGER, true),
    BONE_DAGGER_P(new int[]{ItemID.BONE_DAGGER_P_, ItemID.CLEANING_CLOTH}, ItemID.BONE_DAGGER, true),

    VOLCANIC_WHIP_C(new int[]{12773, 3188}, 4151, true),

    FROZEN_WHIP(new int[]{4151, 12769}, 12774, true),

    FROZEN_WHIP_C(new int[]{12774, 3188}, 4151, true),

    ZGS_OR(new int[]{11808, 20077}, 20374, false),

    SGS_OR(new int[]{11806, 20074}, 20372, false),

    BGS_OR(new int[]{11804, 20071}, 20370, false),

    AGS_OR(new int[]{11802, 20068}, 20368, false),

    TORTURE_OR(new int[]{19553, 20062}, 20366, false),

    ANGUISH_OR(new int[]{19547, 22246}, 22249, false),

    TORMENTED_OR(new int[]{19544, 23348}, 23444, false),

    OCCULT_OR(new int[]{12002, 20065}, 19720, false),

    FURY_OR(new int[]{6585, 12526}, 12436, false),

    BLUE_DBOW(new int[]{11235, 12757}, 12766, true),

    BLUE_DBOW_C(new int[]{12766, 3188}, 11235, true),

    YELLOW_DBOW(new int[]{11235, 12761}, 12767, true),

    YELLOW_DBOW_C(new int[]{12767, 3188}, 11235, true),

    GREEN_DBOW(new int[]{11235, 12759}, 12765, true),

    GREEN_DBOW_C(new int[]{12767, 3188}, 11235, true),

    WHITE_DBOW(new int[]{11235, 12763}, 12768, true),

    WHITE_DBOW_C(new int[]{12768, 3188}, 11235, true),

    SLAYER_HELMET(new int[]{8921, 4166, 4164, 4168, 4551, 4155}, 11864,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    SLAYER_HELMET_10(new int[]{8901, 4166, 4164, 4168, 4551, 4155}, 11864,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    SLAYER_HELMET_9(new int[]{8903, 4166, 4164, 4168, 4551, 4155}, 11864,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    SLAYER_HELMET_8(new int[]{8905, 4166, 4164, 4168, 4551, 4155}, 11864,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    SLAYER_HELMET_7(new int[]{8907, 4166, 4164, 4168, 4551, 4155}, 11864,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    SLAYER_HELMET_6(new int[]{8909, 4166, 4164, 4168, 4551, 4155}, 11864,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    SLAYER_HELMET_5(new int[]{8911, 4166, 4164, 4168, 4551, 4155}, 11864,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    SLAYER_HELMET_4(new int[]{8913, 4166, 4164, 4168, 4551, 4155}, 11864,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    SLAYER_HELMET_3(new int[]{8915, 4166, 4164, 4168, 4551, 4155}, 11864,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    SLAYER_HELMET_2(new int[]{8917, 4166, 4164, 4168, 4551, 4155}, 11864,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    SLAYER_HELMET_1(new int[]{8919, 4166, 4164, 4168, 4551, 4155}, 11864,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    SLAYER_HELMET_IMBUED(new int[]{11784, 4166, 4164, 4168, 4551, 4155}, 11865,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    SLAYER_HELMET_IMBUED_10(new int[]{11774, 4166, 4164, 4168, 4551, 4155}, 11865,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    SLAYER_HELMET_IMBUED_9(new int[]{11775, 4166, 4164, 4168, 4551, 4155}, 11865,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    SLAYER_HELMET_IMBUED_8(new int[]{11776, 4166, 4164, 4168, 4551, 4155}, 11865,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    SLAYER_HELMET_IMBUED_7(new int[]{11777, 4166, 4164, 4168, 4551, 4155}, 11865,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    SLAYER_HELMET_IMBUED_6(new int[]{11778, 4166, 4164, 4168, 4551, 4155}, 11865,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    SLAYER_HELMET_IMBUED_5(new int[]{11779, 4166, 4164, 4168, 4551, 4155}, 11865,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    SLAYER_HELMET_IMBUED_4(new int[]{11780, 4166, 4164, 4168, 4551, 4155}, 11865,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    SLAYER_HELMET_IMBUED_3(new int[]{11781, 4166, 4164, 4168, 4551, 4155}, 11865,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    SLAYER_HELMET_IMBUED_2(new int[]{11782, 4166, 4164, 4168, 4551, 4155}, 11865,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    SLAYER_HELMET_IMBUED_1(new int[]{11783, 4166, 4164, 4168, 4551, 4155}, 11865,
            new SkillRequirement(Skill.CRAFTING, 55, -1)),

    PURPLE_SLAYER_HELM(new int[]{21275, 11864}, 21264, false),

    PURPLE_SLAYER_HELM_I(new int[]{21275, 11865}, 21266, false),

    GREEN_SLAYER_HELM(new int[]{7981, 11864}, 19643, false),

    GREEN_SLAYER_HELM_I(new int[]{7981, 11865}, 19645, false),

    RED_SLAYER_HELM(new int[]{7979, 11864}, 19647, false),

    RED_SLAYER_HELM_I(new int[]{7979, 11865}, 19649, false),

    BLACK_SLAYER_HELM(new int[]{7980, 11864}, 19639, false),

    BLACK_SLAYER_HELM_I(new int[]{7980, 11865}, 19641, false),

    TURQOISE_SLAYER_HELM(new int[]{21907, 11864}, 21888, false),

    TURQOISE_SLAYER_HELM_I(new int[]{21907, 11865}, 21890, false),

    HYDRA_SLAYER_HELM(new int[]{23077, 11864}, 23073, false),

    HYDRA_SLAYER_HELM_I(new int[]{23077, 11865}, 23075, false),

    TWISTED_SLAYER_HELMET(new int[]{24466, 11864}, 24370, false),

    TWISTED_SLAYER_HELMET_I(new int[]{24466, 11865}, 24444, false),

    TZTOK_SLAYER_HELMET(new int[]{6570, 11864}, 25898, true),

    TZTOK_SLAYER_HELMET_I(new int[]{6570, 11865}, 25900, true),

    VAMPYRIC_SLAYER_HELMET(new int[]{22486, 11864}, 25904, true),

    VAMPYRIC_SLAYER_HELMET_I(new int[]{22486, 11865}, 25906, true),

    TZKAL_SLAYER_HELMET(new int[]{21295, 11864}, 25910, true),

    TZKAL_SLAYER_HELMET_I(new int[]{21295, 11865}, 25912, true),

    ZENYTE_SHARD(new int[]{19529, 6571}, 19496, false),

    DRAGON_PICKAXE_OR(new int[]{11920, 12800}, 12797, true),

    GRANITE_MAUL_OR(new int[]{12849, 4153}, 12848, true),

    GRANITE_MAUL_ORNATE_HANDLE(new int[]{24229, 4153}, 24225, true),

    BERSERKER_NECKLACE_OR(new int[]{11128, 23237}, 23240, true),

    GRANITE_MAUL_ORNATE_HANDLE_OR(new int[]{12849, 24225}, 24227, true),

    DRAGON_SHIELD_G(new int[]{12532, 1187}, 12418, false),

    DRAGON_CHAINBODY_G(new int[]{12534, 3140}, 12414, false),

    DRAGON_PLATELEGS_G(new int[]{12536, 4087}, 12415, false),

    DRAGON_PLATESKIRT_G(new int[]{12536, 4585}, 12416, false),

    DRAGON_HELM_G(new int[]{12538, 11335}, 12417, false),

    DRAGON_SCIMITAR_OR(new int[]{20002, 4587}, 20000, false),

    DRAGON_PLATEBODY_G(new int[]{22236, 21892}, 22242, false),

    DRAGON_BOOTS_G(new int[]{22231, 11840}, 22234, false),

    DRAGON_DEFENDER_T(new int[]{20143, 12954}, 19722, false),

    BONECRUSHER_NECKLACE(new int[]{
            ItemID.BONECRUSHER,
            ItemID.DRAGONBONE_NECKLACE,
            ItemID.HYDRA_TAIL
    }, ItemID.BONECRUSHER_NECKALCE, false),

    BRIMSTONE_RING(new int[]{
        22969,
                22971,
                22973
    }, 22975, true)


    ;

    /**
     * The components required
     */
    private int[] components;

    /**
     * The finished product(s)
     */
    private int[] result;

    /**
     * The skill requirement
     */
    private SkillRequirement skillRequirement;

    /**
     * The confirmation
     */
    private boolean confirmation;

    Buildable(int[] components, int result, SkillRequirement skillRequirement) {
        this(components, new int[]{result}, skillRequirement);
    }

    Buildable(int[] components, int[] result, SkillRequirement skillRequirement) {
        this.setComponents(components);
        this.setResult(result);
        this.setSkillRequirement(skillRequirement);
        this.setConfirmation(confirmation);
    }

    Buildable(int[] components, int result, SkillRequirement skillRequirement, boolean confirmation) {
        this(components, new int[]{result}, skillRequirement, confirmation);
    }

    Buildable(int[] components, int[] result, SkillRequirement skillRequirement, boolean confirmation) {
        this.setComponents(components);
        this.setResult(result);
        this.setSkillRequirement(skillRequirement);
        this.setConfirmation(confirmation);
    }

    Buildable(int[] components, int result) {
        this(components, new int[]{result});
    }

    Buildable(int[] components, int[] result) {
        this.setComponents(components);
        this.setResult(result);
        this.setSkillRequirement(null);
        this.setConfirmation(false);
    }

    /**
     * Represents an item build
     *
     * @param components   the components
     * @param result       the result
     * @param confirmation if confirmation required
     */
    Buildable(int[] components, int result, boolean confirmation) {
        this(components, new int[]{result}, confirmation);
    }

    /**
     * Represents an item build
     *
     * @param components   the components
     * @param result       the result
     * @param confirmation if confirmation required
     */
    Buildable(int[] components, int[] result, boolean confirmation) {
        this.setComponents(components);
        this.setResult(result);
        this.setSkillRequirement(null);
        this.setConfirmation(confirmation);
    }

    public int[] getComponents() {
        return components;
    }

    public void setComponents(int[] components) {
        this.components = components;
    }

    public int[] getResult() {
        return result;
    }

    public void setResult(int[] result) {
        this.result = result;
    }

    public SkillRequirement getSkillRequirement() {
        return skillRequirement;
    }

    public void setSkillRequirement(SkillRequirement skillRequirement) {
        this.skillRequirement = skillRequirement;
    }

    public boolean isConfirmation() {
        return confirmation;
    }

    public void setConfirmation(boolean confirmation) {
        this.confirmation = confirmation;
    }

}
