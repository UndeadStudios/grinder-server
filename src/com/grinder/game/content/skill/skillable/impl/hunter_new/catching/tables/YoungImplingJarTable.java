package com.grinder.game.content.skill.skillable.impl.hunter_new.catching.tables;

import com.grinder.game.definition.droptable.DropTable;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

public class YoungImplingJarTable {
    
    public static final DropTable table = new DropTable();
    private static final int COMMON = 30;
    private static final int UNCOMMON = 10;
    private static final int RARE = 4;
    
    static {
        table.append(ItemID.STEEL_NAILS, COMMON, 5)
                .append(ItemID.LOCKPICK, COMMON)
                .append(ItemID.PURE_ESSENCE, COMMON)
                .append(ItemID.TUNA, COMMON)
                .append(ItemID.CHOCOLATE_SLICE, COMMON)
                
                .append(ItemID.STEEL_AXE, UNCOMMON)
                .append(ItemID.MEAT_PIZZA, UNCOMMON)
                .append(ItemID.GARDEN_PIE, UNCOMMON)
                .append(ItemID.JANGERBERRIES, UNCOMMON)
                .append(ItemID.COAL, UNCOMMON)
                .append(ItemID.BOW_STRING, UNCOMMON)
                .append(ItemID.SNAPE_GRASS, UNCOMMON)
                .append(ItemID.SOFT_CLAY, UNCOMMON)
                
                .append(ItemID.STUDDED_CHAPS, RARE)
                .append(ItemID.STEEL_FULL_HELM, RARE)
                .append(ItemID.OAK_PLANK, RARE)
                .append(ItemID.DEFENCE_POTION_3_, RARE)
                .append(ItemID.MITHRIL_BAR, RARE)
                .append(ItemID.YEW_LONGBOW, RARE);
    }
    
    public static Item roll() {
        return table.rollItem();
    }
    
}
