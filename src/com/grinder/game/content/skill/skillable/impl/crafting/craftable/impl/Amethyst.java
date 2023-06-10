package com.grinder.game.content.skill.skillable.impl.crafting.craftable.impl;

import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

import java.util.HashMap;
import java.util.Map;

import static com.grinder.game.content.skill.skillable.impl.crafting.craftable.impl.CraftableConstants.*;


/**
 * An enumerated type listing for all amethyst ammo that
 * is craftable.
 *
 * @author Maranami
 */



public enum Amethyst {

    AMMO(ItemID.AMETHYST,
        new CraftableAmethyst(new Item(ItemID.AMETHYST_BOLT_TIPS, 15), 83, 60, AMETHYST_ANIM),
        new CraftableAmethyst(new Item(ItemID.AMETHYST_ARROWTIPS, 15), 85, 60 , AMETHYST_ANIM),
        new CraftableAmethyst(new Item(ItemID.AMETHYST_JAVELIN_HEADS, 5), 87, 60, AMETHYST_ANIM),
        new CraftableAmethyst(new Item(ItemID.AMETHYST_DART_TIP, 8), 89, 60, AMETHYST_ANIM)
    );

    public static Map<Integer, Amethyst> ore = new HashMap<>();

    static {
        for (Amethyst l : Amethyst.values()) {
            ore.put(l.getOreId(), l);
        }
    }

    private final int oreId;
    private final CraftableAmethyst[] craftable;

    Amethyst(int logId, CraftableAmethyst... craftable) {
        this.oreId = logId;
        this.craftable = craftable;
    }

    public int getOreId() {
        return oreId;
    }

    public CraftableAmethyst[] getCraftable() {
        return craftable;
    }
}
