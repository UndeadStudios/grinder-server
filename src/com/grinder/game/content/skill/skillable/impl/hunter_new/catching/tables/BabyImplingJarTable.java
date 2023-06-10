package com.grinder.game.content.skill.skillable.impl.hunter_new.catching.tables;

import com.grinder.game.definition.droptable.DropTable;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

public class BabyImplingJarTable {

    public static final DropTable table = new DropTable();
    private static final int COMMON = 30;
    private static final int UNCOMMON = 10;
    private static final int RARE = 4;

    static {
        table.append(ItemID.CHISEL, COMMON)
                .append(ItemID.THREAD, COMMON)
                .append(ItemID.NEEDLE, COMMON)
                .append(ItemID.KNIFE, COMMON)
                .append(ItemID.CHEESE, COMMON)
                .append(ItemID.HAMMER, COMMON)
                .append(ItemID.BALL_OF_WOOL, COMMON)

                .append(ItemID.BUCKET_OF_MILK, UNCOMMON)
                .append(ItemID.ANCHOVIES, UNCOMMON)
                .append(ItemID.SPICE, UNCOMMON)
                .append(ItemID.FLAX, UNCOMMON)
                .append(ItemID.MUD_PIE, UNCOMMON)
                .append(ItemID.SEAWEED, UNCOMMON)
                .append(ItemID.AIR_TALISMAN, UNCOMMON)

                .append(ItemID.SILVER_BAR, RARE)
                .append(ItemID.SAPPHIRE, RARE)
                .append(ItemID.HARD_LEATHER, RARE)
                .append(ItemID.LOBSTER, RARE)
                .append(ItemID.SOFT_CLAY, RARE);
    }

    public static Item roll() {
        return table.rollItem();
    }

}
