package com.grinder.game.content.skill.skillable.impl.hunter_new.catching.tables;

import com.grinder.game.definition.droptable.DropTable;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

public class NinjaImplingJarTable {
    
    public static final DropTable table = new DropTable();
    
    static {
        table.append(ItemID.SNAKESKIN_BOOTS)
                .append(ItemID.GRANITE_BODY)
                .append(ItemID.SPLITBARK_HELM)
                .append(ItemID.MYSTIC_BOOTS)
                .append(ItemID.RUNE_CHAINBODY)
                .append(ItemID.MYSTIC_GLOVES)
                .append(ItemID.OPAL_MACHETE)
                .append(ItemID.RUNE_CLAWS)
                .append(ItemID.RUNE_SCIMITAR)
                .append(ItemID.DRAGON_DAGGER_P_PLUS_PLUS_)
                .append(ItemID.RUNE_ARROW, 1, 70)
                .append(ItemID.RUNE_DART, 1, 70)
                .append(ItemID.RUNE_KNIFE, 1, 40)
                .append(ItemID.RUNE_THROWNAXE, 1, 50)
                .append(ItemID.ONYX_BOLTS, 1, 2)
                .append(ItemID.ONYX_BOLT_TIPS, 1, 4)
                .append(ItemID.BLACK_DRAGONHIDE_2, 1, 10)
                .append(ItemID.PRAYER_POTION_3_2, 1, 4)
                .append(ItemID.WEAPON_POISON_PLUS_2, 1, 4)
                .append(ItemID.DAGANNOTH_HIDE_2, 1, 3);
    }
    
    public static Item roll() {
        return table.rollItem();
    }
    
}
