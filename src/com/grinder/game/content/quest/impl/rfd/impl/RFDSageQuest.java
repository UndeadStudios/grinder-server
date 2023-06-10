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
public class RFDSageQuest extends Quest {
    private static final Item EGG = new Item(1944);
    private static final Item ENCHANTED_EGG = new Item(7544);

    private static final Item BUCKET_OF_MILK = new Item(1927);
    private static final Item ENCHANTED_MILK = new Item(7545);
    private static final Item POT_OF_FLOUR = new Item(1933);
    private static final Item ENCHANTED_POT_OF_FLOUR = new Item(2516);

    private static final Item CAKE_OF_GUIDANCE = new Item(7542);

    private static final Item[] ENCHANTED_INGREDIENTS = {
            ENCHANTED_MILK, ENCHANTED_EGG, ENCHANTED_POT_OF_FLOUR
    };

    public RFDSageQuest() {
        super("Lumbridge Sage", QuestType.SPECIAL, 1, 5);
    }

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{

                {"",
                        "You need a cake of guidance to protect the",
                        "Lumbridge Guide. Wizard Traiborn in the",
                        "Wizards' Tower can teach you how to make one."
                },
                {"",
                        "I should gather these ingredients in this order: Egg",},

                {"",
                        "Next, I should enchant the milk.",},

                {"",
                        "Next, I should enchant a pot of flour.",},
                {"",
                        "I should combine the ingredients to make the cake.",
                },
                {""}

        };
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{5081};
    }

    @Override
    public void getEndDialogue(Player player, int npcId) {
        int id = player.getDialogue().id();

        if (id == 14) {
            increaseStage(player);
            player.getPacketSender().sendInterfaceRemoval();
        } else if (id == 16) {
            boolean hasEgg = player.getInventory().contains(EGG);

            sendDialogue(player, hasEgg ? 20 : 18);
        } else if (id == 24) {
            player.getInventory().delete(EGG);
            player.getInventory().add(ENCHANTED_EGG);
            increaseStage(player);
            player.getPacketSender().sendInterfaceRemoval();
        } else if (id == 27) {
            boolean hasMilk = player.getInventory().contains(BUCKET_OF_MILK);

            sendDialogue(player, hasMilk ? 30 : 29);
        } else if (id == 31) {
            player.getInventory().delete(BUCKET_OF_MILK);
            player.getInventory().add(ENCHANTED_MILK);
            increaseStage(player);
            player.getPacketSender().sendInterfaceRemoval();
        } else if (id == 33) {
            boolean hasFlour = player.getInventory().contains(POT_OF_FLOUR);

            sendDialogue(player, hasFlour ? 35 : 34);
        } else if (id == 36) {
            player.getInventory().delete(POT_OF_FLOUR);
            player.getInventory().add(ENCHANTED_POT_OF_FLOUR);
            increaseStage(player);
            player.getPacketSender().sendInterfaceRemoval();
        } else if (id == 41) {
            player.getInventory().add(ENCHANTED_EGG);
            player.getInventory().add(ENCHANTED_MILK);
            player.getInventory().add(ENCHANTED_POT_OF_FLOUR);
            player.getPacketSender().sendInterfaceRemoval();
        }
    }

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if ((usedWith.getId() == ENCHANTED_MILK.getId() || usedWith.getId() == ENCHANTED_EGG.getId() || usedWith.getId() == ENCHANTED_POT_OF_FLOUR.getId()) && (use.getId() == ENCHANTED_MILK.getId() || use.getId() == ENCHANTED_EGG.getId() || use.getId() == ENCHANTED_POT_OF_FLOUR.getId())) {
            if (player.getInventory().contains(ENCHANTED_INGREDIENTS)) {
                player.getInventory().delete(ENCHANTED_INGREDIENTS);
                player.getInventory().add(CAKE_OF_GUIDANCE);
                player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleItemOnObjectInteraction(Player player, Item item, GameObject object) {
        if (item.getId() == CAKE_OF_GUIDANCE.getId()) {
            if (object.getId() == 12339) {
                if (getStage(player) == 4) {
                    increaseStage(player);
                    player.getInventory().delete(CAKE_OF_GUIDANCE);
                    QuestManager.complete(player, quest, new String[]{"200,500 Cooking XP", "375,500 Magic XP",
                            "Increased Culinaromancer's", "Chest Access",}, CAKE_OF_GUIDANCE.getId());
                    RecipeForDisaster.completeAllSubquest(player);
                    player.getSkillManager().addFixedDelayedExperience(Skill.COOKING, 200_500);
                    player.getSkillManager().addFixedDelayedExperience(Skill.MAGIC, 375_500);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (object.getId()) {
            case 12339:
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
        if (QuestManager.getStage(player, "Recipe for Disaster") == 0) {
            return false;
        }
        return true;
    }

    @Override
    public Position getTeleport() {
        return null;
    }
}
