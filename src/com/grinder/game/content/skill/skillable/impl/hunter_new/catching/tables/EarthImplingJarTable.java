package com.grinder.game.content.skill.skillable.impl.hunter_new.catching.tables;

import com.grinder.game.definition.droptable.DropTable;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

public class EarthImplingJarTable {
    
    public static final DropTable table = new DropTable();
    private static final int COMMON = 50;
    private static final int UNCOMMON = 20;
    private static final int RARE = 4;

    static {
        table.append(ItemID.EARTH_TALISMAN, COMMON)
                .append(ItemID.EARTH_TIARA, COMMON)
                .append(ItemID.EARTH_RUNE, COMMON, 32)
                .append(ItemID.MITHRIL_ORE_2, COMMON, 1, 3)
                .append(ItemID.UNICORN_HORN, COMMON)
                .append(ItemID.STEEL_BAR, COMMON)
                .append(ItemID.MITHRIL_PICKAXE, COMMON)
                .append(ItemID.WILDBLOOD_SEED, COMMON, 2)
                .append(ItemID.JANGERBERRY_SEED, COMMON, 2)
                .append(ItemID.COMPOST_2, COMMON, 6)
        
                .append(ItemID.SUPERCOMPOST_2, UNCOMMON, 2)
                .append(ItemID.BUCKET_OF_SAND_2, UNCOMMON, 4)
                .append(ItemID.HARRALANDER_SEED, UNCOMMON, 2)
                .append(ItemID.COAL_2, UNCOMMON, 2)
        
                .append(ItemID.GOLD_ORE, RARE)
                .append(ItemID.UNCUT_EMERALD_2, RARE, 2)
                .append(ItemID.EMERALD_2, RARE, 2)
                .append(ItemID.RUBY, RARE);
    }
    
    public static Item roll() {
        return table.rollItem();
    }
    
}
