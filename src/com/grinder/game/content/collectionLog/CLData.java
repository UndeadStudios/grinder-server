package com.grinder.game.content.collectionLog;

import com.google.common.collect.ImmutableList;
import com.grinder.game.content.cluescroll.scroll.reward.ScrollReward;
import com.grinder.game.content.skill.skillable.impl.crafting.craftable.impl.LeatherShield;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

import java.util.Arrays;

import static com.grinder.util.ItemID.*;

public enum CLData {


    /*
    BOSSES
    */
//    ABYSSAL_SIRE(CLTabType.BOSSES, "Abyssal Sire",  13262, 13273, 13507, 13274, 13275, 13276, 13277, 13265, 4151),
    ALCHEMICAL_HYDRA(CLTabType.BOSSES, "Alchemical Hydra",  22973, 22969, 22971, 22988, 22966, 21730, 22981, 22486, 22746),
    BARRELCHEST(CLTabType.BOSSES, "Barrelchest",  10887),
    BLACK_KNIGHT_TITAN(CLTabType.BOSSES, "Black Knight Titan",  3486, 3481, 3483, 3485, 3488, 12389, 12391, 20146, 20149, 20152, 20155, 19547, 21295, 15911),
    BRYOPHYTA(CLTabType.BOSSES, "Bryophyta",  22372),
    CALLISTO(CLTabType.BOSSES, "Callisto",  991, 11920, 7158, 12603, 9470, 13178),
    CERBERUS(CLTabType.BOSSES, "Cerberus",  13231, 13229, 13227, 13233, 13274, 13275, 13276, 19544, 21015, 13265, 13245, 13247),
    CHAOS_ELEMENTAL(CLTabType.BOSSES, "Chaos Elemental",  12932, 12922, 7158, 11920, 3140, 20080, 20083, 20086, 20089, 20092, 19973,
            19979, 19976, 19982, 19707, 11995),
    CHAOS_FANATIC(CLTabType.BOSSES, "Chaos Fanatic",  12932, 12853, 21043, 11928, 11929, 11930, 11931, 11932, 11933, 11995),
    COMMANDER_ZILYANA(CLTabType.BOSSES, "Commander Zilyana",  11838, 13256, 11818, 11820, 11822, 11785, 11814, 12651),
    CORPOREAL_BEAST(CLTabType.BOSSES, "Corporeal Beast",  15152, 10586, 12829, 12833, 12823, 12827, 12819, 12816, 15802, 22318),
    CRAZY_ARCHAEOLOGIST(CLTabType.BOSSES, "Crazy Archaeologist",  11931, 11932, 11933, 22481, 11990, 21043, 20716, 12932),
    CHRONOZON(CLTabType.BOSSES, "Chronozon",  15298),
    DAGANNOTH_PRIME(CLTabType.BOSSES, "Dagannoth Prime",  6131, 6139, 6562, 6739, 6731, 12644),
    DAGANNOTH_REX(CLTabType.BOSSES, "Dagannoth Rex",  6739, 6737, 6735, 12645),
    DAGANNOTH_SUPREME(CLTabType.BOSSES, "Dagannoth Supreme",  6133, 6135, 6724, 6739, 6733, 12643),
    GALVEK(CLTabType.BOSSES, "Galvek",  11341, 20838, 20840, 20842, 20844, 20846, 6640, 11286, 22326, 22327, 22328),
    GENERAL_GRAARDOR(CLTabType.BOSSES, "General Graardor",  11832, 11834, 11836, 11812, 11818, 11820, 11822, 15751, 12650),
    GIANT_MOLE(CLTabType.BOSSES, "Giant Mole",  7416, 7418, 12646),
    GIANT_SEA_SNAKE(CLTabType.BOSSES, "Giant Sea Snake",  12855, 11785),
    GLOD(CLTabType.BOSSES, "Glod",  15804, 15912),
    ICE_QUEEN(CLTabType.BOSSES, "Ice Queen",  12419, 12420, 12421, 6922, 6920, 12932, 12422, 15153),
    ICE_TROLL_KING(CLTabType.BOSSES, "Ice Troll King",  20035, 20038, 20041, 20044, 20047, 6912, 19544, 20724, 12002),
    JUNGLE_DEMON(CLTabType.BOSSES, "Jungle Demon",  21043, 6914, 15163),
    KALPHITE_QUEEN(CLTabType.BOSSES, "Kalphite Queen",  11335, 3140, 21892, 4087, 13271, 13576, 7158, 12596, 7981, 12885, 15156, 12654),
    KAMIL(CLTabType.BOSSES, "Kamil",  11709, 12000, 20736, 21200, 6563, 12199, 20730, 13227, 20128, 20131, 20137, 20134, 20140, 2579, 6731, 12932, 15158, 21018, 21024, 21021),
    KING_BLACK_DRAGON(CLTabType.BOSSES, "King Black Dragon",  7462, 21012, 21892, 21895, 11286, 6640, 12653),
    KREE_ARRA(CLTabType.BOSSES, "Kree'arra",  11826, 11828, 11830, 10976, 21000, 11785, 11810, 11818, 11820, 11822, 12649),
    KRIL_TSATRUTH(CLTabType.BOSSES, "K'ril Tsutsaroth",  11824, 11818, 11820, 11822, 11791, 11816, 11889, 19553, 13275, 13274, 13276, 15824, 12652),
    MERODACH(CLTabType.BOSSES, "Merodach",  12752, 11286, 24419, 24420, 24421, 24417),
    MUTANT_TARN(CLTabType.BOSSES, "Mutant Tarn",  21003, 13652, 22557, 13231, 20997, 12603, 15915),
    NEX(CLTabType.BOSSES, "Nex",  26231, 26235, 26372, 26370, 26376, 26378, 26380, 15892, 15893, 15894, 15895, 15896, 15897, 26348),
    SCORPIA(CLTabType.BOSSES, "Scorpia", 11928, 11929, 11930, 11931, 11932, 11933, 2581, 12596, 2577, 19994, 13181),
    SEA_TROLL_QUEEN(CLTabType.BOSSES, "Sea Troll Queen",  6914, 6918, 6916, 6924, 6922, 6920, 11908, 12932, 6889, 12004, 12655),
    SLASH_BASH(CLTabType.BOSSES, "Slash Bash",  21000, 21015, 12426, 13275, 13274, 13276, 19550, 13271, 15914),
//    THE_NIGHTMARE(CLTabType.BOSSES, "The Nightmare",  24511, 24514, 24517, 24422, 24419, 24420, 24421, 24417, 24491),
    THE_UNTOUCHABLE(CLTabType.BOSSES, "The Untouchable",  7400, 7399, 7398, 22552, 19550, 12932, 15164, 15917),
    THERMONUCLEAR_SMOKE_DEVIL(CLTabType.BOSSES, "Thermonuclear Smoke Devil",  4675, 12002, 11998, 3140, 12648),
    VENENATIS(CLTabType.BOSSES, "Venenatis",  11920, 10976, 12605, 19553, 20211, 12922, 13177),
    VETION(CLTabType.BOSSES, "Vet'ion",  22324, 19707, 11920, 7158, 20595, 20517, 20520, 11928, 11929, 11930, 13179),
    VORKATH(CLTabType.BOSSES, "Vorkath",  22106, 22111, 11286, 22006, 19707, 21992),
    OBOR(CLTabType.BOSSES, "Obor",  20756),
    PORAZDIR(CLTabType.BOSSES, "Porazdir",  15804),
    ZULRAH(CLTabType.BOSSES, "Zulrah",  12922, 12004, 12932, 12927, 6571, 12936, 13200, 13201,12921),


    /*
    RAIDS
    */
    CHAMBERS_OF_XERIC(CLTabType.RAIDS, "Chambers of Xeric - Coming Soon", -1),
    THEATER_OF_BLOOD(CLTabType.RAIDS, "Theater of Blood - Coming Soon", -1),


    /*
    TREASURE TRAILS
    */
    EASY_TREASURE_TRAIL(CLTabType.CLUES, "Easy Treasure Trails", 2577, 6920, 2579, 11919, 12956, 12957,
            12958, 12959, 10366, 2633, 2635, 2637, 9944, 9640, 9642, 9644, 6065, 6066, 6068, 6069, 12335, 12337, 2631, 23381, 23384, 23413, 15352, 15353,
            15354, 15355, 15356, 15357, 15154, 23366, 23369, 23372, 23375, 23378, 10877, 10878, 10879, 10880, 10881, 10882, 24537,
            6724, 20199, 20202, 6912, 12639),

    MEDIUM_TREASURE_TRAIL(CLTabType.CLUES, "Medium Treasure Trails", 23392,
            23395, 23398, 23401, 23404, 10364, 2599, 2601, 2603, 2605, 2607, 2611, 2613, 3474, 3475, 7321, 7323, 7325, 7327, 23309, 23354, 12361, 12428, 7376, 7382,
            13125, 13126, 13127, 13128, 13120, 13139, 13140, 13134, 13135, 13136, 13143, 13144, 25592, 25594, 25596, 25598, 10454, 10462, 10466, 10446, 10452, 10458, 10442, 10470, 10464, 10450, 10456,
            10460, 10468, 7378, 7380, 2579, 13097, 13103, 12327, 12329, 12331, 12333, 2577, 13115, 13113, 13111, 13107, 13109, 8464, 8468, 8472, 8476, 8718, 8726, 8732, 8740, 12253, 12255,
            12259, 12263, 12470, 12472, 12474, 12476, 12478, 12512, 12508, 12510, 19930, 12265, 12267, 12269, 12271, 12275, 12506, 12480, 12482, 12484,
            12486, 12488, 12498, 12500, 12502, 12504, 6764, 11824, 15405, 15399, 15400, 15401, 15402, 15403, 15404, 15426, 15428, 15418, 15420, 15422, 15424, 19924, 7804,
            6762, 19933, 11889, 19936, 19927, 10510, 13130, 13131, 13132, 13129, 19958, 19961, 19964, 19967, 19991, 10426, 10424, 10434, 10432, 19943,
            23246, 19952, 15154, 22284, 14161, 21298, 21301, 21304, 20166, 20205, 20208, 6918, 6916, 6924, 6731, 6735, 6737, 12601),

    HARD_TREASURE_TRAIL(CLTabType.CLUES, "Hard Treasure Trails", 20113, 20116, 20125, 10280, 10282, 10284, 10428, 10430, 8470, 8482, 8478, 8490, 8484, 6714, 20720, 20712, 12381, 12383, 12385, 12387, 23209, 23212, 23215, 23218,
            23221, 2643, 10472, 10474, 10440, 10444, 19946, 19949, 19955, 22400, 22516, 20020, 20023, 20026, 20029, 20008, 20110, 20053, 20493, 20436, 20442, 20433, 11137, 732, 25129,
            25131, 25133, 25135, 25137, 23407, 23410, 24525, 10388, 10390, 10368, 10370, 10372, 10374, 10380, 10382, 10378, 10376, 26531, 26533, 26535, 26537, 26539, 24539, 25916, 15841, 23917,
            23200, 23203, 23188, 3488, 3485, 3483, 3481, 3486, 12596, 19994, 20017, 15414, 15416, 15406, 15408, 15410, 15412, 12638, 10346, 10348, 10350, 10352, 15859, 15861, 15863, 10342, 10340, 10338, 10344, 10330, 10332, 10334, 10336),

    ELITE_TREASURE_TRAIL(CLTabType.CLUES, "Elite Treasure Trails", 15180, 15178,
            15179, 15159, 15181, 23047, 23050, 23053, 23056, 23059, 6887, 8466, 15297, 8474, 8480, 8486, 8488, 8494, 8720, 8728, 8734, 8742, 8746,22713, 22715,
            22717, 20214, 20217, 24527, 19997, 21695, 12727, 20590, 20249, 20056, 19941, 20773, 20775, 20777, 20781, 20164, 20836, 12637,
            22109, 12457, 15308, 15306, 15307, 15309, 15345, 15349, 15350, 15351, 25918, 12459, 20059, 19918, 22719),

    MASTER_TREASURE_TRAIL(CLTabType.CLUES, "Global Rewards", 3827, 3828,
            3829, 3830, 3835, 3836, 3837, 3838, 3831, 3832, 3833, 3834, 12613, 12614, 12615, 12616, 12617, 12618, 12619, 12620, 12621, 12622, 12623, 12624,
            20235, 20229, 20232, 20226, 20223, 20220, 12938, 21802, 12402, 12411, 12406, 12404, 12405, 12403, 12408, 12407, 12409, 12642, 12410, 12402, 7329, 7330, 7331, 10326, 4561, 24982, 24984, 24986),


    /*
    MINIGAMES
    */
    AQUAIS_NEIGE(CLTabType.MINIGAMES, "Aquais Neige", 15916, 15749, 15301, 15302, 15303, 15304, 15305, 15377, 15690, 15692, 15694, 8971, 8967, 8962, 8955, 8994,
            8959, 8952, 8991, 8960, 8953, 8992, 8965, 8958, 8997, 8964, 8957, 8996, 8961, 8954, 8993, 26488, 26490, 26492, 26494, 26496, 26498, 20779, 6910, 26870, 26872, 26874, 26549, 26225, 26221, 26223, 26227, 26229),
    BARROWS_CHESTS(CLTabType.MINIGAMES, "Barrows",  15370, 4732, 4736, 4738, 4734, 4708, 4712, 4714, 4710, 4716,
            4720, 4722, 4718, 4724, 4728, 4730, 4726, 4745, 4749, 4751, 4747, 4753, 4757, 4759, 4755),
    CASTLE_WARS(CLTabType.MINIGAMES, "Castle Wars", 24192, 24201, 12637, 12638, 12639,
            24195, 24198, 24204, 25165, 25163, 4071, 4069, 4070, 11893, 4072, 4068, 25169, 25167, 4506, 4504, 4505, 11894, 4507, 4503, 25174, 25171, 4511, 4509, 4510, 11895, 4512, 4508, 4513, 4514, 11891, 4515, 4516, 11892, 11898, 11896, 11897, 11899, 11900, 11901),
    FIGHT_CAVE(CLTabType.MINIGAMES, "Fight Cave", 6570),
    MEDALLION_CASINO(CLTabType.MINIGAMES, "Medallion Casino", 10944),
    MINIGAME_STORE(CLTabType.MINIGAMES, "Minigame Store", 24664, 24666, 24668,
            25001, 25004, 25007, 25010, 25013, 25110, 25112, 25114, 25056, 25322, 25324, 25326, 25330, 25328, 25332, 25334, 25042, 25044, 25046, 25048, 25050, 25054,
            25052, 24862, 26260, 24034, 24037, 24040, 24043, 24046, 23522, 13283, 24315, 24317, 24319, 24321, 23785, 23787, 23789, 23911, 23913, 23915, 23917,
            23919, 23921, 23923, 23925, 23995, 24009, 24012, 24021, 24003, 24006, 24015, 24018, 24027, 24413, 24428, 24430, 24431),
    INFERNO(CLTabType.MINIGAMES, "Inferno ", INFERNAL_CAPE),
    MOTHERLODE_MINE(CLTabType.MINIGAMES, "Motherlode Mine", 12013, 12014, 12015, 12016, 12019, 12020),
    PEST_CONTROL(CLTabType.MINIGAMES, "Pest Control",
            11665, 11664, 11663, 8839, 8840, 8842, 8841, // Regular Void
            13072, 13073, // Elite Void
            26477, 26475, 26473, 26469, 26471, 26467, //Superor Void
            15294, 15293, 15292, 15290, 15291, 15295, 15296, // Blue void
            15451, 15450, 15449, 15447, 15448, 15452, 15453, //Green Void
            15458, 15457, 15456, 15454, 15455, 15459, 15460, // Purple Void
            15444, 15443, 15442, 15440, 15441, 15445, 15446), // Red Void
    WARRIORS_GUILD(CLTabType.MINIGAMES, "Warriors Guild", 8844, 8845, 8846, 8847, 8848, 8849, 8850, 12954, 22477, 10551, 15394, 15396),
//    WINTERTODT(CLTabType.MINIGAMES, "Wintertodt", 4151),



    /*
    OTHER
    */
    MUDDY_CHEST(CLTabType.OTHER, "Muddy Chest", 1961, 1907, 12375, 12377,
            12757, 12759, 12769, 12771, 10398, 1187, 11335, 6585, 10150, 6912, 6914, 5556, 5557, 2577, 2579, 21301, 21304, 12249, 20199, 20202, 6724, 10316, 10318, 10320, 10322, 10324, 10392, 10394, 10296, 10298, 10300, 10302, 10304, 7334, 7340, 7346, 7352, 7358,
            10286, 10288, 10290, 10292, 10294, 7336, 7342, 7348, 7354, 7360, 6568, 20050, 21298, 11812, 20166, 10075, 12596, 14160, 11838, 11840, 6731, 6733, 6735, 6737, 14161, 20050, 22284, 11128, 6524, 6528, 6568, 2417, 2415, 7462, 11838, 21009, 12954, 10551, 20223, 20226,
            11826, 11828, 11832, 11836, 20716, 11907, 12856, 12877, 12883, 12881, 4151, 4587, 5698, 4585, 4087, 3140, 4675, 9185, 7158, 4153, 11235, 11808, 2581,
            3204, 11284, 7398, 7399, 7400, 6916, 6918, 6920, 6922, 6924, 10446, 10448, 10450, 12197, 12273, 6889, 4224, 11759),
    CRYSTAL_CHEST(CLTabType.OTHER, "Crystal Chest", ItemID.SWAMPBARK_HELM, ItemID.SWAMPBARK_BODY, ItemID.SWAMPBARK_LEGS, ItemID.SWAMPBARK_GAUNTLETS, ItemID.SWAMPBARK_BOOTS,
            13319, 20220, 20223, 20226, 20229, 20232, 20235, 3827, 3828, 3829, 3830, 3835, 3836, 3831, 3832, 3833, 3834, 3837, 3838, 12211, 12205, 12207, 12213, 12209, 12309, 12221, 12215, 12217, 12223, 12219, 12311,
            12241, 12235, 12237, 12243, 12239, 12313, 12231, 12225, 12227, 12233, 12229, 12321, 20178, 20169, 20172, 20181, 20175, 12323, 20193, 20184, 20187, 20196, 20190, 12325, 12283, 12277, 12279, 12281, 12285, 13317, 12293, 12287, 12289, 12291, 12295, 13318, 2613, 2607, 2609, 2611, 3475, 10306, 2605, 2599, 2601, 2603, 3474, 10308,
            2619, 2615, 2617, 2621, 3476, 10310, 2627, 2623, 2625, 2629, 3477, 10312, 15335, 15336, 15337, 15338, 15339, 10314, 15340, 15341, 15342, 15343, 15344, 7332, 15330, 15331, 15332, 15333, 15334, 7338, 15310, 15311, 15312, 15313, 15314, 7344,
            2595, 2591, 2593, 2597, 3473, 7350, 2587, 2583, 2585, 2589, 3472, 10316, 15325, 15326, 15327, 15328, 15329, 10318, 15320, 15321, 15322, 15323, 15324, 7362, 15315, 15316, 15317, 15318, 15319, 7366,
            2657, 2653, 2655, 2659, 3478, 7364, 2665, 2661, 2663, 2667, 3479, 7368, 2673, 2669, 2671, 2675, 3480, 20756, 10404, 10406, 10408, 10410, 10412, 10414, 12769, 12771, 12757, 12759, 12761, 12763, 10450, 10446, 10448, 12197, 12261, 12273,
            7370,  12245, 12247, 7445, 7447, 7451, 7441, 7443, 12245, 7372, 7374, 12245, 12773, 12247, 12600),

    SKILLING_PETS(CLTabType.OTHER, "Skilling Pets", 13320, 13322, 13324, 13321, 20659, 20661, 20663, 20693, 21509, 20667),
    GRACE_OUTFIT(CLTabType.OTHER, "Grace's Graceful Clothing", 10069, 10071, 88, 10553, 11850, 11852, 11854, 11856, 11858, 11860, 12641),
    COMMAND_TRIVIA(CLTabType.OTHER, "Command Trivia", 775, 21009, 10589, 10564, 6809),
    RANDOM_EVENTS(CLTabType.OTHER, "Random Events", FROG_TOKEN, 6962, 23312, 23315, 23318, 24872, 24874, 24876, 24878, SPINACH_ROLL, MYSTERY_BOX),

    SLAYER_REWARDS(CLTabType.OTHER, "Slayer Rewards", 11866, 21268, 4081, 10588, 21183, 21177, 12783, 22978, 24268, 20724, 10586, 1052, 6714, 15154, 10858, 776, 777, 11902, 20727, 11200,
            12863, 23206, 430, 6547, 12337, 19699, 4251, 19564, 13116, 22941, 22943, 22945, 22947, 10146, 10147, 10148, 9788, 9786, 9787),

    VOTING_REWARDS(CLTabType.OTHER, "Voting Rewards", 15366,15168,15169,15170, 1050, 1040, 962, 15437,15245, 15246, 15247, 15248,15249, 1961,12357,1419,4083,4566,4567,4565,5608,5609,5607,9470,9472,9946,6818,981, 15381,15382,15383,15384,15385,15251,15252,15253, 21859, 12399, 962, 9013, 23108,23303,23306,2581,12596,23249,19994,25606,26486,26822,
            25557,25106,24942,24727,26427,26430,26433,26436,26517,26850,26852,26854,26856,24806,24808,25979,2577,21214,12887,12888,12889,12890,12891,12892,12893,12894,12895,12896,13104,20116,20119,20122,20125,20113,13679,20251,
            20254,20263,1037,13663,13664,13665,12397,12393,12395,12430,8652,8963,8956,8995,2643,11280,7668,7927,20050,23297,19970,12432,7918,12516,10883,
            9920,9005,9006,12375,12377,12379,10394,10392,10396,10398,7675,7676,6665,6666,23389,6549,1907,15274,20355,20439,20436,20442,20433,12363,12365,12367,12369,23270,12518,12520,12522,12524),

    AGILITY_TICKET_EXCHANGE(CLTabType.OTHER, "Agility Tickets", 19727,12412,12355,8950,2651,2997,7114,8928,8925,8926,8927,22353,12361,12249,4502,19724,747,7112,7110,7116,7124,7122,7126,7136,7134,7138,7130,7128,7132,8949,
            8738,8744,8724,8730,8722,8716,8714,8960,8962,8961,8964,8962,8961,8964,2984,2984,2985,2986,2987,2988,2989,2978,2979,2980,2981,2982,2983),

    SKILLING_POINTS_EXCHANGE(CLTabType.OTHER, "Skilling Points", 15190,12600,22351,12434,6548,23448,4202,19687,19689,19693,19697,19691,19695,12514,12359,11990,12769,12771,12757,12759,12761,12763,12514,10172,20246,
            20269,20266,10400,10402,10420,10422,10416,10418,10436,10438,12315,12317,12339,12341,12343,12345,12347,12349,19699,13203,11136,11138,11140,10878,10879,10880,10881,10882,12299,12301,12303,12305,12307,12309,12311,12313),

    SKILLING_MASTERS(CLTabType.OTHER, "Skilling Masters Outfits", 3327, 3331, 3333, 3329, 3337, 3341, 3343, 3339, 7539, 7537,
            5554, 5553, 5555, 5556, 5557, 10075, 23224, 21143, 12013, 12014, 12015, 12016, 21343, 21345, 21392, 23101, 23097, 23095, 23091,
            23093, 23099, 22842, 13258, 13259, 13260, 13261, 22838, 10941, 10939, 10940, 10933, 20708, 20704, 20706, 20710, 25438, 25434, 25436, 25440, 20208, 20205, 11898, 11896, 11897, 13646, 13642, 13640,
            13644),
    STALL_BONUS_REWARDS(CLTabType.OTHER, "Stall Miscellenous", ItemID.CLOCKWORK, 10362, 1506, 7122, 7124, 7409, 7126, 7128, 7130, 7132, 7134, 10390, 10364, 10366, 10368, 10370, 10372, 10374, 10376, 10378, 10380, 10382, 10384,
            10386, 10388, 6188, 13097, 6862, 6863, 6885, 7114, 7116, 7928, 7929, 7930, 7931, 13099, 13095, 13103, 13105, 6853, 6856, 6857, 6858, 6859, 6861, 7136, 7138, 3057, 3058, 3059, 3060, 3061, 6180, 6181, 6182, 6184, 6185,
            6186, 6187, 6654, 6655, 6656, 7534, 7535, 2633, 12447, 12449, 12445, 2635, 2637, 2639, 2641, 7394, 7390, 7386, 9634, 9636, 9638, 9640, 9642, 9644, 10631, 7592, 7593, 7594, 7595, 7596, 10150, 7396, 7392, 7388,
            2460, 2462, 2464, 2466, 2468, 2470, 2472, 2474, 2476, 2631, 12451, 12453, 12455, 2645, 2647, 2649, 2952, 9946, 9944, 9945, 9921, 9922, 9923, 9924, 9925, 10069, 10063, 10061, 6773, 7141, 7142, 6860,
            1025, 6065, 6067, 6068, 6069, 6070, 10071, 13188, 405, 3698, 3696, 3757, 3758, 4464, 4613, 5345, 7934, 2568, 13354, 2520),

    //ACHIEVEMENT_REWARDS(CLTabType.OTHER, "Achievement Rewards", 11037, 20220, 24384, 24382, 24380, 24378, 24376, 24374, 24207, 24215, 24209, 6040),

    PVP_MYSTERY_BOX(CLTabType.OTHER, "PvP Mystery Box", ItemID.TWISTED_BOW, ItemID.GHRAZI_RAPIER, ItemID.DRAGON_CLAWS, ItemID.DRAGON_WARHAMMER, ItemID.ARMADYL_GODSWORD, ItemID.BANDOS_GODSWORD,
            ItemID.SARADOMIN_GODSWORD, ItemID.ZAMORAK_GODSWORD, ItemID.ELDRITCH_NIGHTMARE_STAFF, ItemID.NIGHTMARE_STAFF, ItemID.STAFF_OF_LIGHT, ItemID.KODAI_WAND, 15155, 15153, 15157, 15158, 15164,
            ItemID.KRAKEN_TENTACLE, ItemID.LIGHT_BALLISTA, ItemID.HEAVY_BALLISTA, ItemID.TWISTED_BUCKLER, ItemID.ARMADYL_CROSSBOW, ItemID.DRAGON_HUNTER_CROSSBOW, ItemID.BERSERKER_RING, ItemID.ARCHERS_RING, ItemID.SEERS_RING, ItemID.RING_OF_THE_GODS, ItemID.TYRANNICAL_RING, ItemID.RING_OF_SUFFERING,
            ItemID.DRAGONFIRE_SHIELD, ItemID.DRAGONFIRE_WARD, ItemID.BANDOS_CHESTPLATE, ItemID.BANDOS_TASSETS, ItemID.RANGER_BOOTS, ItemID.WIZARD_BOOTS, ItemID.ELDER_MAUL_3,
            ItemID.ARCANE_SPIRIT_SHIELD, ItemID.ELYSIAN_SPIRIT_SHIELD, ItemID.SPECTRAL_SPIRIT_SHIELD, ItemID.PRIMORDIAL_BOOTS, ItemID.PEGASIAN_BOOTS,
            ItemID.ETERNAL_BOOTS,
            22613, // Vesta set start
            22616, 22619,
            22610, // Vesta set end
            22625, // Statius set start
            22628, 22631,
            22622, // Statius set end
            22638, // Morrigans set start
            22641,
            22644, // Morrigans set end
            22650, // Zuriels set start
            22653, 22656,
            22647, // Zuriels set end
            ItemID.ABYSSAL_DAGGER_P_PLUS_PLUS_,
            ItemID.ABYSSAL_BLUDGEON, 22322, ItemID.AMULET_OF_ETERNAL_GLORY, 22547, 22323, 22552, ItemID.DARK_BOW, 22486, ItemID.STAFF_OF_THE_DEAD, ItemID.MASTER_WAND,
            ItemID.MAGES_BOOK),
    SUPER_MYSTERY_BOX(CLTabType.OTHER, "Super Mystery Box", 11804, ItemID.KRAKEN_TENTACLE, 11908, ItemID.UNCHARGED_TOXIC_TRIDENT,
            2581, 19994, 12596, 12848, 12809, 21902, 12926, 21000, 22284, 6918, 6916, 6924, 6889, 6914, 2579, 11791, 10551, 6585, 7462, 11284, 6563, 13233, 13221, 10075, 13117, 12845, 12337, 12351,
            6548, 12359, 20595, 20517, 20520, 4675, 4151, 11235, 6731, 6733, 6735, 6737, 12601, 19710, 21892, 21847, 21849, 21851,
            21853, 21855, 21857, 13265, 13576, 10887, 11826, 11828, 11830, 11832, 11834, 7668, 12856, 12855, 20716, 11926, 11924, 20166),
    EXTREME_MYSTERY_BOX(CLTabType.OTHER, "Extreme Mystery Box", 13263, 13652, 21003, 22296,
            11802, 11804, 11806, 11808, 19478, 15152, 13271, 21015, 21000, 21018, 21021, 21024, 11826, 11828, 11830, 11832, 11834, 21634, 22003, 11785, 12002, 19547,
            19553, 12691, 12692, 22975, 20724, 21301, 21304, 15157, 15158, 15153, 15164, 19707, 3486, 3481, 3483, 3485, 3488,
            12931, 13239, 13237, 13235, 11791, 12457, 12458, 12459, 12419, 12420, 12421, 13222, 12637, 12638, 12639, 1419, 9013, 20263, 8969),
    LEGENDARY_MYSTERY_BOX(CLTabType.OTHER, "Legendary Mystery Box", 22324, 21295, 20997,
            22326, 22327, 22328, 22486, 13263, 19481, 21003, 21006, 11802, 15153, 15155, 15156, 12821, 12825, 12817, 11785, 22978, 13271, 11826,
            11828, 11830, 11832, 11834, 22981, 10350, 10346, 10348, 10352, 10334, 10330, 10332, 10336, 10342, 10338, 10340, 10344, 12422,
            12424, 12426, 12437, 21018, 21021, 21024, 21733, 13239, 13237, 13235, 5609, 5608, 5607, 21214, 22351, 22353, 4565),
    GILDED_MYSTERY_BOX(CLTabType.OTHER, "Gilded Mystery Box", 15378, 15380, 15432,
            15379, 15435, 15270, 15166, 15250, 15223, 15225, 15169, 15156, 15393, 15389, 15391, 15386, 15387, 15388, 15390, 15392, 15220, 20205, 20208,
            13036, 13038, 3486, 3481, 3483, 3485, 3488, 23258, 23264, 23267, 23261, 12389, 20155, 15224, 15237,
            15225, ItemID.GILDED_MED_HELM, ItemID.GILDED_CHAINBODY, ItemID.GILDED_SQ_SHIELD, ItemID.GILDED_SPEAR, ItemID.GILDED_HASTA, ItemID.GILDED_BOOTS, 15229, 15230, 15231, 15227, 15228, 15232, 15233, 15226, 15221, 15222, 15254, 15235,
            15234, 15244, 15236, 15238, 15239, 15240, 15241, 15242, 15243, 12419, 12420, 12421, 6465, 23282, 2949, 2946, 2948, 13074, 15262, 26788),
    SACRED_MYSTERY_BOX(CLTabType.OTHER, "Sacred Mystery Box", 15806, 15807, 15808, 15809, 15810, 15811, 15812, 15813, 15814, 15815, 15816, 15817, 15818, 15819, 15820, 15821, 15822, 15823, 15855, 15856, 15192, 15167, 15367, 15368, 15369, 15168, 13343, 13344, 1050, 15169, 15170, 15255,
            15256, 15257, 15258, 15259, 15260, 15361, 15362, 15363, 15364, 15365, 15161, 15162, 15187, 15188, 15189,
            15190, 15191, 11863, 11862, 1038, 1040, 1042, 1044, 1046, 1048, 15358, 15359, 15360, 15171, 15182, 15183, 15184, 15185, 15186, 11847,
            1053, 1055, 1057, 15268, 15269, 15376, 15375),

    VIP_MYSTERY_BOX(CLTabType.OTHER, "VIP Mystery Box",  13190, 15830, 15372, 15373, 15374, 15431, 25604, 25314, 15750, 15798, 26382, 26384, 26386, 15720, 15883, 15885, 15887, 15877, 15879, 15881, 15804, 25733,
            25741, 25738, 25734, 23842, 23845, 23848, 24419, 24420, 24421, 24417, 20997, 24423, 24424, 24425, 26233, 26374, 20014, 20011, 24865, 24864, 24863, 24866, 13173, 13175, 21295, 15263, 15266, 15210, 15211, 15212

    ),

    // TODO: QUESTS
    // TODO: WINTERTODT
    // TODO: Skilling master smith outfit
    ;
    private final CLTabType tab;
    private final String name;
    private final int[] itemIds;

    CLData(CLTabType tab, String name, int... itemIds) {
        this.tab = tab;
        this.name = name;
        this.itemIds = itemIds;
    }
    
    public String getName() {
        return name;
    }

    public int[] getItemIds() {
        return itemIds;
    }

    public CLTabType getTab() {
        return tab;
    }


    public static boolean isCollectionLogItem(int itemId) {
        return COLLECTION_LOG_DATA.stream().anyMatch(i -> Arrays.stream(i.itemIds).anyMatch(id -> id == itemId));
    }

    public static final ImmutableList<CLData> COLLECTION_LOG_DATA = ImmutableList.copyOf(CLData.values());

    public static final ImmutableList<CLData> LOG_DATA_BOSSES = Arrays.stream(CLData.values()).filter(clData -> clData.tab.equals(CLTabType.BOSSES)).collect(ImmutableList.toImmutableList());
    public static final ImmutableList<CLData> LOG_DATA_RAIDS = Arrays.stream(CLData.values()).filter(clData -> clData.tab.equals(CLTabType.RAIDS)).collect(ImmutableList.toImmutableList());
    public static final ImmutableList<CLData> LOG_DATA_CLUES = Arrays.stream(CLData.values()).filter(clData -> clData.tab.equals(CLTabType.CLUES)).collect(ImmutableList.toImmutableList());
    public static final ImmutableList<CLData> LOG_DATA_MINIGAMES = Arrays.stream(CLData.values()).filter(clData -> clData.tab.equals(CLTabType.MINIGAMES)).collect(ImmutableList.toImmutableList());
    public static final ImmutableList<CLData> LOG_DATA_OTHER = Arrays.stream(CLData.values()).filter(clData -> clData.tab.equals(CLTabType.OTHER)).collect(ImmutableList.toImmutableList());

}