package com.grinder.game.content.skill.skillable.impl.hunter_new.catching.tables;

import com.grinder.game.definition.droptable.DropTable;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

public class GourmetImplingJarTable {
    
    public static final DropTable table = new DropTable();
    
    static {
        table.append(ItemID.TUNA, 20)
                .append(ItemID.BASS, 10)
                .append(ItemID.CURRY, 10)
                .append(ItemID.MEAT_PIE, 10)
                .append(ItemID.CHOCOLATE_CAKE, 10)
                .append(ItemID.FROG_SPAWN, 10)
                .append(ItemID.SPICE, 10)
                .append(ItemID.CURRY_LEAF, 10)
        
                .append(ItemID.UGTHANKI_KEBAB, 1)
                .append(ItemID.LOBSTER_2, 1, 4)
                .append(ItemID.SHARK_2, 1, 3)
                .append(ItemID.FISH_PIE)
                .append(ItemID.CHEFS_DELIGHT)
                .append(ItemID.RAINBOW_FISH_2, 1, 5)
                .append(ItemID.GARDEN_PIE_2, 1, 6)
                .append(ItemID.SWORDFISH_2, 1, 3)
                .append(ItemID.COOKED_KARAMBWAN_2, 1, 2);
        
        // TODO grubby key
    }
    
    public static Item roll() {
        return table.rollItem();
    }
    
}
