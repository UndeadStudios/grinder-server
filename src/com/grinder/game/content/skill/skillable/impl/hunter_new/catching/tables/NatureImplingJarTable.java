package com.grinder.game.content.skill.skillable.impl.hunter_new.catching.tables;

import com.grinder.game.definition.droptable.DropTable;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

public class NatureImplingJarTable {
    
    public static final DropTable table = new DropTable();
    
    static {
        table.append(ItemID.LIMPWURT_SEED, 10)
                .append(ItemID.JANGERBERRY_SEED, 10)
                .append(ItemID.BELLADONNA_SEED, 10)
                .append(ItemID.HARRALANDER_SEED, 10)
                .append(ItemID.CACTUS_SPINE, 10)
                .append(ItemID.MAGIC_LOGS, 10)
                .append(ItemID.TARROMIN_2, 10, 4)
                .append(ItemID.COCONUT, 10)
                .append(ItemID.IRIT_SEED, 10)
        
                .append(ItemID.CURRY_TREE_SEED, 1)
                .append(ItemID.ORANGE_TREE_SEED, 1)
                .append(ItemID.SNAPDRAGON, 1)
                .append(ItemID.KWUARM_SEED, 1)
                .append(ItemID.AVANTOE_SEED, 1, 5)
                .append(ItemID.WILLOW_SEED, 1)
                .append(ItemID.TORSTOL_SEED, 1)
                .append(ItemID.RANARR_SEED, 1)
                .append(ItemID.TORSTOL_2, 1, 2)
                .append(ItemID.DWARF_WEED_SEED, 1);
    }
    
    public static Item roll() {
        return table.rollItem();
    }
    
}
