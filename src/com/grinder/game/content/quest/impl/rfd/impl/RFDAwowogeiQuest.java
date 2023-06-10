package com.grinder.game.content.quest.impl.rfd.impl;

import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.content.quest.QuestType;
import com.grinder.game.content.quest.impl.rfd.RecipeForDisaster;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sounds;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class RFDAwowogeiQuest extends Quest {
    private static final Item RED_BANANA = new Item(7572);

    private static final Item SLICED_RED_BANANA = new Item(7574);
    private static final Item TCHIKI_MONKEY_NUTS = new Item(7573);
    private static final Item TCHIKI_NUT_PASTE = new Item(7575);
    private static final Item SNAKE_CORPSE = new Item(7576);
    private static final Item RAW_STUFFED_SNAKE = new Item(7577);
    private static final Item STUFFED_SNAKE = new Item(7579);

    private static final Item PESTLE_AND_MORTAR = new Item(233);
    private static final Item KNIFE = new Item(946);

    private static final Item[] REQUIRED = {
            TCHIKI_NUT_PASTE, SLICED_RED_BANANA, SNAKE_CORPSE
    };

    public RFDAwowogeiQuest() {
        super("Awowogei Quest", QuestType.SPECIAL, 1, 2);
    }

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{
                {
                        "",
                        "Awowogei needs his favourite food, but nobody",
                        " but nobody seems to know what it is.",
                },
                {"",
                        "I have found out that he likes a snake",
                        "stuffed with cut red banana and tchiki",
                        "nut paste.",
                        "",
                        "I should grind tchiki monkey nuts to make paste",
                        "I should cut red banana to make banana slices.",
                        "",
                        "The cook said he will cook it for me if I bring",
                        "him all the ingredients."},
                {""

                },
                {""},
        };
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{2975, 4626};
    }

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if ((use.getId() == KNIFE.getId() && usedWith.getId() == RED_BANANA.getId())
        || (use.getId() == RED_BANANA.getId() && usedWith.getId() == KNIFE.getId())) {
            player.getInventory().delete(RED_BANANA);
            player.getInventory().add(SLICED_RED_BANANA);
            player.getPacketSender().sendMessage("You slice the banana.");
            player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
            return true;
        }
        if ((use.getId() == PESTLE_AND_MORTAR.getId() && usedWith.getId() == TCHIKI_MONKEY_NUTS.getId())
                || (use.getId() == TCHIKI_MONKEY_NUTS.getId() && usedWith.getId() == PESTLE_AND_MORTAR.getId())) {
            player.getInventory().delete(TCHIKI_MONKEY_NUTS);
            player.getInventory().add(TCHIKI_NUT_PASTE);
            player.getPacketSender().sendMessage("You grind the monkey nuts.");
            player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
            return true;
        }
        if (usedWith.getId() == SNAKE_CORPSE.getId()) {
            if (player.getInventory().contains(REQUIRED)) {
                player.getInventory().delete(REQUIRED);
                player.getInventory().add(RAW_STUFFED_SNAKE);
                player.getPacketSender().sendMessage("You stuff the snake.");
                player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleItemOnObjectInteraction(Player player, Item item, GameObject object) {
        if (item.getId() == STUFFED_SNAKE.getId()) {
            if (object.getId() == 12347) {
                if (getStage(player) == 1) {
                    increaseStage(player);
                    player.getInventory().delete(STUFFED_SNAKE);
                    QuestManager.complete(player, quest, new String[]{
                            "150,000 Cooking XP",
                            "75,000 Agility XP",
                            "Increased access to the",
                            "Culinaromancer's Chest",
                    }, STUFFED_SNAKE.getId());
                    player.getSkillManager().addFixedDelayedExperience(Skill.COOKING, 150_000);
                    player.getSkillManager().addFixedDelayedExperience(Skill.AGILITY, 75_000);
                }

                RecipeForDisaster.completeAllSubquest(player);
            }
            return true;
        }
        return false;
    }

    @Override
    public void getEndDialogue(Player player, int npcId) {
        int id = player.getDialogue().id();

        if (id == 22) {
            increaseStage(player);
            player.getPacketSender().sendInterfaceRemoval();
        } else if (id == 23) {
            boolean hasSnake = player.getInventory().contains(RAW_STUFFED_SNAKE);

            sendDialogue(player, hasSnake ? 26 : 25);
        } else if (id == 26) {
            player.getInventory().delete(RAW_STUFFED_SNAKE);
            sendDialogue(player, 28);
        } else if (id == 28) {
            player.getInventory().add(STUFFED_SNAKE);
            sendDialogue(player, 30);
        }
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (object.getId()) {
            case 12347:
                QuestManager.sendQuestDisplay(player, quest);
                return true;
        }
        return false;
    }


    @Override
    public boolean hasRequirements(Player player) {
        if (!QuestManager.hasCompletedQuest(player, "Cook's Assistant")) {
            return false;
        }
        if (QuestManager.getStage(player, "Recipe for Disaster") == 3) {
            return true;
        }
        return false;
    }

    @Override
    public Position getTeleport() {
        return null;
    }
}
