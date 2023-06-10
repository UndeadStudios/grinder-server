package com.grinder.game.content.skill.skillable.impl.crafting;

import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.ItemCreationSkillable;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.AnimationLoop;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.menu.CreationMenu;
import com.grinder.game.model.interfaces.menu.impl.QuardrupleItemCreationMenu;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.RequiredItem;
import com.grinder.game.model.sound.SoundLoop;
import com.grinder.util.ItemID;
import kotlin.Pair;

import java.util.*;

public class Pottery {

    private static final int CLAY = 434;
    private static final Animation WHEEL_ANIMATION = new Animation(883);
    private static final Animation OVEN_ANIMATION = new Animation(1317);

    private static final int[][] WATER_ITEMS = {
            {ItemID.BUCKET_OF_WATER, ItemID.BUCKET},
            {ItemID.JUG_OF_WATER, ItemID.JUG},
            {ItemID.BOWL_OF_WATER, ItemID.BOWL}
    };

    public static boolean makeSoftClay(Player player, int itemUsed, int itemUsedWith) {
        int waterItem;
        if (itemUsed == CLAY) {
            waterItem = itemUsedWith;
        } else {
            waterItem = itemUsed;
        }

        for (int i = 0; i < WATER_ITEMS.length; i++) {
            if (WATER_ITEMS[i][0] == waterItem) {
                player.getInventory().delete(ItemID.CLAY, 1);
                player.getInventory().add(ItemID.SOFT_CLAY, 1);
                player.getInventory().delete(WATER_ITEMS[i][0], 1);
                player.getInventory().add(WATER_ITEMS[i][1], 1);
            }
        }

        return false;
    }

    public static void openPottersWheelInterface(Player player) {
        Optional<CreationMenu> menu = Optional.empty();

        CreationMenu.CreationMenuAction action = (index, item, amount) -> {
            PotteryEnum pottery = PotteryEnum.forReward(item);
            if (pottery != null) {
                SkillUtil.startSkillable(
                        player, new ItemCreationSkillable(
                                Arrays.asList(new RequiredItem[]{new RequiredItem(ItemID.SOFT_CLAY, true)}),
                                new Item(pottery.getUnfired(), 1),
                                amount,
                                new AnimationLoop(WHEEL_ANIMATION, 6),
                                pottery.getLevelRequired(),
                                pottery.getWheelExperienceGain(),
                                Skill.CRAFTING, null, 5));
                player.getPacketSender().sendInterfaceRemoval();
            }
        };

        menu = Optional.of(new QuardrupleItemCreationMenu(player,
                ItemID.UNFIRED_POT, ItemID.UNFIRED_PIE_DISH,
                ItemID.UNFIRED_BOWL, ItemID.UNFIRED_PLANT_POT,
                "What would you like to fire in the oven?", action));

        if (menu.isPresent()) {
            player.setCreationMenu(menu);
            menu.get().open();
        }
    }

    public static void openPotteryOvenInterface(Player player) {
        Optional<CreationMenu> menu = Optional.empty();

        CreationMenu.CreationMenuAction action = (index, item, amount) -> {
            PotteryEnum pottery = PotteryEnum.forReward(item);
            if (pottery != null) {
                SkillUtil.startSkillable(
                        player, new ItemCreationSkillable(
                                Arrays.asList(new RequiredItem[]{new RequiredItem(pottery.getUnfired(), true)}),
                                new Item(pottery.getFired(), 1),
                                amount,
                                new AnimationLoop(OVEN_ANIMATION, 5),
                                pottery.getLevelRequired(),
                                pottery.getOvenExperienceGain(),
                                Skill.CRAFTING, null, 3));
                player.getPacketSender().sendInterfaceRemoval();
            }
        };

        menu = Optional.of(new QuardrupleItemCreationMenu(player,
                ItemID.UNFIRED_POT, ItemID.UNFIRED_PIE_DISH,
                ItemID.UNFIRED_BOWL, ItemID.UNFIRED_PLANT_POT,
                "What would you like to make?", action));

        if (menu.isPresent()) {
            player.setCreationMenu(menu);
            menu.get().open();
        }
    }

    private enum PotteryEnum {

        POT(ItemID.UNFIRED_POT, ItemID.POT, 1, 6, 6),
        PIE_DISH(ItemID.UNFIRED_PIE_DISH, ItemID.PIE_DISH, 7, 15, 10),
        BOWL(ItemID.UNFIRED_BOWL, ItemID.BOWL, 8, 18, 15),
        EMPTY_PLANT_POT(ItemID.UNFIRED_PLANT_POT, ItemID.PLANT_POT, 19, 20, 17);

        private int unfired;
        private int fired;
        private int wheelExperienceGain;
        private int ovenExperienceGain;
        private int levelRequired;

        private static final Map<Integer, PotteryEnum> POTTERY = new HashMap<>();

        static {
            Arrays.stream(values()).forEach(v -> POTTERY.put(v.getUnfired(), v));
        }

        PotteryEnum(int unfired, int fired, int levelRequired, int wheelExperienceGain, int ovenExperienceGain) {
            this.unfired = unfired;
            this.fired = fired;
            this.wheelExperienceGain = wheelExperienceGain;
            this.ovenExperienceGain = ovenExperienceGain;
            this.levelRequired = levelRequired;
        }

        public int getUnfired() {
            return unfired;
        }

        public int getFired() {
            return fired;
        }

        public int getLevelRequired() {
            return levelRequired;
        }

        public int getWheelExperienceGain() {
            return wheelExperienceGain;
        }

        public int getOvenExperienceGain() {
            return ovenExperienceGain;
        }

        public static PotteryEnum forReward(int id) {
            return POTTERY.get(id);
        }
    }

}
