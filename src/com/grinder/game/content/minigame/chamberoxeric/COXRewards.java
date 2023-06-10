package com.grinder.game.content.minigame.chamberoxeric;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class COXRewards {

    private static final int PET_REWARD_RATE = 50_000;

    private static final int RARE_REWARD_RATE = 35_000;

    private static final int DOUBLE_REWARD_RATE = 45_000;

    private static final int DOUBLE_ITEM_CHANCE = 4;

    private static final Item DARK_JOURNAL = new Item(ItemID.DARK_JOURNAL);

    private static final int[] TOP_REWARDS = {
            ItemID.DRAGON_CLAWS, ItemID.ELDER_MAUL_3, ItemID.DRAGON_HUNTER_CROSSBOW,
            ItemID.TWISTED_BOW, ItemID.TWISTED_BUCKLER,
            ItemID.ANCESTRAL_HAT, ItemID.ANCESTRAL_ROBE_TOP, ItemID.ANCESTRAL_ROBE_BOTTOM,
            ItemID.KODAI_WAND, ItemID.TORN_PRAYER_SCROLL, ItemID.DEXTEROUS_PRAYER_SCROLL,
            ItemID.ARCANE_PRAYER_SCROLL, ItemID.DINHS_BULWARK, ItemID.DARK_RELIC,
            ItemID.ANCIENT_TABLET, ItemID.TWISTED_ANCESTRAL_COLOUR_KIT
    };

    private static final int[] BASIC_REWARDS = {
            ItemID.DEATH_RUNE, ItemID.BLOOD_RUNE, ItemID.RUNE_ARROW,
            ItemID.RUNITE_ORE_2, ItemID.RANARR_SEED, ItemID.KWUARM_2,
            ItemID.SILVER_ORE_2, ItemID.PURE_ESSENCE_2, ItemID.UNCUT_SAPPHIRE_2,
            ItemID.UNCUT_DIAMOND_2,
    };

    private static final int[] PETS = {
            22376, 22378, 22380, 22380, 22380, 22382, 22384
    };

    public static void grantReward(Player p) {
        if (p.getCOX().getParty().time.elapsed(1000 * 60 * 20)) {
            p.getCOX().points += 1000;
        } else if (p.getCOX().getParty().time.elapsed(1000 * 60 * 15)) {
            p.getCOX().points += 3000;
        } else if (p.getCOX().getParty().time.elapsed(1000 * 60 * 10)) {
            p.getCOX().points += 5000;
        } else if (!p.getCOX().getParty().time.elapsed(1000 * 60 * 5)) {
            p.getCOX().points += 7000;
        }

        p.getCOX().reward.resetItems();

        int amount = 1;

        if (p.getCOX().points >= DOUBLE_REWARD_RATE) {
            if (Misc.random(DOUBLE_ITEM_CHANCE) == 1) {
                amount = 2;
            }
        }

        for (int i = 0; i < amount; i++) {
            int random = Misc.random(100);

            Item reward = new Item(Misc.randomElement(BASIC_REWARDS), 1 + Misc.random(3000));

            if (reward.getId() == 7937) {
                reward.setAmount(reward.getAmount() * 4);
            }

            if (random >= 0 && random <= 4 && p.getCOX().points >= RARE_REWARD_RATE) {
                reward = new Item(Misc.randomElement(TOP_REWARDS));

                p.getCOX().getParty().sendMessage(p.getUsername() + " has received: " + reward.getName() + "!");
            } else if (random >= 5 && random <= 8 && p.getCOX().points >= PET_REWARD_RATE) {
                reward = new Item(Misc.randomElement(PETS));

                p.getCOX().getParty().sendMessage(p.getUsername() + " has received pet: " + reward.getName() + "!");
            }

            p.getCOX().reward.add(reward);
        }

        p.getCOX().reward.add(DARK_JOURNAL);
    }
}
