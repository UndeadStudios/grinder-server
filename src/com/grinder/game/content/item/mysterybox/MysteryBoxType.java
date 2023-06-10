package com.grinder.game.content.item.mysterybox;

import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

import java.util.Arrays;

import static com.grinder.util.ItemID.BLOOD_MONEY;

/**
 * The rewards
 */
public enum MysteryBoxType {
    // MEMBERS MYSTERY BOX
    MYSTERY_BOX(6199, 10, new MysteryBoxRewardItem[]{


            new MysteryBoxRewardItem(new Item(ItemID.WARRIOR_RING, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.HELM_OF_NEITIZNOT, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.CURVED_BONE, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.LONG_BONE, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.GRANITE_CLAMP, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.MYSTIC_MUD_STAFF, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.DHAROKS_HELM, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.DHAROKS_PLATEBODY, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.DHAROKS_PLATELEGS, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.DHAROKS_GREATAXE, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.GUTHANS_HELM, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.GUTHANS_CHAINSKIRT, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.GUTHANS_PLATEBODY, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.GUTHANS_WARSPEAR, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.TORAGS_HELM, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.TORAGS_PLATEBODY, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.TORAGS_PLATELEGS, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.TORAGS_HAMMERS, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.VERACS_HELM, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.VERACS_BRASSARD, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.VERACS_PLATESKIRT, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.VERACS_FLAIL, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.DRAGON_CHAINBODY_2, 1), 10.0),


            new MysteryBoxRewardItem(new Item(ItemID.SUPER_ATTACK_4_2, 100), 20.0),
            new MysteryBoxRewardItem(new Item(ItemID.SUPER_STRENGTH_4_2, 100), 20.0),
            new MysteryBoxRewardItem(new Item(ItemID.SUPER_DEFENCE_4_2, 100), 20.0),
            new MysteryBoxRewardItem(new Item(ItemID.RANGING_POTION_4_2, 100), 20.0),
            new MysteryBoxRewardItem(new Item(ItemID.MAGIC_POTION_4_2, 100), 20.0),
            new MysteryBoxRewardItem(new Item(ItemID.BURNT_PAGE, 25), 20.0),
            new MysteryBoxRewardItem(new Item(ItemID.RED_CHINCHOMPA_2, 250), 20.0),
            new MysteryBoxRewardItem(new Item(ItemID.BLACK_CHINCHOMPA, 100), 20.0),
            new MysteryBoxRewardItem(new Item(ItemID.RUNE_PLATELEGS, 1), 20.0),
            new MysteryBoxRewardItem(new Item(ItemID.RUNITE_BAR_2, 50), 20.0),
            new MysteryBoxRewardItem(new Item(ItemID.WIZARD_BOOTS, 1), 20.0),
            new MysteryBoxRewardItem(new Item(ItemID.RANGER_BOOTS, 1), 20.0),
            new MysteryBoxRewardItem(new Item(ItemID.INFINITY_BOOTS, 1), 20.0),
            new MysteryBoxRewardItem(new Item(ItemID.INFINITY_GLOVES, 1), 20.0),
            new MysteryBoxRewardItem(new Item(ItemID.DRAGON_BATTLEAXE, 1), 20.0),
            new MysteryBoxRewardItem(new Item(ItemID.RUNE_CROSSBOW, 1), 20.0),
            new MysteryBoxRewardItem(new Item(ItemID.DRAGON_MACE, 1), 20.0),


            new MysteryBoxRewardItem(new Item(ItemID.RING_OF_RECOIL_2, 15), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.DRAGON_SQ_SHIELD, 1), 30.0),


            new MysteryBoxRewardItem(new Item(ItemID.COMBAT_BRACELET_4_, 1), 40.0),
            new MysteryBoxRewardItem(new Item(ItemID.DORGESHUUN_CROSSBOW, 1), 40.0),
            new MysteryBoxRewardItem(new Item(ItemID.TEACHER_WAND, 1), 40.0),
            new MysteryBoxRewardItem(new Item(ItemID.BONE_BOLTS, 1000), 40.0),


            new MysteryBoxRewardItem(new Item(ItemID.DRAGON_BONES_2, 25), 50.0),
            new MysteryBoxRewardItem(new Item(ItemID.DRAGON_SCIMITAR, 1), 50.0),
            new MysteryBoxRewardItem(new Item(ItemID.YEW_LOGS_2, 75), 50.0),
            new MysteryBoxRewardItem(new Item(ItemID.SUPER_RESTORE_4_2, 50), 50.0),
            new MysteryBoxRewardItem(new Item(ItemID.BLACK_DRAGONHIDE_2, 100), 50.0),
            new MysteryBoxRewardItem(new Item(ItemID.RAW_MONKFISH_2, 75), 50.0),


            new MysteryBoxRewardItem(new Item(ItemID.DRAGON_MED_HELM, 1), 60.0),
            new MysteryBoxRewardItem(new Item(ItemID.IRON_BAR_2, 75), 60.0),
            new MysteryBoxRewardItem(new Item(ItemID.STEEL_BAR_2, 75), 60.0),
            new MysteryBoxRewardItem(new Item(ItemID.BOW_STRING_2, 100), 60.0),
            new MysteryBoxRewardItem(new Item(ItemID.BRONZE_BAR_2, 250), 60.0),
            new MysteryBoxRewardItem(new Item(ItemID.RUNITE_BOLTS, 100), 60.0),


            new MysteryBoxRewardItem(new Item(ItemID.FEATHER, 1000), 70.0),
            new MysteryBoxRewardItem(new Item(ItemID.MAGIC_SHORTBOW, 1), 70.0),
            new MysteryBoxRewardItem(new Item(ItemID.UNCUT_SAPPHIRE_2, 75), 70.0),
            new MysteryBoxRewardItem(new Item(ItemID.UNCUT_DIAMOND_2, 50), 70.0),
            new MysteryBoxRewardItem(new Item(ItemID.MAPLE_LOGS_2, 100), 70.0),


            new MysteryBoxRewardItem(new Item(ItemID.DRAGON_DAGGER, 1), 80.0),
            new MysteryBoxRewardItem(new Item(ItemID.AIR_BATTLESTAFF, 1), 80.0),
            new MysteryBoxRewardItem(new Item(ItemID.RUNE_PLATEBODY, 1), 80.0),


            new MysteryBoxRewardItem(new Item(ItemID.RUNE_SWORD, 1), 90.0),
            new MysteryBoxRewardItem(new Item(ItemID.RUNE_ARROW, 100), 90.0),
            new MysteryBoxRewardItem(new Item(ItemID.LOBSTER_2, 250), 90.0),
            new MysteryBoxRewardItem(new Item(22281, 250), 90.0)


    }),

    BARROWS_MYSTERY_BOX(15200, 10, new MysteryBoxRewardItem[]{new MysteryBoxRewardItem(new Item(ItemID.DHAROKS_ARMOUR_SET), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.VERACS_ARMOUR_SET, 2), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.KARILS_ARMOUR_SET, 2), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.AHRIMS_ARMOUR_SET, 2), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.TORAGS_ARMOUR_SET, 2), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.DHAROKS_HELM, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.DHAROKS_PLATEBODY, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.DHAROKS_PLATELEGS, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.DHAROKS_GREATAXE, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.VERACS_HELM, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.VERACS_BRASSARD, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.VERACS_PLATESKIRT, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.VERACS_FLAIL, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.KARILS_COIF, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.KARILS_LEATHERTOP, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.KARILS_LEATHERSKIRT, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.KARILS_CROSSBOW, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.BOLT_RACK, 3500), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.AHRIMS_HOOD, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.AHRIMS_ROBETOP, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.AHRIMS_ROBESKIRT, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.AHRIMS_STAFF, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.TORAGS_HELM, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.TORAGS_PLATEBODY, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.TORAGS_PLATELEGS, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.TORAGS_HAMMERS, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.DHAROKS_HELM, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.DHAROKS_PLATEBODY, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.DHAROKS_PLATELEGS, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.DHAROKS_GREATAXE, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.VERACS_HELM, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.VERACS_BRASSARD, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.VERACS_PLATESKIRT, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.VERACS_FLAIL, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.KARILS_COIF, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.KARILS_LEATHERTOP, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.KARILS_LEATHERSKIRT, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.KARILS_CROSSBOW, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.BOLT_RACK, 3500), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.AHRIMS_HOOD, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.AHRIMS_ROBETOP, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.AHRIMS_ROBESKIRT, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.AHRIMS_STAFF, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.TORAGS_HELM, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.TORAGS_PLATEBODY, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.TORAGS_PLATELEGS, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.TORAGS_HAMMERS, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.DHAROKS_HELM, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.DHAROKS_PLATEBODY, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.DHAROKS_PLATELEGS, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.DHAROKS_GREATAXE, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.VERACS_HELM, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.VERACS_BRASSARD, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.VERACS_PLATESKIRT, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.VERACS_FLAIL, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.KARILS_COIF, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.KARILS_LEATHERTOP, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.KARILS_LEATHERSKIRT, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.KARILS_CROSSBOW, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.BOLT_RACK, 3500), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.AHRIMS_HOOD, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.AHRIMS_ROBETOP, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.AHRIMS_ROBESKIRT, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.AHRIMS_STAFF, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.TORAGS_HELM, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.TORAGS_PLATEBODY, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.TORAGS_PLATELEGS, 4), 30.0),
            new MysteryBoxRewardItem(new Item(ItemID.TORAGS_HAMMERS, 4), 30.0)
    }),


    LEGENDARY_MYSTERY_BOX(15201, 1, new MysteryBoxRewardItem[]{ // 9.99 / 6.99
            new MysteryBoxRewardItem(new Item(ItemID.GHRAZI_RAPIER), 1.0),
            new MysteryBoxRewardItem(new Item(ItemID.INFERNAL_CAPE), 1.0),
            new MysteryBoxRewardItem(new Item(ItemID.TWISTED_BOW), 1.0),
            new MysteryBoxRewardItem(new Item(22326), 1.0),
            new MysteryBoxRewardItem(new Item(22327), 1.0),
            new MysteryBoxRewardItem(new Item(22328), 1.0),
            new MysteryBoxRewardItem(new Item(22486), 1.0),
            new MysteryBoxRewardItem(new Item(13263), 1.0),
            new MysteryBoxRewardItem(new Item(19481), 1.0),
            new MysteryBoxRewardItem(new Item(21003), 1.0),
            new MysteryBoxRewardItem(new Item(21006), 1.0),
            new MysteryBoxRewardItem(new Item(11802, 1), 5.0),
            new MysteryBoxRewardItem(new Item(15153), 5.0),
            new MysteryBoxRewardItem(new Item(15155), 5.0),
            new MysteryBoxRewardItem(new Item(15156), 5.0),
            new MysteryBoxRewardItem(new Item(12821), 5.0),
            new MysteryBoxRewardItem(new Item(12825), 5.0),
            new MysteryBoxRewardItem(new Item(12817), 5.0),
            new MysteryBoxRewardItem(new Item(11785, 1), 5.0),
            new MysteryBoxRewardItem(new Item(22978), 1.0),
            new MysteryBoxRewardItem(new Item(13271, 1), 5.0),
            new MysteryBoxRewardItem(new Item(11826, 1), 5.0),
            new MysteryBoxRewardItem(new Item(11828, 1), 5.0),
            new MysteryBoxRewardItem(new Item(11830, 1), 5.0),
            new MysteryBoxRewardItem(new Item(11832, 1), 5.0),
            new MysteryBoxRewardItem(new Item(11834, 1), 5.0),
            new MysteryBoxRewardItem(new Item(22981), 5.0),
            new MysteryBoxRewardItem(new Item(10350), 5.0),
            new MysteryBoxRewardItem(new Item(10346), 5.0),
            new MysteryBoxRewardItem(new Item(10348), 5.0),
            new MysteryBoxRewardItem(new Item(10352), 5.0),
            new MysteryBoxRewardItem(new Item(10334), 5.0),
            new MysteryBoxRewardItem(new Item(10330), 5.0),
            new MysteryBoxRewardItem(new Item(10332), 5.0),
            new MysteryBoxRewardItem(new Item(10336), 5.0),
            new MysteryBoxRewardItem(new Item(10342), 5.0),
            new MysteryBoxRewardItem(new Item(10338), 5.0),
            new MysteryBoxRewardItem(new Item(10340), 5.0),
            new MysteryBoxRewardItem(new Item(10344), 5.0),
            new MysteryBoxRewardItem(new Item(12422), 5.0),
            new MysteryBoxRewardItem(new Item(12424), 5.0),
            new MysteryBoxRewardItem(new Item(12426), 5.0),
            new MysteryBoxRewardItem(new Item(12437), 5.0),
            new MysteryBoxRewardItem(new Item(21018), 5.0),
            new MysteryBoxRewardItem(new Item(21021), 5.0),
            new MysteryBoxRewardItem(new Item(21024), 5.0),
            new MysteryBoxRewardItem(new Item(21733, 1), 5.0),
            new MysteryBoxRewardItem(new Item(13239, 1), 5.0),
            new MysteryBoxRewardItem(new Item(13237, 1), 5.0),
            new MysteryBoxRewardItem(new Item(13235, 1), 5.0),
            new MysteryBoxRewardItem(new Item(5609), 5.0),
            new MysteryBoxRewardItem(new Item(5608), 5.0),
            new MysteryBoxRewardItem(new Item(5607), 5.0),
            new MysteryBoxRewardItem(new Item(21214), 5.0),
            new MysteryBoxRewardItem(new Item(22351), 5.0),
            new MysteryBoxRewardItem(new Item(22353), 5.0),
            new MysteryBoxRewardItem(new Item(4565), 5.0)
    }), // Top rare items in-game guranteed

    PVP_MYSTERY_BOX(15202, 5, new MysteryBoxRewardItem[]{new MysteryBoxRewardItem(new Item(ItemID.TWISTED_BOW), 1.0), // 4.99
            new MysteryBoxRewardItem(new Item(ItemID.GHRAZI_RAPIER), 1.0),
            new MysteryBoxRewardItem(new Item(ItemID.DRAGON_CLAWS), 1.0),

            new MysteryBoxRewardItem(new Item(ItemID.ELDRITCH_NIGHTMARE_STAFF), 1.0),
            new MysteryBoxRewardItem(new Item(ItemID.DRAGON_WARHAMMER, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.ARMADYL_GODSWORD), 5.0),
            new MysteryBoxRewardItem(new Item(ItemID.BANDOS_GODSWORD, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.SARADOMIN_GODSWORD, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.ZAMORAK_GODSWORD, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.NIGHTMARE_STAFF), 5.0),
            new MysteryBoxRewardItem(new Item(ItemID.STAFF_OF_LIGHT), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.KODAI_WAND), 5.0),
            new MysteryBoxRewardItem(new Item(15155), 10.0),
            new MysteryBoxRewardItem(new Item(15153), 10.0),
            new MysteryBoxRewardItem(new Item(15157), 10.0),
            new MysteryBoxRewardItem(new Item(15158), 10.0),
            new MysteryBoxRewardItem(new Item(15164), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.KRAKEN_TENTACLE, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.LIGHT_BALLISTA), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.HEAVY_BALLISTA), 5.0),
            new MysteryBoxRewardItem(new Item(ItemID.TWISTED_BUCKLER), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.ARMADYL_CROSSBOW), 8.0),
            new MysteryBoxRewardItem(new Item(ItemID.DRAGON_HUNTER_CROSSBOW), 8.0),
            new MysteryBoxRewardItem(new Item(ItemID.BERSERKER_RING, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.ARCHERS_RING, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.SEERS_RING, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.RING_OF_THE_GODS, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.TYRANNICAL_RING, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.RING_OF_SUFFERING, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.DRAGONFIRE_SHIELD, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.DRAGONFIRE_WARD, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.BANDOS_CHESTPLATE, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.BANDOS_TASSETS, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.RANGER_BOOTS, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.WIZARD_BOOTS, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.AMULET_OF_FURY_2, 5), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.ELDER_MAUL_3), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.ARCANE_SPIRIT_SHIELD), 2.0),
            new MysteryBoxRewardItem(new Item(ItemID.ELYSIAN_SPIRIT_SHIELD), 2.0),
            new MysteryBoxRewardItem(new Item(ItemID.SPECTRAL_SPIRIT_SHIELD), 2.0),
            new MysteryBoxRewardItem(new Item(ItemID.PRIMORDIAL_BOOTS), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.PEGASIAN_BOOTS), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.ETERNAL_BOOTS), 10.0),
            new MysteryBoxRewardItem(new Item(22613, 1), 10.0), // Vesta set start
            new MysteryBoxRewardItem(new Item(22616, 1), 10.0),
            new MysteryBoxRewardItem(new Item(22619, 1), 10.0),
            new MysteryBoxRewardItem(new Item(22610, 1), 10.0), // Vesta set end
            new MysteryBoxRewardItem(new Item(22625, 1), 10.0), // Statius set start
            new MysteryBoxRewardItem(new Item(22628, 1), 10.0),
            new MysteryBoxRewardItem(new Item(22631, 1), 10.0),
            new MysteryBoxRewardItem(new Item(22622, 1), 10.0), // Statius set end
            new MysteryBoxRewardItem(new Item(22638, 1), 10.0), // Morrigans set start
            new MysteryBoxRewardItem(new Item(22641, 1), 10.0),
            new MysteryBoxRewardItem(new Item(22644, 1), 10.0), // Morrigans set end
            new MysteryBoxRewardItem(new Item(22650, 1), 10.0), // Zuriels set start
            new MysteryBoxRewardItem(new Item(22653, 1), 10.0),
            new MysteryBoxRewardItem(new Item(22656, 1), 10.0),
            new MysteryBoxRewardItem(new Item(22647, 1), 10.0), // Zuriels set end
            new MysteryBoxRewardItem(new Item(ItemID.ABYSSAL_DAGGER_P_PLUS_PLUS_), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.ABYSSAL_BLUDGEON), 10.0),
            new MysteryBoxRewardItem(new Item(22322), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.AMULET_OF_ETERNAL_GLORY), 2.0),
            new MysteryBoxRewardItem(new Item(22547), 10.0),
            new MysteryBoxRewardItem(new Item(22323), 10.0),
            new MysteryBoxRewardItem(new Item(22552), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.DARK_BOW, 1), 10.0),
            new MysteryBoxRewardItem(new Item(22486), 7.0),
            new MysteryBoxRewardItem(new Item(ItemID.STAFF_OF_THE_DEAD), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.ANGLERFISH_2, 1000), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.MASTER_WAND, 1), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.MAGES_BOOK, 1), 10.0)
    }),

    GILDED_MYSTERY_BOX(15203, 6, new MysteryBoxRewardItem[]{ // 9.99
            new MysteryBoxRewardItem(new Item(15378), 1.0),
            new MysteryBoxRewardItem(new Item(15380), 1.0),
            new MysteryBoxRewardItem(new Item(15432), 1.0),
            new MysteryBoxRewardItem(new Item(15379), 1.0),
            new MysteryBoxRewardItem(new Item(15435), 1.0),
            new MysteryBoxRewardItem(new Item(15270), 1.0),
            new MysteryBoxRewardItem(new Item(15166), 1.0),
            new MysteryBoxRewardItem(new Item(15250), 1.0),
            new MysteryBoxRewardItem(new Item(15223), 1.0),
            new MysteryBoxRewardItem(new Item(15225), 1.0),

            new MysteryBoxRewardItem(new Item(15239), 1.0),
            new MysteryBoxRewardItem(new Item(15240), 1.0),
            new MysteryBoxRewardItem(new Item(15241), 1.0),
            new MysteryBoxRewardItem(new Item(15242), 1.0),
            new MysteryBoxRewardItem(new Item(15243), 1.0),
            new MysteryBoxRewardItem(new Item(15169), 1.0),
            new MysteryBoxRewardItem(new Item(15225), 1.0),
            new MysteryBoxRewardItem(new Item(15156), 6.0),
            new MysteryBoxRewardItem(new Item(15393), 3.0),
            new MysteryBoxRewardItem(new Item(15389), 3.0),
            new MysteryBoxRewardItem(new Item(15391), 3.0),
            new MysteryBoxRewardItem(new Item(15386), 3.0),
            new MysteryBoxRewardItem(new Item(15387), 3.0),
            new MysteryBoxRewardItem(new Item(15388), 3.0),
            new MysteryBoxRewardItem(new Item(15390), 3.0),
            new MysteryBoxRewardItem(new Item(15392), 3.0),
            new MysteryBoxRewardItem(new Item(15220), 2.0),
            new MysteryBoxRewardItem(new Item(20205), 8.0),
            new MysteryBoxRewardItem(new Item(20208), 8.0),
            new MysteryBoxRewardItem(new Item(ItemID.GILDED_ARMOUR_SET_LG_), 3.0),
            new MysteryBoxRewardItem(new Item(ItemID.GILDED_ARMOUR_SET_SK_), 3.0),
            new MysteryBoxRewardItem(new Item(ItemID.GILDED_FULL_HELM), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.GILDED_PLATEBODY), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.GILDED_PLATELEGS), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.GILDED_PLATESKIRT), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.GILDED_KITESHIELD), 10.0),
            new MysteryBoxRewardItem(new Item(23258), 10.0),
            new MysteryBoxRewardItem(new Item(23264), 10.0),
            new MysteryBoxRewardItem(new Item(23267), 10.0),
            new MysteryBoxRewardItem(new Item(23261), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.GILDED_SCIMITAR), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.GILDED_2H_SWORD), 10.0),
            new MysteryBoxRewardItem(new Item(15224), 5.0),
            new MysteryBoxRewardItem(new Item(15237), 5.0),
            new MysteryBoxRewardItem(new Item(ItemID.GILDED_MED_HELM), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.GILDED_CHAINBODY), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.GILDED_SQ_SHIELD), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.GILDED_SPEAR), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.GILDED_HASTA), 10.0),
            new MysteryBoxRewardItem(new Item(ItemID.GILDED_BOOTS), 10.0),
            new MysteryBoxRewardItem(new Item(15229), 8.0),
            new MysteryBoxRewardItem(new Item(15230), 8.0),
            new MysteryBoxRewardItem(new Item(15231), 8.0),
            new MysteryBoxRewardItem(new Item(15227), 8.0),
            new MysteryBoxRewardItem(new Item(15228), 8.0),
            new MysteryBoxRewardItem(new Item(15232), 8.0),
            new MysteryBoxRewardItem(new Item(15233), 8.0),
            new MysteryBoxRewardItem(new Item(15226), 5.0),
            new MysteryBoxRewardItem(new Item(15221), 8.0),
            new MysteryBoxRewardItem(new Item(15222), 8.0),
            new MysteryBoxRewardItem(new Item(15254), 8.0),
            new MysteryBoxRewardItem(new Item(15235), 8.0),
            new MysteryBoxRewardItem(new Item(15234), 8.0),
            new MysteryBoxRewardItem(new Item(15244), 8.0),
            new MysteryBoxRewardItem(new Item(15236), 8.0),
            new MysteryBoxRewardItem(new Item(15238), 8.0),
            new MysteryBoxRewardItem(new Item(12419), 10.0),
            new MysteryBoxRewardItem(new Item(12420), 10.0),
            new MysteryBoxRewardItem(new Item(12421), 10.0),
            new MysteryBoxRewardItem(new Item(6465), 10.0),
            new MysteryBoxRewardItem(new Item(23282), 10.0),
            new MysteryBoxRewardItem(new Item(2949), 10.0),
            new MysteryBoxRewardItem(new Item(2946), 10.0),
            new MysteryBoxRewardItem(new Item(2948), 10.0),
            new MysteryBoxRewardItem(new Item(13074), 10.0),
            new MysteryBoxRewardItem(new Item(15262, 75), 10.0),
            new MysteryBoxRewardItem(new Item(26788), 10.0),


    }),

    SACRED_MYSTERY_BOX(15204, 1, new MysteryBoxRewardItem[]{ // 14.99

            new MysteryBoxRewardItem(new Item(15806), 1.0),
            new MysteryBoxRewardItem(new Item(15807), 1.0),
            new MysteryBoxRewardItem(new Item(15808), 1.0),
            new MysteryBoxRewardItem(new Item(15809), 1.0),
            new MysteryBoxRewardItem(new Item(15810), 1.0),
            new MysteryBoxRewardItem(new Item(15811), 1.0),
            new MysteryBoxRewardItem(new Item(15812), 1.0),
            new MysteryBoxRewardItem(new Item(15813), 1.0),
            new MysteryBoxRewardItem(new Item(15814), 1.0),
            new MysteryBoxRewardItem(new Item(15815), 1.0),
            new MysteryBoxRewardItem(new Item(15816), 1.0),
            new MysteryBoxRewardItem(new Item(15817), 1.0),
            new MysteryBoxRewardItem(new Item(15818), 1.0),
            new MysteryBoxRewardItem(new Item(15819), 1.0),
            new MysteryBoxRewardItem(new Item(15820), 1.0),
            new MysteryBoxRewardItem(new Item(15821), 1.0),
            new MysteryBoxRewardItem(new Item(15822), 1.0),
            new MysteryBoxRewardItem(new Item(15823), 1.0),
            new MysteryBoxRewardItem(new Item(15855), 1.0),
            new MysteryBoxRewardItem(new Item(15856), 1.0),

            //new ItemReward(new Item(15194), 1.0),
            new MysteryBoxRewardItem(new Item(15192), 5.0),
            new MysteryBoxRewardItem(new Item(15167), 5.0),
            new MysteryBoxRewardItem(new Item(15367), 5.0),


            new MysteryBoxRewardItem(new Item(15368), 5.0),
            new MysteryBoxRewardItem(new Item(15369), 5.0),



            new MysteryBoxRewardItem(new Item(15168), 5.0),
            new MysteryBoxRewardItem(new Item(13343), 5.0),
            new MysteryBoxRewardItem(new Item(13344), 5.0),
            new MysteryBoxRewardItem(new Item(1050), 5.0),
            new MysteryBoxRewardItem(new Item(15169), 5.0),
            new MysteryBoxRewardItem(new Item(15170), 5.0),
            new MysteryBoxRewardItem(new Item(15255), 5.0),
            new MysteryBoxRewardItem(new Item(15256), 5.0),
            new MysteryBoxRewardItem(new Item(15257), 5.0),
            new MysteryBoxRewardItem(new Item(15258), 5.0),
            new MysteryBoxRewardItem(new Item(15259), 5.0),
            new MysteryBoxRewardItem(new Item(15260), 5.0),
            new MysteryBoxRewardItem(new Item(15361), 5.0),
            new MysteryBoxRewardItem(new Item(15362), 5.0),
            new MysteryBoxRewardItem(new Item(15363), 5.0),
            new MysteryBoxRewardItem(new Item(15364), 5.0),
            new MysteryBoxRewardItem(new Item(15365), 5.0),
            // End of santa

            new MysteryBoxRewardItem(new Item(15161), 5.0),
            new MysteryBoxRewardItem(new Item(15162), 5.0),
            new MysteryBoxRewardItem(new Item(15187), 5.0),
            new MysteryBoxRewardItem(new Item(15188), 5.0),
            new MysteryBoxRewardItem(new Item(15189), 5.0),
            new MysteryBoxRewardItem(new Item(15190), 5.0),
            new MysteryBoxRewardItem(new Item(15191), 5.0),
            new MysteryBoxRewardItem(new Item(11863), 5.0),
            new MysteryBoxRewardItem(new Item(11862), 5.0),
            new MysteryBoxRewardItem(new Item(1038), 5.0),
            new MysteryBoxRewardItem(new Item(1040), 5.0),
            new MysteryBoxRewardItem(new Item(1042), 5.0),
            new MysteryBoxRewardItem(new Item(1044), 5.0),
            new MysteryBoxRewardItem(new Item(1046), 5.0),
            new MysteryBoxRewardItem(new Item(1048), 5.0),
            new MysteryBoxRewardItem(new Item(15358), 5.0),
            new MysteryBoxRewardItem(new Item(15359), 5.0),
            new MysteryBoxRewardItem(new Item(15360), 5.0),
            // End of phats
            new MysteryBoxRewardItem(new Item(15171), 5.0),
            new MysteryBoxRewardItem(new Item(15182), 5.0),
            new MysteryBoxRewardItem(new Item(15183), 5.0),
            new MysteryBoxRewardItem(new Item(15184), 5.0),
            new MysteryBoxRewardItem(new Item(15185), 5.0),
            new MysteryBoxRewardItem(new Item(15186), 5.0),
            new MysteryBoxRewardItem(new Item(11847), 5.0),
            new MysteryBoxRewardItem(new Item(1053), 5.0),
            new MysteryBoxRewardItem(new Item(1055), 5.0),
            new MysteryBoxRewardItem(new Item(1057), 5.0),
            new MysteryBoxRewardItem(new Item(15268), 5.0),
            new MysteryBoxRewardItem(new Item(15269), 5.0),
            new MysteryBoxRewardItem(new Item(15376), 5.0),
            new MysteryBoxRewardItem(new Item(15375), 5.0)

    }),

    VIP_MYSTERY_BOX(15724, 10, new MysteryBoxRewardItem[]{ // 49.99

            new MysteryBoxRewardItem(new Item(995, 1_000_000_000), 1.0),
            new MysteryBoxRewardItem(new Item(13307, 750_000), 1.0),
            new MysteryBoxRewardItem(new Item(13190), 1.0),
            new MysteryBoxRewardItem(new Item(15830), 1.0),
            new MysteryBoxRewardItem(new Item(15372), 1.0),
            new MysteryBoxRewardItem(new Item(15373), 1.0),
            new MysteryBoxRewardItem(new Item(15374), 1.0),

            new MysteryBoxRewardItem(new Item(15431), 1.0),
            new MysteryBoxRewardItem(new Item(25604), 1.0),
            new MysteryBoxRewardItem(new Item(25314), 1.0),
            new MysteryBoxRewardItem(new Item(15750), 1.0),
            new MysteryBoxRewardItem(new Item(15798), 1.0),
            new MysteryBoxRewardItem(new Item(25733), 1.0),
            new MysteryBoxRewardItem(new Item(25741), 1.0),
            new MysteryBoxRewardItem(new Item(25738), 1.0),
            new MysteryBoxRewardItem(new Item(25734), 1.0),

            new MysteryBoxRewardItem(new Item(23842), 1.0),
            new MysteryBoxRewardItem(new Item(23845), 1.0),
            new MysteryBoxRewardItem(new Item(23848), 1.0),

            new MysteryBoxRewardItem(new Item(24419), 1.0),
            new MysteryBoxRewardItem(new Item(24420), 1.0),
            new MysteryBoxRewardItem(new Item(24421), 1.0),
            new MysteryBoxRewardItem(new Item(24417), 1.0),

            new MysteryBoxRewardItem(new Item(26382), 1.0),
            new MysteryBoxRewardItem(new Item(26384), 1.0),
            new MysteryBoxRewardItem(new Item(26386), 1.0),
            new MysteryBoxRewardItem(new Item(15720), 1.0),
            new MysteryBoxRewardItem(new Item(15883), 1.0),
            new MysteryBoxRewardItem(new Item(15885), 1.0),
            new MysteryBoxRewardItem(new Item(15887), 1.0),
            new MysteryBoxRewardItem(new Item(15877), 1.0),
            new MysteryBoxRewardItem(new Item(15879), 1.0),
            new MysteryBoxRewardItem(new Item(15881), 1.0),
            new MysteryBoxRewardItem(new Item(15804), 1.0),

            new MysteryBoxRewardItem(new Item(20997), 1.0),
            new MysteryBoxRewardItem(new Item(21295), 1.0),
            new MysteryBoxRewardItem(new Item(15722), 1.0),

            new MysteryBoxRewardItem(new Item(24423), 1.0),
            new MysteryBoxRewardItem(new Item(24424), 10.0),
            new MysteryBoxRewardItem(new Item(24425), 10.0),
            new MysteryBoxRewardItem(new Item(26233), 10.0),
            new MysteryBoxRewardItem(new Item(26374), 10.0),

            new MysteryBoxRewardItem(new Item(26374), 10.0),
            new MysteryBoxRewardItem(new Item(20014), 10.0),
            new MysteryBoxRewardItem(new Item(20011), 10.0),

            new MysteryBoxRewardItem(new Item(24865), 10.0),
            new MysteryBoxRewardItem(new Item(24864), 10.0),
            new MysteryBoxRewardItem(new Item(24866), 10.0),
            new MysteryBoxRewardItem(new Item(24863), 10.0),

            new MysteryBoxRewardItem(new Item(13173), 10.0),
            new MysteryBoxRewardItem(new Item(13175), 10.0),
            new MysteryBoxRewardItem(new Item(15263), 10.0),
            new MysteryBoxRewardItem(new Item(15266), 10.0),
            new MysteryBoxRewardItem(new Item(15210), 10.0),
            new MysteryBoxRewardItem(new Item(15211), 10.0),
            new MysteryBoxRewardItem(new Item(15212), 10.0),



    }),

    /*
    Ideas

    Nightmare siren's tome
    https://oldschool.runescape.wiki/w/Tumeken%27s_heka
    Ancient ceremonial robes
    https://oldschool.runescape.wiki/w/Zaryte_bow#Charged                            https://runescape.fandom.com/wiki/Zaryte_bow
    https://runescape.fandom.com/wiki/Virtus_wand
    https://runescape.fandom.com/wiki/Virtus_book
    3x Zuriel set
    3x Morrigans set
    3x Statius set
    3x Vesta set
    Nightmare staffs


    Dragon hunter bow
    Divine spirit shield
    Hydro gear pieces
    Inquisitor pieces
    Torva
    Virtus
    Pernix

    Shayzien blowpipe
    Gilded Ancestral set
    Max Level Lamp
    $50 BOND
    Infernal Cape
    Twisted Bow
    Special rapier
    Special sangui
    Special scythe
    Champion amulet
    Cape of skulls
    Gilded Osmumten's fang
    Masori Set
    Zaryte Vambs
    Zaryte Bow
    Eldinis Ward
    Corrupted set (perfected) non degrading
    Celestial Wing
    Tarantula's cephalorax
    Devout Boots (In diff colors)
    Holy wraps (in diff colors)
    Tumeken's shadow staff

     */
    HUNDRED_DOLLAR_MYSTERY_BOX(15725, 5, new MysteryBoxRewardItem[]{ // 99.99
            new MysteryBoxRewardItem(new Item(15378), 1.0),
            new MysteryBoxRewardItem(new Item(15380), 1.0),
            new MysteryBoxRewardItem(new Item(15432), 1.0),
            new MysteryBoxRewardItem(new Item(15379), 1.0),
            new MysteryBoxRewardItem(new Item(15435), 1.0),
            new MysteryBoxRewardItem(new Item(15270), 1.0),
            new MysteryBoxRewardItem(new Item(15166), 1.0),
            new MysteryBoxRewardItem(new Item(15250), 1.0),
            new MysteryBoxRewardItem(new Item(15223), 3.0),
            new MysteryBoxRewardItem(new Item(15225), 1.0),
            new MysteryBoxRewardItem(new Item(15169), 2.0),
            new MysteryBoxRewardItem(new Item(15156), 6.0),
            new MysteryBoxRewardItem(new Item(15393), 3.0),
            new MysteryBoxRewardItem(new Item(2949), 10.0),
            new MysteryBoxRewardItem(new Item(2946), 10.0),
            new MysteryBoxRewardItem(new Item(2948), 10.0),
            new MysteryBoxRewardItem(new Item(13074), 10.0),
            new MysteryBoxRewardItem(new Item(15262, 75), 10.0)
    }),

    SUPER_MYSTERY_BOX(15205, 3, new MysteryBoxRewardItem[]{ // Med-rare items in quantities 1.99
            new MysteryBoxRewardItem(new Item(11826), 3.0),
            new MysteryBoxRewardItem(new Item(11828), 3.0),
            new MysteryBoxRewardItem(new Item(11830), 3.0),
            new MysteryBoxRewardItem(new Item(11832), 3.0),
            new MysteryBoxRewardItem(new Item(11834), 3.0),
            new MysteryBoxRewardItem(new Item(11804), 3.0),
            new MysteryBoxRewardItem(new Item(ItemID.KRAKEN_TENTACLE, 1), 3.0),
            new MysteryBoxRewardItem(new Item(11908), 3.0),
            new MysteryBoxRewardItem(new Item(ItemID.UNCHARGED_TOXIC_TRIDENT), 3.0),
            new MysteryBoxRewardItem(new Item(2581), 10.0),
            new MysteryBoxRewardItem(new Item(19994), 10.0),
            new MysteryBoxRewardItem(new Item(12596), 10.0),
            new MysteryBoxRewardItem(new Item(12848), 10.0),
            new MysteryBoxRewardItem(new Item(12809), 3.0),
            new MysteryBoxRewardItem(new Item(21902), 10.0),
            new MysteryBoxRewardItem(new Item(12926), 10.0),
            new MysteryBoxRewardItem(new Item(21000), 10.0),
            new MysteryBoxRewardItem(new Item(22284), 10.0),
            new MysteryBoxRewardItem(new Item(6918), 10.0),
            new MysteryBoxRewardItem(new Item(6916), 10.0),
            new MysteryBoxRewardItem(new Item(6924), 10.0),
            new MysteryBoxRewardItem(new Item(6889), 10.0),
            new MysteryBoxRewardItem(new Item(6914), 3.0),
            new MysteryBoxRewardItem(new Item(2579), 10.0),
            new MysteryBoxRewardItem(new Item(11791), 10.0),
            new MysteryBoxRewardItem(new Item(10551), 10.0),
            new MysteryBoxRewardItem(new Item(6585, 1), 10.0),
            new MysteryBoxRewardItem(new Item(7462, 1), 10.0),
            new MysteryBoxRewardItem(new Item(11284), 10.0),
            new MysteryBoxRewardItem(new Item(6563), 10.0),
            new MysteryBoxRewardItem(new Item(13233), 3.0),
            new MysteryBoxRewardItem(new Item(13221), 3.0),
            new MysteryBoxRewardItem(new Item(10075), 10.0),
            new MysteryBoxRewardItem(new Item(13117), 10.0),
            new MysteryBoxRewardItem(new Item(12845), 10.0),
            new MysteryBoxRewardItem(new Item(12337), 10.0),
            new MysteryBoxRewardItem(new Item(12351), 10.0),
            new MysteryBoxRewardItem(new Item(6548), 10.0),
            new MysteryBoxRewardItem(new Item(12359), 10.0),
            new MysteryBoxRewardItem(new Item(20595), 3.0),
            new MysteryBoxRewardItem(new Item(20517), 3.0),
            new MysteryBoxRewardItem(new Item(20520), 3.0),
            new MysteryBoxRewardItem(new Item(4675, 1), 10.0),
            new MysteryBoxRewardItem(new Item(4151, 1), 3.0),
            new MysteryBoxRewardItem(new Item(11235, 1), 3.0),
            new MysteryBoxRewardItem(new Item(6731, 1), 3.0),
            new MysteryBoxRewardItem(new Item(6733, 1), 3.0),
            new MysteryBoxRewardItem(new Item(6735, 1), 3.0),
            new MysteryBoxRewardItem(new Item(6737, 1), 3.0),
            new MysteryBoxRewardItem(new Item(12601), 3.0),
            new MysteryBoxRewardItem(new Item(19710), 3.0),
            new MysteryBoxRewardItem(new Item(21892), 3.0),
            new MysteryBoxRewardItem(new Item(21847), 10.0),
            new MysteryBoxRewardItem(new Item(21849), 10.0),
            new MysteryBoxRewardItem(new Item(21851), 10.0),
            new MysteryBoxRewardItem(new Item(21853), 10.0),
            new MysteryBoxRewardItem(new Item(21855), 10.0),
            new MysteryBoxRewardItem(new Item(21857), 10.0),
            new MysteryBoxRewardItem(new Item(13265), 3.0),
            new MysteryBoxRewardItem(new Item(13576), 10.0),
            new MysteryBoxRewardItem(new Item(10887), 10.0),

            new MysteryBoxRewardItem(new Item(7668), 10.0),
            new MysteryBoxRewardItem(new Item(12856), 10.0),
            new MysteryBoxRewardItem(new Item(12855), 10.0),
            new MysteryBoxRewardItem(new Item(20716), 10.0),
            new MysteryBoxRewardItem(new Item(11926), 3.0),
            new MysteryBoxRewardItem(new Item(11924), 11.0),
            new MysteryBoxRewardItem(new Item(20166), 3.0)
    }),

    EXTREME_MYSTERY_BOX(15206, 1, new MysteryBoxRewardItem[]{ // Real good rares and some in quantities 3.99
            new MysteryBoxRewardItem(new Item(13263), 1.0),
            new MysteryBoxRewardItem(new Item(13652), 1.0),
            new MysteryBoxRewardItem(new Item(21003), 1.0),
            new MysteryBoxRewardItem(new Item(22296), 1.0),
            new MysteryBoxRewardItem(new Item(11802), 1.0),
            new MysteryBoxRewardItem(new Item(11804, 1), 5.0),
            new MysteryBoxRewardItem(new Item(11806, 1), 5.0),
            new MysteryBoxRewardItem(new Item(11808, 1), 5.0),
            new MysteryBoxRewardItem(new Item(19478), 5.0),
            new MysteryBoxRewardItem(new Item(15152), 5.0),
            new MysteryBoxRewardItem(new Item(13271, 1), 2.0),
            new MysteryBoxRewardItem(new Item(21015), 1.0),
            new MysteryBoxRewardItem(new Item(21000), 1.0),
            new MysteryBoxRewardItem(new Item(21018), 1.0),
            new MysteryBoxRewardItem(new Item(21021), 1.0),
            new MysteryBoxRewardItem(new Item(21024), 1.0),
            new MysteryBoxRewardItem(new Item(11826), 1.0),
            new MysteryBoxRewardItem(new Item(11828), 1.0),
            new MysteryBoxRewardItem(new Item(11830), 1.0),
            new MysteryBoxRewardItem(new Item(11832), 1.0),
            new MysteryBoxRewardItem(new Item(11834), 1.0),
            new MysteryBoxRewardItem(new Item(21634), 1.0),
            new MysteryBoxRewardItem(new Item(22003), 1.0),
            new MysteryBoxRewardItem(new Item(11785), 1.0),
            new MysteryBoxRewardItem(new Item(12002), 1.0),
            new MysteryBoxRewardItem(new Item(19547), 1.0),
            new MysteryBoxRewardItem(new Item(19553), 1.0),
            new MysteryBoxRewardItem(new Item(12691), 1.0),
            new MysteryBoxRewardItem(new Item(12692), 1.0),
            new MysteryBoxRewardItem(new Item(22975), 1.0),
            new MysteryBoxRewardItem(new Item(20724), 1.0),
            new MysteryBoxRewardItem(new Item(21301), 1.0),
            new MysteryBoxRewardItem(new Item(21304), 1.0),
            new MysteryBoxRewardItem(new Item(15157), 1.0),
            new MysteryBoxRewardItem(new Item(15158), 1.0),
            new MysteryBoxRewardItem(new Item(15153), 1.0),
            new MysteryBoxRewardItem(new Item(15164), 1.0),
            new MysteryBoxRewardItem(new Item(19707), 1.0),
            new MysteryBoxRewardItem(new Item(3486), 1.0),
            new MysteryBoxRewardItem(new Item(3481), 1.0),
            new MysteryBoxRewardItem(new Item(3483), 1.0),
            new MysteryBoxRewardItem(new Item(3485), 1.0),
            new MysteryBoxRewardItem(new Item(3488), 1.0),
            new MysteryBoxRewardItem(new Item(12931), 1.0),
            new MysteryBoxRewardItem(new Item(13239), 1.0),
            new MysteryBoxRewardItem(new Item(13237), 1.0),
            new MysteryBoxRewardItem(new Item(13235), 1.0),
            new MysteryBoxRewardItem(new Item(11791), 1.0),
            new MysteryBoxRewardItem(new Item(12457), 1.0),
            new MysteryBoxRewardItem(new Item(12458), 1.0),
            new MysteryBoxRewardItem(new Item(12459), 1.0),
            new MysteryBoxRewardItem(new Item(12419), 1.0),
            new MysteryBoxRewardItem(new Item(12420), 1.0),
            new MysteryBoxRewardItem(new Item(12421), 1.0),
            new MysteryBoxRewardItem(new Item(13222), 1.0),
            new MysteryBoxRewardItem(new Item(12637), 1.0),
            new MysteryBoxRewardItem(new Item(12638), 1.0),
            new MysteryBoxRewardItem(new Item(12639), 1.0),
            new MysteryBoxRewardItem(new Item(1419), 1.0),
            new MysteryBoxRewardItem(new Item(9013), 1.0),
            new MysteryBoxRewardItem(new Item(20263), 1.0),
            new MysteryBoxRewardItem(new Item(8969), 1.0)
    }),

    VOTING_MYSTERY_BOX(15207, 1, new MysteryBoxRewardItem[]{
            /*new ItemReward(new Item(12526), 30.0),
            new ItemReward(new Item(20071), 30.0),
            new ItemReward(new Item(20068), 30.0),
            new ItemReward(new Item(20074), 30.0),
            new ItemReward(new Item(20077), 30.0),
            new ItemReward(new Item(20143), 30.0),
            new ItemReward(new Item(12532), 30.0),
            new ItemReward(new Item(12534), 30.0),
            new ItemReward(new Item(12536), 30.0),
            new ItemReward(new Item(12538), 30.0),
            new ItemReward(new Item(20002), 30.0),
            new ItemReward(new Item(12757), 30.0),
            new ItemReward(new Item(12759), 30.0),
            new ItemReward(new Item(12761), 30.0),
            new ItemReward(new Item(12763), 30.0),
            new ItemReward(new Item(12769), 30.0),
            new ItemReward(new Item(12771), 30.0),*/
            new MysteryBoxRewardItem(new Item(554, 500), 30.0),
            new MysteryBoxRewardItem(new Item(555, 500), 30.0),
            new MysteryBoxRewardItem(new Item(556, 500), 30.0),
            new MysteryBoxRewardItem(new Item(557, 500), 30.0),
            new MysteryBoxRewardItem(new Item(558, 500), 30.0),
            new MysteryBoxRewardItem(new Item(559, 500), 30.0),
            new MysteryBoxRewardItem(new Item(560, 250), 30.0),
            new MysteryBoxRewardItem(new Item(561, 250), 30.0),
            new MysteryBoxRewardItem(new Item(562, 250), 30.0),
            new MysteryBoxRewardItem(new Item(563, 250), 30.0),
            new MysteryBoxRewardItem(new Item(564, 250), 30.0),
            new MysteryBoxRewardItem(new Item(565, 250), 30.0),
            new MysteryBoxRewardItem(new Item(566, 250), 30.0),
            new MysteryBoxRewardItem(new Item(314, 500), 30.0),
            new MysteryBoxRewardItem(new Item(313, 500), 30.0),
            new MysteryBoxRewardItem(new Item(1734, 500), 30.0),
            new MysteryBoxRewardItem(new Item(2358, 250), 30.0),
            new MysteryBoxRewardItem(new Item(1738, 250), 30.0),
            new MysteryBoxRewardItem(new Item(1760, 250), 30.0),
            new MysteryBoxRewardItem(new Item(1762, 250), 30.0),
            new MysteryBoxRewardItem(new Item(1626, 100), 30.0),
            new MysteryBoxRewardItem(new Item(1630, 100), 30.0),
            new MysteryBoxRewardItem(new Item(1624, 50), 30.0),
            new MysteryBoxRewardItem(new Item(1622, 50), 30.0),
            new MysteryBoxRewardItem(new Item(1620, 50), 30.0),
            new MysteryBoxRewardItem(new Item(1618, 50), 30.0),
            new MysteryBoxRewardItem(new Item(1632, 25), 30.0),
            new MysteryBoxRewardItem(new Item(1750, 150), 30.0),
            new MysteryBoxRewardItem(new Item(1748, 50), 30.0),
            new MysteryBoxRewardItem(new Item(1780, 250), 30.0),
            new MysteryBoxRewardItem(new Item(238, 250), 30.0),
            new MysteryBoxRewardItem(new Item(244, 250), 30.0),
            new MysteryBoxRewardItem(new Item(4698, 250), 30.0),
            new MysteryBoxRewardItem(new Item(222, 250), 30.0),
            new MysteryBoxRewardItem(new Item(226, 250), 30.0),
            new MysteryBoxRewardItem(new Item(224, 250), 30.0),
            new MysteryBoxRewardItem(new Item(1974, 250), 30.0),
            new MysteryBoxRewardItem(new Item(240, 250), 30.0),
            new MysteryBoxRewardItem(new Item(232, 250), 30.0),
            new MysteryBoxRewardItem(new Item(2971, 250), 30.0),
            new MysteryBoxRewardItem(new Item(3139, 250), 30.0),
            new MysteryBoxRewardItem(new Item(6019, 250), 30.0),
            new MysteryBoxRewardItem(new Item(200, 100), 30.0),
            new MysteryBoxRewardItem(new Item(204, 100), 30.0),
            new MysteryBoxRewardItem(new Item(208, 100), 30.0),
            new MysteryBoxRewardItem(new Item(212, 100), 30.0),
            new MysteryBoxRewardItem(new Item(216, 100), 30.0),
            new MysteryBoxRewardItem(new Item(220, 100), 30.0),
            new MysteryBoxRewardItem(new Item(3052, 100), 30.0),
            new MysteryBoxRewardItem(new Item(2486, 50), 30.0),
            new MysteryBoxRewardItem(new Item(5299, 50), 30.0),
            new MysteryBoxRewardItem(new Item(5300, 50), 30.0),
            new MysteryBoxRewardItem(new Item(5303, 50), 30.0),
            new MysteryBoxRewardItem(new Item(5301, 50), 30.0),
            new MysteryBoxRewardItem(new Item(5302, 50), 30.0),
            new MysteryBoxRewardItem(new Item(5303, 50), 30.0),
            new MysteryBoxRewardItem(new Item(5304, 50), 30.0),
            new MysteryBoxRewardItem(new Item(537, 50), 30.0),
            new MysteryBoxRewardItem(new Item(1520, 250), 30.0),
            new MysteryBoxRewardItem(new Item(1512, 300), 30.0),
            new MysteryBoxRewardItem(new Item(1518, 250), 30.0),
            new MysteryBoxRewardItem(new Item(1522, 300), 30.0),
            new MysteryBoxRewardItem(new Item(1516, 250), 30.0),
            new MysteryBoxRewardItem(new Item(1514, 100), 30.0),
            new MysteryBoxRewardItem(new Item(2350, 250), 30.0),
            new MysteryBoxRewardItem(new Item(2352, 250), 30.0),
            new MysteryBoxRewardItem(new Item(2354, 150), 30.0),
            new MysteryBoxRewardItem(new Item(2360, 100), 30.0),
            new MysteryBoxRewardItem(new Item(2362, 50), 30.0),
            new MysteryBoxRewardItem(new Item(2364, 50), 30.0),
            new MysteryBoxRewardItem(new Item(437, 500), 30.0),
            new MysteryBoxRewardItem(new Item(439, 500), 30.0),
            new MysteryBoxRewardItem(new Item(441, 500), 30.0),
            new MysteryBoxRewardItem(new Item(454, 250), 30.0),
            new MysteryBoxRewardItem(new Item(448, 250), 30.0),
            new MysteryBoxRewardItem(new Item(336, 250), 30.0),
            new MysteryBoxRewardItem(new Item(332, 250), 30.0),
            new MysteryBoxRewardItem(new Item(378, 250), 30.0),
            new MysteryBoxRewardItem(new Item(372, 250), 30.0),
            new MysteryBoxRewardItem(new Item(384, 150), 30.0),
            new MysteryBoxRewardItem(new Item(396, 150), 30.0),
            new MysteryBoxRewardItem(new Item(390, 100), 30.0),
            new MysteryBoxRewardItem(new Item(73, 10), 30.0)
    }),

    STAFF_PRESENT(15215, 0, new MysteryBoxRewardItem[]{
            new MysteryBoxRewardItem(new Item(995, 20000000), 1.0),
            new MysteryBoxRewardItem(new Item(995, 1720000), 1.0),
            new MysteryBoxRewardItem(new Item(995, 1690000), 2.0),
            new MysteryBoxRewardItem(new Item(995, 1660000), 3.0),
            new MysteryBoxRewardItem(new Item(995, 1630000), 4.0),
            new MysteryBoxRewardItem(new Item(995, 1600000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 1570000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 1540000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 1510000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 1480000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 1450000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 1420000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 1390000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 1360000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 1330000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 1300000), 6.0),
            new MysteryBoxRewardItem(new Item(995, 1270000), 7.0),
            new MysteryBoxRewardItem(new Item(995, 1240000), 8.0),
            new MysteryBoxRewardItem(new Item(995, 1210000), 9.0),
            new MysteryBoxRewardItem(new Item(995, 1180000), 10.0),
            new MysteryBoxRewardItem(new Item(995, 1150000), 11.0),
            new MysteryBoxRewardItem(new Item(995, 1120000), 12.0),
            new MysteryBoxRewardItem(new Item(995, 1080000), 13.0),
            new MysteryBoxRewardItem(new Item(995, 1050000), 14.0),
            new MysteryBoxRewardItem(new Item(995, 1020000), 15.0),
            new MysteryBoxRewardItem(new Item(995, 990000), 16.0),
            new MysteryBoxRewardItem(new Item(995, 960000), 17.0),
            new MysteryBoxRewardItem(new Item(995, 930000), 18.0),
            new MysteryBoxRewardItem(new Item(995, 900000), 19.0),
            new MysteryBoxRewardItem(new Item(995, 870000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 840000), 21.0),
            new MysteryBoxRewardItem(new Item(995, 810000), 22.0),
            new MysteryBoxRewardItem(new Item(995, 780000), 23.0),
            new MysteryBoxRewardItem(new Item(995, 750000), 24.0),
            new MysteryBoxRewardItem(new Item(995, 720000), 25.0),
            new MysteryBoxRewardItem(new Item(995, 690000), 26.0),
            new MysteryBoxRewardItem(new Item(995, 660000), 27.0),
            new MysteryBoxRewardItem(new Item(995, 630000), 28.0),
            new MysteryBoxRewardItem(new Item(995, 600000), 29.0),
            new MysteryBoxRewardItem(new Item(995, 570000), 30.0),
            new MysteryBoxRewardItem(new Item(995, 540000), 31.0),
            new MysteryBoxRewardItem(new Item(995, 510000), 32.0),
            new MysteryBoxRewardItem(new Item(995, 480000), 33.0),
            new MysteryBoxRewardItem(new Item(995, 450000), 34.0),
            new MysteryBoxRewardItem(new Item(995, 420000), 35.0),
            new MysteryBoxRewardItem(new Item(995, 390000), 36.0),
            new MysteryBoxRewardItem(new Item(995, 360000), 37.0),
            new MysteryBoxRewardItem(new Item(995, 330000), 38.0),
            new MysteryBoxRewardItem(new Item(995, 300000), 39.0),
            new MysteryBoxRewardItem(new Item(995, 270000), 40.0),
            new MysteryBoxRewardItem(new Item(995, 240000), 41.0),
            new MysteryBoxRewardItem(new Item(995, 210000), 42.0),
            new MysteryBoxRewardItem(new Item(995, 180000), 43.0),
            new MysteryBoxRewardItem(new Item(995, 150000), 44.0),
            new MysteryBoxRewardItem(new Item(995, 120000), 45.0),
            new MysteryBoxRewardItem(new Item(995, 90000), 46.0),
            new MysteryBoxRewardItem(new Item(995, 70000), 47.0),
            new MysteryBoxRewardItem(new Item(995, 50000), 48.0),
            new MysteryBoxRewardItem(new Item(995, 30000), 49.0),
            new MysteryBoxRewardItem(new Item(995, 10000), 50.0)
    }),

    DAILY_LUCK_PRESENT(15267, 1, new MysteryBoxRewardItem[]{
            new MysteryBoxRewardItem(new Item(995, 10000000), 1.0),
            new MysteryBoxRewardItem(new Item(995, 670000), 1.0),
            new MysteryBoxRewardItem(new Item(995, 660000), 2.0),
            new MysteryBoxRewardItem(new Item(995, 650000), 3.0),
            new MysteryBoxRewardItem(new Item(995, 640000), 4.0),
            new MysteryBoxRewardItem(new Item(995, 630000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 620000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 610000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 600000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 590000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 580000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 570000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 560000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 550000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 540000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 530000), 6.0),
            new MysteryBoxRewardItem(new Item(995, 520000), 7.0),
            new MysteryBoxRewardItem(new Item(995, 510000), 8.0),
            new MysteryBoxRewardItem(new Item(995, 500000), 9.0),
            new MysteryBoxRewardItem(new Item(995, 490000), 10.0),
            new MysteryBoxRewardItem(new Item(995, 480000), 11.0),
            new MysteryBoxRewardItem(new Item(995, 470000), 12.0),
            new MysteryBoxRewardItem(new Item(995, 460000), 13.0),
            new MysteryBoxRewardItem(new Item(995, 450000), 14.0),
            new MysteryBoxRewardItem(new Item(995, 440000), 15.0),
            new MysteryBoxRewardItem(new Item(995, 430000), 16.0),
            new MysteryBoxRewardItem(new Item(995, 420000), 17.0),
            new MysteryBoxRewardItem(new Item(995, 410000), 18.0),
            new MysteryBoxRewardItem(new Item(995, 400000), 19.0),
            new MysteryBoxRewardItem(new Item(995, 390000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 380000), 21.0),
            new MysteryBoxRewardItem(new Item(995, 370000), 22.0),
            new MysteryBoxRewardItem(new Item(995, 360000), 23.0),
            new MysteryBoxRewardItem(new Item(995, 350000), 24.0),
            new MysteryBoxRewardItem(new Item(995, 340000), 25.0),
            new MysteryBoxRewardItem(new Item(995, 330000), 26.0),
            new MysteryBoxRewardItem(new Item(995, 320000), 27.0),
            new MysteryBoxRewardItem(new Item(995, 310000), 28.0),
            new MysteryBoxRewardItem(new Item(995, 300000), 29.0),
            new MysteryBoxRewardItem(new Item(995, 250000), 30.0),
            new MysteryBoxRewardItem(new Item(995, 240000), 31.0),
            new MysteryBoxRewardItem(new Item(995, 230000), 32.0),
            new MysteryBoxRewardItem(new Item(995, 220000), 33.0),
            new MysteryBoxRewardItem(new Item(995, 210000), 34.0),
            new MysteryBoxRewardItem(new Item(995, 200000), 35.0),
            new MysteryBoxRewardItem(new Item(995, 150000), 36.0),
            new MysteryBoxRewardItem(new Item(995, 140000), 37.0),
            new MysteryBoxRewardItem(new Item(995, 130000), 38.0),
            new MysteryBoxRewardItem(new Item(995, 120000), 39.0),
            new MysteryBoxRewardItem(new Item(995, 110000), 40.0),
            new MysteryBoxRewardItem(new Item(995, 100000), 41.0),
            new MysteryBoxRewardItem(new Item(995, 90000), 42.0),
            new MysteryBoxRewardItem(new Item(995, 80000), 43.0),
            new MysteryBoxRewardItem(new Item(995, 70000), 44.0),
            new MysteryBoxRewardItem(new Item(995, 60000), 45.0),
            new MysteryBoxRewardItem(new Item(995, 50000), 46.0),
            new MysteryBoxRewardItem(new Item(995, 40000), 47.0),
            new MysteryBoxRewardItem(new Item(995, 30000), 48.0),
            new MysteryBoxRewardItem(new Item(995, 20000), 49.0),
            new MysteryBoxRewardItem(new Item(995, 10000), 50.0)
    }),

    SMALL_CASKET(405, 5, new MysteryBoxRewardItem[]{
            new MysteryBoxRewardItem(new Item(995, 1000000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 2500000), 10.0),
            new MysteryBoxRewardItem(new Item(995, 5000000), 8.0),
            new MysteryBoxRewardItem(new Item(995, 6000000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 7500000), 1.0),
            new MysteryBoxRewardItem(new Item(13442, 20), 5.0),
            new MysteryBoxRewardItem(new Item(13431, 20), 35.0),
            //new ItemReward(new Item(11959, 20), 35.0),
            new MysteryBoxRewardItem(new Item(4813, 2), 15.0),
            new MysteryBoxRewardItem(new Item(4811, 1), 3.0),
            new MysteryBoxRewardItem(new Item(4087, 1), 3.0),
            new MysteryBoxRewardItem(new Item(2364, 40), 3.0)
    }),

    ELITE_CASKET(ItemID.CASKET_ELITE_, 5, new MysteryBoxRewardItem[]{
            new MysteryBoxRewardItem(new Item(995, 1_000_000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 1_500_000), 19.0),
            new MysteryBoxRewardItem(new Item(995, 3_000_000), 18.0),
            new MysteryBoxRewardItem(new Item(995, 5_000_000), 5.0),
            new MysteryBoxRewardItem(new Item(BLOOD_MONEY, 25000), 10.0),
            new MysteryBoxRewardItem(new Item(BLOOD_MONEY, 30000), 5.0),
            new MysteryBoxRewardItem(new Item(BLOOD_MONEY, 50000), 1.0),
            new MysteryBoxRewardItem(new Item(13442, 20), 25.0),
            new MysteryBoxRewardItem(new Item(13431, 20), 35.0),
            //new ItemReward(new Item(11959, 20), 35.0),
            new MysteryBoxRewardItem(new Item(4813, 2), 15.0),
            new MysteryBoxRewardItem(new Item(4811, 1), 23.0),
            new MysteryBoxRewardItem(new Item(4087, 1), 23.0),
            new MysteryBoxRewardItem(new Item(2364, 40), 23.0)
    }),

    HARD_CASKET(ItemID.CASKET_HARD_, 5, new MysteryBoxRewardItem[]{
            new MysteryBoxRewardItem(new Item(995, 9_000_000), 2.0),
            new MysteryBoxRewardItem(new Item(995, 1_000_000), 10.0),
            new MysteryBoxRewardItem(new Item(995, 1_500_000), 5.0),
            new MysteryBoxRewardItem(new Item(995, 2_000_000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 10_000_000), 1.0),
            new MysteryBoxRewardItem(new Item(BLOOD_MONEY, 20000), 20.0),
            new MysteryBoxRewardItem(new Item(13442, 20), 5.0),
            new MysteryBoxRewardItem(new Item(13431, 20), 35.0),
            //new ItemReward(new Item(11959, 20), 35.0),
            new MysteryBoxRewardItem(new Item(4813, 2), 15.0),
            new MysteryBoxRewardItem(new Item(4811, 1), 3.0),
            new MysteryBoxRewardItem(new Item(4087, 1), 3.0),
            new MysteryBoxRewardItem(new Item(2364, 40), 3.0)
    }),

    MEDIUM_CASKET(ItemID.CASKET_MEDIUM_, 2, new MysteryBoxRewardItem[]{
            new MysteryBoxRewardItem(new Item(995, 600000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 800000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 1000000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 120000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 1500000), 20.0),
            new MysteryBoxRewardItem(new Item(BLOOD_MONEY, 20000), 20.0),
            new MysteryBoxRewardItem(new Item(13442, 20), 5.0),
            new MysteryBoxRewardItem(new Item(13431, 20), 35.0),
            //new ItemReward(new Item(11959, 20), 35.0),
            new MysteryBoxRewardItem(new Item(4813, 2), 15.0),
            new MysteryBoxRewardItem(new Item(4811, 1), 3.0),
            new MysteryBoxRewardItem(new Item(4087, 1), 3.0),
            new MysteryBoxRewardItem(new Item(2364, 40), 3.0)
    }),

    MEDIUM_CASKET2(2808, 2, new MysteryBoxRewardItem[]{
            new MysteryBoxRewardItem(new Item(995, 600000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 800000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 1000000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 120000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 1500000), 20.0),
            new MysteryBoxRewardItem(new Item(BLOOD_MONEY, 20000), 20.0),
            new MysteryBoxRewardItem(new Item(13442, 20), 5.0),
            new MysteryBoxRewardItem(new Item(13431, 20), 35.0),
            //new ItemReward(new Item(11959, 20), 35.0),
            new MysteryBoxRewardItem(new Item(4813, 2), 15.0),
            new MysteryBoxRewardItem(new Item(4811, 1), 3.0),
            new MysteryBoxRewardItem(new Item(4087, 1), 3.0),
            new MysteryBoxRewardItem(new Item(2364, 40), 3.0)
    }),
    MEDIUM_CASKET3(2810, 2, new MysteryBoxRewardItem[]{
            new MysteryBoxRewardItem(new Item(995, 600000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 800000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 1000000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 120000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 1500000), 20.0),
            new MysteryBoxRewardItem(new Item(BLOOD_MONEY, 20000), 20.0),
            new MysteryBoxRewardItem(new Item(13442, 20), 5.0),
            new MysteryBoxRewardItem(new Item(13431, 20), 35.0),
            //new ItemReward(new Item(11959, 20), 35.0),
            new MysteryBoxRewardItem(new Item(4813, 2), 15.0),
            new MysteryBoxRewardItem(new Item(4811, 1), 3.0),
            new MysteryBoxRewardItem(new Item(4087, 1), 3.0),
            new MysteryBoxRewardItem(new Item(2364, 40), 3.0)
    }),

    MEDIUM_CASKET4(2812, 2, new MysteryBoxRewardItem[]{
            new MysteryBoxRewardItem(new Item(995, 600000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 800000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 1000000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 120000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 1500000), 20.0),
            new MysteryBoxRewardItem(new Item(BLOOD_MONEY, 20000), 20.0),
            new MysteryBoxRewardItem(new Item(13442, 20), 5.0),
            new MysteryBoxRewardItem(new Item(13431, 20), 35.0),
            //new ItemReward(new Item(11959, 20), 35.0),
            new MysteryBoxRewardItem(new Item(4813, 2), 15.0),
            new MysteryBoxRewardItem(new Item(4811, 1), 3.0),
            new MysteryBoxRewardItem(new Item(4087, 1), 3.0),
            new MysteryBoxRewardItem(new Item(2364, 40), 3.0)
    }),

    MEDIUM_CASKET5(2814, 2, new MysteryBoxRewardItem[]{
            new MysteryBoxRewardItem(new Item(995, 600000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 800000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 1000000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 120000), 20.0),
            new MysteryBoxRewardItem(new Item(995, 1500000), 20.0),
            new MysteryBoxRewardItem(new Item(BLOOD_MONEY, 20000), 20.0),
            new MysteryBoxRewardItem(new Item(13442, 20), 5.0),
            new MysteryBoxRewardItem(new Item(13431, 20), 35.0),
            //new ItemReward(new Item(11959, 20), 35.0),
            new MysteryBoxRewardItem(new Item(4813, 2), 15.0),
            new MysteryBoxRewardItem(new Item(4811, 1), 3.0),
            new MysteryBoxRewardItem(new Item(4087, 1), 3.0),
            new MysteryBoxRewardItem(new Item(2364, 40), 3.0)
    }),

    EASY_CASKET(ItemID.CASKET_EASY_, 3,
            new MysteryBoxRewardItem[]{
                    new MysteryBoxRewardItem(new Item(995, 1000000), 20.0),
                    new MysteryBoxRewardItem(new Item(995, 2500000), 2.0),
                    new MysteryBoxRewardItem(new Item(995, 5000000), 1.0),
                    new MysteryBoxRewardItem(new Item(995, 1000000), 15.0),
                    new MysteryBoxRewardItem(new Item(995, 1500000), 10.0),
                    new MysteryBoxRewardItem(new Item(13442, 20), 5.0),
                    new MysteryBoxRewardItem(new Item(13431, 20), 35.0),
                    //new ItemReward(new Item(11959, 20), 35.0),
                    new MysteryBoxRewardItem(new Item(4813, 2), 15.0),
                    new MysteryBoxRewardItem(new Item(4811, 1), 3.0),
                    new MysteryBoxRewardItem(new Item(4087, 1), 3.0),
                    new MysteryBoxRewardItem(new Item(2364, 40), 3.0)}),

    CASKET(ItemID.CASKET, 1,
            new MysteryBoxRewardItem[]{
                    new MysteryBoxRewardItem(new Item(995, 1000000), 20.0),
                    new MysteryBoxRewardItem(new Item(995, 2500000), 2.0),
                    new MysteryBoxRewardItem(new Item(995, 5000000), 1.0),
                    new MysteryBoxRewardItem(new Item(995, 1000000), 15.0),
                    new MysteryBoxRewardItem(new Item(995, 1500000), 10.0),
                    new MysteryBoxRewardItem(new Item(2364, 40), 3.0)}),

    ;

    /**
     * The item id
     */
    private int id;

    /**
     * At what percentage is the global shout at
     */
    private int globalShout;

    /**
     * The possible rewards
     */
    private MysteryBoxRewardItem[] rewards;

    /**
     * Represents a reward
     *
     * @param id      the id
     * @param rewards the rewards
     */
    MysteryBoxType(int id, int globalShout, MysteryBoxRewardItem[] rewards) {
        this.setId(id);
        this.setGlobalShout(globalShout);
        this.setRewards(rewards);
    }

    /**
     * Sets the id
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the globalShout
     *
     * @return the globalShout
     */
    public int getGlobalShout() {
        return globalShout;
    }

    /**
     * Sets the globalShout
     *
     * @param globalShout the globalShout
     */
    public void setGlobalShout(int globalShout) {
        this.globalShout = globalShout;
    }

    /**
     * Sets the rewards
     *
     * @return the rewards
     */
    public MysteryBoxRewardItem[] getItemRewards() {
        return rewards;
    }

    /**
     * Sets the rewards
     *
     * @param rewards the rewards
     */
    public void setRewards(MysteryBoxRewardItem[] rewards) {
        this.rewards = rewards;
    }

    /**
     * Gets a reward
     *
     * @param id the id
     * @return the reward
     */
    public static MysteryBoxType forId(int id) {
        return Arrays.stream(values()).filter(c -> c.getId() == id).findFirst().orElse(null);
    }
}
