package com.grinder.game.content.skill.skillable.impl.crafting;

import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.ItemCreationSkillable;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.AnimationLoop;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.menu.CreationMenu;
import com.grinder.game.model.interfaces.menu.impl.FiveItemCreationMenu;
import com.grinder.game.model.interfaces.menu.impl.QuardrupleItemCreationMenu;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.RequiredItem;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.model.sound.SoundLoop;
import com.grinder.util.ItemID;
import kotlin.Pair;

import java.util.*;

public class SpinningWheel {

    private static final int ANIMATION = 894;
    private static final Sound SOUND = new Sound(2725);

    public static void openInterface(Player player) {
        Optional<CreationMenu> menu = Optional.empty();

        CreationMenu.CreationMenuAction action = (index, item, amount) -> {
            SpinningItems spinningItem =  SpinningItems.forReward(item);
            RequiredItem[] requiredItems = {new RequiredItem(new Item(spinningItem.getItemUsed(), 1), true)};
            SkillUtil.startSkillable(
                    player, new ItemCreationSkillable(
                            Arrays.asList(requiredItems),
                            new Item(spinningItem.getReward(), 1),
                            amount,
                            new AnimationLoop(new Animation(ANIMATION), 5),
                            new SoundLoop(SOUND, 4),
                            spinningItem.getLevelRequired(),
                            (int)spinningItem.getExperience(),
                            Skill.CRAFTING, null, 3)
                    );
                player.getPacketSender().sendInterfaceRemoval();

        };

        menu = Optional.of(new FiveItemCreationMenu(player, "What would you like to make?", action,
                new Pair<>(ItemID.BALL_OF_WOOL, "Ball of Wool"),
                new Pair<>(ItemID.BOW_STRING, "Bow String"),
                new Pair<>(ItemID.CROSSBOW_STRING, "Crossbow String"),
                new Pair<>(ItemID.MAGIC_STRING, "Magic String"),
                new Pair<>(ItemID.ROPE, "Rope") ));

        if (menu.isPresent()) {
            player.setCreationMenu(menu);
            menu.get().open();
        }
    }

    private enum SpinningItems {
        WOOL(ItemID.WOOL, ItemID.BALL_OF_WOOL, 1, 2.5),
        FLAX(ItemID.FLAX, ItemID.BOW_STRING, 1, 15.0),
        SINEW(ItemID.SINEW, ItemID.CROSSBOW_STRING, 10, 15.0),
        MAGIC_ROOTS(ItemID.MAGIC_ROOTS, ItemID.MAGIC_STRING, 19, 30.0),
        YAK_HAIR(ItemID.HAIR, ItemID.ROPE, 30, 25.0);

        private int itemUsed;
        private int reward;
        private int levelRequired;
        private double experience;

        private static final Map<Integer, SpinningItems> SPINNING_ITEMS = new HashMap<>();

        static {
            Arrays.stream(values()).forEach(v -> SPINNING_ITEMS.put(v.getReward(), v));
        }

        SpinningItems(int itemUsed, int reward, int levelRequired, double experience) {
            this.itemUsed = itemUsed;
            this.reward = reward;
            this.levelRequired = levelRequired;
            this.experience = experience;
        }

        public int getItemUsed() {
            return itemUsed;
        }

        public int getReward() {
            return reward;
        }

        public int getLevelRequired() {
            return levelRequired;
        }

        public double getExperience() {
            return experience;
        }

        public static SpinningItems forReward(int id) {
            return SPINNING_ITEMS.get(id);
        }

    }
}
