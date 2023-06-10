package com.grinder.game.content.cluescroll.scroll;

import com.grinder.game.content.cluescroll.ClueConstants;
import com.grinder.game.content.cluescroll.scroll.reward.RewardTable;
import com.grinder.game.content.item.mysterybox.MysteryBoxRewardItem;
import com.grinder.game.content.skill.skillable.impl.crafting.craftable.impl.LeatherShield;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;
import com.grinder.util.oldgrinder.ValueRange;
import com.grinder.game.content.cluescroll.scroll.reward.ScrollReward;

public enum ScrollDifficulty {

    EASY(2677, ItemID.REWARD_CASKET_EASY_,
            new ValueRange(2, 4),
            new ValueRange(4, 6),
            // Table 1 - Global Rewards
            new RewardTable(25F, ClueConstants.GLOBAL_REWARDS),
            // Table 2 - Common Rewards
            new RewardTable(25F,
                    new ScrollReward(556, 150),
                    new ScrollReward(554, 150),
                    new ScrollReward(557, 150),
                    new ScrollReward(555, 150),
                    new ScrollReward(7937, 150),
                    new ScrollReward(1381), new ScrollReward(1383),
                    new ScrollReward(1387), new ScrollReward(1385),
                    new ScrollReward(1440), new ScrollReward(1442),
                    new ScrollReward(1444), new ScrollReward(1438),
                    new ScrollReward(1159), new ScrollReward(1121),
                    new ScrollReward(1071), new ScrollReward(1197),
                    new ScrollReward(1299), new ScrollReward(1315),
                    new ScrollReward(888, 50),
                    new ScrollReward(1744, 10),
                    new ScrollReward(563, 20),
                    new ScrollReward(561, 20)),
            // Table 3 - Easy Rewards
            new RewardTable(75F, new ScrollReward(11919),
                    new ScrollReward(12956), new ScrollReward(12957),
                    new ScrollReward(12958), new ScrollReward(12959),
                    new ScrollReward(86), new ScrollReward(10366),
                    new ScrollReward(2633), new ScrollReward(2635),
                    new ScrollReward(2637), new ScrollReward(278),
                    new ScrollReward(279), new ScrollReward(272),
                    new ScrollReward(273), new ScrollReward(338),
                    new ScrollReward(337), new ScrollReward(426),
                    new ScrollReward(428), new ScrollReward(456),
                    new ScrollReward(522), new ScrollReward(553),
                    new ScrollReward(552), new ScrollReward(616),
                    new ScrollReward(625), new ScrollReward(9944),
                    new ScrollReward(9640), new ScrollReward(9642),
                    new ScrollReward(9644), new ScrollReward(6065),
                    new ScrollReward(6066), new ScrollReward(6068),
                    new ScrollReward(6069), new ScrollReward(3208),
                    new ScrollReward(12335), new ScrollReward(12337),
                    new ScrollReward(2631), new ScrollReward(23381),
                    new ScrollReward(23413),
                    new ScrollReward(15352), // Metal whips
                    new ScrollReward(15353), // Metal whips
                    new ScrollReward(15354), // Metal whips
                    new ScrollReward(15355), // Metal whips
                    new ScrollReward(15356), // Metal whips
                    new ScrollReward(15357), // Metal whips
                    new ScrollReward(23366), // Black platebody (h)
                    new ScrollReward(23369), // Black platebody (h)
                    new ScrollReward(23372), // Black platebody (h)
                    new ScrollReward(23375), // Black platebody (h)
                    new ScrollReward(23378), // Black platebody (h)
                    new ScrollReward(10877), // Satchels
                    new ScrollReward(10878), // Satchels
                    new ScrollReward(10879), // Satchels
                    new ScrollReward(10880), // Satchels
                    new ScrollReward(10881), // Satchels
                    new ScrollReward(10882), // Satchels
                    new ScrollReward(24537),
                    new ScrollReward(23384)),
            new RewardTable(30F,
                    new ScrollReward(15154), new ScrollReward(6724),
                    new ScrollReward(20202), new ScrollReward(6912),
                    new ScrollReward(2577), new ScrollReward(6920),
                    new ScrollReward(2579), new ScrollReward(20199),
                    new ScrollReward(6585), new ScrollReward(12639))
            // End of easy rewards
    ),
    MEDIUM(2801, ItemID.REWARD_CASKET_MEDIUM_, new ValueRange(3, 5), new ValueRange(4, 6),
            // Table 1 - Global Rewards
            new RewardTable(5F, ClueConstants.GLOBAL_REWARDS),
            // Table 2 - Common Rewards
            new RewardTable(70F, new ScrollReward(554, 300),
                    new ScrollReward(555, 300), new ScrollReward(558, 300),
                    new ScrollReward(556, 300), new ScrollReward(557, 300),
                    new ScrollReward(7937, 300), new ScrollReward(1746, 5),
                    new ScrollReward(890, 1500), new ScrollReward(9143, 1500),
                    new ScrollReward(563, 20, 75),
                    new ScrollReward(23392), // Adamant platebody (h)
                    new ScrollReward(23395), // Adamant platebody (h)
                    new ScrollReward(23398), // Adamant platebody (h)
                    new ScrollReward(23401), // Adamant platebody (h)
                    new ScrollReward(23404), // Adamant platebody (h)
                    new ScrollReward(9183), new ScrollReward(1301),
                    new ScrollReward(1317), new ScrollReward(1397),
                    new ScrollReward(1395), new ScrollReward(1393),
                    new ScrollReward(1399), new ScrollReward(1161),
                    new ScrollReward(1123), new ScrollReward(1199),
                    new ScrollReward(1073), new ScrollReward(1135),
                    new ScrollReward(1099), new ScrollReward(10364),
                    new ScrollReward(2599), new ScrollReward(2601),
                    new ScrollReward(2603), new ScrollReward(2605),
                    new ScrollReward(2607), new ScrollReward(2609),
                    new ScrollReward(2611), new ScrollReward(2613),
                    new ScrollReward(3474), new ScrollReward(3475),
                    new ScrollReward(7321), new ScrollReward(7323),
                    new ScrollReward(7325), new ScrollReward(7327),
                    new ScrollReward(23309), new ScrollReward(23354),
                    new ScrollReward(12361), new ScrollReward(12428),
                    new ScrollReward(7376), new ScrollReward(7382),
                    // Aurimeelis suggested items
                    new ScrollReward(ItemID.EXPLORERS_RING_1), new ScrollReward(ItemID.EXPLORERS_RING_2),
                    new ScrollReward(ItemID.EXPLORERS_RING_3), new ScrollReward(ItemID.EXPLORERS_RING_4),
                    new ScrollReward(ItemID.FALADOR_SHIELD_4), new ScrollReward(ItemID.KANDARIN_HEADGEAR_3),
                    new ScrollReward(ItemID.KANDARIN_HEADGEAR_4), new ScrollReward(ItemID.DESERT_AMULET_2),
                    new ScrollReward(ItemID.DESERT_AMULET_3), new ScrollReward(ItemID.DESERT_AMULET_4),
                    new ScrollReward(ItemID.WESTERN_BANNER_3), new ScrollReward(ItemID.WESTERN_BANNER_4),
                    // End
                    new ScrollReward(25592), new ScrollReward(25594),
                    new ScrollReward(25596), new ScrollReward(25598),
                    new ScrollReward(10454), new ScrollReward(10462),
                    new ScrollReward(10466), new ScrollReward(10446),
                    new ScrollReward(10452), new ScrollReward(10458),
                    new ScrollReward(10442), new ScrollReward(10470),
                    new ScrollReward(10464), new ScrollReward(10450),
                    new ScrollReward(10456), new ScrollReward(10460),
                    new ScrollReward(10468), new ScrollReward(10682),
                    new ScrollReward(10683), new ScrollReward(7378),
                    new ScrollReward(7380), new ScrollReward(2579),
                    new ScrollReward(13097), new ScrollReward(13103),
                    new ScrollReward(12327), new ScrollReward(12329),
                    new ScrollReward(12331), new ScrollReward(12333),
                    new ScrollReward(2577), new ScrollReward(13115),
                    new ScrollReward(13113), new ScrollReward(13111),
                    new ScrollReward(13107), new ScrollReward(13109),
                    new ScrollReward(8464), new ScrollReward(8468),
                    new ScrollReward(8472), new ScrollReward(8476),
                    new ScrollReward(8718), new ScrollReward(8726),
                    new ScrollReward(8732), new ScrollReward(8740)),
            // Table - 3 - Rare Rewards
            new RewardTable(50F,
                    new ScrollReward(12253), new ScrollReward(12255),
                    new ScrollReward(12259),
                    new ScrollReward(12263), new ScrollReward(12470),
                    new ScrollReward(12472), new ScrollReward(12474),
                    new ScrollReward(12476), new ScrollReward(12478),
                    new ScrollReward(12512), new ScrollReward(12508), new ScrollReward(12510),
                    new ScrollReward(19930), new ScrollReward(12265),
                    new ScrollReward(12267), new ScrollReward(12269),
                    new ScrollReward(12271), new ScrollReward(12275),
                    new ScrollReward(12506), new ScrollReward(12480),
                    new ScrollReward(12482), new ScrollReward(12484),
                    new ScrollReward(12486), new ScrollReward(12488),
                    new ScrollReward(12498), new ScrollReward(12500),
                    new ScrollReward(12502), new ScrollReward(12504),
                    new ScrollReward(6764), new ScrollReward(11824),
                    new ScrollReward(15405), new ScrollReward(15399), // Berets
                    new ScrollReward(15400), new ScrollReward(15401), // Berets
                    new ScrollReward(15402), new ScrollReward(15403), // Berets
                    new ScrollReward(15404), // Berets
                    new ScrollReward(15426), // Ranger boots
                    new ScrollReward(15428), // Ranger boots
                    new ScrollReward(15418), // Ranger boots
                    new ScrollReward(15420), // Ranger boots
                    new ScrollReward(15422), // Ranger boots
                    new ScrollReward(15424), // Ranger boots
                    new ScrollReward(19924), new ScrollReward(7804),
                    new ScrollReward(6762), new ScrollReward(19933),
                    new ScrollReward(11889), new ScrollReward(19936),
                    new ScrollReward(19927), new ScrollReward(10510),
                    new ScrollReward(13130), new ScrollReward(13131),
                    new ScrollReward(13132), new ScrollReward(13129),
                    new ScrollReward(10806), new ScrollReward(21649),
                    new ScrollReward(19958), new ScrollReward(19961),
                    new ScrollReward(19964), new ScrollReward(19967),
                    new ScrollReward(19991), new ScrollReward(10426),
                    new ScrollReward(10424), new ScrollReward(10434),
                    new ScrollReward(10432), new ScrollReward(19943),
                    new ScrollReward(23246),
                    new ScrollReward(19952), new ScrollReward(15154)),


            new RewardTable(35F,
                    new ScrollReward(22284), new ScrollReward(14161),
                    new ScrollReward(21298), new ScrollReward(21301),
                    new ScrollReward(21304), new ScrollReward(20166),
                    new ScrollReward(20205), new ScrollReward(20208),
                    new ScrollReward(6918), new ScrollReward(6916),
                    new ScrollReward(6924), new ScrollReward(6731),
                    new ScrollReward(6735), new ScrollReward(6737),
                    new ScrollReward(12601), new ScrollReward(6918)
            )),
    HARD(2722, ItemID.REWARD_CASKET_HARD_,
            new ValueRange(4, 6),
            new ValueRange(4, 7),
            // Table 1 - Global Rewards
            //new RewardTable(5F, ClueConstants.GLOBAL_REWARDS),
            // Table 2 - Common Rewards
            new RewardTable(70F,
                    new ScrollReward(1275), new ScrollReward(1303),
                    new ScrollReward(1319), new ScrollReward(2503),
                    new ScrollReward(2497),
                    new ScrollReward(1401), new ScrollReward(1403),
                    new ScrollReward(1405), new ScrollReward(1407),
                    new ScrollReward(3054), new ScrollReward(1163),
                    new ScrollReward(1127), new ScrollReward(1079),
                    new ScrollReward(1201),
                    new ScrollReward(20113),
                    new ScrollReward(20116),
                    new ScrollReward(20125),
                    new ScrollReward(892, 150),
                    new ScrollReward(563, 75),
                    new ScrollReward(561, 75),
                    new ScrollReward(565, 75),
                    new ScrollReward(9075, 75),
                    new ScrollReward(20223), new ScrollReward(20226),
                    new ScrollReward(20229), new ScrollReward(20232),
                    new ScrollReward(20235), new ScrollReward(20220),
                    new ScrollReward(10280), new ScrollReward(10282),
                    new ScrollReward(10284), new ScrollReward(8490),
                    new ScrollReward(8484), new ScrollReward(10428),
                    new ScrollReward(10430), new ScrollReward(8470),
                    new ScrollReward(8482), new ScrollReward(8478),
                    new ScrollReward(6714), new ScrollReward(20720),
                    new ScrollReward(20712), new ScrollReward(1405),
                    new ScrollReward(12381), new ScrollReward(12383),
                    new ScrollReward(12385), new ScrollReward(12387),
                    new ScrollReward(23209), // Rune platebody (h)
                    new ScrollReward(23212), // Rune platebody (h)
                    new ScrollReward(23215), // Rune platebody (h)
                    new ScrollReward(23218), // Rune platebody (h)
                    new ScrollReward(23221), // Rune platebody (h)
                    new ScrollReward(1745, 5)),
            // Table 3 - Rare Rewards
            new RewardTable(50F,
                    new ScrollReward(2643), new ScrollReward(10719),
                    new ScrollReward(10472), new ScrollReward(10474),
                    new ScrollReward(10440), new ScrollReward(10444),
                    new ScrollReward(19946), new ScrollReward(19949),
                    new ScrollReward(19955), new ScrollReward(12241),
                    new ScrollReward(12243), new ScrollReward(22400),
                    new ScrollReward(22516), new ScrollReward(20020),
                    new ScrollReward(20023), new ScrollReward(20026),
                    new ScrollReward(20029), new ScrollReward(20008),
                    new ScrollReward(20110), new ScrollReward(20110),
                    new ScrollReward(20053), new ScrollReward(20493),
                    new ScrollReward(20436), new ScrollReward(20442),
                    new ScrollReward(20433), new ScrollReward(11137),
                    new ScrollReward(732),
                    new ScrollReward(ItemID.BEEKEEPERS_HAT),
                    new ScrollReward(ItemID.BEEKEEPERS_TOP),
                    new ScrollReward(ItemID.BEEKEEPERS_LEGS),
                    new ScrollReward(ItemID.BEEKEEPERS_GLOVES),
                    new ScrollReward(ItemID.BEEKEEPERS_BOOTS),
                    new ScrollReward(23407), // Wolf mask
                    new ScrollReward(23410), // Wolf cape
                    new ScrollReward(24525), // Cat ears
                    new ScrollReward(10388), new ScrollReward(10390),
                    new ScrollReward(10368), new ScrollReward(10370),
                    new ScrollReward(10372), new ScrollReward(10374),
                    new ScrollReward(10380), new ScrollReward(10382),
                    new ScrollReward(10378), new ScrollReward(10376),
                    new ScrollReward(26531), new ScrollReward(26533),
                    new ScrollReward(26535), new ScrollReward(26537),
                    new ScrollReward(26539), new ScrollReward(24539),
                    new ScrollReward(25916),
                    new ScrollReward(15841),
                    new ScrollReward(10378), new ScrollReward(LeatherShield.ANCIENT_DHIDE_SHIELD),
                    new ScrollReward(10378), new ScrollReward(LeatherShield.ARMADYL_DHIDE_SHIELD),
                    new ScrollReward(10378), new ScrollReward(LeatherShield.BANDOS_DHIDE_SHIELD),
                    new ScrollReward(10378), new ScrollReward(LeatherShield.GUTHIX_DHIDE_SHIELD)
            ),
            // Table 4 - Gilded
            new RewardTable(15F,
                    new ScrollReward(3488), new ScrollReward(3485),
                    new ScrollReward(3483), new ScrollReward(3481),
                    new ScrollReward(3486), new ScrollReward(12596),
                    new ScrollReward(19994), new ScrollReward(20017),
                    new ScrollReward(15414), // Robin hood hat
                    new ScrollReward(15416), // Robin hood hat
                    new ScrollReward(15406), // Robin hood hat
                    new ScrollReward(15408), // Robin hood hat
                    new ScrollReward(15410), // Robin hood hat
                    new ScrollReward(15412), // Robin hood hat
                    new ScrollReward(12638)),
            // Table 5 - Third-age
            new RewardTable(10F,
                    new ScrollReward(10346), new ScrollReward(10348),
                    new ScrollReward(10350), new ScrollReward(10352),
                    new ScrollReward(15859), new ScrollReward(15861), new ScrollReward(15863), // custom 3rd age staff, mage gloves, mage boots
                    new ScrollReward(10342), new ScrollReward(10340),
                    new ScrollReward(10338), new ScrollReward(10344),
                    new ScrollReward(10330), new ScrollReward(10332),
                    new ScrollReward(10334), new ScrollReward(10336))),
    ELITE(12073, ItemID.REWARD_CASKET_ELITE_,
            new ValueRange(4, 7),
            new ValueRange(5, 7),
            // Table 1 - Global Rewards
            //new RewardTable(5F, ClueConstants.GLOBAL_REWARDS),
            // Table 2 - Common Rewards
            //new RewardTable(70F, new ScrollReward(268, 2), new ScrollReward(5302, 2), new ScrollReward(1445, 8), new ScrollReward(1632, 2), new ScrollReward(1127), new ScrollReward(3025, 9), new ScrollReward(2453, 9), new ScrollReward(2435, 9), new ScrollReward(238, 10), new ScrollReward(1392, 8), new ScrollReward(1149)),
            // Table 3 - Rare Rewards
            new RewardTable(50F,
                    new ScrollReward(15180), new ScrollReward(15178),
                    new ScrollReward(15179), new ScrollReward(15159),
                    new ScrollReward(15181), new ScrollReward(23047),
                    new ScrollReward(23050), new ScrollReward(23053),
                    new ScrollReward(23056), new ScrollReward(23059),
                    new ScrollReward(6887), new ScrollReward(8466),
                    new ScrollReward(268, 2),
                    new ScrollReward(15297),
                    new ScrollReward(5302, 2),
                    new ScrollReward(1445, 8),
                    new ScrollReward(1632, 2),
                    new ScrollReward(1127), new ScrollReward(8474),
                    new ScrollReward(8480), new ScrollReward(8486),
                    new ScrollReward(8488), new ScrollReward(8494),
                    new ScrollReward(8720),
                    new ScrollReward(3025, 9),
                    new ScrollReward(2453, 9),
                    new ScrollReward(2435, 9),
                    new ScrollReward(238, 10),
                    new ScrollReward(8728), new ScrollReward(8734),
                    new ScrollReward(8742), new ScrollReward(8746)),
            // Table 4 - Phats / Santas / Masks
//            new RewardTable(5F,
//                    new ScrollReward(15168), new ScrollReward(15169),
//                    new ScrollReward(6465), new ScrollReward(15170),
//                    new ScrollReward(15192), new ScrollReward(13343),
//                    new ScrollReward(22296), new ScrollReward(20000),
//                    new ScrollReward(13199), new ScrollReward(15161),
//                    new ScrollReward(15188), new ScrollReward(15189),
//                    new ScrollReward(13222), new ScrollReward(15191),
//                    new ScrollReward(15185), new ScrollReward(15183)),
            new RewardTable(40F,
                    new ScrollReward(22713), new ScrollReward(22715),
                    new ScrollReward(22717), new ScrollReward(20214),
                    new ScrollReward(20217)),
            // Table 4 -
            new RewardTable(30F,
                    new ScrollReward(24527), // Hell Cat ears
                    new ScrollReward(19997), new ScrollReward(21695),
                    new ScrollReward(12727), new ScrollReward(20590),
                    new ScrollReward(20249), new ScrollReward(20056),
                    new ScrollReward(19941), new ScrollReward(20773),
                    new ScrollReward(20775), new ScrollReward(20777),
                    new ScrollReward(20781), new ScrollReward(20164),
                    new ScrollReward(20836), new ScrollReward(12637),
                    new ScrollReward(22109), new ScrollReward(12457),
                    new ScrollReward(15308), new ScrollReward(15306), // D long
                    new ScrollReward(15307), new ScrollReward(15309), // D long
                    new ScrollReward(15345), new ScrollReward(15349), // D scim
                    new ScrollReward(15350), new ScrollReward(15351), // D scim
                    new ScrollReward(25918),
                    new ScrollReward(12458), new ScrollReward(12459)),
            // Table 5 - God Bows
            new RewardTable(20F,
                    new ScrollReward(20059), new ScrollReward(19918),
                    new ScrollReward(22719))
            // Table 6 - T bow, Vitur, Kodai, Twisted Bucker, Light Ballista
            /*new RewardTable(15F,
                    new ScrollReward(20997), new ScrollReward(21006),
                    new ScrollReward(21000), new ScrollReward(19478),
                    new ScrollReward(22486))*/

    );

    private final int scrollID;
    private final int casketID;
    private final RewardTable[] rewards;
    private final ValueRange taskAmount;
    private final ValueRange rewardAmount;

    ScrollDifficulty(int scrollID, int casketID, ValueRange taskAmount, ValueRange rewardAmount, RewardTable... rewards) {
        this.scrollID = scrollID;
        this.casketID = casketID;
        this.taskAmount = taskAmount;
        this.rewardAmount = rewardAmount;
        this.rewards = rewards;
    }

    public RewardTable[] getRewards() {
        return rewards;
    }

    public int getScrollID() {
        return scrollID;
    }

    public int getCasketID() {
        return casketID;
    }

    public ValueRange getTaskAmount() {
        return taskAmount;
    }

    public ValueRange getRewardAmount() {
        return rewardAmount;
    }

    public static ScrollDifficulty forCasket(int casketID) {
        for (ScrollDifficulty difficulty : values()) {
            if (difficulty.getCasketID() == casketID) {
                return difficulty;
            }
        }
        return null;
    }

    public static ScrollDifficulty forScrollID(int scrollID) {
        for (ScrollDifficulty difficulty : values()) {
            if (difficulty.getScrollID() == scrollID) {
                return difficulty;
            }
        }
        return null;
    }

    public static ScrollDifficulty forName(String name) {
        for (ScrollDifficulty difficulty : values()) {
            if (difficulty.name().equalsIgnoreCase(name.toLowerCase())) {
                return difficulty;
            }
        }
        return null;
    }
}
