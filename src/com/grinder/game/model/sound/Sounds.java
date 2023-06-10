package com.grinder.game.model.sound;

import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.game.model.item.Item;
import com.grinder.util.Misc;

public class Sounds {

	/**
	 * Max volume possible
	 */
	private static final int VOLUME_LIMIT = 100;

	public static final int EQUIP_SOUND = 2244;
	public static final int EAT_SOUND = 2393;
	public static final int ARROW_HIT_SOUND = 1979;
	public static final int GRANITE_MAUL_SPEC = 2715;
	public static final int DARK_BOW_REGULAR_SPECIAL_SOUND = 3736;
	public static final int DARK_BOW_DRAGON_SPECIAL_SOUND = 3733;
	public static final int DRAGON_DAGGER_SPECIAL = 2537;

	public static final int PRAYER_UNAVAILABLE_SOUND = 2673;
	public static final int PRAYER_TURNED_OFF = 2663;
	public static final int PRAYER_DEPLETED = 2672;

	/** Default equip SOUND **/
	public static final int EQUIP_HELM_DEFAULT_SOUND = 2238;
	public static final int EQUIP_CAPE_DEFAULT_SOUND = 2238;
	public static final int EQUIP_AMULETS_DEFAULT_SOUND = 2238;
	public static final int EQUIP_ARROWS_DEFAULT_SOUND = 2238;
	public static final int EQUIP_WEAPON_DEFAULT_SOUND = 2248;
	public static final int EQUIP_BODY_DEFAULT_SOUND = 2238;
	public static final int EQUIP_SHIELD_DEFAULT_SOUND = 2245;
	public static final int EQUIP_LEGS_DEFAULT_SOUND = 2238;
	public static final int EQUIP_GLOVES_DEFAULT_SOUND = 2248;
	public static final int EQUIP_BOOTS_DEFAULT_SOUND = 2238;
	public static final int EQUIP_RING_DEFAULT_SOUND = 2238;
	public static final int EQUIP_HELM_METAL_SOUND = 2240;

	/**
	 * Bronze-iron platebodies, chainbodies, guthans body, torags body, ahrims
	 * body, void, granite, armadyl, fighter torso,
	 **/
	public static final int EQUIP_BODY_METAL_SOUND = 2239;
	
	/**
	 * Robes, Shirts, proselyte, initiate, spined body, snake skin, mystic,
	 * infinity, splitbark,
	 **/
	public static final int EQUIP_BOOTS_METAL_SOUND = 2237;
	
	/**
	 * Dragon boots, Bandos boots, Bronze-Rune boots, Climbing, snakeskin,
	 * mourner boots, fancy boots, fighting boots;
	 **/
	public static final int EQUIP_LEGS_METAL_SOUND = 2242;
	public static final int EQUIP_GLOVES_METAL_SOUND = 2236;
	/** Bronze-barrows gloves, karamja, lunar, slayer **/
	public static final int EQUIP_SHIELD_METAL_SOUND = 2245;
	
	/*public static final int ELEMENTAL_HELM_EQUIP = 1539;
	public static final int DARK_BOW_EQUIP = 3738;
	public static final int WOODEN_SHIELD_EQUIP = 2250;
	public static final int BOW_AND_ARROWS_EQUIP = 2244;*/
	/**
	 * Includes all arrows, all bows, all crossbows except exceptional ones.
	 **/
	//public static final int BOLTS_EQUIP_SOUND = 2238;
	/** Includes crystal bow, tars **/
	
	/*public static final int DAGGER_EQUIP_SOUND = 2248;
	public static final int SWORD_EQUIP_SOUND = 2248;
	public static final int LONGSWORD_EQUIP_SOUND = 2248;
	public static final int TWO_HANDED_SWORD_EQUIP_SOUND = 2248;
	public static final int SCIMITAR_EQUIP_SOUND = 2248;*/
	/**
	 * Obby knife and its attack style like scimitar, Brine sabre, Egg whisk
	 **/

	public static final int EQUIP_AXE_SOUND = 2229;
	public static final int EQUIP_PICKAXE_SOUND = 2232;
	public static final int TOXIC_BLOW_PIPE_EQUIP = 2247;
	//public static final int BATTLE_AXE_EQUIP_SOUND = 2232;
	
	//public static final int CLAWS_EQUIP_SOUND = 2238;
	//public static final int GRANITE_MAUL_EQUIP_SOUND = 2233;
	//public static final int BONE_BOLTS_EQUIP_SOUND = 2235;
//	public static final int KNIVES_EQUIP_SOUND = 2242;
	//public static final int DARTS_EQUIP_SOUND = 2242;
	/*public static final int STAFFS_EQUIP_SOUND = 2247;
	public static final int JAVELIN_EQUIP_SOUND = 2247;
	public static final int ANCIENT_STAFF_EQUIP = 2238;
	public static final int CHINCHOMPA_EQUIP = 2238;
	public static final int FLAG_EQUIP_SOUND = 2231;
	public static final int BONE_BOLTS_EQUIP = 2235;
	public static final int WAND_EQUIP_SOUND = 2247;
	public static final int HASTA_EQUIP_SOUND = 2247;
	public static final int HALBERD_EQUIP_SOUND = 2247;
	public static final int SPEAR_EQUIP_SOUND = 2247;
	public static final int MACE_EQUIP_SOUND = 2246;
	public static final int CANE_EQUIP_SOUND = 2246;
	public static final int DRAGON_CANE_EQUIP = 2247;
	public static final int WARHAMMER_EQUIP_SOUND = 2233;
	public static final int MIND_SHIELDS_EQUIP_SOUND = 1539;
	public static final int META_TENDERIZER_EQUIP_SOUND = 2233;
	public static final int SALAMANDER_EQUIP_SOUND = 732;
	public static final int WHIP_EQUIP_SOUND = 2249;
	public static final int BONE_SPEAR_EQUIP = 2247;
	public static final int SEERCULL_EQUIP = 2244;
	public static final int EXCALIBUR_EQUIP_SOUND = 2248;
	public static final int OBBY_MAUL_EQUIP_SOUND = 2233;
	public static final int TORAGS_HAMMERS_EQUIP_SOUND = 2233;
	public static final int KARIL_CROSS_BOW_EQUIP_SOUND = 2244;*/

	public static final int PUNCH_SOUND = 2566;
	public static final int KICK_SOUND = 2565;

	/** Including ballista **/
	public static final int VERACS_FLAIL_EQUIP_SOUND = 2246;

	public static final int DRAGON_HIDE_BODY_EQUIP = 2241;


	/**
	 * Bronze-rune platelegs, plateskirts, granite legs, guthans chainskirt,
	 * karils leatherskirt, saradomin, zamorak..etc, bandos tasset,
	 **/
	public static final int CHAPS_LEGS_EQUIP = 2241;
	/** Ghostly legs, skirt, robe, bottom, trousers, cuisse, fancy legs, etc **/
	public static final int VAMBS_EQUIP_SOUND = 2241;


	/** Crystal shield, fancy shields, defenders, **/
	public static final int FREMMINIK_SHIELD_EQUIP = 2250;
	/** Round shields, Broodoo, cabbage round shield, **/
	public static final int LIGHT_WEAPON_EQUIP_SOUND = 2232;
	/** Darklight, Silverlight, arc light..etc **/
	public static final int AVA_ACCUMULATOR_EQUIP = 3284;
	public static final int THROWN_AXE_EQUIP_SOUND = 2232;
	/** Prayers: **/
	/** NOTE: SWITCHING_PRAYER_OFF: XXXX, **/
	/**
	 * NOTE: When you put 3 prayers of melee burst of strength, steel skin..etc
	 * and then you put sharp eye it will make sharp eye sound only and it wont
	 * make the switching off prayer sound although it auto switched off the 3
	 * SOUND of melee.
	 **/
	public static final int THICK_SKIN_PRAYER = 2690;
	public static final int BURST_OF_STRENGTH_PRAYER = 2688;
	public static final int CLARITY_OF_THOUGHT_PRAYER = 2664;
	public static final int SHARP_EYE_PRAYER = 2685;
	public static final int MYSTIC_WILL_PRAYER = 2670;
	public static final int ROCK_SKIN_PRAYER = 2684;
	public static final int SUPERHUMAN_STRENGTH_PRAYER = 2689;
	public static final int IMPROVED_REFLEXES_PRAYER = 2662;
	public static final int RAPID_RESTORE_PRAYER = 2679;
	public static final int RAPID_HEAL_PRAYER = 2678;
	public static final int PROTECT_ITEM_PRAYER = 1982;
	public static final int HAWK_EYE_PRAYER = 2666;
	public static final int MYSTIC_LORE_PRAYER = 2668;
	public static final int STEEL_SKIN_PRAYER = 2687;
	public static final int ULTIMATE_STRENGTH_PRAYER = 2691;
	public static final int INCREDIBLE_REFLEXES_PRAYER = 2667;
	public static final int PROTECT_MAGIC_PRAYER = 2675;
	public static final int PROTECT_MISSILES_PRAYER = 2677;
	public static final int PROTECT_MELEE_PRAYER = 2676;
	public static final int EAGLE_EYE_PRAYER = 2665;
	public static final int MYSTIC_MIGHT_PRAYER = 2669;
	public static final int RETRIBUTION_PRAYER = 2682;
	public static final int REDEMPTION_PRAYER = 2680;
	public static final int SMITE_PRAYER = 2686;
	public static final int CHIVALRY_PRAYER = 3826;
	public static final int PIETY_PRAYER = 3825;

	/** Magic prayers SOUND: **/
	public static final int WIND_STRIKE_CAST = 220;
	public static final int WIND_STRIKE_CONTACT = 221;
	public static final int ENCHANT_BOLTS_SPELL = 2091;
	public static final int CONFUSE_CAST = 119;
	public static final int CONFUSE_CONTACT = 121;
	public static final int WATER_STRIKE_CAST = 211;
	public static final int WATER_STRIKE_CONTACT = 212;
	public static final int ENCHANT_ONE_SPELL = 136;
	public static final int EARTH_STRIKE_CAST = 132;
	public static final int EARTH_STRIKE_CONTACT = 133;
	public static final int WEAKEN_CAST = 3011;
	public static final int WEAKEN_CONTACT = 3010;
	public static final int FIRE_STRIKE_CAST = 160;
	public static final int FIRE_STRIKE_CONTACT = 161;
	public static final int BONES_TO_BANANA_SPELL = 114;
	public static final int WIND_BOLT_CAST = 218;
	public static final int WIND_BOLT_CONTACT = 219;
	public static final int CURSE_CAST = 127;
	public static final int CURSE_CONTACT = 126;
	public static final int BIND_CAST = 101;
	public static final int BIND_CONTACT = 99;
	public static final int LOW_LVL_ALCH_SPELL = 98;
	public static final int WATER_BOLT_CAST = 209;
	public static final int WATER_BOLT_CONTACT = 210;
	public static final int VARROCK_TELEPORT_SPELL = 200;
	public static final int ENCHANT_TWO_SPELL = 141;
	public static final int EARTH_BOLT_CAST = 130;
	public static final int EARTH_BOLT_CONTACT = 131;
	public static final int LUMBRIDGE_TELEPORT_SPELL = 200;
	public static final int TELEGRAB_SPELL = 3006;
	public static final int FIRE_BOLT_CAST = 157;
	public static final int FIRE_BOLT_CONTACT = 158;
	public static final int FALADOR_TELEPORT_SPELL = 200;
	public static final int CRUMBLE_UNDEAD_CAST = 122;
	public static final int CRUMBLE_UNDEAD_CONTACT = 124;
	public static final int HOUSE_TELEPORT_SPELL = 200;
	public static final int WIND_BLAST_CAST = 216;
	public static final int WIND_BLAST_CONTACT = 217;
	public static final int SUPERHEAT_ITEM_SPELL = 190;
	public static final int CAMELOT_TELEPORT_SPELL = 200;
	public static final int WATER_BLAST_CAST = 207;
	public static final int WATER_BLAST_CONTACT = 208;
	public static final int ENCHANT_THREE_SPELL = 145;
	public static final int IBANS_BLAST_CAST = 162;
	public static final int IBANS_BLAST_CONTACT = 163;
	public static final int SNARE_CAST = 3003;
	public static final int SNARE_CONTACT = 3002;
	public static final int MAGIC_DART_CAST = 1718;
	public static final int MAGIC_DART_CONTACT = 174;
	public static final int EARTH_BLAST_CAST = 128;
	public static final int EARTH_BLAST_CONTACT = 129;
	public static final int HIGH_LVL_ALCH_SPELL = 97;
	public static final int CHARGE_WATER_ORB_SPELL = -1;
	public static final int ENCHANT_FOUR_SPELL = 137;
	public static final int FIRE_BLAST_CAST = 155;
	public static final int FIRE_BLAST_CONTACT = 156;
	public static final int CHARGE_EARTH_ORB = -1;
	public static final int BONES_TO_PEACHES_SPELL = 114;
	public static final int SARADOMIN_STRIKE_CAST = -1;
	public static final int SARADOMIN_STRIKE_CONTACT = 1659;
	public static final int CLAWS_OF_GUTHIX_CAST = -1;
	public static final int CLAWS_OF_GUTHIX_CONTACT = 1653;
	public static final int FLAMES_OF_ZAMORAK_CAST = -1;
	public static final int FLAMES_OF_ZAMORAK_CONTACT = 1655;
	public static final int WIND_WAVE_CAST = 222;
	public static final int WIND_WAVE_CONTACT = 223;
	public static final int WIND_SURGE_CAST = 4028;
	public static final int WIND_SURGE_CONTACT = 4027;
	public static final int CHARGE_FIRE_ORB_SPELL = -1;
	public static final int WATER_WAVE_CAST = 213;
	public static final int WATER_WAVE_CONTACT = 214;
	public static final int WATER_SURGE_CAST = 4030;
	public static final int WATER_SURGE_CONTACT = 4029;
	public static final int CHARGE_AIR_ORB_SPELL = -1;
	public static final int VULNERABILITY_CAST = 3009;
	public static final int VULNERABILITY_CONTACT = 3008;
	public static final int EARTH_WAVE_CAST = 134;
	public static final int EARTH_WAVE_CONTACT = 135;
	public static final int EARTH_SURGE_CAST = 4025;
	public static final int EARTH_SURGE_CONTACT = 4026;
	public static final int ENFEEBLE_CAST = 148;
	public static final int ENFEEBLES_CONTACT = 150;
	public static final int TELE_OTHER_LUMBRIDGE_SPELL = 199;
	public static final int FIRE_WAVE_CAST = 162;
	public static final int FIRE_WAVE_CONTACT = 163;
	public static final int FIRE_SURGE_CAST = 4032;
	public static final int FIRE_SURGE_CONTACT = 4031;
	public static final int ENTANGLE_CAST = 151;
	public static final int ENTANGLE_CONTACT = 153;
	public static final int STUN_CAST = 3004;
	public static final int STUN_CONTACT = 3005;
	public static final int CHARGE_SPELL = 1651;
	public static final int TELE_OTHER_FALADOR_SPELL = 199;
	public static final int TELE_BLOCK_CAST = 202;
	public static final int TELE_BLOCK_CONTACT = 203;
	public static final int ENCHANT_SIX_SPELL = 144;
	public static final int TELEOTHER_CAMELOT_SPELL = 199;
	public static final int TELEOTHER_TELEPORTING = 201;
	/** Ancient **/
	public static final int SMOKE_RUSH_CAST = 183;
	public static final int SMOKE_RUSH_CONTACT = 185;
	public static final int SHADOW_RUSH_CAST = 178;
	public static final int SHADOW_RUSH_CONTACT = 179;
	public static final int PADEWAA_TELEPORT_SPELL = 197;
	public static final int BLOOD_RUSH_CAST = 106;
	public static final int BLOOD_RUSH_CONTACT = 110;
	public static final int ICE_RUSH_CAST = 171;
	public static final int ICE_RUSH_CONTACT = 173;
	public static final int SENISTEN_TELEPORT_SPELL = 197;
	public static final int SMOKE_BURST_CAST = 183;
	public static final int SMOKE_BURST_CONTACT = 182;
	public static final int SHADOW_BURST_CAST = 178;
	public static final int SHADOW_BURST_CONTACT = 177;
	public static final int KHARYLL_TELEPORT_SPELL = 197;
	public static final int BLOOD_BURST_CAST = 106;
	public static final int BLOOD_BURST_CONTACT = 105;
	public static final int ICE_BURST_CAST = 171;
	public static final int ICE_BURST_CONTACT = 170;
	public static final int LASSAR_TELEPORT_SPELL = 197;
	public static final int SMOKE_BLITZ_CAST = 183;
	public static final int SMOKE_BLITZ_CONTACT = 181;
	public static final int SHADOW_BLITZ_CAST = 178;
	public static final int SHADOW_BLITZ_CONTACT = 176;
	public static final int DAREEYAK_TELEPORT_SPELL = 197;
	public static final int BLOOD_BLITZ_CAST = 106;
	public static final int BLOOD_BLITZ_CONTACT = 104;
	public static final int ICE_BLITZ_CAST = 171;
	public static final int ICE_BLITZ_CONTACT = 169;
	public static final int CARRALANGAR_TELEPORT = 197;
	public static final int SMOKE_BARRAGE_CAST = 183;
	public static final int SMOKE_BARRAGE_CONTACT = 180;
	public static final int SHADOW_BARRAGE_CAST = 178;
	public static final int SHADOW_BARRAGE_CONTACT = 175;
	public static final int ANNAKARL_TELEPORT_SPELL = 197;
	public static final int BLOOD_BARRAGE_CAST = 106;
	public static final int BLOOD_BARRAGE_CONTACT = 102;
	public static final int ICE_BARRAGE_CAST = 171;
	public static final int ICE_BARRAGE_CONTACT = 168;

	public static final int BAKE_PIE_SPELL = 2879;
	/** It keeps baking until you don't have any pie left in inventory **/
	public static final int CURE_PLANT_SPELL = -1;
	public static final int MONSTER_EXAMINE_SPELL = 3620;
	public static final int NPC_CONTACT_SPELL = 3618;
	public static final int CURE_OTHER_SPELL = -1;
	public static final int HUMIDIFY_SPELL = 3614;
	public static final int MOONCLAN_TELEPORT_SPELL = 200;
	public static final int CURE_ME_SPELL = -1;
	public static final int HUNTER_KIT_SPELL = 3615;
	public static final int WATERBIRTH_TELEPORT_SPELL = 200;
	public static final int TELE_GROUP_WATERBIRTH_SPELL = 200;
	public static final int CURE_GROUP_SPELL = -1;
	public static final int[] STAT_SPY_SPELL = new int[]{3620, 3621};
	public static final int BARBARIAN_TELPORT_SPELL = 200;
	public static final int TELEGROUP_BARBARIAN_SPELL = 200;
	public static final int SUPERGLASS_MAKE_SPELL = 2896;
	public static final int TAN_LEATHER_SPELL = -1;
	public static final int KHAZHARD_TELEPORT_SPELL = 200;
	public static final int TELEGROUP_KHAZARD_SPELL = 200;
	public static final int DREAM_SPELL = 3619;
	public static final int STRING_JEWERLY_SPELL = 2903;
	public static final int[] STAT_RESTORE_SPELL = new int[]{2899, 2897};
	public static final int MAGIC_IMBUE_SPELL = 2888;
	public static final int FERTILE_SOIL_SPELL = -1;
	public static final int[] BOOST_POTION_SHARE_SPELL = new int[]{2901, 2904};
	public static final int FISHING_GUILD_TELEPORT_SPELL = 200;
	public static final int TELEGROUP_FISHING_TELEPORT = 200;
	public static final int PLANK_MAKE_SPELL = 3617;
	public static final int CATHERBY_TELEPORT_SPELL = 200;
	public static final int TELEGROUP_CATHERBY_SPELL = 200;
	public static final int RECHARGE_DRAGONSTONE_SPELL = -1;
	public static final int ICE_PLATEAU_TELEPORT = 200;
	public static final int TELE_GROUP_ICE_PLATEAU = 200;
	public static final int ENERGY_TRANSFER_SPELL = 2885;
	public static final int HEAL_OTHER_SPELL = -1;
	public static final int VENG_OTHER_SPELL = 2908;
	public static final int VENGENANCE_SPELL = 2907;
	public static final int HEAL_GROUP_SPELL = -1;
	public static final int SPELL_BOOK_SWAP_SPELL = 3613;
	public static final int GEOMANCY_SPELL = 3613;
	public static final int SPIN_FLAX_SPELL = 3615;
	public static final int FILLING_BUCKET_WITH_WATER = 2609;


	public static final int AUBURY_TELEPORT_FULL = 125;
	public static final int AUBURY_TELEPORT = 126;

	/** SPECIAL ATTACK SOUND: **/
	public static final int WHIP_SPECIAL_SOUND = 2713;
	public static final int GODSWORD_SPECIAL_SOUND = 3869;
	public static final int SARADOMIN_SWORD_SPECIAL_SOUND = 3887;
	public static final int DRAGON_LONG_SPECIAL_SOUND = 2529;
	public static final int DRAGON_SCIM_SPECIAL_SOUND = 2540;
	public static final int DRAGON_DAGGER_SPECIAL_SOUND = 2537;
	public static final int DRAGON_MACE_SPECIAL_SOUND = 2541;
	public static final int DRAGON_CLAWS_SPECIAL_SOUND = 2535;
	/** Its not real one but I chose it cause its not supported on osrs **/
	public static final int DARK_BOW_SPECIAL_SOUND = 3731;
	public static final int DARK_BOW_SPECIAL_SOUND_2 = 3733;
	public static final int MSB_SPECIAL_SOUND = 2545;
	public static final int DRAGON_2H_SPECIAL_SOUND = 2530;
	public static final int DRAGON_BATTLE_AXE_SPECIAL_SOUND = 2538;
	public static final int DRAGON_AXE_SPECIAL_SOUND = 2531;
	public static final int DRAGON_HALLY_SPECIAL_SOUND = 2533;
	public static final int DRAGON_SPEAR_SPECIAL_SOUND = 2544;
	public static final int DRAGON_PICKAXE_SPECIAL_SOUND = 2655;
	public static final int BARRELCHEST_ANCHOR_SPECIAL_SOUND = 3481;
	public static final int TELEPORT_REQUEST_SOUND = 225;
	public static final int DARKLIGHT_SPECIAL_SOUND = 225; // Yes its 225
	public static final int BONE_DAGGER_SPECIAL_SOUND = 1084;
	public static final int ABYSSAL_BLUDGEON_SPECIAL_SOUND = 2766;
	public static final int BRINE_SABRE_SPECIAL_SOUND = 3473;
	public static final int ANCIENT_MACE_SPECIAL_SOUND = 3592;
	public static final int ARMADYL_CROSSBOW_SPECIAL_SOUND = 3892;
	public static final int ARMADYL_GODSWORD_SPECIAL_SOUND = 3869;
	public static final int ZAMORAK_SPEAR_SPECIAL_SOUND = 2544;
	public static final int GRANITE_HAMMER_SPECIAL_SOUND = 2520;
	public static final int DRAGON_WHIP_SPECIAL_SOUND = 3761;
	public static final int INDIGO_WHIP_SPECIAL_SOUND = 169;
	public static final int DRAGON_GODSWORD_SPECIAL_SOUND = 3762; // TODO: FIND
	public static final int GOLD_WHIP_SPECIAL_SOUND = 384;
	public static final int FOREST_WHIP_SPECIAL_SOUND = 224;
	public static final int WHITE_WHIP_SPECIAL_SOUND = 2713; // TODO: FIND
	public static final int UNKNOWN_WHIP_SPECIAL_SOUND = 75;
	public static final int BANANA_WHIP_SPECIAL_SOUND = 2713; // TODO: FIND
	public static final int PIMPZ_WHIP_SPECIAL_SOUND = 2713; // TODO: FIND
	public static final int ROSE_WHIP_SPECIAL_SOUND = 323;


	/** Same for zamorak hasta **/
	public static final int RUNE_THROWNAXE_SPECIAL_SOUND = 2528;
	public static final int DOGRESHUUN_CBOW_SPECIAL_SOUND = 1080;
	public static final int SEERCULL_SPECIAL_SOUND = 2546;
	public static final int[] BLOW_PIPE_SPECIAL_SOUND = new int[]{2696, 800};
	public static final int BALLISTA_SPECIAL_SOUND = 2536;
	//public static final int[] DFS_SPECIAL_ATTACK_SOUND = new int[]{3761, 161};
	public static final int DFS_SPECIAL_ATTACK_SOUND = 3761;

	/** 161 on contact with target **/
	public static final int EXCALIBUR_SPECIAL_SOUND = 2539;
	public static final int RUNE_CLAWS_SPECIAL_SOUND = 2534;
	public static final int DRAGON_CROSSBOW_SPECIAL_SOUND = 1080;
	public static final int DRAGON_THROWNAXE_SPECIAL_SOUND = 2706;
	public static final int DRAGON_WARHAMMER_SPECIAL_SOUND = 2520;
	public static final int OPAL_BOLTS = 2918;
	public static final int JADE_BOLTS = 2916;
	public static final int PEARL_BOLTS = 2920;
	public static final int TOPAZ_BOLTS = 2914;
	public static final int SAPPHIRE_BOLTS = 2912;
	public static final int EMERALD_BOLTS = 2914;
	public static final int RUBY_BOLTS = 2919;
	public static final int DIAMOND_BOLTS = 2913;
	public static final int DRAGON_BOLTS = 2915;
	public static final int ONYX_BOLTS = 2917;

	/** Miscellaneous : **/
	public static final int BANK_PIN_SUCESSFULLY = 2655;
	public static final int OPEN_BANK_BOOTH = 52;
	public static final int OPEN_BANK_BOOTH_2 = 72;
	public static final int STRONGHOLD_SECURITY_DOOR_OPENING = 2858;
	public static final int CROSSING_BRIDGE = 2451;
	public static final int POTION_DRINKING = 2401;
	public static final int BURY_BONES = 2738;
	public static final int FILLING_POTION_FROM_FOUNTAIN = 2609;
	public static final int DIGGING_SPADE = 1470;
	public static final int FROZEN_CANT_MOVE = 154;
	public static final int CHIN_DROP = 2733;
	public static final int DROP_ITEM = 2739;
	public static final int CAT_MEOW = 333;
	public static final int PICKUP_ITEM = 2582;
	public static final int CLIENT_SETTINGS_BUTTONS = 2266;
	public static final int PIN_DIGIT_INPUT = 1041;
	public static final int NOT_HIGH_ENOUGH_PRAYER = 2673;
	public static final int HEALED_BY_NURSE = 166;
	public static final int SLASH_WEB = 2500;
	public static final int USE_KEY_ON_LOCKED_DOOR = 2402;// 325
	public static final int BROKEN_DOOR_OR_GATE = 834;
	public static final int DOOR_STUCK = 61;
	public static final int PRAY_ALTAR = 2674;
	public static final int SWITCH_SPELLBOOK = 1097; // 83
	public static final int POTION_EFFECT_GONE = 2672;
	public static final int CRAWL_THROUGH_TUNNEL = 2454;

	/** Antifire, stamina, antipoison **/
	public static final int PROSPECT_ORE = 2661;
	public static final int USING_LAMP_REWARD = 2655;
	public static final int DOOR_IS_LOCKED = 2402;
	public static final int CASKET_OPEN = 52;

	/** 2655 after you get your reward (can be done for mystery box) **/
	public static final int DITCH_JUMP = 2467;
	public static final int PRAYER_FINISHES = 2672;
	public static final int BANK_PIN_SUCCESSFULLY_OPENS = 2274;
	public static final int BANK_PIN_WRONG = 2277;
	public static final int DFS_RECHARGING = 3740;
	public static final int MAGIC_SPLASH = 227;
	public static final int STEP_INTO_POOL = 1658;
	public static final int[] RANDOM_SOUND_WHILE_WALKING = new int[]{2043, 2044, 2256};

	/** Skilling **/
	public static final int INVENTORY_FULL_SOUND = 2277;
	public static final int HIT_TREE = 2735;

	/** AREA SOUND: **/
	public static final int TELEPORT_TABLET = 965;
	public static final int ANCIENT_TELEPORT = 197;
	public static final int FAIRY_RING_TELEPORT = 1098;
	public static final int ECTOPHIAL = 1132;
	public static final int LEVER_TELEPORT = 200;
	public static final int NORMAL_TELEPORT = 200;
	public static final int ENTER_RIFT = 2346; // custom
	public static final int[] HOME_TELEPORT = new int[]{193, 196, 194, 195};
	public static final int HOME_TELEPORT_QUICK = 195;
	public static final int[] SMITHING = new int[]{3790, 3791};
	public static final int[] WALKING_BETWEEN_TREES = new int[]{2015, 1997, 3046, 3047, 2018, 1999, 2018, 3047, 1986};
	public static final int[] PEST_CONTROL_AREA_SOUNDS = new int[]{198, 847, 807, 3118};
	/** Or when changing region and not in Wilderness and not in combat **/
	public static final int[] STANDING_IN_GRAND_EXCHANGE = new int[]{3766, 3769};
	public static final int[] STANDING_STILL_RANDOMLY = new int[]{3971, 3915};
	public static final int TRYING_TO_LIGHT_FIRE = 2597;
	public static final int EVIL_BOB_APPEARING = 333;
	public static final int EVIL_BOB_LEAVING = 1930;
	public static final int CUT_LOGS = 2605;
	public static final int WOODCUTTING = 2735;
	public static final int FINISH_TREE = 2734;
	public static final int OPEN_METAL_GATE = 71;
	public static final int OPEN_CLOSE_CURTAIN = 59;
	public static final int VERY_BIG_DOOR_OPENS = 2410;
	public static final int TELEPORT_OBELISK = 204;
	public static final int SOMETHING_IN_GRAND_EXCHANGE = 3924;
	public static final int GENIE_APPEARING = 2301;
	public static final int CLOSE_LARGE_DOOR = 318;
	public static final int OPEN_LARGE_DOOR = 327;
	public static final int CLOSE_SMALL_DOOR = 328;
	public static final int OPEN_SMALL_DOOR = 326;
	public static final int PULL_LEVEL = 2400;
	public static final int RUBBER_CHICKEN_ATK = 2257;
	public static final int OPEN_WOOD_GATE = 67;
	public static final int CLOSE_WOOD_GATE = 66;
	public static final int OPEN_MANHOLE = 54;
	public static final int WHISTLE_SOUND = 1936;
	public static final int OPEN_WARDOBE = 96;
	public static final int CLOSE_WARDOBE = 95;
	public static final int COW_YAK_MOO = 3044;
	public static final int DUCK_QUACK = 413;
	public static final int DUCKLING_QUACK = 411;

	/** WEAPONS ATTACKING SOUND: **/
	public static final Sound WHIP_ATTACK_SOUND = new Sound(2720, 15);
	public static final int GODSWORD_ATTACK_SOUND = 3847;
	public static final Sound GODSWORD_SMASH_SOUND = new Sound(3846, 20);
	public static final int LONGSWORD_ATTACK_SOUND = 2500;
	public static final int LONGSWORD_LUNGE_SOUND = 2501;
	public static final int SCIMITAR_ATTACK_SOUND = 2500;
	public static final int SCIMITAR_LUNGE_SOUND = 2501;
	public static final int DAGGER_ATTACK_SOUND = 2451;// ?
	public static final int DAGGER_SLASH_SOUND = 2548;
	public static final int DRAGON_DAGGER_ATTACK_SOUND = 2549;
	public static final int DRAGON_DAGGER_SLASH_SOUND = 2548;
	public static final int DART_ATTACK_SOUND = 2696;
	public static final int THROWN_AXE_ATTACK_SOUND = 2706;
	public static final int MACE_ATTACK_SOUND = 2508;
	public static final int MACE_SPIKE_SOUND = 2509;
	public static final int CLAWS_ATTACK_SOUND = 2548;
	public static final int CLAWS_LUNGE_SOUND = 2549;
	public static final int STAFF_ATTACK_SOUND = 2562;
	public static final int STAFF_SWIPE_SOUND = 2556;
	public static final int STAFF_POUND_SOUND = 2555;
	public static final int CROSSBOW_ATTACK_SOUND = 2695;
	public static final int DORGRESHUUN_CROSS_ATTACK = 1081;
	public static final int STAFF_WAND_BATTLESTAFF_ATK_SOUND = 2555;
	public static final int DARK_BOW_ATTACK_SOUND = 3731;
	public static final int SHORTBOW_SEERCULL_ATTACK_SOUND = 2693;
	public static final int TWO_HAND_SWORD_ATTACK_SOUND = 2503;
	public static final int TWO_HAND_SMASH_SOUND = 2502;
	public static final int BATTLEAXE_WARHAMMER_ATK_SOUND = 2498;
	public static final int BATTLEAXE_WARHAMMER_SMASH = 2497;
	public static final int AXE_ATTACK_SOUND = 2498;
	public static final int AXE_SMASH_SOUND = 2497;
	public static final int HALBERD_ATTACK_SOUND = 2562;
	public static final int HALBERD_SWIPE_SOUND = 2524;
	public static final int SPEAR_ATTACK_SOUND = 2556;
	public static final int SPEAR_LUNGE_SOUND = 2562;
	public static final int PICKAXE_ATTACK_SOUND = 2498;
	public static final int GRANITE_MAUL_ATTACK_SOUND = 2714;
	public static final int DARKLIGHT_SILVERLIGHT_ATK_SOUND = 2500;
	public static final int DARKLIGHT_SILVER_LIGHT_LUNGE = 2501;
	public static final int BRINE_SABRE_ATK_SOUND = 3551;
	public static final int BRINE_SABRE_LUNGE = 3552;
	public static final int EXCALIBUR_ATTK_SOUND = 2500;
	public static final int EXCALIBUR_LUNGE = 2501;
	public static final int CANE_ATTACK_SOUND = 2508;
	public static final int CANE_SWIPE_SOUND = 2509;
	public static final int DRAGON_CANE_ATK_SOUND = 2555;
	public static final int SARADOMIN_SWORD_ATK_SOUND = 2549;
	public static final int KNIVES_ATTACK_SOUND = 2549;
	public static final int SARADOMIN_SWORD_SMASH = 2548;
	public static final int JAVELIN_ATTACK_SOUND = 2706;
	public static final int BOW_ATTACK_SOUND = 2693;
	public static final int WARHAMMER_ATTACK_SOUND = 2567;
	public static final int LIZARD_SCORCH = 734;
	public static final int LIZARD_FLARE = 735;
	public static final int LIZARD_BLAZE = 734;//953
	public static final int SALAMANDER_SCORCH = 738;
	public static final int SALAMANDER_FLARE = 736;
	public static final int SALAMANDER_BLAZE = 740;//952
	public static final int CHINCHOMPA_ATK_SOUND = 2706;
	public static final int CHINCHOMPA_CONTACT_SOUND = 360;
	public static final int TOXIC_PIPE_ATK_SOUND = 2696;
	public static final int TOXIC_PIPE_SPEC_SOUND = 800;
	public static final int SWORD_ATK_SOUND = 2549;
	public static final int SWORD_SMASH = 2548;
	/** NOTE : FREMMINIKK BLADE IS LIKE SWORD WITH SAME SWORD SOUND **/
	public static final int MEAT_TENDERIZER_ATK_SOUND = 2567;
	/**
	 * EGG WHISK/ KITCHEN KNIFE/ WOODEN SPOON / SPORK / LEAF BLADED SWORD / OBBY
	 * KNIFE have same SOUND and atk style like swords
	 **/
	/** CLEAVER HAVE ATTACK SOUND AND STYLE LIKE SCIMITAR **/
	public static final int BONE_SPEAR_ATK_SOUND = 2554;
	public static final int BONE_SPEAR_POUND = 1309;
	public static final int SILVER_SICKLE_ATK_SOUND = 2526;
	public static final int SILVER_SICKLLE_LUNGE = 2527;
	public static final int NOOSE_WAND_ATK_SOUND = -1;
	public static final int OBBY_RING_ATK_SOUND = 2706;
	/** Attack style like darts **/
	public static final int OBBY_MAUL_ATK_SOUND = 2520;
	public static final int WAND_ATTACK_SOUND = 2563;
	public static final int BUTTERFLY_NET_ATK_SOUND = -1;
	public static final int OBBY_BLADE_ATK_SOUND = 2549;
	public static final int OBBY_BLADE_SLASH = 2548;
	public static final int DHAROK_AXE_ATK_SOUND = 1321;
	public static final int DHAROK_AXE_SMASH = 1316;
	public static final int VERACS_FLAIL_ATK_SOUND = 1323;
	public static final int VERACS_FLAIL_SPIKE_SOUND = 1324;
	/** Attack like mace style **/
	public static final int GUTHANS_WARSPEAR_ATK_SOUND = 1337;
	public static final int GUTHANS_POUND = 1335;
	/** Attack style like spear **/
	public static final int CRYSTAL_BOW_ATK_SOUND = 1352;
	/**
	 * Note : Abyssal tentacle is classified as whip make sure when you add it
	 * later.
	 **/
	public static final int AHRIMS_STAFF_ATK_SOUND = 1329;
	public static final int BALLISTA_ATK_SOUND = 2699;
	public static final int WOLFBANE_ATK_SOUND = 2549;
	public static final int WOLFBANE_SMASH = 2548;
	public static final int DRAGONBONE_NECKLACE_ACTIVE_TIMER = 2686;
	public static final int ACTIVATE_CRUSHER = 975;
	public static final int DEACTIVATE_CRUSHER = 974;
			public static final int RECHARGE_AND_UNCHARGE_ITEMS_SOUND = 1413;
	public static final Sound ATTACK_DEFAULT_SOUND = new Sound(2500);
	public static final Sound ATTACK_LUNGE_DEFAULT_SOUND = new Sound(2549);
	/** Now to add SOUND for blocking I need to restart from potions area **/

	public static final int MINING_SOUND = 3220;
	public static final int ROCK_MINED_SOUND = 3600;
	public static final int CRAFT_RUNES_SOUND = 2710;
	/*
	 * public static final int AGILITY_MONKEY_BARS_SOUND = 2474; public static
	 * final int AGILITY_MONKEY_BARS_SOUND_WALKING = 2466; public static final
	 * int AGILITY_MONKEY_BARS_SOUND_REACH = 2473; public static final int
	 * AGILITY_BLADE_SOUND = 1386; public static final int
	 * AGILITY_HAND_HOLDS_SOUND = 2450 / 2460 when you reach public static final
	 * int AGILITY_BALANCING_ROPE_SOUND = 2495 public static final int
	 * AGILITY_SPINING_BLADES_SOUND = 1377 / 2469 when u reach / 1376 when u
	 * fail + hit sound 509 public static final int AGILITY_PRESSURE_PAD_SOUND =
	 * 1379 / 1387 fail public static final int AGILITY_FALL_DOWN_SOUND = 2041 +
	 * hit sound 509 public static final int AGILITY_BALANCING_LEDGE_SOUND =
	 * 2451 public static final int AGILITY_ROPE_SWING_SOUND = 2494 public
	 * static final int AGILITY_PILLAR_JUMP_SOUND = 2462 public static final int
	 * AGILITY_FLOOR_SPIKES_SOUND = 1381 / when fail1383 + Hit sound 509 public
	 * static final int AGILITY_ARROW_FLY_ SOUND = 1382 when fail / 1378 when u
	 * successful with camera angle change public static final int
	 * AGILITY_CLIMB_WALL_SOUND = 2453;
	 */

	public static final int AGILITY_WALK_ON_PLANK_SOUND = 2480;// Loop during
																// walk
	public static final int BRIMHAVEN_DISPENCER_TAG = 1385;
	public static final int PICKPOCKET_SOUND = 2581;
	public static final int PICKPOCKET_FAILURE_SOUND = 2727;
	public static final int CUT_GEM_SOUND = 2586;
	public static final int CUT_GEM_FAILURE_SOUND = 2589;
	public static final int MAKE_ARROWS_SOUND = 2605;
	public static final int TAN_LEATHER_SOUND = -1; // Non existent on OSRS
	public static final int SPIN_FLAX_SOUND = 2590;

	public static final int SHEAR_SHEEP = 2592;
	public static final int STRING_BOW_SOUND[] = new int[]{3766, 3769, 3768};
	public static final int SMELT_BAR_SOUND = 2725;
	public static final int SMITH_BAR_SOUND[] = new int[]{3790, 3791};
	public static final int FISHING_SOUND = 2603;
	public static final int FISHING_START = 2600;
	public static final int FISHING_DEPLETE_SOUND = 2601;
	public static final int COOK_FOOD_SOUND = 2577;
	public static final int BURN_LOGS_SOUND = 2597;
	public static final int LOGS_LIT = 2596;
	public static final int WARNING = 2394;
	public static final int AGILITY_FALL_SOUND = 2493;
	public static final int LEVELUP_SOUND = 2396;
	public static final int RUB_JEWELRY = 2610;
	public static final int LIT_LOGS_DISAPPEAR_SOUND = 2596;
	public static final int WATER_FARM_SOUND = 2609;
	public static final int CLEAN_HERB_SOUND[] = new int[]{3923, 3920, 3921, 3922};
	public static final int CREATE_POTION_SOUND = 2608;
	public static final int EXTINGUISH_FIRE = 1146;
	public static final int SET_ON_FIRE = 1445;
	public static final int SKILL_REFRESH_SOUND = 1270;
	public static final int FIRE_EXPLODING_SOUND = 3674;
	public static final int DEATH_SOUND = 146;
	public static final int SET_BARRICADE_SOUND = 1447;
	public static final int STEAL_STALL = 2581;
	public static final int OFFER_BONES_SOUND = 1628;

	/** Block Sounds **/

	public static final int DEFAULT_BLOCK = 511;
	public static final int DEFAULT_MALE_UNPROCTED_BLOCK[] = new int[]{516, 517, 518, 519, 520, 521, 522};
	public static final int DEFAULT_FEMALE_UNPROCTED_BLOCK[] = new int[]{506, 507, 508, 509, 510};

	public static final int DEFAULT_MALE_METAL_SHIELD_BLOCK[] = new int[]{2958, 2959, 2960, 2961};
	public static final int DEFAULT_MALE_SHIELD_BLOCK[] = new int[]{2813, 2812, 2814, 2815};;

	public static final int DEFAULT_FEMALE_METAL_SHIELD_BLOCK[] = new int[]{2958, 2959, 2960, 2961};
	public static final int DEFAULT_FEMALE_SHIELD_BLOCK[] = new int[]{2746, 2747, 2748, 2750};

	public static final int DEFAULT_MALE_METAL_PLATE_BLOCK_SOUND[] = new int[]{2807, 2808, 2809, 2810};
	public static final int DEFAULT_MALE_PLATE_BLOCK_SOUND[] = new int[]{2813, 2812, 2814, 2815};

	public static final int DEFAULT_FEMALE_METAL_PLATE_BLOCK_SOUND[] = new int[]{2831, 2832, 2833, 2834, 2835};
	public static final int DEFAULT_FEMALE_PLATE_BLOCK_SOUND[] = new int[]{2813, 2812, 2814, 2815};


	public static Sound getEquipmentSounds(Item item, int slot) {
		ItemDefinition def = item.getDefinition();
		switch (slot) {
		case EquipmentConstants.HEAD_SLOT:
			if (def.getName().toLowerCase().contains("bronze")
					 || def.getName().toLowerCase().contains("iron")
					 || def.getName().toLowerCase().contains("steel")
					 || def.getName().toLowerCase().contains("black")
					 || def.getName().toLowerCase().contains("granite")
					 || def.getName().toLowerCase().contains("obsidian")
					 || def.getName().toLowerCase().contains("mithril")
					 || def.getName().toLowerCase().contains("adamant")
					 || def.getName().toLowerCase().contains("rune")
					 || def.getName().toLowerCase().contains("dragon")
					 || def.getName().toLowerCase().contains("vesta")
					 || def.getName().toLowerCase().contains("statius")
					 || def.getName().toLowerCase().contains("zuriel")
					 || def.getName().toLowerCase().contains("torag")
					 || def.getName().toLowerCase().contains("dharok")
					 || def.getName().toLowerCase().contains("guthan")
					 || def.getName().toLowerCase().contains("verac")
					 || def.getName().toLowerCase().contains(" armour")
					 || def.getName().toLowerCase().contains("santa helm")
					 || def.getName().toLowerCase().contains("coif")
					 || def.getName().toLowerCase().contains("cowl")) {
				return new Sound(2240);
			} else {
			return new Sound(EQUIP_HELM_DEFAULT_SOUND);
			}
		case EquipmentConstants.CAPE_SLOT:
		if (def.getName().toLowerCase().contains("ava's")) {
			return new Sound(3284);
		} else {
			return new Sound(EQUIP_CAPE_DEFAULT_SOUND);
		}
		case EquipmentConstants.AMULET_SLOT:
			return new Sound(EQUIP_AMULETS_DEFAULT_SOUND);
		case EquipmentConstants.WEAPON_SLOT:

			
			if (def == null) {
				return new Sound(EQUIP_WEAPON_DEFAULT_SOUND);
			}
			
			if (def.getName().toLowerCase().contains("pickaxe")) {
				return new Sound(EQUIP_PICKAXE_SOUND);
			} else if (def.getName().toLowerCase().contains("dark bow")) {
				return new Sound(3738);
			} else if (def.getName().toLowerCase().contains("anchor")) {
				return new Sound(2246);
			} else if (def.getName().toLowerCase().contains("karil's crossbow") || def.getName().toLowerCase().contains("seercull")) {
				return new Sound(2244);
			} else if (def.getName().toLowerCase().contains("meat te")) {
				return new Sound(2233);
			} else if (def.getName().toLowerCase().contains("whip") || def.getName().toLowerCase().contains("tentacle")) {
				return new Sound(2249);
			} else if (def.getName().toLowerCase().contains("godsword")) {
				return new Sound(2251);
			} else if (def.getName().toLowerCase().contains("crystal bow")) {
				return new Sound(1355);
			} else if (def.getName().toLowerCase().contains("axe")) {
				return new Sound(EQUIP_AXE_SOUND);
			} else if (def.getName().toLowerCase().contains("battleaxe")) {
				return new Sound(EQUIP_PICKAXE_SOUND);
			} else if (def.getName().toLowerCase().contains("crossbow")) {
				return new Sound(EQUIP_ARROWS_DEFAULT_SOUND);
			} else if (def.getName().toLowerCase().contains("bow")) {
				return new Sound(EQUIP_ARROWS_DEFAULT_SOUND);
			} else if (def.getName().toLowerCase().contains("thrownaxe")) {
				return new Sound(2232);
			} else if (def.getName().toLowerCase().contains("knife") || def.getName().toLowerCase().contains("dart")) {
				return new Sound(2244);
			} else if (def.getName().toLowerCase().contains("bow")) {
				return new Sound(EQUIP_ARROWS_DEFAULT_SOUND);
			} else if (def.getName().toLowerCase().contains("spear")) {
				return new Sound(2247);
			} else if (def.getName().toLowerCase().contains("warhammer")) {
				return new Sound(2233);
			} else if (def.getName().toLowerCase().contains("boxing") || def.getName().toLowerCase().contains("heavy casket")
					|| def.getName().toLowerCase().contains("ale of the gods") || def.getName().toLowerCase().contains("yo-yo")
					|| def.getName().toLowerCase().contains("chinchompa") || def.getName().toLowerCase().contains("stale baguette")) {
				return new Sound(2249);
			} else if (def.getName().toLowerCase().contains("claws")) {
				return new Sound(EQUIP_ARROWS_DEFAULT_SOUND);
			} else if (def.getName().toLowerCase().contains("rubber chicken")) {
				return new Sound(RUBBER_CHICKEN_ATK);
			} else if (def.getName().toLowerCase().contains("chinchompa")) {
				return new Sound(2238);
			} else if (def.getName().toLowerCase().contains("staff") || def.getName().toLowerCase().contains("javelin")) {
				return new Sound(2247);
			} else if (def.getName().toLowerCase().contains("wand")) {
				return new Sound(2238);
			} else if (def.getName().toLowerCase().contains("maul") || def.getName().toLowerCase().contains("ket-om")) {
				return new Sound(2233);
			} else if (def.getName().toLowerCase().contains("halberd")) {
				return new Sound(2247);
			} else if (def.getName().toLowerCase().contains("flag") || def.getName().toLowerCase().contains("banner")) {
				return new Sound(2231);
			} else if (def.getName().toLowerCase().contains("mace") || def.getName().toLowerCase().contains("cane")) {
				return new Sound(2246);
			} else if (def.getName().toLowerCase().contains("bow")) {
				return new Sound(EQUIP_ARROWS_DEFAULT_SOUND);
			} else if (def.getName().toLowerCase().contains("blowpipe")) {
				return new Sound(TOXIC_BLOW_PIPE_EQUIP);
			} else if (def.getName().toLowerCase().contains("knife")){
				return new Sound(EQUIP_SOUND);
			} else {
				return new Sound(EQUIP_WEAPON_DEFAULT_SOUND);
			}
		case EquipmentConstants.BODY_SLOT:
			if (def.getName().toLowerCase().contains("bronze")
					 || def.getName().toLowerCase().contains("iron")
					 || def.getName().toLowerCase().contains("steel")
					 || def.getName().toLowerCase().contains("black")
					 || def.getName().toLowerCase().contains("granite")
					 || def.getName().toLowerCase().contains("obsidian")
					 || def.getName().toLowerCase().contains("mithril")
					 || def.getName().toLowerCase().contains("adamant")
					 || def.getName().toLowerCase().contains("rune")
					 || def.getName().toLowerCase().contains("dragon")
					 || def.getName().toLowerCase().contains("vesta")
					 || def.getName().toLowerCase().contains("statius")
					 || def.getName().toLowerCase().contains("zuriel")
					 || def.getName().toLowerCase().contains("torag")
					 || def.getName().toLowerCase().contains("dharok")
					 || def.getName().toLowerCase().contains("guthan")
					 || def.getName().toLowerCase().contains(" armour")
					 || def.getName().toLowerCase().contains("verac")) {
				return new Sound(2239);
			} else if (def.getName().toLowerCase().contains("d'hide")
					 || def.getName().toLowerCase().contains("leather")) {
				return new Sound(2241);
			} else {
			return new Sound(EQUIP_BODY_DEFAULT_SOUND);
			}
		case EquipmentConstants.SHIELD_SLOT:
			if (def.getName().toLowerCase().contains("wooden")
					 || def.getName().toLowerCase().contains("book")
					 || def.getName().toLowerCase().contains("tome of")
					 || def.getName().toLowerCase().contains("defender")
					 || def.getName().toLowerCase().contains("ward")
					 || def.getName().toLowerCase().contains("fremennik")
					 || def.getName().toLowerCase().contains("decorative")
					 || def.getName().toLowerCase().contains("santa shield")
					 || def.getName().toLowerCase().contains("elemental")) {
				return new Sound(2250);	
			} else {
			return new Sound(EQUIP_SHIELD_DEFAULT_SOUND);
			}
		case EquipmentConstants.LEG_SLOT:
			if (def.getName().toLowerCase().contains("bronze")
					 || def.getName().toLowerCase().contains("iron")
					 || def.getName().toLowerCase().contains("steel")
					 || def.getName().toLowerCase().contains("black")
					 || def.getName().toLowerCase().contains("granite")
					 || def.getName().toLowerCase().contains("obsidian")
					 || def.getName().toLowerCase().contains("mithril")
					 || def.getName().toLowerCase().contains("adamant")
					 || def.getName().toLowerCase().contains("rune")
					 || def.getName().toLowerCase().contains("dragon")
					 || def.getName().toLowerCase().contains("vesta")
					 || def.getName().toLowerCase().contains("statius")
					 || def.getName().toLowerCase().contains("zuriel")
					 || def.getName().toLowerCase().contains("torag")
					 || def.getName().toLowerCase().contains("dharok")
					 || def.getName().toLowerCase().contains("guthan")
					 || def.getName().toLowerCase().contains("decorative")
					 || def.getName().toLowerCase().contains("santa skirt")
					|| def.getName().toLowerCase().contains("santa armour")
					 || def.getName().toLowerCase().contains("verac")) {
				return new Sound(2242);
			} else if (def.getName().toLowerCase().contains("d'hide")
					 || def.getName().toLowerCase().contains("chaps")) {
				return new Sound(2241);
			} else {
			return new Sound(EQUIP_LEGS_DEFAULT_SOUND);
			}
		case EquipmentConstants.HANDS_SLOT:
		if (def.getName().toLowerCase().contains("vamb")) {
			return new Sound(2241);
		} else {
		return new Sound(EQUIP_HELM_DEFAULT_SOUND);
		}
		case EquipmentConstants.FEET_SLOT:
			return new Sound(EQUIP_HELM_DEFAULT_SOUND);
		case EquipmentConstants.RING_SLOT:
			return new Sound(EQUIP_RING_DEFAULT_SOUND);
		case EquipmentConstants.AMMUNITION_SLOT:
			if (def.getName().toLowerCase().contains("bolt")) {
				return new Sound(2238);
			} else {
				return new Sound(EQUIP_ARROWS_DEFAULT_SOUND);			
			}
		}
		
		return null;
	}

    public static int getBlockSound(Player player, int damage) {
        boolean male = player.getAppearance().isMale();
        if (damage > 0) {
            return male ? Misc.randomInt(DEFAULT_MALE_UNPROCTED_BLOCK) : Misc.randomInt(DEFAULT_FEMALE_UNPROCTED_BLOCK);
        }
        int shieldItem = player.getEquipment().getAmountForSlot(EquipmentConstants.SHIELD_SLOT);
        ItemDefinition definition = player.getEquipment().getItems()[EquipmentConstants.SHIELD_SLOT].getDefinition();

        if (shieldItem > 0 && definition != null) {
            if (definition.getName().toLowerCase().contains("black d'hide shield")) {
                return 2817;
			} else if (definition.getName().toLowerCase().contains("wooden shield")) {
				return 2758;
            } else if (definition.getName().toLowerCase().contains("defender")) {
                return 2809;
            } else if (definition.getName().toLowerCase().contains("tome of")) {
                return 2812;
            } else if (definition.getName().toLowerCase().contains("crystal")) {
            return 1349 + Misc.getRandomInclusive(2);
            }
                boolean metal = EquipmentUtil.isWearingMetalBody(player);
                if (definition.getId() == 15284) { // Santa shield
                    metal = true;
                }
                if (male) {
                    return metal ? Misc.randomInt(DEFAULT_MALE_METAL_SHIELD_BLOCK) : Misc.randomInt(DEFAULT_MALE_METAL_SHIELD_BLOCK);
                } else {
                    return metal ? Misc.randomInt(DEFAULT_FEMALE_METAL_SHIELD_BLOCK) : Misc.randomInt(DEFAULT_FEMALE_METAL_SHIELD_BLOCK);
                }
        }
        int plateID = player.getEquipment().getAmountForSlot(EquipmentConstants.BODY_SLOT);
        ItemDefinition definition2 = player.getEquipment().getItems()[EquipmentConstants.BODY_SLOT].getDefinition();
        if (plateID >= 0 && definition2 != null) {
                    boolean isMetal = EquipmentUtil.isWearingMetalBody(player);
                    if (definition2.getName().toLowerCase().contains(" amour")) {
                        isMetal = true;
                    }
                    if (male) {
                        return isMetal ? Misc.randomInt(DEFAULT_MALE_METAL_PLATE_BLOCK_SOUND) : Misc.randomInt(DEFAULT_MALE_PLATE_BLOCK_SOUND);
                    } else {
                        return isMetal ? Misc.randomInt(DEFAULT_FEMALE_METAL_PLATE_BLOCK_SOUND) : Misc.randomInt(DEFAULT_FEMALE_PLATE_BLOCK_SOUND);
                    }
        }
        return DEFAULT_BLOCK;
    }
}
