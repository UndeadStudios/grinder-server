package com.grinder.game.content.minigame.blastfurnace;

import com.google.common.base.CaseFormat;
import com.grinder.util.ItemID;

/**
 * @author L E G E N D
 * @date 2/17/2021
 * @time 6:34 AM
 * @discord L E G E N D#4380
 */
public enum BlastFurnaceOre {
    TIN(ItemID.TIN_ORE),
    COPPER(ItemID.COPPER_ORE),
    IRON(ItemID.IRON_ORE),
    SILVER(ItemID.SILVER_ORE),
    COAL(ItemID.COAL, 254),
    GOLD(ItemID.GOLD_ORE),
    MITHRIL(ItemID.MITHRIL_ORE),
    ADAMANTITE(ItemID.ADAMANTITE_ORE),
    RUNITE(ItemID.RUNITE_ORE);

    private final int oreId;
    private final int capacity;

    BlastFurnaceOre(int oreId) {
        this(oreId, 28);
    }

    BlastFurnaceOre(int oreId, int capacity) {
        this.oreId = oreId;
        this.capacity = capacity;
    }

    public int getOreId() {
        return oreId;
    }

    public int getCapacity() {
        return capacity;
    }

    public static BlastFurnaceOre forId(int oreId) {
        for (var ore : values()) {
            if (ore.getOreId() == oreId) {
                return ore;
            }
        }
        return null;
    }

    public String getName(){
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name().replace(" Ore", ""));
    }
}
