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
import com.grinder.util.ItemID;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Weaving {

    private static final Animation ANIMATION = new Animation(894);

    public static void showWeavingInterface(Player player){
        Optional<CreationMenu> menu = Optional.empty();

        CreationMenu.CreationMenuAction action = (index, item, amount) -> {
            WeavingEnum weaving = WeavingEnum.forReward(item);
            if (weaving != null) {
                SkillUtil.startSkillable(
                        player, new ItemCreationSkillable(
                                Arrays.asList(new RequiredItem[]{new RequiredItem(weaving.getItemRequired(), true)}),
                                weaving.getRewardItem(),
                                amount,
                                new AnimationLoop(ANIMATION, 5),
                                weaving.getLevelRequired(),
                                weaving.getExperienceGained(),
                                Skill.CRAFTING, null, 3));
                player.getPacketSender().sendInterfaceRemoval();
            }
        };

        menu = Optional.of(new QuardrupleItemCreationMenu(player,
                ItemID.BASKET, ItemID.EMPTY_SACK,
                ItemID.DRIFT_NET, ItemID.STRIP_OF_CLOTH,
                "How many do you wish to make?", action));

        if (menu.isPresent()) {
            player.setCreationMenu(menu);
            menu.get().open();
        }
    }

    private enum WeavingEnum {

        CLOTH(new Item(ItemID.BALL_OF_WOOL, 4), new Item(ItemID.STRIP_OF_CLOTH, 1), 10, 12),
        EMPTY_SACK(new Item(ItemID.JUTE_FIBRE, 4), new Item(ItemID.EMPTY_SACK, 1), 21, 38),
        DRIFT_NET(new Item(ItemID.JUTE_FIBRE, 2), new Item(ItemID.DRIFT_NET, 1), 26, 55),
        BASKET(new Item(ItemID.WILLOW_BRANCH, 6), new Item(ItemID.BASKET, 1), 36, 56);

        private Item itemRequired;
        private Item rewardItem;
        private int levelRequired;
        private int experienceGained;

        private static final Map<Integer, WeavingEnum> WEAVING = new HashMap<>();

        static {
            Arrays.stream(values()).forEach(v -> WEAVING.put(v.getRewardItem().getId(), v));
        }
        WeavingEnum(Item itemRequired, Item rewardItem, int levelRequired, int experienceGained) {
            this.itemRequired = itemRequired;
            this.rewardItem = rewardItem;
            this.levelRequired = levelRequired;
            this.experienceGained = experienceGained;
        }

        public Item getItemRequired() {
            return itemRequired;
        }

        public Item getRewardItem() {
            return rewardItem;
        }

        public int getLevelRequired() {
            return experienceGained;
        }

        public int getExperienceGained() {
            return experienceGained;
        }

        public static WeavingEnum forReward(int id) {
            return WEAVING.get(id);
        }
    }

}
