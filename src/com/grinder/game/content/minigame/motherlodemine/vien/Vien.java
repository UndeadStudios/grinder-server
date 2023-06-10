package com.grinder.game.content.minigame.motherlodemine.vien;

import com.grinder.util.ItemID;
import com.grinder.util.Misc;

/**
 * @author L E G E N D
 * @date 2/9/2021
 * @time 4:27 AM
 * @discord L E G E N D#4380
 */
public enum Vien {
    COAL(ItemID.COAL, 30, 0, 63.94, 41.80, 29.64),
    GOLD(ItemID.GOLD_ORE, 40, 0, 17.88, 23.29, 24.10),
    MITHRIL(ItemID.MITHRIL_ORE, 55, 0, 15.42, 23.70, 26.09),
    ADAMANTITE(ItemID.ADAMANTITE_ORE, 70, 0, 0, 8.58, 15.85),
    RUNITE(ItemID.RUNITE_ORE, 85, 0, 0, 0, 3.56),
    NUGGET(ItemID.GOLDEN_NUGGET, 30, 0, 8.74, 5.74, 8.74);

    private final int itemId;
    private final int levelRequired;
    private final int experience;
    private final double chanceLow;
    private final double chanceMedium;
    private final double chanceHigh;

    Vien(int itemId, int levelRequired, int experience, double chanceLow, double chanceMedium, double chanceHigh) {
        this.itemId = itemId;
        this.levelRequired = levelRequired;
        this.experience = experience;
        this.chanceLow = chanceLow;
        this.chanceMedium = chanceMedium;
        this.chanceHigh = chanceHigh;
    }

    public static Vien roll(int level) {
        var minimumChance = Double.MAX_VALUE;
        var minimumVien = (Vien) COAL;
        var roll = Misc.getRandomDouble() * 100.0;
        for (var vien : values()) {
            var vienChance = vien.getChance(level);
            if (roll <= vienChance && vienChance <= minimumChance) {
                minimumVien = vien;
                minimumChance = vienChance;
            }
        }
        return minimumVien;
    }

    public double getChance(int level) {
        if (level <= 69) {
            return chanceLow;
        } else if (level <= 84) {
            return chanceMedium;
        } else {
            return chanceHigh;
        }
    }

    public int getItemId() {
        return itemId;
    }

    public int getLevelRequired() {
        return levelRequired;
    }

    public int getExperience() {
        return experience;
    }
}
