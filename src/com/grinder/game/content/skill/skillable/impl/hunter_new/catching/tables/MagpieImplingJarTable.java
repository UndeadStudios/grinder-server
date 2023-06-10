package com.grinder.game.content.skill.skillable.impl.hunter_new.catching.tables;

import com.grinder.game.definition.droptable.DropTable;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

public class MagpieImplingJarTable {
    
    public static final DropTable table = new DropTable();
    
    static {
        table.append(ItemID.BLACK_DRAGONHIDE_2, 10, 6)
                .append(ItemID.DIAMOND_AMULET_2, 5, 3)
                .append(ItemID.AMULET_OF_POWER_2, 5, 3)
                .append(ItemID.RING_OF_FORGING_2, 5, 3)
                .append(ItemID.SPLITBARK_GAUNTLETS, 5)
                .append(ItemID.MYSTIC_BOOTS, 5)
                .append(ItemID.MYSTIC_GLOVES, 5)
                .append(ItemID.RUNE_WARHAMMER, 5)
                .append(ItemID.RING_OF_LIFE_2, 5, 4)
                .append(ItemID.RUNE_SQ_SHIELD, 5)
                .append(ItemID.DRAGON_DAGGER, 5)
                .append(ItemID.NATURE_TIARA, 5)
                .append(ItemID.RUNITE_BAR_2, 5, 2)
                .append(ItemID.DIAMOND_2, 5, 4)
                .append(ItemID.PINEAPPLE_SEED, 5)
                .append(ItemID.LOOP_HALF_OF_KEY, 5)
                .append(ItemID.TOOTH_HALF_OF_KEY, 5)
                .append(ItemID.SNAPDRAGON_SEED, 5)
                .append(ItemID.GRANITE_BODY, 5)
                .append(ItemID.SINISTER_KEY, 5);
    }
    
    public static Item roll() {
        return table.rollItem();
    }
    
}
