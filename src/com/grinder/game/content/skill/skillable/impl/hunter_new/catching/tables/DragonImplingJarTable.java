package com.grinder.game.content.skill.skillable.impl.hunter_new.catching.tables;

import com.grinder.game.definition.droptable.DropTable;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

public class DragonImplingJarTable { 
    public static final DropTable table = new DropTable();
    static {
        table.append(ItemID.DRAGON_BOLT_TIPS, 1, 10, 30)
                .append(ItemID.DRAGON_BOLT_TIPS, 1, 36)
                .append(ItemID.MYSTIC_ROBE_BOTTOM)
                .append(ItemID.GRANITE_BODY)
                .append(ItemID.AMULET_OF_GLORY_2, 1, 3)
                .append(ItemID.DRAGONSTONE_AMULET_2, 1, 2)
                .append(ItemID.DRAGON_ARROW, 1, 100, 250)
                .append(ItemID.DRAGON_BOLTS, 1, 10, 40)
                .append(ItemID.DRAGON_LONGSWORD)
                .append(ItemID.DRAGON_DAGGER_P_PLUS_PLUS_2, 1, 3)
                .append(ItemID.DRAGON_DART, 1, 100, 250)
                .append(ItemID.DRAGONSTONE_2, 1, 3)
                .append(ItemID.DRAGON_DART_TIP, 1, 100, 350)
                .append(ItemID.DRAGON_ARROWTIPS, 1, 100, 350)
                .append(ItemID.DRAGON_JAVELIN_HEADS, 1, 25, 35)
                .append(ItemID.BABYDRAGON_BONES_2, 1, 100, 350)
                .append(ItemID.DRAGON_BONES_2, 1, 50, 100)
                .append(ItemID.MAGIC_SEED)
                .append(ItemID.SNAPDRAGON_SEED, 1, 6)
                .append(ItemID.SUMMER_PIE_2, 1, 15);
    }
    
    public static Item roll() {
        return table.rollItem();
    }
    
}
