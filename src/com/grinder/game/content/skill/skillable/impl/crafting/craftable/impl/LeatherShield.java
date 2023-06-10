package com.grinder.game.content.skill.skillable.impl.crafting.craftable.impl;

import com.grinder.game.content.skill.skillable.impl.crafting.Crafting;
import com.grinder.game.content.skill.skillable.impl.crafting.craftable.Craftable;
import com.grinder.game.content.skill.skillable.impl.crafting.craftable.CraftableItem;
import com.grinder.game.model.Animation;
import com.grinder.game.model.AnimationLoop;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.RequiredItem;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.model.sound.SoundLoop;
import com.grinder.util.ItemID;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Zach (zach@findzach.com)
 * @since 7/25/2021
 *
 * Will handle the creation of our Snakeskin -> Black D'Hide Shields
 */
public enum LeatherShield implements Craftable {
    HARD_LEATHER(new Item(ItemID.HARD_LEATHER), new Item(LeatherShield.OAK_SHIELD),
            new CraftableItem(new Item(LeatherShield.HARD_LEATHER_SHIELD), 41, 70.0, "You make a Hard leather shield.", 5,
                    new Item(ItemID.HARD_LEATHER, 2),
                    new Item(ItemID.BRONZE_NAILS, 15),
                    new Item(LeatherShield.OAK_SHIELD, 1))),

    SNAKESKIN(new Item(ItemID.SNAKESKIN), new Item(LeatherShield.WILLOW_SHIELD),
            new CraftableItem(new Item(LeatherShield.SNAKESKIN_SHIELD), 56, 100.0, "You make a Snakeskin shield.", 5,
                    new Item(ItemID.SNAKESKIN, 2),
                    new Item(ItemID.IRON_NAILS, 15),
                    new Item(LeatherShield.WILLOW_SHIELD, 1))),

    GREEN_DHIDE(new Item(ItemID.GREEN_DRAGON_LEATHER), new Item(LeatherShield.MAPLE_SHIELD),
            new CraftableItem(new Item(LeatherShield.GREEN_DHIDE_SHIELD), 62, 124.0, "You make a Green d'hide shield.", 5,
                    new Item(ItemID.GREEN_DRAGON_LEATHER, 2),
                    new Item(ItemID.STEEL_NAILS, 15),
                    new Item(LeatherShield.MAPLE_SHIELD, 1))),

    BLUE_DHIDE(new Item(ItemID.BLUE_DRAGON_LEATHER), new Item(LeatherShield.YEW_SHIELD),
            new CraftableItem(new Item(LeatherShield.BLUE_DHIDE_SHIELD), 69, 140.0, "You make a Blue d'hide shield.", 5,
                    new Item(ItemID.BLUE_DRAGON_LEATHER, 2),
                    new Item(ItemID.MITHRIL_NAILS, 15),
                    new Item(LeatherShield.YEW_SHIELD, 1))),

    RED_DHIDE(new Item(ItemID.RED_DRAGON_LEATHER), new Item(LeatherShield.MAGIC_SHIELD),
            new CraftableItem(new Item(LeatherShield.RED_DHIDE_SHIELD), 76, 156.0, "You make a Red d'hide shield.", 5,
                    new Item(ItemID.RED_DRAGON_LEATHER, 2),
                    new Item(ItemID.ADAMANTITE_NAILS, 15),
                    new Item(LeatherShield.MAGIC_SHIELD, 1))),

    BLACK_DHIDE(new Item(ItemID.BLACK_DRAGON_LEATHER), new Item(LeatherShield.REDWOOD_SHIELD),
            new CraftableItem(new Item(LeatherShield.BLACK_DHIDE_SHIELD), 83, 172.0, "You make a Black d'hide shield.", 5,
                    new Item(ItemID.BLACK_DRAGON_LEATHER, 2),
                    new Item(ItemID.RUNE_NAILS, 15),
                    new Item(LeatherShield.REDWOOD_SHIELD, 1)))
    ;

    static {
        Arrays.stream(values()).forEach(Crafting::addCraftable);
    }

    public static final int OAK_SHIELD = 22251;
    public static final int WILLOW_SHIELD = 22254;
    public static final int MAPLE_SHIELD = 22257;
    public static final int YEW_SHIELD = 22260;
    public static final int MAGIC_SHIELD = 22263;
    public static final int REDWOOD_SHIELD = 22266;

    public static final int HARD_LEATHER_SHIELD = 22269;
    public static final int SNAKESKIN_SHIELD = 22272;
    public static final int GREEN_DHIDE_SHIELD = 22275;
    public static final int BLUE_DHIDE_SHIELD = 22278;
    public static final int RED_DHIDE_SHIELD = 22281;
    public static final int BLACK_DHIDE_SHIELD = 22284;

    public static final int GUTHIX_DHIDE_SHIELD = 23188;
    public static final int SARADOMIN_DHIDE_SHIELD = 23191;
    public static final int ZAMORAK_DHIDE_SHIELD = 23194;
    public static final int ANCIENT_DHIDE_SHIELD = 23197;
    public static final int ARMADYL_DHIDE_SHIELD = 23200;
    public static final int BANDOS_DHIDE_SHIELD = 23203;


    private Item use;
    private Item with;
    private CraftableItem[] craftableItems;

    /**
     * Constructor for Leather Shield Creation
     * @param use - The hide
     * @param with - The shield
     * @param craftableItems
     */
    LeatherShield(Item use, Item with, CraftableItem... craftableItems) {
        this.use = use;
        this.with = with;
        this.craftableItems = craftableItems;
    }

    @Override
    public String getName() {
        return "Leather Shield";
    }

    @Override
    public Item getUse() {
        return use;
    }

    @Override
    public Item getWith() {
        return with;
    }

    @Override
    public CraftableItem[] getCraftableItems() {
        return craftableItems;
    }

    @Override
    public RequiredItem[] getRequiredItems(int index) {
        final CraftableItem craftableItem = craftableItems[index];
        final Item[] required = craftableItem.getRequiredItems();
        final ArrayList<RequiredItem> requiredItems = new ArrayList<>();
        requiredItems.add(new RequiredItem(getUse(), true));
        for (Item item : required){
            requiredItems.add(new RequiredItem(item, true));
        }
        return requiredItems.toArray(new RequiredItem[]{});
    }

    @Override
    public AnimationLoop getAnimationLoop() {
        return new AnimationLoop(new Animation(1249), 6);
    }

    @Override
    public SoundLoop getSoundLoop() {
        return new SoundLoop(new Sound(2587), 6);
    }

    public static ArrayList<Integer> shieldMaterialIdList
            = new ArrayList<>(Arrays.asList(OAK_SHIELD, WILLOW_SHIELD, MAGIC_SHIELD, YEW_SHIELD, REDWOOD_SHIELD, MAPLE_SHIELD));

    public static boolean isShieldMaterial(int id) {
        return  shieldMaterialIdList.contains(id);
    }
}
