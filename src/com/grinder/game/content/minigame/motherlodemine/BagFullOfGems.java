package com.grinder.game.content.minigame.motherlodemine;

import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.Rollable;

import java.util.Optional;

/**
 * @author L E G E N D
 * @date 2/15/2021
 * @time 4:06 AM
 * @discord L E G E N D#4380
 */
public enum BagFullOfGems implements Rollable {
    UNCUT_SAPPHIRE(ItemID.UNCUT_SAPPHIRE_2, 2.003),
    UNCUT_EMERALD(ItemID.UNCUT_EMERALD_2, 2.884),
    UNCUT_RUBY(ItemID.UNCUT_RUBY_2, 8.475),
    UNCUT_DIAMOND(ItemID.UNCUT_DIAMOND_2, 32.36),
    UNCUT_DRAGONSTONE(ItemID.UNCUT_DRAGONSTONE_2, 161.3),
    UNCUT_ONYX(ItemID.UNCUT_ONYX_2, 100_000_000);

    private final int itemId;
    private final double roll;

    BagFullOfGems(int itemId, double roll) {
        this.itemId = itemId;
        this.roll = roll;
    }

    public int getItemId() {
        return itemId;
    }

    public double getRoll() {
        return roll;
    }

    public static BagFullOfGems roll() {
        var minimumChance = Double.MAX_VALUE;
        var minimumGem = (BagFullOfGems) UNCUT_SAPPHIRE;
        var roll = Misc.getRandomDouble();
        for (var gem : values()) {
            var gemChance = 1.0 / gem.getRoll();
            if (roll <= gemChance && gemChance <= minimumChance) {
                minimumGem = gem;
                minimumChance = gemChance;
            }
        }
        return minimumGem;
    }

    public static Optional<BagFullOfGems> get(double chance) {
        for (var item : values()) {
            if (item.roll == chance) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }
}

