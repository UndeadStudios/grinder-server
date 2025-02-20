package com.grinder.game.content.skill.skillable.impl.fletching;

import com.grinder.game.content.skill.skillable.impl.slayer.SlayerRewards;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.util.ItemID;

/**
 * Represents ammo which can be made using the Fletching skill.
 *
 * @author Professor Oak
 */
public enum FletchableAmmo {

    HEADLESS_ARROWS(ItemID.ARROW_SHAFT, ItemID.FEATHER, ItemID.HEADLESS_ARROW, 15, 1),
    BRONZE_ARROWS(ItemID.HEADLESS_ARROW, ItemID.BRONZE_ARROWTIPS, ItemID.BRONZE_ARROW, 20, 1),
    IRON_ARROWS(ItemID.HEADLESS_ARROW, ItemID.IRON_ARROWTIPS, ItemID.IRON_ARROW, 38, 15),
    STEEL_ARROWS(ItemID.HEADLESS_ARROW, ItemID.STEEL_ARROWTIPS, ItemID.STEEL_ARROW, 75, 30),
    MITHRIL_ARROWS(ItemID.HEADLESS_ARROW, ItemID.MITHRIL_ARROWTIPS, ItemID.MITHRIL_ARROW, 113, 45),
    BROAD_ARROWS(ItemID.HEADLESS_ARROW, ItemID.BROAD_ARROWHEADS, ItemID.BROAD_ARROWS_2, 150, 52),
    ADAMANT_ARROWS(ItemID.HEADLESS_ARROW, ItemID.ADAMANT_ARROWTIPS, ItemID.ADAMANT_ARROW, 150, 60),
    RUNE_ARROWS(ItemID.HEADLESS_ARROW, ItemID.RUNE_ARROWTIPS, ItemID.RUNE_ARROW, 188, 75),
    AMETHYST_ARROWS(ItemID.HEADLESS_ARROW, ItemID.AMETHYST_ARROWTIPS, ItemID.AMETHYST_ARROW, 202, 82),
    DRAGON_ARROWS(ItemID.HEADLESS_ARROW, ItemID.DRAGON_ARROWTIPS, ItemID.DRAGON_ARROW, 225, 90),

    BRONZE_DARTS(314, 819, 806, 2, 10),
    IRON_DARTS(314, 820, 807, 4, 22),
    STEEL_DARTS(314, 821, 808, 8, 37),
    MITHRIL_DARTS(314, 822, 809, 12, 52),
    ADAMANT_DARTS(314, 823, 810, 15, 67),
    RUNE_DARTS(314, 824, 811, 20, 81),
    AMETHYST_DARTS(314, 25853, 25849, 21, 90),
    DRAGON_DARTS(314, 11232, 11230, 25, 95),

    BRONZE_BOLTS(314, 9375, 877, 5, 9),
    OPAL_BOLTS(877, 45, 879, 16, 11),
    IRON_BOLTS(314, 9377, 9140, 15, 39),
    PEARL_BOLTS(9140, 46, 880, 32, 41),
    SILVER_BOLTS(314, 9382, 9145, 25, 43),
    STEEL_BOLTS(314, 9378, 9141, 35, 46),
    RED_TOPAZ_BOLTS(9141, 9188, 9336, 39, 48),
    BARBED_BOLTS(877, 47, 881, 95, 51),
    MITHRIL_BOLTS(314, 9379, 9142, 50, 54),
    BROAD_BOLTS(314, 11876, 11875, 30, 55),
    SAPPHIRE_BOLTS(9142, 9189, 9337, 47, 56),
    EMERALD_BOLTS(9142, 9190, 9338, 58, 55),
    ADAMANTITE_BOLTS(314, 9380, 9143, 70, 61),
    RUBY_BOLTS(9143, 9191, 9339, 63, 63),
    DIAMOND_BOLTS(9143, 9192, 9340, 70, 65),
    RUNITE_BOLTS(314, 9381, 9144, 100, 69),
    DRAGONSTONE_BOLTS(9144, 9193, 9341, 82,71),
    ONYX_BOLTS(9144, 9194, 9342, 94, 73),
    AMETHYST_BROAD_BOLTS(11875, 21338, 21316, 106, 76),
    DRAGON_BOLTS(314, 21930, 21905, 120, 84),
    OPAL_DRAGON_BOLTS(21905, 45, 21955, 16,84),
    JADE_DRAGON_BOLTS(21905, 9187, 21957, 24,84),
    PEARL_DRAGON_BOLTS(21905, 46, 21959, 32,84),
    TOPAZ_DRAGON_BOLTS(21905, 9188, 21961, 39,84),
    SAPPHIRE_DRAGON_BOLTS(21905, 9189, 21963, 47,84),
    EMERALD_DRAGON_BOLTS(21905, 9190, 21965, 55,84),
    RUBY_DRAGON_BOLTS(21905, 9191, 21967, 63,84),
    DIAMOND_DRAGON_BOLTS(21905, 9192, 21969, 70,84),
    DRAGONSTONE_DRAGON_BOLTS(21905, 9193, 21971, 82,84),
    ONYX_DRAGON_BOLTS(21905, 9194, 21973, 94,84),

    FLIGHTED(ItemID.FEATHER, ItemID.HEADLESS_ARROW, ItemID.OGRE_ARROW_SHAFT, 1, 5),
    OGRE(ItemID.OGRE_ARROW_SHAFT, ItemID.WOLFBONE_ARROWTIPS, ItemID.OGRE_ARROW, 15, 5),
    BRONZE_BRUTAL(ItemID.OGRE_ARROW_SHAFT, ItemID.BRONZE_NAILS, ItemID.BRONZE_BRUTAL, 2, 7),
    IRON_BRUTAL(ItemID.OGRE_ARROW_SHAFT, ItemID.IRON_NAILS, ItemID.IRON_BRUTAL,3, 18),
    STEEL_BRUTAL(ItemID.OGRE_ARROW_SHAFT, ItemID.STEEL_NAILS, ItemID.STEEL_BRUTAL,5, 33),
    BLACK_BRUTAL(ItemID.OGRE_ARROW_SHAFT, ItemID.BLACK_NAILS,ItemID.BLACK_BRUTAL,  7, 38),
    MITHRIL_BRUTAL(ItemID.OGRE_ARROW_SHAFT, ItemID.MITHRIL_NAILS,ItemID.MITHRIL_BRUTAL,  8, 49),
    ADAMANT_BRUTAL(ItemID.OGRE_ARROW_SHAFT, ItemID.ADAMANTITE_NAILS,ItemID.ADAMANT_BRUTAL,  10, 62),
    RUNE_BRUTAL(ItemID.OGRE_ARROW_SHAFT, ItemID.RUNE_NAILS,ItemID.RUNE_BRUTAL,  12, 77),

    BRONZE_JAVELIN(ItemID.JAVELIN_SHAFT, ItemID.BRONZE_JAVELIN_HEADS,ItemID.BRONZE_JAVELIN,  1, 3),
    IRON_JAVELIN(ItemID.JAVELIN_SHAFT, ItemID.IRON_JAVELIN_HEADS,ItemID.IRON_JAVELIN,  2, 17),
    STEEL_JAVELIN(ItemID.JAVELIN_SHAFT, ItemID.STEEL_JAVELIN_HEADS,ItemID.STEEL_JAVELIN,  5, 32),
    MITHRIL_JAVELIN(ItemID.JAVELIN_SHAFT, ItemID.MITHRIL_JAVELIN_HEADS,ItemID.MITHRIL_JAVELIN,  8, 47),
    ADAMANT_JAVELIN(ItemID.JAVELIN_SHAFT, ItemID.ADAMANT_JAVELIN_HEADS,ItemID.ADAMANT_JAVELIN,  10, 62),
    RUNE_JAVELIN(ItemID.JAVELIN_SHAFT, ItemID.RUNE_JAVELIN_HEADS,ItemID.RUNE_JAVELIN,  12, 77),
    AMETHYST_JAVELIN(ItemID.JAVELIN_SHAFT, ItemID.AMETHYST_JAVELIN_HEADS,ItemID.AMETHYST_JAVELIN,  14, 84),
    DRAGON_JAVELIN(ItemID.JAVELIN_SHAFT, ItemID.DRAGON_JAVELIN_HEADS,ItemID.DRAGON_JAVELIN,  15, 92);

    public final int item1, item2, outcome, xp, levelReq;

    FletchableAmmo(int item1, int item2, int outcome, int xp, int levelReq) {
        this.item1 = item1;
        this.item2 = item2;
        this.outcome = outcome;
        this.xp = xp;
        this.levelReq = levelReq;
    }

    public int getItem1() {
        return item1;
    }

    public int getItem2() {
        return item2;
    }

    public int getOutcome() {
        return outcome;
    }

    public int getXp() {
        return xp;
    }

    public int getLevelReq() {
        return levelReq;
    }

    public boolean hasRequirements(Player player) {
        if (this == BROAD_ARROWS || this == BROAD_BOLTS || this == AMETHYST_BROAD_BOLTS) {
            if (!player.getSlayer().getUnlocked()[SlayerRewards.Rewards.BROADER_FLETCHING.ordinal()]) {
                player.sendMessage("You need to have unlocked the 'Broader Fletching' perk in order to do this.");
                return false;
            }
        }
        return true;
    }
}
