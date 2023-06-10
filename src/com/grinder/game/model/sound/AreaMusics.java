package com.grinder.game.model.sound;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.godwars.NexChamber;

import java.util.Random;

public enum AreaMusics { // musics 300-310 are good
	// Coordinates X > X Y > Y(Max 4 Values) //Random musics values for this
	// area, any amount of musics
	// edgevilleArea(new int[] { 3068, 3128, 3463, 3518 } , new int[] { 72, 98,
	// 400, 191, 52, 75, 74, 73, 49, 36 }),

	// Varrock (new int[] { 3159, 3271, 3398, 3511 } , new int[] { 2, 125, 121,
	// 175, 116, 301, 57, 93, 105 }),
	

	// edgevilleArea (new int[] { 3068, 3128, 3463, 3518 } , new int[] { 301,
	// 145, 125, 12, 118, 15, 130, 54, 61, 116, 93, 141, 327, 175, 57, 93, 105,
	// 106, 107, 108, 110, 111 }),
	
	// http://runescape.wikia.com/wiki/Music/track_list
	
	// new edge, 674, 157, 400, 450, 596, 134, 304, 245, 340, 306, 313, 191, 52, 75, 74, 72, 36, 501, 502, 503, 504, 770, 49 
	//Dungeneering(new int[] { 3150, 3250, 9250, 9350 }, new int[] { 339 }),
	
	
	/*
	 * How to add a new coordinate of music region
	 * CONSTANT(new int[] { MINIMUM_X, MAX_X, MINIMUM_Y, MAX_Y }, new int[] { MUSIC_ID, MUSIC_ID, MUSIC_ID }),
	 */
		// GREAT MUSICS: 449, 443, 440
 		// newbie meldoy 553
	// 455 dungeon raids
	// musuem 3043, 9932
	// 461 inferno



	// TODO: Wintertodt / Chambers of xeric / Theatre of blood / Glod / Tears of guthix / Nightmare / Nex

	
	/**
	 * Boss Chambers
	 */
	LEGENDARY_BOSS_DUNGEON(new int[] { 2245, 2299, 2562, 2621 }, new int[] { 551, 555 }),
	SARADOMIN_BOSS_CHAMBER(new int[] { 2889, 2919, 5238, 5273 }, new int[] { 360, 442 }),
	KRIL(new int[] { 2882, 2936, 5325, 5350 }, new int[] { 360, 442 }),
	DAGGANOTH_BOSSES_CHAMBER(new int[] { 2903, 2924, 4437, 4458 }, new int[] { 495, 517 }),
	KAMIL_BOSS(new int[] { 2620, 2680, 3970, 4090 }, new int[] { 423, 432, 433, 434 }),
	MUTANT_TARN(new int[] { 2511, 2536, 4634, 4659 }, new int[] { 242, 430, 214 }),
	MUTANT_TARN_NEW(new int[] { 3103, 3223, 4546, 4669 }, new int[] { 242, 430, 214 }),
	ICE_TROLL_KING(new int[] { 2826, 2873, 3785, 3827 }, new int[] { 514, 519 }),
	CORPORAL_BEAST_CHAMBER(new int[] { 2720, 2746, 5060, 5117 }, new int[] { 318 }),
	ICE_QUEEN(new int[] { 2812, 2894, 9895, 9976 }, new int[] { 217, 650 }),
	LIZARDMAN_SHAMAN(new int[] { 1407, 1565, 3674, 3725 }, new int[] { 459 }),
	MERODACH_LAIR(new int[] { 2125, 2164, 4678, 4717 }, new int[] { 25, 375, 26, 27, 28 }),
	KING_BLACK_DRAGON_LAIR(new int[] { 2256, 2287, 4681, 4711 }, new int[] { 25, 375, 26, 27, 28 }),
	SLASH_BASH_LAIR(new int[] { 2847, 2862, 9628, 9645 }, new int[] { 507 }),
	//HYDRA(new int[] { 1250, 1381, 10172, 10281 }, new int[] { 131 }),
	BLACK_KNIGHT_TITAN(new int[] { 2545, 2622, 9487, 9536 }, new int[] { 1, 41 }),
	HYDRAS_LAIR(new int[] { 1246, 1352, 10145, 10252 }, new int[] { 595, 351 }),
	ALCHEMICAL_HYDRA(new int[] { 1356, 1377, 10257, 10278 }, new int[] { 249 }),
	VORKATH_BOSS_LAIR(new int[] { 2256, 2299, 4033, 4084 }, new int[] { 461 }),
	CORPOREAL_BEAST(new int[] { 2959, 3001, 4372, 4397 }, new int[] { 564 }),
	CERBERUS_LAIR(new int[] { 1227, 1262, 1226, 1271 }, new int[] { 594 }),
	JUNGLE_DEMON(new int[] { 2692, 2740, 9160, 9210 }, new int[] { 311, 362 }),
	GIANT_SEA_SNAKE(new int[] { 2451, 2479, 4765, 4794 }, new int[] { 509 }),
	ZULRAH(new int[] { 2257, 2277, 3067, 3079 }, new int[] { 514 }),
	KALPHITE_QUEEN(new int[] { 3464, 3522, 9481, 9517 }, new int[] { 260 }),
	GIANT_MOLE(new int[] { 1716, 1786, 5126, 5251 }, new int[] { 573 }),

	/**
	 * Minigames
	 */
	WEAPON_GAME_ENTRANCE(new int[] { 1676, 1718, 5597, 5608 }, new int[] { 503 }),
	WEAPON_GAME_INSIDE(new int[] { 1600, 1750, 5650, 5733 }, new int[] { 277, 401, 507 }),
	MOTHERLORDE_MINE(new int[] { 3707, 3778, 5627, 5695 }, new int[] { 426 }),
	AQUAIS_NEIGE_OUTSIDE(new int[] { 1280, 1334, 3008, 3052 }, new int[] { 468, 474 }),
	AQUAIS_NEIGE_INSIDE(new int[] { 1286, 1340, 3090, 3120 }, new int[] { 472, 520 }),
	TOKKUL_MINIGAME(new int[] { 3151, 3190, 9740, 9780 }, new int[] { 285 }),
	BLAST_FURNACE(new int[] { 1934, 1960, 4955, 4976 }, new int[] { 434 }),
	BOUNTY_HUNTER(new int[] { 2824, 2870, 5641, 5688 }, new int[] { 443, 444, 445 }),
	PEST_CONTROL_MINIGAME(new int[] { 2626, 2682, 2562, 2615 }, new int[] { 588 }),
	TZHAAR_AREA(new int[] { 2370, 2499, 5125, 5190 }, new int[] { 463, 469 }),
	FIGHT_CAVES(new int[] { 2368, 2428, 5054, 5118 }, new int[] { 473 }),
	FALADOR_PARTY_ROOM(new int[] { 3037, 3055, 3371, 3385 }, new int[] { 503, 494, 112 }),
	MIME_RANDOM_EVENT(new int[] { 2002, 2017, 4749, 4765 }, new int[] { 247 }),
	CASTLE_WARS_MINIGAME(new int[] { 2362, 2456, 3073, 3137 }, new int[] { 314, 317, 318 }),
	BARROWS_MINIGAME_TOP(new int[] { 3548, 3578, 3277, 3307 }, new int[] { 333, 380 }),
	FUN_PK_ZONE(new int[] { 3262, 3327, 4927, 4990 }, new int[] { 401, 507 }),
	CLAN_WARS(new int[] { 3343, 3417, 3137, 3189 }, new int[] { 442 }),
	WARRIORS_GUILD(new int[] { 2836, 2876, 3531, 3556 }, new int[] { 634 }),
	WARRIORS_GUILD_UNDERGROUND(new int[] { 2905, 2940, 9957, 9973 }, new int[] { 634, 635 }),
	NEW_PVP_ARENA(new int[] { 3392, 3455, 4673, 4799 }, new int[] { 449, 429, 474, 475, 579, 488 }),
	WEAPON_MINIGAME_OSRS(new int[] { 1530, 1610, 5050, 5108 }, new int[] { 446, 509, 543, 556, 443, 440 }),
	BATTLE_ROYALE_MINIGAME(new int[] { 1737, 1788, 3393, 3454 }, new int[] { 401, 507, 508 }),
	MOR_UL_REK(new int[] { 2433, 2556, 5067, 5182 }, new int[] { 502 }),
	/**
	 * Wilderness area's
	 */
	EDVILLE_WILDERNESS_AREA(new int[] { 2949, 3200, 3519, 3535 }, new int[] { 169 }),
	VARROCK_WILD(new int[] { 3201, 3382, 3518, 3535 }, new Song[] { Song.ADVENTURE }),
	MIDDLE_WILDERNESS(new int[] { 2949, 3382, 3536, 3675 }, new int[] { 96, 120, 113, 121, 326, 329, 332, 331, 337 }),
	DEEP_WILDERNESS(new int[] { 2949, 3382, 3676, 3975 }, new int[] { 8, 331, 332, 326, 337, 435 }),
	WILDERNESS_ALONE(new int[] { 3311, 3380, 3667, 3690 }, new int[] { 34 }),
	DUEL_FIGHTING_AREA(new int[] { 3330, 3392, 3203, 3261 }, new Song[] { Song.DUEL_ARENA }),
	DUEL_AREA(new int[] { 3341, 3388, 3266, 3280 }, new Song[] { Song.SHINE }),
	MAGE_BANK(new int[] { 2505, 2548, 4685, 4730 }, new int[] { 13, 88, 156 }),
	BOUNTY_HUNTER_AREA(new int[] { 3085, 3223, 3728, 3746 }, new int[] { 724 }),
	REVENANT_CAVES(new int[] { 3085, 3223, 3728, 3746 }, new int[] { 96, 120, 113, 121, 326, 329, 332, 331, 337 }),
	/**
	 * Cities
	 */
	RELLEKA(new int[] { 2600, 2693, 3642, 3695 }, new int[] { 290 }),
	DRAYNOR_CITY(new int[] { 3070, 3143, 3218, 3330 }, new int[] { 151, 11, 320, 319, 333, 361 }),
	TAVERLY_DUNGEON(new int[] { 2841, 2991, 9755, 9848 }, new int[] { 17, 115, 308 }),
	NEITZNOT_CITY(new int[] { 2310, 2380, 3786, 3841 }, new int[] { 12, 130, 225, 228 }),
	NEITZNOT_DRAGONS(new int[] { 2304, 2421, 3842, 3902 }, new int[] { 231, 238 }),
	VARROCK_CITY(new int[] { 3159, 3271, 3360, 3511 }, new int[] { 175 }),
	DICING_MINIGAME(new int[] { 3215, 3249, 5075, 5108 }, new int[] { 555, 556 }),
	DICING_MINIGAME2(new int[] { 2961, 2984, 9731, 9749 }, new int[] { 503, 494, 112, 556, 544  }),
	DICING_MINIGAME3(new int[] { 2816, 2879, 2560, 2623 }, new int[] { 503, 494, 112, 556, 544  }),
	//EDGEVILLE_CITY(new int[] { 3068, 3128, 3463, 3518 }, new int[] { 98, 301, 229, 230, 603, 98, 400, 630, 631, 633, 512, 516 }),
	EDGEVILLE_CITY(new int[] { 3068, 3128, 3463, 3518 }, new int[] { 324, 98, 301, 98, 54, 130, 633, 512, 516 }), // 630, 631, 633
	EDGEVILLE_CITY2(new int[] { 3264, 3327, 9792, 9855 }, new int[] { 98 }),
	BARBARIAN_VILLAGE(new int[] { 3069, 3099, 3396, 3462 }, new Song[] { Song.BARBARIANISM }),
	PEST_CONTROL_CITY(new int[] { 2644, 2676, 2634, 2681 }, new int[] { 587 }),
	MISCELLANIA_CITY(new int[] { 2498, 2504, 3856, 3865 }, new int[] { 39, 284, 506 }),
	GRAND_EXCHANGE_ZONE(new int[] { 3140, 3191, 3469, 3512 }, new int[] { 496 }),
	BURTHOPE(new int[] { 2873, 2939, 3477, 353 }, new int[] { 269, 97, 149, 402 }),
	BRIMHAVEN_CITY(new int[] { 2816, 3030, 3145, 3195 }, new int[] { 6, 55, 92, 115, 164, 402 }),
	AL_KHARID_CITY(new int[] { 3268, 3326, 3140, 3263 }, new int[] { 36, 50 }),
	AL_KHARID_TOP(new int[] { 3266, 3336, 3265, 3323 }, new int[] { 123 }),
	DESERT_MEMBERS_ISLAND(new int[] { 2112, 2175, 2560, 2623 }, new int[] { 123, 553, 548 }),
	BETWEEN_DRAYNOR_AND_LUMBRIDGE(new int[] { 3144, 3201, 3217, 3307 }, new int[] { 49, 127, 327 }),
	BELOW_FALADOR(new int[] { 2919, 3014, 3259, 3280 }, new int[] { 39, 43, 54, 60, 327 }),
	RIMMINGTON_CITY(new int[] { 2924, 2981, 3192, 3226 }, new int[] { 12, 57, 180 }),
	BELOW_VARROCK_ONE(new int[] { 3157, 3303, 3358, 3374 }, new int[] { 60, 127 }),
	LUMBRIDGE_TOP_TO_VARROCK_SOUTH(new int[] { 3202, 3269, 3238, 3357 }, new int[] { 2, 3, 107 }),
	BETWEEN_CAMELOT_AND_CATHERBY(new int[] { 2763, 2791, 3420, 3534 }, new int[] { 104 }),
	CAMELOT_CITY(new int[] { 2690, 2762, 3455, 3534 }, new int[] { 7 }),
	FAlADOR_CITY(new int[] { 2937, 3059, 3328, 3399 }, new int[] { 72 }),
	SOUTH_FALADOR(new int[] { 2919, 3060, 3260, 3327 }, new int[] { 745, 310 }),
	CATHERBY_CITY(new int[] { 2791, 2840, 3415, 3501 }, new int[] { 74 }),
	MISCELLENIA_AND_SEA_TROLL_QUEEN(new int[] { 2492, 2634, 3837, 3915 }, new int[] { 506, 284 }),
	LUMBRIDGE_CITY(new int[] { 3198, 3265, 3198, 3253 }, new int[] { 76 }),
	FALADOR_TO_MONASTERY(new int[] { 2942, 3061, 3396, 3469 }, new int[] { 93 }),
	SHILO_VILLAGE_CITY(new int[] { 2817, 2876, 2950, 3004 }, new int[] { 90, 124 }),
	BELOW_ARDOUGNE(new int[] { 2573, 2674, 3199, 3252 }, new int[] { 622, 620, 615, 617, 619, 638, 645 }),
	CATHERBY_FISHING_AREA(new int[] { 2841, 2862, 3427, 3440 }, new int[] { 119 }),
	YANILLE_EAST(new int[] { 2577, 2618, 3075, 3105 }, new int[] { 18, 118}),
	YANILLE_WEST(new int[] { 2539, 2576, 3079, 3108 }, new int[] { 437 }),
	CANIFIS_CITY(new int[] { 3432, 3528, 3461, 3522 }, new int[] { 61, 244 }),
	ARDOUGNE_EAST(new int[] { 2561, 2709, 3200, 3346 }, new int[] { 99, 118, 130, 146, 191 }),
	ARDOUGNE_WEST(new int[] { 2434, 2556, 3281, 3334 }, new int[] { 150, 153 }),
	SOUTH_AL_KHARID(new int[] { 3208, 3338, 3101, 3122 }, new int[] { 124, 123 }),
	ANCIENT_DUNGEON(new int[] { 1725, 1797, 5278, 5372 }, new int[] { 367, 249 }),
	MOON_ISLE(new int[] { 2051, 2173, 3846, 3960 }, new int[] { 301, 625, 627 }),
	ZULRAH_OUTSIDE(new int[] { 2162, 2221, 3048, 3084 }, new int[] { 539 }),
	POLLIVINEACH(new int[] { 3323, 3455, 2945, 3012 }, new int[] { 266, 351, 123, 124 }),
	ARDOUGNE_MARKET_PLACE(new int[] { 2648, 2675, 3292, 3320 }, new int[] { 450, 245, 596, 306, 433, 436, 437, 438, 441, 448, 500, 145, 130, 118, 116, 317, 330, 113 }),
	FISHING_GUILD(new int[] { 2578, 2643, 3386, 3456 }, new int[] { 193 }),
	/**
	 * Caves, Dungeons, Pits
	 */
	TRAINING_GROUNDS(new int[] { 3100, 3400, 5290, 5600 }, new int[] { 216, 300, 362, 431 }), // COORDINATES TO BE CHECKED IF IT IS THE DRAGONS DUNGEON // NEEDS GOOD MUSIC
	NEW_SLAYER_CAVE(new int[] { 2650, 2820, 9970, 10050 }, new int[] { 340, 341 }),
	GOD_WARS_ALL_DUNGEON(new int[] { 2820, 2940, 5250, 5370 }, new int[] { 391, 399, 404, 408 }),
	KARAMJA_LESSER_DUNGEON(new int[] { 2826, 2845, 9549, 9662 }, new int[] { 331 }),
	KARAMJA_LESSER_DUNGEON_PART2(new int[] { 2846, 2865, 9549, 9590 }, new int[] { 331, 362 }),
	BELOW_FILDIP_HILLS(new int[] { 1857, 2003, 8947, 9101 }, new int[] { 363 }),
	DAGGANOTH_DUNGEON(new int[] { 2444, 2486, 10118, 10167 }, new int[] { 152, 403 }),
	BRIMHAVEN_DRAGON_AREA(new int[] { 2694, 2745, 9417, 9525 }, new int[] { 53, 117, 362 }),
	BRIMHAVEN_MOSSGIANT_TO_ENTRANCE(new int[] { 2625, 2732, 9408, 9597 }, new int[] { 115, 407 }),
	ROCK_CRABS(new int[] { 2652, 2726, 3696, 3735 }, new int[] { 289, 296 }),
	SAND_CRABS(new int[] { 1666, 1883, 3460, 3518 }, new int[] { 452 /*352*/ }),
	KBD_POISON_SPIDERS(new int[] { 3065, 3069, 10254, 10260 }, new int[] { 28 }),
	UNDERGROUND_TUNNELS_FIRE_GIANTS(new int[] { 3215, 3242, 5490, 5504 }, new int[] { 114, 302, 322, 329 }),
	BARROWS_PIT(new int[] { 3520, 3580, 9660, 9740 }, new int[] { 381 }),
	VARROCK_DUNGEON(new int[] { 3097, 3175, 9828, 9909 }, new int[] { 144 }),
	STRONGHOLD_SECURITY(new int[] { 1800, 2400, 5175, 5300 }, new int[] { 558, 560, 537 }),
	ASGARNIAN_DUNGEON(new int[] { 2977, 3082, 9537, 9597 }, new int[] { 108, 529 }),
	ELF_WARRIORS_ISLAND(new int[] { 2887, 2938, 2707, 2735 }, new int[] { 255 }),
	NORTH_APE_ATOLL(new int[] { 2698, 2811, 2772, 2807 }, new int[] { 303, 306 }),
	KALPHITE_BASIC(new int[] { 3453, 3463, 9471, 9531 }, new int[] { 124 }),
	KHARID_CROCODILES(new int[] { 3272, 3356, 2904, 2944 }, new int[] { 266 }),


	DEMONIC_GORILLAS(new int[] { 2066, 2168, 5634, 5685 }, new int[] { 360, 442 }), // Monkey Business
	DEMONIC_GORILLAS_OUTSIDE(new int[] { 1970, 2047, 5566, 5631 }, new int[] { 22 }),
	//TODO: CATACOMBS_OF_KOUREND
	//TODO: KOUREND_CITY
	//TODO: LIZARDMAN_SHAMAN_PITS
	//TODO: OBOR's LAIR
	//TODO: ZALCANO
	//TODO: REDWOOD CUTTING
	//TODO: THE GUANTLET
	//TODO: GORTESQUE GUARDIANS
	//TODO: CHASM OF FIRE
	//TODO: OUTSIDE RAIDS ZONE
	//TODO: OUTSIDE WINTERTODT
	//TODO: SKOTIZO
	//TODO: TEARS OF GUTHIX
	//TODO: FARMING GUILD
	//TODO: ABYSSAL SIRE
	//TODO: OURANIA ALTAR
	//TODO: AIR, WATER, FIRE, MIND, BODY, DEATH, COSMIC, NATURE, LAW, BLOOD ALTAR
	//TODO: WOODCUTTING GUILD


	 // music id 420
	/**
	 * Miscellaneous 
	 */
	STAFF_AREA(new int[] { 2028, 2045, 4480, 4504 }, new int[] { 615, 616, 617, 618, 118, 619, 620, 621, 622, 623 }),
	JAIL2(new int[] { 2791, 2805, 3319, 3328 }, new int[] { 95, 512 }),
	NEW_JAIL(new int[] { 3203, 3257, 9761, 9815 }, new int[] { 549, 572, 578 }),
	PRIVATE_AREA(new int[] { 2976, 2988, 9907, 9916 }, new int[] { 95, 113, 118 }),
	MEMBERS_ISLAND(new int[] { 3624, 3901, 2911, 3070 }, new int[] { 114, 609, 613, 615, 522, 616 }),
	PLAT_MEMBERS_ISLAND(new int[] { 1977, 2098, 3664, 3761 }, new int[] { 114, 609, 613, 615, 522, 616 }),
	CAVE_HORROR(new int[] { 3711, 3846, 9347, 9474 }, new int[] { 273, 357 }),
	DRAYNOR_JAIL(new int[] { 3121, 3130, 3240, 3246 }, new int[] { 87, 512, 333, 319, 338 }),
	BRIMHAVEN_AGILITY_COURSE(new int[] { 2755, 2815, 9526, 9601 }, new int[] { 248/*, 158, 243*/ }),
	UNDERWATER(new int[] { 2948, 3002, 9475, 9530 }, new int[] { 489 }),
	MONASTERY(new int[] { 3042, 3063, 3470, 3512 }, new int[] { 310 }),
	BATTLE_FIELD_ARDOUGNE(new int[] { 2480, 2543, 3190, 3257 }, new int[] { 693 }),
	KILLERWATT(new int[] { 2636, 2683, 5193, 5234 }, new int[] { 568 }),
	FIGHT_ARENA(new int[] { 2583, 2603, 3154, 3170 }, new int[] { 292 }),
	HUNTER_AREA(new int[] { 2692, 2742, 3752, 3799 }, new int[] { 460, 633 }),
	BARBARIAN_AGILITY_COURSE(new int[] { 2528, 2556, 3540, 3574 }, new int[] { 103 }),
	STRONGHOLD_SLAYER(new int[] { 2386, 2501, 9765, 9838 }, new int[] { 501, 213 }),
	BANDIT_CAMP(new int[] { 3139, 3190, 2955, 3003 }, new int[] { 36, 123 }),
	SLAYER_TOWER(new int[] { 3405, 3453, 3530, 3579 }, new int[] { 133, 339, 480 }),
	SLAYER_TOWER_UNDERGROUND(new int[] { 3399, 3450, 9923, 9980 }, new int[] { 133, 339, 480 }),
	TUTORIAL_ISLAND(new int[] { 3059, 3141, 3064, 3131 }, new int[] { 62 }),
	LUMBRIDGE_YARD(new int[] { 3290, 3384, 3483, 3520 }, new Song[] { Song.YESTERYEAR }),
	EXPERIMENT_CAVE(new int[] { 3456, 3591, 9920, 9970 }, new int[] { 342 }),
	BURTHOPE_GAMES_ROOM(new int[] { 2194, 2221, 4947, 4972 }, new int[] { 269 }),
	CHAOS_CRAFTING_ALTAR(new int[] { 2267, 2275, 4838, 4846 }, new int[] { 142 }),
	DWARVEN_MINES(new int[] { 2950, 3090, 9680, 9885 }, new int[] { 325 }),
	ESSENCE_MINE(new int[] { 2880, 2942, 4800, 4863 }, new int[] { 57 }),
	CONSTRUCTION_ZONE(new int[] { 1880, 1951, 5720, 5791 }, new int[] { 454 }),
	MORYTANIA(new int[] { 3408, 3518, 3270, 3457 }, new int[] { 48, 333 }),
	EAST_MORYTANIA(new int[] { 3425, 3464, 3272, 3309 }, new int[] {  331 }),
	EARTH_ALTAR(new int[] { 2640, 2668, 4818, 4844 }, new int[] { 143 }),
	SMOKE_TUNNELS(new int[] { 3199, 3277, 9346, 9404 }, new int[] { 272, 505 }),
	DUST_TUNNELS(new int[] { 3278, 3326, 9346, 9407 }, new int[] { 376, 379 }),
	TROLLHEIM(new int[] { 2816, 2923, 3563, 370 }, new int[] { 220 }),
	SMOKE_DEVIL_DUNGEON(new int[] { 2325, 2435, 9410, 9470 }, new int[] { 424 }),
	LUMBRIDGE_SWAMP(new int[] { 3158, 3250, 3165, 3188 }, new Song[] { Song.BOOK_OF_SPELLS }),
	VARROCK_DUNGEON_PART_2(new int[] { 3150, 3278, 9858, 9890 }, new int[] { 325 }),
	ANCIENT_PYRAMID(new int[] { 3200, 3259, 2810, 2880 }, new int[] { 329 }),
	COOKING_GUILD(new int[] { 3115, 3158, 3401, 3463 }, new int[] { 310 }),
	GNOME_AGILITY_COURSE(new int[] { 2467, 2492, 3414, 3440 }, new int[] { 33/* 101*/ }),
	GNOME_AREA(new int[] { 2408, 2462, 3391, 3460 }, new int[] { 22 }),
	GNOME_NORTH_AREA(new int[] { 2432, 2486, 3461, 3513 }, new int[] { 522 }),
	NEX_LOBBY(new int[] {2899, 2908, 5197, 5209}, new int[] {NexChamber.LOBBY_SOUND_TRACK}),
	NEX_CHAMBER(new int[] {2909, 2941, 5188, 5218}, new int[] {NexChamber.FIGHT_SOUND_TRACK}),
	MIMIC_AREA(new int[]{2705, 2734, 4308, 4332}, new int[]{637}),

	PURO_PURO(new int[]{2560, 2622, 4291, 4350}, new int[]{349}),
	ALL_OTHER_AREAS(new int[]{0, 9999, 0, 11000}, new int[]{ // KEEP THIS AT THE BOTTOM OF THE AREA MUSICS
			1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
			30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56,
			57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83,
			84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108,
			109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130,
			131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152,
			153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174,
			175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196,
			197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218,
			219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240,
			241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255, 256, 257, 258, 259, 260, 261, 262,
			263, 264, 265, 266, 267, 268, 269, 270, 271, 272, 273, 274, 275, 276, 277, 278, 279, 280, 281, 282, 283, 284,
			285, 286, 287, 288, 289, 290, 291, 292, 293, 294, 295, 296, 297, 298, 299, 300, 301, 302, 303, 304, 305, 306,
			307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317, 318, 319, 320, 321, 322, 323, 324, 325, 326, 327, 328,
			329, 330, 331, 332, 333, 334, 335, 336, 337, 338, 339, 340, 341, 342, 343, 344, 345, 346, 347, 348, 349, 350
	}),
	;
	
	private final int[] areaCoordinates;
	private final int[] possibleSongs;

	public boolean isInArea(final Player player) {
		if (player.getPosition().getX() >= getAreaCoordinates()[0]
				&& player.getPosition().getX() < getAreaCoordinates()[1]
				&& player.getPosition().getY() >= getAreaCoordinates()[2]
				&& player.getPosition().getY() < getAreaCoordinates()[3])
			return true;

		return false;
	}

	public int getFilteredRandomMusic(final Player player) {
		if (getPossibleSongs().length == 1)
			return getPossibleSongs()[0];
		int index = getFilteredRandom(0, getPossibleSongs().length - 1, player.getMusic().getLastPlayed());
		return getPossibleSongs()[index];
	}

	/**
	 * Get's a random integer between 0 and specified max, and won't return the specified undesired music id
	 */
	private int getFilteredRandom(int min, int max, int undesired) {
		Random random = new Random();
		int index = random.nextInt(max - min + 1) + min;
		if (getPossibleSongs()[index] == undesired && max - min > 0)
			index = getFilteredRandom(min, max, undesired);
		return index;
	}
	
	private AreaMusics(final int[] areaCoordinates, final Song[] possibleSongs) {
		this.areaCoordinates = areaCoordinates;
		this.possibleSongs = new int[possibleSongs.length];
		for (int i = 0; i < possibleSongs.length; i++) {
			this.possibleSongs[i] = possibleSongs[i].getId();
		}
	}

	private AreaMusics(final int[] areaCoordinates, final int[] possibleSongs) {
		this.areaCoordinates = areaCoordinates;
		this.possibleSongs = possibleSongs;
	}

	public int[] getPossibleSongs() {
		return possibleSongs;
	}

	public int[] getAreaCoordinates() {
		return areaCoordinates;
	}

	@Override
	public String toString() {
		return name().substring(0, 1).toUpperCase()
				+ name().substring(1).toLowerCase();
	}
}