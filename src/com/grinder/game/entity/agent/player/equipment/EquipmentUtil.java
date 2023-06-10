package com.grinder.game.entity.agent.player.equipment;

import com.grinder.game.content.GameMode;
import com.grinder.game.content.skill.skillable.impl.magic.ElementStaffType;
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerEquipment;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackContext;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonType;
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeapon;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.player.Equipment;
import com.grinder.util.ItemID;
import com.grinder.util.NpcID;

import java.util.*;

import static com.grinder.game.entity.agent.player.equipment.EquipmentConstants.*;
import static com.grinder.util.ItemID.*;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-05
 */
public class EquipmentUtil {

    /**
     * A hash collection which contains the ids of the {@link GameMode#IRONMAN} armor.
     */
    public static final Set<Integer> IRONMAN_ARMOR = new HashSet<>(Arrays.asList(IRONMAN_HELM, IRONMAN_PLATEBODY, IRONMAN_PLATELEGS));

    /**
     * A hash collection which contains the ids of the {@link GameMode#HARDCORE_IRONMAN} armor.
     */
    public static final Set<Integer> HARDCORE_IRONMAN_ARMOR = new HashSet<>(Arrays.asList(HARDCORE_IRONMAN_HELM, HARDCORE_IRONMAN_PLATEBODY, HARDCORE_IRONMAN_PLATELEGS));

    /**
     * A hash collection which contains the ids of the {@link GameMode#ULTIMATE_IRONMAN} armor.
     */
    public static final Set<Integer> ULTIMATE_IRONMAN_ARMOR = new HashSet<>(Arrays.asList(ULTIMATE_IRONMAN_HELM, ULTIMATE_IRONMAN_PLATEBODY, ULTIMATE_IRONMAN_PLATELEGS));

    /**
     * A hash collection which contains the ids of the {@link GameMode#ONE_LIFE} armor.
     */
    public static final Set<Integer> ONE_LIFE_ARMOR = new HashSet<>(Arrays.asList(GROUP_IRON_HELM, GROUP_IRON_BRACERS, GROUP_IRON_PLATEBODY, GROUP_IRON_PLATELEGS, NIGHTMARE_WARBLADE));

    /**
     * A hash collection which contains the ids of the {@link GameMode#REALISM} armor.
     */
    public static final Set<Integer> REALISM_ARMOR = new HashSet<>(Arrays.asList(HARDCORE_GROUP_IRON_BRACERS, HARDCORE_GROUP_IRON_HELM, HARDCORE_GROUP_IRON_PLATEBODY, HARDCORE_GROUP_IRON_PLATELEGS, STARSHATTERER, DIVINELIGHT_BATTLESTAFF, FIRESOUL_SHORTBOW));

    /**
     * Shayzien armour that a player can wear, this array does _not_ contain the helmet because it conjucts with
     * other shayzien type of helmets such as Slayer helmet variants.
     */
    private static final int[] SHAYZIEN_ARMOUR = {SHAYZIEN_GLOVES_5_, SHAYZIEN_BOOTS_5_, SHAYZIEN_GREAVES_5_, SHAYZIEN_PLATEBODY_5_};

    /**
     * The variants of helmets that count as shayzien armour (including slayer helmet variants).
     */
    private static final int[] SHAYZIEN_HELMETS = {SHAYZIEN_HELM_5_, SLAYER_HELMET, SLAYER_HELMET_I_,
            BLACK_SLAYER_HELMET, BLACK_SLAYER_HELMET_I_,
            GREEN_SLAYER_HELMET, GREEN_SLAYER_HELMET_I_,
            PURPLE_SLAYER_HELMET, PURPLE_SLAYER_HELMET_I_,
            RED_SLAYER_HELMET, RED_SLAYER_HELMET_I_,
            21888, 21890, // Turquoise Slayer Helmet
            23073, 23075, // Hydra Slayer Helmet
            15910, // Maranami's Custom slayer helmet
            ItemID.TWISTED_SLAYER_HELMET, ItemID.TWISTED_SLAYER_HELMET_I,
            ItemID.TZTOK_SLAYER_HELMET, ItemID.TZTOK_SLAYER_HELMET_I,
            ItemID.VAMPYRIC_SLAYER_HELMET, ItemID.VAMPYRIC_SLAYER_HELMET_I,
            ItemID.TZKAL_SLAYER_HELMET, ItemID.TZKAL_SLAYER_HELMET_I
    };

    public static final int CRAWS_BOW_ITEM_ID = 22550;
    public static final int UNCHARGED_CRAWS_BOW_ITEM_ID = 22547;
    public static final int LAVA_BLADE_ITEM_ID = 15152;
    public static final int INFERNAL_BLADE_ITEM_ID = 15918;
    public static final int SERPENTINE_HELM_ITEM_ID = 12931;
    public static final int MAGMA_HELM_ITEM_ID = 13199;
    public static final int TANZANITE_HELM_ITEM_ID = 13197;

    private static final int ELYSIAN_SHIELD_ITEM_ID = 12817;
    private static final int DIVINE_SHIELD_ITEM_ID = 15798;
    private static final int INFERNAL_DIVINE_SHIELD_ITEM_ID = 15954;

    private static final int SUPERIOR_MAGIC_VOID_HELM_ITEM_ID = 26473;
    private static final int SUPERIOR_RANGED_VOID_HELM_ITEM_ID = 26475;
    private static final int SUPERIOR_MELEE_VOID_HELM_ITEM_ID = 26477;

    private static final int VOID_KNIGHT_DEFLECTOR_ITEM_ID = 19712;
    private static final int MAGIC_VOID_HELM_ITEM_ID = 11663;
    private static final int RANGED_VOID_HELM_ITEM_ID = 11664;
    private static final int MELEE_VOID_HELM_ITEM_ID = 11665;
    
    private static final int MAGIC_VOID_HELM_ITEM_ID_G = 15229;
    private static final int RANGED_VOID_HELM_ITEM_ID_G = 15230;
    private static final int MELEE_VOID_HELM_ITEM_ID_G = 15231;

    private static final int MAGIC_VOID_HELM_ITEM_ID_T = 15292;
    private static final int RANGED_VOID_HELM_ITEM_ID_T = 15293;
    private static final int MELEE_VOID_HELM_ITEM_ID_T = 15294;

    private static final int MAGIC_VOID_HELM_ITEM_ID_RED = 15442;
    private static final int RANGED_VOID_HELM_ITEM_ID_RED = 15443;
    private static final int MELEE_VOID_HELM_ITEM_ID_RED = 15444;

    private static final int MAGIC_VOID_HELM_ITEM_ID_GREEN = 15449;
    private static final int RANGED_VOID_HELM_ITEM_ID_GREEN = 15450;
    private static final int MELEE_VOID_HELM_ITEM_ID_GREEN = 15451;

    private static final int MAGIC_VOID_HELM_ITEM_ID_PINK = 15456;
    private static final int RANGED_VOID_HELM_ITEM_ID_PINK = 15457;
    private static final int MELEE_VOID_HELM_ITEM_ID_PINK = 15458;

    private static final int[][] VOID_SET_SLOT_AND_ITEM_IDS = {
            {EquipmentConstants.BODY_SLOT, 8839},
            {EquipmentConstants.LEG_SLOT, 8840},
            {EquipmentConstants.HANDS_SLOT, 8842}
    };
    private static final int[][] ELITE_VOID_SET_SLOT_AND_ITEM_IDS = {
            {EquipmentConstants.BODY_SLOT, 13072},
            {EquipmentConstants.LEG_SLOT, 13073},
            {EquipmentConstants.HANDS_SLOT, 8842}
    };

    private static final int[][] SUPERIOR_VOID_SET_SLOT_AND_ITEM_IDS = {
            {EquipmentConstants.BODY_SLOT, 26469},
            {EquipmentConstants.LEG_SLOT, 26471},
            {EquipmentConstants.HANDS_SLOT, 26467}
    };
    
    private static final int[][] VOID_SET_SLOT_AND_ITEM_IDS_G = {
            {EquipmentConstants.BODY_SLOT, 15227},
            {EquipmentConstants.LEG_SLOT, 15228},
            {EquipmentConstants.HANDS_SLOT, 8842}
    };
    private static final int[][] ELITE_VOID_SET_SLOT_AND_ITEM_IDS_G = {
            {EquipmentConstants.BODY_SLOT, 15232},
            {EquipmentConstants.LEG_SLOT, 15233},
            {EquipmentConstants.HANDS_SLOT, 8842}
    };

    private static final int[][] VOID_SET_SLOT_AND_ITEM_IDS_T = {
            {EquipmentConstants.BODY_SLOT, 15290},
            {EquipmentConstants.LEG_SLOT, 15291},
            {EquipmentConstants.HANDS_SLOT, 8842}
    };
    private static final int[][] ELITE_VOID_SET_SLOT_AND_ITEM_IDS_T = {
            {EquipmentConstants.BODY_SLOT, 15295},
            {EquipmentConstants.LEG_SLOT, 15296},
            {EquipmentConstants.HANDS_SLOT, 8842}
    };

    private static final int[][] VOID_SET_SLOT_AND_ITEM_IDS_RED = {
            {EquipmentConstants.BODY_SLOT, 15440},
            {EquipmentConstants.LEG_SLOT, 15441},
            {EquipmentConstants.HANDS_SLOT, 8842}
    };
    private static final int[][] ELITE_VOID_SET_SLOT_AND_ITEM_IDS_RED = {
            {EquipmentConstants.BODY_SLOT, 15445},
            {EquipmentConstants.LEG_SLOT, 15446},
            {EquipmentConstants.HANDS_SLOT, 8842}
    };

    private static final int[][] VOID_SET_SLOT_AND_ITEM_IDS_GREEN = {
            {EquipmentConstants.BODY_SLOT, 15447},
            {EquipmentConstants.LEG_SLOT, 15448},
            {EquipmentConstants.HANDS_SLOT, 8842}
    };
    private static final int[][] ELITE_VOID_SET_SLOT_AND_ITEM_IDS_GREEN = {
            {EquipmentConstants.BODY_SLOT, 15452},
            {EquipmentConstants.LEG_SLOT, 15452},
            {EquipmentConstants.HANDS_SLOT, 8842}
    };

    private static final int[][] VOID_SET_SLOT_AND_ITEM_IDS_PINK = {
            {EquipmentConstants.BODY_SLOT, 15454},
            {EquipmentConstants.LEG_SLOT, 15455},
            {EquipmentConstants.HANDS_SLOT, 8842}
    };
    private static final int[][] ELITE_VOID_SET_SLOT_AND_ITEM_IDS_PINK = {
            {EquipmentConstants.BODY_SLOT, 15459},
            {EquipmentConstants.LEG_SLOT, 15460},
            {EquipmentConstants.HANDS_SLOT, 8842}
    };

    public static final int[] RECOIL_RING_IDS = new int[] {
            ItemID.RING_OF_RECOIL,
            ItemID.RING_OF_SUFFERING_R_,
            ItemID.RING_OF_SUFFERING_RI_
    };

    private static final int[] OBSIDIAN_WEAPONS_ITEM_IDS = {746, 747, 6523, 6525, 6526, 6527, 6528};
    private static final int[] OBSIDIAN_ARMOUR_SET_IDS = {ItemID.OBSIDIAN_HELMET, ItemID.OBSIDIAN_PLATEBODY, ItemID.OBSIDIAN_PLATELEGS};

    private static final int[] VERAC_HELMS = {ItemID.VERACS_HELM, ItemID.VERACS_HELM_100, ItemID.VERACS_HELM_75, ItemID.VERACS_HELM_50, ItemID.VERACS_HELM_25};
    private static final int[] VERAC_BRASSARDS = {ItemID.VERACS_BRASSARD, ItemID.VERACS_BRASSARD_100, ItemID.VERACS_BRASSARD_75, ItemID.VERACS_BRASSARD_50, ItemID.VERACS_BRASSARD_25 };
    private static final int[] VERAC_PLATESKIRTS = {ItemID.VERACS_PLATESKIRT, ItemID.VERACS_PLATESKIRT_100, ItemID.VERACS_PLATESKIRT_75, ItemID.VERACS_PLATESKIRT_50, ItemID.VERACS_PLATESKIRT_25};
    private static final int[] VERAC_FLAILS = {ItemID.VERACS_FLAIL, ItemID.VERACS_FLAIL_100, ItemID.VERACS_FLAIL_75, ItemID.VERACS_FLAIL_50, ItemID.VERACS_FLAIL_25};

    private static final int[] DHAROK_HELMS = {ItemID.DHAROKS_HELM, ItemID.DHAROKS_HELM_100, ItemID.DHAROKS_HELM_75, ItemID.DHAROKS_HELM_50, ItemID.DHAROKS_HELM_25};
    private static final int[] DHAROK_PLATEBODIES = {ItemID.DHAROKS_PLATEBODY, ItemID.DHAROKS_PLATEBODY_100, ItemID.DHAROKS_PLATEBODY_75, ItemID.DHAROKS_PLATEBODY_50, ItemID.DHAROKS_PLATEBODY_25 };
    private static final int[] DHAROK_PLATELEGS = {ItemID.DHAROKS_PLATELEGS, ItemID.DHAROKS_PLATELEGS_100, ItemID.DHAROKS_PLATELEGS_75, ItemID.DHAROKS_PLATELEGS_50, ItemID.DHAROKS_PLATELEGS_25};
    private static final int[] DHAROK_GREATAXES = {ItemID.DHAROKS_GREATAXE, ItemID.DHAROKS_GREATAXE_100, ItemID.DHAROKS_GREATAXE_75, ItemID.DHAROKS_GREATAXE_50, ItemID.DHAROKS_GREATAXE_25};

    private static final int[] AHRIMS_HOODS = {ItemID.AHRIMS_HOOD, ItemID.AHRIMS_HOOD_100, ItemID.AHRIMS_HOOD_75, ItemID.AHRIMS_HOOD_50, ItemID.AHRIMS_HOOD_25};
    private static final int[] AHRIMS_ROBETOPS = {ItemID.AHRIMS_ROBETOP, ItemID.AHRIMS_ROBETOP_100, ItemID.AHRIMS_ROBETOP_75, ItemID.AHRIMS_ROBETOP_50, ItemID.AHRIMS_ROBETOP_25 };
    private static final int[] AHRIMS_ROBESKIRTS = {ItemID.AHRIMS_ROBESKIRT, ItemID.AHRIMS_ROBESKIRT_100, ItemID.AHRIMS_ROBESKIRT_75, ItemID.AHRIMS_ROBESKIRT_50, ItemID.AHRIMS_ROBESKIRT_25};
    private static final int[] AHRIMS_STAFFS = {ItemID.AHRIMS_STAFF, ItemID.AHRIMS_STAFF_100, ItemID.AHRIMS_STAFF_75, ItemID.AHRIMS_STAFF_50, ItemID.AHRIMS_STAFF_25};

    private static final int[] GUTHANS_HELMS = {ItemID.GUTHANS_HELM, ItemID.GUTHANS_HELM_100, ItemID.GUTHANS_HELM_75, ItemID.GUTHANS_HELM_50, ItemID.GUTHANS_HELM_25};
    private static final int[] GUTHANS_PLATEBODIES = {ItemID.GUTHANS_PLATEBODY, ItemID.GUTHANS_PLATEBODY_100, ItemID.GUTHANS_PLATEBODY_75, ItemID.GUTHANS_PLATEBODY_50, ItemID.GUTHANS_PLATEBODY_25 };
    private static final int[] GUTHANS_CHAINSKIRTS = {ItemID.GUTHANS_CHAINSKIRT, ItemID.GUTHANS_CHAINSKIRT_100, ItemID.GUTHANS_CHAINSKIRT_75, ItemID.GUTHANS_CHAINSKIRT_50, ItemID.GUTHANS_CHAINSKIRT_25};
    private static final int[] GUTHANS_WARSPEARS = {ItemID.GUTHANS_WARSPEAR, ItemID.GUTHANS_WARSPEAR_100, ItemID.GUTHANS_WARSPEAR_75, ItemID.GUTHANS_WARSPEAR_50, ItemID.GUTHANS_WARSPEAR_25};


    private static final int[] KARILS_COIFS = {ItemID.KARILS_COIF, ItemID.KARILS_COIF_100, ItemID.KARILS_COIF_75, ItemID.KARILS_COIF_50, ItemID.KARILS_COIF_25};
    private static final int[] KARILS_LEATHERTOPS = {ItemID.KARILS_LEATHERTOP, ItemID.KARILS_LEATHERTOP_100, ItemID.KARILS_LEATHERTOP_75, ItemID.KARILS_LEATHERTOP_50, ItemID.KARILS_LEATHERTOP_25 };
    private static final int[] KARILS_LEATHERSKIRTS = {ItemID.KARILS_LEATHERSKIRT, ItemID.KARILS_LEATHERSKIRT_100, ItemID.KARILS_LEATHERSKIRT_75, ItemID.KARILS_LEATHERSKIRT_50, ItemID.KARILS_LEATHERSKIRT_25};
    private static final int[] KARILS_CROSSBOWS = {ItemID.KARILS_CROSSBOW, ItemID.KARILS_CROSSBOW_100, ItemID.KARILS_CROSSBOW_75, ItemID.KARILS_CROSSBOW_50, ItemID.KARILS_CROSSBOW_25};


    private static final int[] TORAGS_HELMS = {ItemID.TORAGS_HELM, ItemID.TORAGS_HELM_100, ItemID.TORAGS_HELM_75, ItemID.TORAGS_HELM_50, ItemID.TORAGS_HELM_25};
    private static final int[] TORAGS_PLATEBODIES = {ItemID.TORAGS_PLATEBODY, ItemID.TORAGS_PLATEBODY_100, ItemID.TORAGS_PLATEBODY_75, ItemID.TORAGS_PLATEBODY_50, ItemID.TORAGS_PLATEBODY_25 };
    private static final int[] TORAGS_PLATELEGS = {ItemID.TORAGS_PLATELEGS, ItemID.TORAGS_PLATELEGS_100, ItemID.TORAGS_PLATELEGS_75, ItemID.TORAGS_PLATELEGS_50, ItemID.TORAGS_PLATELEGS_25};
    private static final int[] TORAGS_HAMMERS = {ItemID.TORAGS_HAMMERS, ItemID.TORAGS_HAMMERS_100, ItemID.TORAGS_HAMMERS_75, ItemID.TORAGS_HAMMERS_50, ItemID.TORAGS_HAMMERS_25};

    private static final int[] JUSTICIAR_SET_ITEM_IDS = {22326, 22327, 22328};

    private static final int[] RING_OF_WEALTHS_IDS = {ItemID.RING_OF_WEALTH, ItemID.RING_OF_WEALTH_5_, ItemID.RING_OF_WEALTH_4_, ItemID.RING_OF_WEALTH_3_, ItemID.RING_OF_WEALTH_2_, ItemID.RING_OF_WEALTH_1_, ItemID.RING_OF_WEALTH_I_, ItemID.RING_OF_WEALTH_I5_, ItemID.RING_OF_WEALTH_I4_,
                                                        ItemID.RING_OF_WEALTH_I3_, ItemID.RING_OF_WEALTH_I2_, ItemID.RING_OF_WEALTH_I1_};


    private static final int[] INQUSITORS_SET_ITEM_IDS = { 24419, 24420, 24421 };


    private static final int[] SWAMPBARK_SET_ITEM_IDS = { 25398, 25389, 25392, 25395, 25401 };

    private static final int[] BLOODBARK_SET_ITEM_IDS = { 25413, 25404, 25407, 25410, 25416 };

    public static boolean isWieldingAbyssalTentacle(Player player) {
        return isWieldingAbyssalTentacle(player.getEquipment());
    }

    public static boolean isWieldingAbyssalTentacle(Equipment equipment) {
        return isWearingAtSlot(equipment, WEAPON_SLOT, ABYSSAL_TENTACLE);
    }

    public static boolean isWearingThievingSet(Player player) {
        return isWearingThievingSet(player.getEquipment());
    }

    public static boolean isWearingThievingSet(Equipment equipment) {
        return isWearingAnyAtSlot(equipment, HEAD_SLOT, ROGUE_MASK)
                && isWearingAnyAtSlot(equipment, LEG_SLOT, ROGUE_TROUSERS)
                && isWearingAnyAtSlot(equipment, FEET_SLOT, ROGUE_BOOTS)
                && isWearingAnyAtSlot(equipment, BODY_SLOT, ROGUE_TOP)
                && isWearingAnyAtSlot(equipment, AMULET_SLOT, DODGY_NECKLACE)
                && isWearingAnyAtSlot(equipment, HANDS_SLOT, GLOVES_OF_SILENCE, ROGUE_GLOVES);
    }

    public static boolean isWearingWoodcuttingSet(Player player) {
        return isWearingWoodcuttingSet(player.getEquipment());
    }

    public static boolean isWearingWoodcuttingSet(Equipment equipment) {
        return isWearingAnyAtSlot(equipment, HEAD_SLOT, LUMBERJACK_HAT)
                && isWearingAnyAtSlot(equipment, LEG_SLOT, LUMBERJACK_LEGS)
                && isWearingAnyAtSlot(equipment, FEET_SLOT, LUMBERJACK_BOOTS)
                && isWearingAnyAtSlot(equipment, BODY_SLOT, LUMBERJACK_TOP);
    }

    public static boolean isWearingMiningSet(Player player) {
        return isWearingMiningSet(player.getEquipment());
    }

    public static boolean isWearingMiningSet(Equipment equipment) {
        return isWearingAnyAtSlot(equipment, HEAD_SLOT, PROSPECTOR_HELMET)
                && isWearingAnyAtSlot(equipment, LEG_SLOT, PROSPECTOR_LEGS)
                && isWearingAnyAtSlot(equipment, FEET_SLOT, PROSPECTOR_BOOTS)
                && isWearingAnyAtSlot(equipment, BODY_SLOT, PROSPECTOR_JACKET);
    }

    public static boolean isWearingFishingSet(Player player) {
        return isWearingFishingSet(player.getEquipment());
    }

    public static boolean isWearingFishingSet(Equipment equipment) {
        return isWearingAnyAtSlot(equipment, HEAD_SLOT, ANGLER_HAT)
                && isWearingAnyAtSlot(equipment, LEG_SLOT, ANGLER_WADERS)
                && isWearingAnyAtSlot(equipment, FEET_SLOT, ANGLER_BOOTS)
                && isWearingAnyAtSlot(equipment, BODY_SLOT, ANGLER_TOP);
    }

    public static boolean isWearingAnyGracefulSet(Player player) {
        return isWearingAnyGracefulSet(player.getEquipment());
    }

    public static boolean isWearingGracefulSet2(Equipment equipment) {
        return isWearingAnyAtSlot(equipment, HEAD_SLOT, GRACEFUL_HOOD_2)
                && isWearingAnyAtSlot(equipment, BODY_SLOT, GRACEFUL_TOP_2)
                && isWearingAnyAtSlot(equipment, LEG_SLOT, GRACEFUL_LEGS_2)
                && isWearingAnyAtSlot(equipment, FEET_SLOT, GRACEFUL_BOOTS_2)
                && isWearingAnyAtSlot(equipment, HANDS_SLOT, GRACEFUL_GLOVES_2)
                && (isWearingAnyAtSlot(equipment, CAPE_SLOT, GRACEFUL_CAPE_2) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE_T_));
    }

    public static boolean isWearingGracefulSet(Equipment equipment) {
        return isWearingAnyAtSlot(equipment, HEAD_SLOT, GRACEFUL_HOOD)
                && isWearingAnyAtSlot(equipment, BODY_SLOT, GRACEFUL_TOP)
                && isWearingAnyAtSlot(equipment, LEG_SLOT, GRACEFUL_LEGS)
                && isWearingAnyAtSlot(equipment, FEET_SLOT, GRACEFUL_BOOTS)
                && isWearingAnyAtSlot(equipment, HANDS_SLOT, GRACEFUL_GLOVES)
                && (isWearingAnyAtSlot(equipment, CAPE_SLOT, GRACEFUL_CAPE) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE_T_));
    }

    public static boolean isWearingArceuusGracefulSet(Equipment equipment) {
        return isWearingAnyAtSlot(equipment, HEAD_SLOT, GRACEFUL_HOOD_4)
                && isWearingAnyAtSlot(equipment, BODY_SLOT, GRACEFUL_TOP_4)
                && isWearingAnyAtSlot(equipment, LEG_SLOT, GRACEFUL_LEGS_4)
                && isWearingAnyAtSlot(equipment, FEET_SLOT, GRACEFUL_BOOTS_4)
                && isWearingAnyAtSlot(equipment, HANDS_SLOT, GRACEFUL_GLOVES_4)
                && (isWearingAnyAtSlot(equipment, CAPE_SLOT, GRACEFUL_CAPE_4) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE_T_));
    }

    public static boolean isWearingPiscariliusGracefulSet(Equipment equipment) {
        return isWearingAnyAtSlot(equipment, HEAD_SLOT, GRACEFUL_HOOD_6)
                && isWearingAnyAtSlot(equipment, BODY_SLOT, GRACEFUL_TOP_6)
                && isWearingAnyAtSlot(equipment, LEG_SLOT, GRACEFUL_LEGS_6)
                && isWearingAnyAtSlot(equipment, FEET_SLOT, GRACEFUL_BOOTS_6)
                && isWearingAnyAtSlot(equipment, HANDS_SLOT, GRACEFUL_GLOVES_6)
                && (isWearingAnyAtSlot(equipment, CAPE_SLOT, GRACEFUL_CAPE_6) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE_T_));
    }

    public static boolean isWearingLovakengjGracefulSet(Equipment equipment) {
        return isWearingAnyAtSlot(equipment, HEAD_SLOT, GRACEFUL_HOOD_8)
                && isWearingAnyAtSlot(equipment, BODY_SLOT, GRACEFUL_TOP_8)
                && isWearingAnyAtSlot(equipment, LEG_SLOT, GRACEFUL_LEGS_8)
                && isWearingAnyAtSlot(equipment, FEET_SLOT, GRACEFUL_BOOTS_8)
                && isWearingAnyAtSlot(equipment, HANDS_SLOT, GRACEFUL_GLOVES_8)
                && (isWearingAnyAtSlot(equipment, CAPE_SLOT, GRACEFUL_CAPE_8) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE_T_));
    }

    public static boolean isWearingShayzienGracefulSet(Equipment equipment) {
        return isWearingAnyAtSlot(equipment, HEAD_SLOT, GRACEFUL_HOOD_10)
                && isWearingAnyAtSlot(equipment, BODY_SLOT, GRACEFUL_TOP_10)
                && isWearingAnyAtSlot(equipment, LEG_SLOT, GRACEFUL_LEGS_10)
                && isWearingAnyAtSlot(equipment, FEET_SLOT, GRACEFUL_BOOTS_10)
                && isWearingAnyAtSlot(equipment, HANDS_SLOT, GRACEFUL_GLOVES_10)
                && (isWearingAnyAtSlot(equipment, CAPE_SLOT, GRACEFUL_CAPE_10) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE_T_));
    }

    public static boolean isWearingHosidiusGracefulSet(Equipment equipment) {
        return isWearingAnyAtSlot(equipment, HEAD_SLOT, GRACEFUL_HOOD_12)
                && isWearingAnyAtSlot(equipment, BODY_SLOT, GRACEFUL_TOP_12)
                && isWearingAnyAtSlot(equipment, LEG_SLOT, GRACEFUL_LEGS_12)
                && isWearingAnyAtSlot(equipment, FEET_SLOT, GRACEFUL_BOOTS_12)
                && isWearingAnyAtSlot(equipment, HANDS_SLOT, GRACEFUL_GLOVES_12)
                && (isWearingAnyAtSlot(equipment, CAPE_SLOT, GRACEFUL_CAPE_12) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE_T_));
    }

    public static boolean isWearingKourendGracefulSet(Equipment equipment) {
        return isWearingAnyAtSlot(equipment, HEAD_SLOT, GRACEFUL_HOOD_14)
                && isWearingAnyAtSlot(equipment, BODY_SLOT, GRACEFUL_TOP_14)
                && isWearingAnyAtSlot(equipment, LEG_SLOT, GRACEFUL_LEGS_14)
                && isWearingAnyAtSlot(equipment, FEET_SLOT, GRACEFUL_BOOTS_14)
                && isWearingAnyAtSlot(equipment, HANDS_SLOT, GRACEFUL_GLOVES_14)
                && (isWearingAnyAtSlot(equipment, CAPE_SLOT, GRACEFUL_CAPE_14) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE_T_));
    }

    public static boolean isWearingBrimhavenGracefulSet(Equipment equipment) {
        return isWearingAnyAtSlot(equipment, HEAD_SLOT, GRACEFUL_HOOD_15)
                && isWearingAnyAtSlot(equipment, BODY_SLOT, GRACEFUL_TOP_15)
                && isWearingAnyAtSlot(equipment, LEG_SLOT, GRACEFUL_LEGS_15)
                && isWearingAnyAtSlot(equipment, FEET_SLOT, GRACEFUL_BOOTS_15)
                && isWearingAnyAtSlot(equipment, HANDS_SLOT, GRACEFUL_GLOVES_15)
                && (isWearingAnyAtSlot(equipment, CAPE_SLOT, GRACEFUL_CAPE_15) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE_T_));
    }

    public static boolean isWearingHallowedGracefulSet(Equipment equipment) { // #CACHEUPDATE
        return isWearingAnyAtSlot(equipment, HEAD_SLOT, GRACEFUL_HOOD_24743)
                && isWearingAnyAtSlot(equipment, BODY_SLOT, GRACEFUL_TOP_24749)
                && isWearingAnyAtSlot(equipment, LEG_SLOT, GRACEFUL_LEGS_24752)
                && isWearingAnyAtSlot(equipment, FEET_SLOT, GRACEFUL_BOOTS_24758)
                && isWearingAnyAtSlot(equipment, HANDS_SLOT, GRACEFUL_GLOVES_24755)
                && (isWearingAnyAtSlot(equipment, CAPE_SLOT, GRACEFUL_CAPE_24746) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE_T_));
    }

    public static boolean isWearingTrailblazerGracefulSet(Equipment equipment) { // #CACHEUPDATE
        return isWearingAnyAtSlot(equipment, HEAD_SLOT, GRACEFUL_HOOD_25069)
                && isWearingAnyAtSlot(equipment, BODY_SLOT, GRACEFUL_TOP_25075)
                && isWearingAnyAtSlot(equipment, LEG_SLOT, GRACEFUL_LEGS_25078)
                && isWearingAnyAtSlot(equipment, FEET_SLOT, GRACEFUL_BOOTS_25084)
                && isWearingAnyAtSlot(equipment, HANDS_SLOT, GRACEFUL_GLOVES_25081)
                && (isWearingAnyAtSlot(equipment, CAPE_SLOT, GRACEFUL_CAPE_25072) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE) || isWearingAnyAtSlot(equipment, CAPE_SLOT, AGILITY_CAPE_T_));
    }

    public static boolean isWearingAnyGracefulSet(Equipment equipment){
        return isWearingGracefulSet(equipment)
                || isWearingGracefulSet2(equipment)
                || isWearingArceuusGracefulSet(equipment)
                || isWearingPiscariliusGracefulSet(equipment)
                || isWearingLovakengjGracefulSet(equipment)
                || isWearingShayzienGracefulSet(equipment)
                || isWearingHosidiusGracefulSet(equipment)
                || isWearingKourendGracefulSet(equipment)
                || isWearingBrimhavenGracefulSet(equipment)
                || isWearingHallowedGracefulSet(equipment)
                || isWearingTrailblazerGracefulSet(equipment);
    }


    public static boolean isImmuneToPoison(Equipment equipment, PoisonType type){
        return isWearingAnyAtSlot(equipment, HEAD_SLOT,
                SERPENTINE_HELM_ITEM_ID,
                TANZANITE_HELM_ITEM_ID,
                MAGMA_HELM_ITEM_ID);
    }

    public static boolean isWieldingKeris(Equipment equipment){
        return isWearingAnyAtSlot(equipment, WEAPON_SLOT,
                ItemID.KERIS,
                ItemID.KERIS_P_,
                ItemID.KERIS_P_PLUS_,
                ItemID.KERIS_P_PLUS_PLUS_);
    }

    public static int countShayzienArmourPieces(Equipment equipment){
        int count = 0;
        for(int id : SHAYZIEN_ARMOUR){
            if(equipment.contains(id))
                count++;
        }
        if(equipment.containsAny(SHAYZIEN_HELMETS))
            count++;
        return count;
    }

    public static boolean isWieldingStaff(Equipment equipment, ElementStaffType type){
        return isWearingAnyAtSlot(equipment, WEAPON_SLOT, type.getStaves());
    }
    public static boolean hasAnyObsidianWeapons(Equipment equipment){
        return isWearingAnyAtSlot(equipment, WEAPON_SLOT, OBSIDIAN_WEAPONS_ITEM_IDS);
    }

    public static boolean isWearingObsidianSet(Equipment equipment){
        return equipment.containsAll(OBSIDIAN_ARMOUR_SET_IDS);
    }

    public static boolean isWearingBerserkerNecklaceOR(Equipment equipment){
        return isWearingAtSlot(equipment, AMULET_SLOT, 23240);
    }
    public static boolean isWearingBerserkerNecklace(Equipment equipment){
        return isWearingAtSlot(equipment, AMULET_SLOT, BERSERKER_NECKLACE);
    }

    public static boolean isSmokeProtect(Equipment equipment) {
        return SlayerEquipment.hasAnySlayerHelmet(equipment) || SlayerEquipment.hasAnySlayerHelmet(equipment) || equipment.containsAtSlot(HEAD_SLOT, FACEMASK);
    }

    /*public static boolean isWieldingObsidianWeaponry(Player player) {
        if (player.getEquipment().getItems()[2].getId() != 11128)
            return false;

        for (int weaponItemId : OBSIDIAN_WEAPONS_ITEM_IDS)
            if (isWearingAtSlot(player, WEAPON_SLOT, weaponItemId))
                return true;

        return false;
    }*/

    public static boolean isWearingAnyVoidSet(Equipment equipment, AttackType attackType){
        return isWearingVoidSet(equipment, attackType)
                || isWearingEliteVoidSet(equipment, attackType);
    }

    public static boolean isWearingVoidSet(Equipment equipment, AttackType attackType) {
        return isWearingVoidArmourSet(equipment, attackType, VOID_SET_SLOT_AND_ITEM_IDS)
                || isWearingVoidArmourSet(equipment, attackType, VOID_SET_SLOT_AND_ITEM_IDS_G)
                || isWearingVoidArmourSet(equipment, attackType, VOID_SET_SLOT_AND_ITEM_IDS_T)
                || isWearingVoidArmourSet(equipment, attackType, VOID_SET_SLOT_AND_ITEM_IDS_RED)
                || isWearingVoidArmourSet(equipment, attackType, VOID_SET_SLOT_AND_ITEM_IDS_GREEN)
                || isWearingVoidArmourSet(equipment, attackType, VOID_SET_SLOT_AND_ITEM_IDS_PINK);
    }

    public static boolean isWearingEliteVoidSet(Equipment equipment, AttackType attackType) {
        return isWearingVoidArmourSet(equipment, attackType, ELITE_VOID_SET_SLOT_AND_ITEM_IDS)
                || isWearingVoidArmourSet(equipment, attackType, ELITE_VOID_SET_SLOT_AND_ITEM_IDS_G)
                || isWearingVoidArmourSet(equipment, attackType, ELITE_VOID_SET_SLOT_AND_ITEM_IDS_T)
                || isWearingVoidArmourSet(equipment, attackType, ELITE_VOID_SET_SLOT_AND_ITEM_IDS_RED)
                || isWearingVoidArmourSet(equipment, attackType, ELITE_VOID_SET_SLOT_AND_ITEM_IDS_GREEN)
                || isWearingVoidArmourSet(equipment, attackType, ELITE_VOID_SET_SLOT_AND_ITEM_IDS_PINK);
    }

    public static boolean isWearingSuperiorVoidSet(Equipment equipment, AttackType attackType) {
        return isWearingSuperiorHelmets(equipment, attackType, SUPERIOR_VOID_SET_SLOT_AND_ITEM_IDS);
    }

    private static boolean isWearingSuperiorHelmets(Equipment equipment, AttackType attackType, int[][] voidArmourSlotAndIdSet) {

        final int helmet = attackType == AttackType.MAGIC ? SUPERIOR_MAGIC_VOID_HELM_ITEM_ID :
                attackType == AttackType.RANGED ? SUPERIOR_RANGED_VOID_HELM_ITEM_ID : SUPERIOR_MELEE_VOID_HELM_ITEM_ID;

        int matchCount = 0;

        for (int slotAndId[] : voidArmourSlotAndIdSet)
            if (isWearingAtSlot(equipment, slotAndId[0], slotAndId[1]))
                matchCount++;

        if (isWearingAtSlot(equipment, SHIELD_SLOT, VOID_KNIGHT_DEFLECTOR_ITEM_ID))
            matchCount++;

        return matchCount >= 3 && equipment.getItems()[EquipmentConstants.HEAD_SLOT].getId() == helmet;
    }

    private static boolean isWearingVoidArmourSet(Equipment equipment, AttackType attackType, int[][] voidArmourSlotAndIdSet) {

        final int helmet = attackType == AttackType.MAGIC ? MAGIC_VOID_HELM_ITEM_ID :
                attackType == AttackType.RANGED ? RANGED_VOID_HELM_ITEM_ID : MELEE_VOID_HELM_ITEM_ID;

        final int helmet_g = attackType == AttackType.MAGIC ? MAGIC_VOID_HELM_ITEM_ID_G :
            attackType == AttackType.RANGED ? RANGED_VOID_HELM_ITEM_ID_G : MELEE_VOID_HELM_ITEM_ID_G;

        final int helmet_t = attackType == AttackType.MAGIC ? MAGIC_VOID_HELM_ITEM_ID_T :
                attackType == AttackType.RANGED ? RANGED_VOID_HELM_ITEM_ID_T : MELEE_VOID_HELM_ITEM_ID_T;

        final int helmet_red = attackType == AttackType.MAGIC ? MAGIC_VOID_HELM_ITEM_ID_RED :
                attackType == AttackType.RANGED ? RANGED_VOID_HELM_ITEM_ID_RED : MELEE_VOID_HELM_ITEM_ID_RED;

        final int helmet_green = attackType == AttackType.MAGIC ? MAGIC_VOID_HELM_ITEM_ID_GREEN :
                attackType == AttackType.RANGED ? RANGED_VOID_HELM_ITEM_ID_GREEN : MELEE_VOID_HELM_ITEM_ID_GREEN;

        final int helmet_pink = attackType == AttackType.MAGIC ? MAGIC_VOID_HELM_ITEM_ID_PINK :
                attackType == AttackType.RANGED ? RANGED_VOID_HELM_ITEM_ID_PINK : MELEE_VOID_HELM_ITEM_ID_PINK;

        int matchCount = 0;

        for (int slotAndId[] : voidArmourSlotAndIdSet)
            if (isWearingAtSlot(equipment, slotAndId[0], slotAndId[1]))
                matchCount++;

        if (isWearingAtSlot(equipment, SHIELD_SLOT, VOID_KNIGHT_DEFLECTOR_ITEM_ID))
            matchCount++;

        return matchCount >= 3 && (equipment.getItems()[EquipmentConstants.HEAD_SLOT].getId() == helmet || equipment.getItems()[EquipmentConstants.HEAD_SLOT].getId() == helmet_g
                || equipment.getItems()[EquipmentConstants.HEAD_SLOT].getId() == helmet_t
                || equipment.getItems()[EquipmentConstants.HEAD_SLOT].getId() == helmet_red
                || equipment.getItems()[EquipmentConstants.HEAD_SLOT].getId() == helmet_green
                || equipment.getItems()[EquipmentConstants.HEAD_SLOT].getId() == helmet_pink);
    }

    public static boolean isWearingAnyAtSlot(Equipment equipment, int slot, int... itemIds){
        for(int itemId: itemIds){
            if(isWearingAtSlot(equipment, slot, itemId))
                return true;
        }
        return false;
    }

    public static boolean isWearingAtSlot(Player player, int slot, int itemId) {
        return player.getEquipment().get(slot).getId() == itemId;
    }
    public static boolean isWearingAtSlot(Equipment equipment, int slot, int itemId) {
        return equipment.get(slot).getId() == itemId;
    }
    public static boolean isWearingArmadylSet(Player player) {
    	return isWearingAtSlot(player, HEAD_SLOT, 11826)
                && isWearingAtSlot(player, BODY_SLOT, 11828)
                && isWearingAtSlot(player, LEG_SLOT, 11830);
    }
    
    public static boolean isWearingRogueSet(Player player) {
    	return isWearingAtSlot(player, HEAD_SLOT, 5554)
                && isWearingAtSlot(player, BODY_SLOT, 5553)
                && isWearingAtSlot(player, LEG_SLOT, 5555)
                && isWearingAtSlot(player, HANDS_SLOT, 5556)
                && isWearingAtSlot(player, FEET_SLOT, 5557);
    }
    
    public static boolean isWearingProspectorSet(Player player) {
    	return isWearingAtSlot(player, HEAD_SLOT, 12013)
                && isWearingAtSlot(player, BODY_SLOT, 12014)
                && isWearingAtSlot(player, LEG_SLOT, 12015)
                && isWearingAtSlot(player, FEET_SLOT, 12016);
    }
    
    public static boolean isWearingAnkouSet(Player player) {
    	return (isWearingAtSlot(player, HEAD_SLOT, 15239)
                && isWearingAtSlot(player, BODY_SLOT, 15240)
                && isWearingAtSlot(player, LEG_SLOT, 15241)
                && isWearingAtSlot(player, HANDS_SLOT, 15242)
                && isWearingAtSlot(player, FEET_SLOT, 15243))
    			|| (isWearingAtSlot(player, HEAD_SLOT, 15245)
    	                && isWearingAtSlot(player, BODY_SLOT, 15246)
    	                && isWearingAtSlot(player, LEG_SLOT, 15247)
    	                && isWearingAtSlot(player, HANDS_SLOT, 15248)
    	                && isWearingAtSlot(player, FEET_SLOT, 15249))
    			|| (isWearingAtSlot(player, HEAD_SLOT, 20095)
    	                && isWearingAtSlot(player, BODY_SLOT, 20098)
    	                && isWearingAtSlot(player, LEG_SLOT, 20104)
    	                && isWearingAtSlot(player, HANDS_SLOT, 20101)
    	                && isWearingAtSlot(player, FEET_SLOT, 20107));
    }
    
    public static boolean isWearingAnglerSet(Player player) {
    	return isWearingAtSlot(player, HEAD_SLOT, 13258)
                && isWearingAtSlot(player, BODY_SLOT, 13259)
                && isWearingAtSlot(player, LEG_SLOT, 13260)
                && isWearingAtSlot(player, FEET_SLOT, 13261);
    }
    
    public static boolean isWearingLumberJackSet(Player player) {
    	return isWearingAtSlot(player, HEAD_SLOT, 10941)
                && isWearingAtSlot(player, BODY_SLOT, 10939)
                && isWearingAtSlot(player, LEG_SLOT, 10940)
                && isWearingAtSlot(player, FEET_SLOT, 10933);
    }
    
    public static boolean isWearingMummiesSet(Player player) {
    	return isWearingAtSlot(player, HEAD_SLOT, 20080)
                && isWearingAtSlot(player, BODY_SLOT, 20083)
                && isWearingAtSlot(player, LEG_SLOT, 20089)
                && isWearingAtSlot(player, HANDS_SLOT, 20086)
                && isWearingAtSlot(player, FEET_SLOT, 20092);
    }

    public static boolean isWearingDragonFireProtection(Player player) {
        return isWearingAtSlot(player, SHIELD_SLOT, ANTI_DRAGON_SHIELD)
                || isWearingAtSlot(player, SHIELD_SLOT, DRAGONFIRE_SHIELD)
                || isWearingAtSlot(player, SHIELD_SLOT, DRAGONFIRE_SHIELD_2)
                || isWearingAtSlot(player, SHIELD_SLOT, ANCIENT_WYVERN_SHIELD_CHARGED)
                || isWearingAtSlot(player, SHIELD_SLOT, ANCIENT_WYVERN_SHIELD)
                || isWearingAtSlot(player, SHIELD_SLOT, DRAGONFIRE_WARD)
                || isWearingAtSlot(player, SHIELD_SLOT, DRAGONFIRE_WARD_CHARGED);
    }

    public static boolean isWearingWyvernBreathProtection(Player player) {
        return isWearingAtSlot(player, SHIELD_SLOT, ELEMENTAL_SHIELD)
                || isWearingAtSlot(player, SHIELD_SLOT, MIND_SHIELD)
                || isWearingAtSlot(player, SHIELD_SLOT, DRAGONFIRE_SHIELD)
                || isWearingAtSlot(player, SHIELD_SLOT, DRAGONFIRE_SHIELD_2)
                || isWearingAtSlot(player, SHIELD_SLOT, ANCIENT_WYVERN_SHIELD_CHARGED)
                || isWearingAtSlot(player, SHIELD_SLOT, ANCIENT_WYVERN_SHIELD)
                || isWearingAtSlot(player, SHIELD_SLOT, DRAGONFIRE_WARD)
                || isWearingAtSlot(player, SHIELD_SLOT, DRAGONFIRE_WARD_CHARGED);
    }

    public static boolean isWearingVeracSet(AttackContext context){
        return isWearingVeracSet(context.getAttackerEquipment());
    }

    public static boolean isWearingVeracSet(Agent entity) {
        return entity instanceof NPC
                ? ((NPC) entity).getId() == NpcID.VERAC_THE_DEFILED
                : isWearingVeracSet(entity.getAsPlayer().getEquipment());
    }

    public static boolean isWearingVeracSet(Equipment equipment) {
        if(equipment == null)
            return false;
        return equipment.containsAny(VERAC_HELMS)
                && equipment.containsAny(VERAC_BRASSARDS)
                && equipment.containsAny(VERAC_PLATESKIRTS)
                && equipment.containsAny(VERAC_FLAILS);
    }

    public static boolean isWearingDharokSet(AttackContext context){
        return isWearingDharokSet(context.getAttackerEquipment());
    }

    public static boolean isWearingDharokSet(Agent entity) {
        return entity instanceof NPC ? ((NPC) entity).getId() == NpcID.DHAROK_THE_WRETCHED
                : (isWearingDharokSet(entity.getAsPlayer().getEquipment()));
    }

    public static boolean isWearingDharokSet(Equipment equipment) {
        return equipment.containsAny(DHAROK_HELMS)
                && equipment.containsAny(DHAROK_PLATEBODIES)
                && equipment.containsAny(DHAROK_PLATELEGS)
                && equipment.containsAny(DHAROK_GREATAXES);
    }

    public static boolean isWearingAhrimsSet(Equipment equipment) {
        return equipment.containsAny(AHRIMS_HOODS)
                && equipment.containsAny(AHRIMS_ROBETOPS)
                && equipment.containsAny(AHRIMS_ROBESKIRTS)
                && equipment.containsAny(AHRIMS_STAFFS);
    }

    public static boolean isWearingToragSet(Agent agent) {
        return agent instanceof NPC ? ((NPC) agent).getId() == NpcID.TORAG_THE_CORRUPTED
                : isWearingToragsSet(agent.getAsPlayer().getEquipment());
    }

    public static boolean isWearingToragsSet(Equipment equipment) {
        return equipment.containsAny(TORAGS_HELMS)
                && equipment.containsAny(TORAGS_PLATEBODIES)
                && equipment.containsAny(TORAGS_PLATELEGS)
                && equipment.containsAny(TORAGS_HAMMERS);
    }

    public static boolean isWearingGuthanSet(Agent entity) {
        return entity instanceof NPC ? ((NPC) entity).getId() == NpcID.GUTHAN_THE_INFESTED
                : isWearingGuthanSet(entity.getAsPlayer().getEquipment());
    }

    public static boolean isWearingGuthanSet(Equipment equipment) {
        return equipment.containsAny(GUTHANS_HELMS)
                && equipment.containsAny(GUTHANS_PLATEBODIES)
                && equipment.containsAny(GUTHANS_CHAINSKIRTS)
                && equipment.containsAny(GUTHANS_WARSPEARS);
    }

    public static boolean isWearingAnyROW(Equipment equipment) {
        return equipment.containsAny(RING_OF_WEALTHS_IDS);
    }
    
    public static boolean isWearingAhrimSet(Agent agent) {
        return agent instanceof NPC ? ((NPC) agent).getId() == NpcID.AHRIM_THE_BLIGHTED
                : isWearingAhrimsSet(agent.getAsPlayer().getEquipment());
    }

    public static boolean isWearingKarilSet(Agent entity) {
        return entity instanceof NPC ? ((NPC) entity).getId() == NpcID.KARIL_THE_TAINTED
                : isWearingKarilSet(entity.getAsPlayer().getEquipment());
    }

    public static boolean isWearingKarilSet(Equipment equipment) {
        return equipment.containsAny(KARILS_COIFS)
                && equipment.containsAny(KARILS_LEATHERTOPS)
                && equipment.containsAny(KARILS_LEATHERSKIRTS)
                && equipment.containsAny(KARILS_CROSSBOWS);
    }
    
    public static boolean isWearingAncestralSet(Agent entity) {
        return (entity.getAsPlayer().getEquipment().containsAll(ANCESTRAL_HAT)
                || entity.getAsPlayer().getEquipment().containsAll(ANCESTRAL_ROBE_TOP)
                || entity.getAsPlayer().getEquipment().containsAll(ANCESTRAL_ROBE_BOTTOM));
    }

    public static boolean isWearingJusticiarSet(Agent entity) {
        return entity instanceof Player
                && entity.getAsPlayer().getEquipment().containsAll(JUSTICIAR_SET_ITEM_IDS);
    }

    public static boolean isWieldingTemporaryWeapon(Player player) {
        return player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getDefinition().getName().endsWith("(c)");
    }

    public static boolean isWearingElysianShield(Player player){
        return isWearingAtSlot(player, SHIELD_SLOT, ELYSIAN_SHIELD_ITEM_ID) || isWearingAtSlot(player, SHIELD_SLOT, 15762);
    }

    public static boolean isWearingDivineShield(Player player){
        return isWearingAtSlot(player, SHIELD_SLOT, DIVINE_SHIELD_ITEM_ID) || isWearingAtSlot(player, SHIELD_SLOT, 15798)
                || isWearingAtSlot(player, SHIELD_SLOT, 16095);
    }

    public static boolean isWearingDivineShield_2(Player player){
        return isWearingAtSlot(player, SHIELD_SLOT, INFERNAL_DIVINE_SHIELD_ITEM_ID) || isWearingAtSlot(player, SHIELD_SLOT, 15954);
    }

    public static boolean isWearingSpectralShield(Player player){
        return isWearingAtSlot(player, SHIELD_SLOT, SPECTRAL_SPIRIT_SHIELD);
    }

    public static boolean isUsingCrossbow(AttackContext context){
        return isUsingCrossbow(context.getAttackerEquipment());
    }

    public static boolean isUsingCrossbow(Equipment equipment){
        return Optional.ofNullable(RangedWeapon.getFor(equipment))
                .filter(weapon -> weapon.getType().isCrossbow())
                .isPresent();
    }

    public static boolean isUsingDragonStoneBolts(AttackContext context){
        return isUsingCrossbow(context) && isUsingDragonStoneBolts(context.getAttackerEquipment());
    }

    public static boolean isUsingDragonStoneBolts(Equipment equipment) {
        return isUsingCrossbow(equipment) && equipment.containsAny(DRAGON_BOLTS, DRAGON_BOLTS_E_, 21971, 21948);
    }

    public static boolean isUsingOpalBolts(AttackContext context){
        return isUsingCrossbow(context) && isUsingOpalBolts(context.getAttackerEquipment());
    }

    public static boolean isUsingOpalBolts(Equipment equipment){
        return isUsingCrossbow(equipment) && equipment.containsAny(OPAL_BOLTS, OPAL_BOLTS_E_);
    }

    public static boolean isUsingPearlBolts(AttackContext context){
        return isUsingCrossbow(context) && isUsingPearlBolts(context.getAttackerEquipment());
    }

    public static boolean isUsingPearlBolts(Equipment equipment){
        return isUsingCrossbow(equipment) && equipment.containsAny(PEARL_BOLTS, PEARL_BOLTS_E_);
    }

    public static boolean hasImbuedGodCape(Equipment equipment){
        return equipment.containsAny(IMBUED_SARADOMIN_MAX_CAPE_L, 21776, IMBUED_SARADOMIN_CAPE, IMBUED_ZAMORAK_CAPE_L, IMBUED_ZAMORAK_MAX_CAPE_L, 21780, IMBUED_ZAMORAK_CAPE, IMBUED_GUTHIX_CAPE_L, IMBUED_GUTHIX_MAX_CAPE_L, 21784, IMBUED_GUTHIX_CAPE);
    }

    public static boolean hasSalveAmulet(Equipment equipment) {
        return equipment.containsAny(SALVE_AMULET) || hasEnchantedSalveAmulet(equipment) || hasImbuedSalveAmulet(equipment);
    }

    public static boolean hasEnchantedSalveAmulet(Equipment equipment) {
        return equipment.containsAny(SALVE_AMULET_E_);
    }

    public static boolean hasImbuedSalveAmulet(Equipment equipment) {
        return equipment.containsAny(SALVE_AMULET_I_);
    }

    public static boolean hasEnchantedImbuedSalveAmulet(Equipment equipment) {
        return equipment.containsAny(SALVE_AMULET_EI_);
    }

    public static boolean hasAnyAmuletOfTheDamned(Player player) {
        return hasAnyAmuletOfTheDamned(player.getEquipment());
    }

    public static boolean hasAnyAmuletOfTheDamned(Equipment equipment) {
        return equipment.containsAny(AMULET_OF_THE_DAMNED, AMULET_OF_THE_DAMNED_FULL_);
    }

    public static boolean isWearingLavaBlade(Equipment equipment){
        return isWearingAtSlot(equipment, WEAPON_SLOT, LAVA_BLADE_ITEM_ID);
    }

    public static boolean isWearingInfernalBlade(Equipment equipment){
        return isWearingAtSlot(equipment, WEAPON_SLOT, INFERNAL_BLADE_ITEM_ID);
    }

    public static boolean hasAnyRingOfRecoil(Equipment equipment) {
        return equipment.containsAny(RECOIL_RING_IDS);
    }

    public static boolean isWearingMetalBody(Player player) {

        final int itemAtBody = player.getEquipment().get(BODY_SLOT).getId();

        if (itemAtBody > 0) {

            final ItemDefinition definition = ItemDefinition.forId(itemAtBody);

            if (definition != null) {

                final String name = definition.getName().toLowerCase();

                if(name.contains("hide")) // exclude d'hides (black d'hide)
                    return false;

                return name.contains("bronze") || name.contains("iron")
                        || name.contains("steel") || name.contains("black")
                        || name.contains("mithril") || name.contains("adamant")
                        || name.contains("rune") || name.contains("dragon")
                        || name.contains("justiciar") || name.contains("initiate")
                        || name.contains("proselyte") || name.contains("white")
                        || name.contains("obsidian") || name.contains("granite")
                        || name.contains("3rd age") || name.contains("hydro")
                        || definition.getExamine().contains("metal");
            }
        }
        return false;
    }

    public static int countMatches(Equipment equipment, String key) {
        return (int) equipment.getValidItems().stream().filter(item -> item.getDefinition().getName().toLowerCase().contains(key)).count();
    }

    /**
     * Gets the amount of item of a type a player has, for example, gets how many Zamorak items a player is wearing for GWD
     *
     * @param p The player
     * @param s The item type to search for
     * @return The amount of item with the type that was found
     */
    public static int getItemCount(Player p, String s, boolean inventory) {
        int count = 0;
        for (Item t : p.getEquipment().getItems()) {
            if (t == null || t.getId() < 1 || t.getAmount() < 1)
                continue;
            if (t.getDefinition().getName().toLowerCase().contains(s.toLowerCase()))
                count++;
        }
        if (inventory) {
            for (Item t : p.getInventory().getItems()) {
                if (t == null || t.getId() < 1 || t.getAmount() < 1)
                    continue;
                if (t.getDefinition().getName().toLowerCase().contains(s.toLowerCase()))
                    count++;
            }
        }
        return count;
    }

    /**
     * Gets the amount of Zamorak items of a type a player has, for example, gets how many Zamorak items a player is wearing for GWD
     *
     * @param p The player
     * @return The amount of item with the type that was found
     */
    public static int getZamorakItems(Player p) {
        int count = 0;
        for (Item t : p.getEquipment().getItems()) {
            if (t == null || t.getId() < 1 || t.getAmount() < 1)
                continue;
            if (t.getId() == 11791 || t.getId() == 22296 || t.getId() == 24144 || t.getId() == 12904
                    || t.getId() == 22978 || t.getId() == 22555 || t.getId() == 22545 || t.getId() == 15824
                    || t.getId() == 24417 || t.getId() == 24419 || t.getId() == 24420 || t.getId() == 24421
                    || t.getId() == 15877 || t.getId() == 15879 || t.getId() == 15881 || t.getId() == 15883
                    || t.getId() == 15885|| t.getId() == 15887 || t.getId() == 15972 ||  t.getId() == 15974 || t.getId() == 15976
            ) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets the amount of Bandos items of a type a player has, for example, gets how many Bandos items a player is wearing for GWD
     *
     * @param p The player
     * @return The amount of item with the type that was found
     */
    public static int getBandosItems(Player p) {
        int count = 0;
        for (Item t : p.getEquipment().getItems()) {
            if (t == null || t.getId() < 1 || t.getAmount() < 1)
                continue;
            if (t.getId() == 12608 || t.getId() == 20232 || t.getId() == 11061 || t.getId() == 21733
                    || t.getId() == 15877 || t.getId() == 15879 || t.getId() == 15881 || t.getId() == 15883
                    || t.getId() == 15885|| t.getId() == 15887 || t.getId() == 15972 ||  t.getId() == 15974 || t.getId() == 15976
            ) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets the amount of Saradomin items of a type a player has, for example, gets how many Saradomin items a player is wearing for GWD
     *
     * @param p The player
     * @return The amount of item with the type that was found
     */
    public static int getSaradominItems(Player p) {
        int count = 0;
        for (Item t : p.getEquipment().getItems()) {
            if (t == null || t.getId() < 1 || t.getAmount() < 1)
                continue;
            if (t.getId() == 22954 || t.getId() == 22296 || t.getId() == 22326 || t.getId() == 22327 || t.getId() == 22328
                    || t.getId() == 15877 || t.getId() == 15879 || t.getId() == 15881 || t.getId() == 15883
                    || t.getId() == 15885|| t.getId() == 15887 || t.getId() == 15972 ||  t.getId() == 15974 || t.getId() == 15976
            ) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets the amount of Armadyl items of a type a player has, for example, gets how many Armadyl items a player is wearing for GWD
     *
     * @param p The player
     * @return The amount of item with the type that was found
     */
    public static int getArmadylItems(Player p) {
        int count = 0;
        for (Item t : p.getEquipment().getItems()) {
            if (t == null || t.getId() < 1 || t.getAmount() < 1)
                continue;
            if (t.getId() == 20229 || t.getId() == 22550 || t.getId() == 12610 || t.getId() == 15877 || t.getId() == 15879 || t.getId() == 15881 || t.getId() == 15883
                    || t.getId() == 15885|| t.getId() == 15887 || t.getId() == 15972 ||  t.getId() == 15974 || t.getId() == 15976
            ) {
                count++;
            }
        }
        return count;
    }

    /**
     * Check if the player is wearing any Ava's device to save arrows.
     */
    public static boolean isWearingAvas(Player player) {
        return player.getInventory().containsAny(21898, 22109, 10499, 10498, 22109);
    }

    public static int getExtraBindSpellDuration(Agent agent) {
        if (agent instanceof Player)
            return countSwapbarkAmourPieces(((Player) agent).getEquipment());
        return 0;
    }

    public static int countSwapbarkAmourPieces(Equipment equipment) {
        int count = 0;
        for (int itemId: SWAMPBARK_SET_ITEM_IDS)
            if (equipment.contains(itemId))
                count++;
        return count;
    }

    public static int countBloodbarkAmourPieces(Equipment equipment) {
        int count = 0;
        for (int itemId: BLOODBARK_SET_ITEM_IDS)
            if (equipment.contains(itemId))
                count++;
        return count;
    }

    public static int getSpellExtraHealAmount(Agent agent) {
        if (agent instanceof Player)
            return countBloodbarkAmourPieces(((Player) agent).getEquipment());
        return 0;
    }

    public static boolean isWearingAnyInquisitors(Equipment equipment) {
        return equipment.containsAny(INQUSITORS_SET_ITEM_IDS);
    }

    public static double getInquisitorsBonus(Equipment equipment) {
        if (equipment.containsAll(INQUSITORS_SET_ITEM_IDS)) {
            return 1.025;
        }

        return Arrays.stream(INQUSITORS_SET_ITEM_IDS)
                .filter(equipment::contains)
                .mapToDouble(id -> 0.005)
                .sum() + 1;
    }

}
