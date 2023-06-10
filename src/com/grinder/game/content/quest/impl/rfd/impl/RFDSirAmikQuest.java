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

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class RFDSirAmikQuest extends Quest {

    private static final Item EVIL_CHICKENS_EGG = new Item(7477);
    private static final Item POT_OF_CORNFLOUR = new Item(7468);
    private static final Item VANILLA_POD = new Item(7465);
    private static final Item CINNAMON = new Item(7472);
    private static final Item BRULEE_SUPREME = new Item(7476);

    private static final Item[] ALL_INGREDIENTS = {
            EVIL_CHICKENS_EGG, POT_OF_CORNFLOUR, VANILLA_POD, CINNAMON
    };

    public RFDSirAmikQuest() {
        super("Sir Amik", QuestType.SPECIAL, 1, 3);
    }

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{

                {"",
                        "Sir Amik Varze requires a crème brulee supreme.",
                        "Talk to the Cook to learn how to make one."
                },
                {
                        "",
                        "The cook said he needs the following ingredients:",
                        "",
                        QuestManager.hasItem(player, EVIL_CHICKENS_EGG, "Evil Chicken's egg"),
                        QuestManager.hasItem(player, POT_OF_CORNFLOUR, "Pot of cornflour"),
                        QuestManager.hasItem(player, VANILLA_POD, "Vanilla pod"),
                        QuestManager.hasItem(player, CINNAMON, "Cinnamon"),
                },
                {"",
                        "I have the creme brulee, I should go give it to Sir",
                        "Amik Varze.",
                },
                {""}

        };
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{4626};
    }

    @Override
    public void getEndDialogue(Player player, int npcId) {
        int id = player.getDialogue().id();

        if (id == 19) {
            increaseStage(player);
            player.getPacketSender().sendInterfaceRemoval();
        } else if (id == 21) {
            boolean hasAll = player.getInventory().contains(ALL_INGREDIENTS);

            sendDialogue(player, hasAll ? 24 : 23);
        } else if (id == 24) {
            player.getInventory().delete(ALL_INGREDIENTS);
            sendDialogue(player, 25);
        } else if (id == 27) {
            player.getInventory().add(BRULEE_SUPREME);
            player.getPacketSender().sendInterfaceRemoval();
            increaseStage(player);
        } else if(id == 28) {
            boolean has = player.getInventory().contains(BRULEE_SUPREME);

            sendDialogue(player, has ? 30 : 31);
        } else if(id == 32) {
            player.getInventory().add(BRULEE_SUPREME);
            sendDialogue(player, 34);
        }
    }

    @Override
    public boolean handleItemOnObjectInteraction(Player player, Item item, GameObject object) {
        if (item.getId() == BRULEE_SUPREME.getId()) {
            if (object.getId() == 12345) {
                if (getStage(player) == 2) {
                    increaseStage(player);
                    QuestManager.complete(player, quest, new String[]{
                            "75,000 Cooking XP",
                            "75,000 Hitpoints XP",
                            "Increased Culinaromancer's",
                            "Chest Access",
                    }, BRULEE_SUPREME.getId());
                    player.getSkillManager().addFixedDelayedExperience(Skill.COOKING, 75_000);
                    player.getSkillManager().addFixedDelayedExperience(Skill.HITPOINTS, 75_000);
                    RecipeForDisaster.completeAllSubquest(player);
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (object.getId()) {
            case 12345:
                QuestManager.sendQuestDisplay(player, quest);
                return true;
        }
        return false;
    }
    @Override
    public boolean hasRequirements(Player player) {
        if(!QuestManager.hasCompletedQuest(player, "Cook's Assistant")) {
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
