package com.grinder.game.content.skill.skillable.impl.hunter_new.catching.tables;

import com.grinder.game.definition.droptable.DropTable;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

public class EssenceImplingJarTable {
    
    public static final DropTable table = new DropTable();
    private static final int COMMON = 50;
    private static final int UNCOMMON = 20;
    private static final int RARE = 4;
    
    static {
        table.append(ItemID.PURE_ESSENCE_2, COMMON, 20, 35)
                .append(ItemID.WATER_RUNE, COMMON, 30)
                .append(ItemID.AIR_RUNE, COMMON, 30)
                .append(ItemID.FIRE_RUNE, COMMON, 50)
                .append(ItemID.MIND_RUNE, COMMON, 25)
                .append(ItemID.BODY_RUNE, COMMON, 28)
                .append(ItemID.CHAOS_RUNE, COMMON, 4)
                .append(ItemID.MIND_TALISMAN, COMMON)
            
                .append(ItemID.LAVA_RUNE, UNCOMMON, 4)
                .append(ItemID.MUD_RUNE, UNCOMMON, 4)
                .append(ItemID.SMOKE_RUNE, UNCOMMON, 4)
                .append(ItemID.STEAM_RUNE, UNCOMMON, 4)
                .append(ItemID.COSMIC_RUNE, UNCOMMON, 4)
            
                .append(ItemID.DEATH_RUNE, RARE, 13)
                .append(ItemID.LAW_RUNE, RARE, 13)
                .append(ItemID.BLOOD_RUNE, RARE, 7)
                .append(ItemID.SOUL_RUNE, RARE, 11)
                .append(ItemID.NATURE_RUNE, RARE, 13);
    }
    
    public static Item roll() {
        return table.rollItem();
    }
    
}
