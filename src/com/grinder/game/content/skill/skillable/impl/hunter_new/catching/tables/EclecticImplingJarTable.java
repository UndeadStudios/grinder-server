package com.grinder.game.content.skill.skillable.impl.hunter_new.catching.tables;

import com.grinder.game.definition.droptable.DropTable;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

public class EclecticImplingJarTable {
    
    public static final DropTable table = new DropTable();
    
    static {
        table.append(ItemID.MITHRIL_PICKAXE, 10)
                .append(ItemID.CURRY_LEAF, 10)
                .append(ItemID.SNAPE_GRASS, 10)
                .append(ItemID.AIR_RUNE, 10, 30, 58)
                .append(ItemID.OAK_PLANK_2, 10, 4)
                .append(ItemID.EMPTY_CANDLE_LANTERN, 10)
                .append(ItemID.GOLD_ORE, 10)
                .append(ItemID.GOLD_BAR_2, 10, 5)
                .append(ItemID.UNICORN_HORN, 10)
        
                .append(ItemID.ADAMANT_KITESHIELD)
                .append(ItemID.BLUE_DHIDE_CHAPS)
                .append(ItemID.RED_SPIKY_VAMBS)
                .append(ItemID.RUNE_DAGGER)
                .append(ItemID.BATTLESTAFF)
                .append(ItemID.ADAMANTITE_ORE_2, 1, 10)
                .append(ItemID.SLAYERS_RESPITE_2, 1, 2)
                .append(ItemID.WILD_PIE)
                .append(ItemID.WATERMELON_SEED, 1, 3)
                .append(ItemID.DIAMOND);
    }
    
    public static Item roll() {
        return table.rollItem();
    }
    
}
