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
public class RFDDwarfQuest extends Quest {
    private static final Item ASGOLDIAN_ALE = new Item(7508);

    private static final Item BUCKET_OF_MILK = new Item(1927);
    private static final Item BOWL_OF_WATER = new Item(1921);
    private static final Item EGG = new Item(1944);
    private static final Item POT_OF_FLOUR = new Item(1933);

    private static final Item DWARVEN_ROCK_CAKE = new Item(7510);

    private static final Item[] INGREDIENTS = {
            BUCKET_OF_MILK, BOWL_OF_WATER, EGG, POT_OF_FLOUR
    };

    public RFDDwarfQuest() {
        super("Dwarf Quest", QuestType.SPECIAL, 1, 4);
    }

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{
                {"",
                        "To save the dwarf, you will need a special dwarven rock cake. ",
                        "Only his father in the tunnel under White Wolf Mountain knows",
                        "how to make one."
                },
                {"",
                        "I found someone who can cook the cake. He said he will",
                        "sell me the secret to Dwarven rock cake for 100 gold..",
                },
                {"",
                        "He is willing to cook it. He said to bring him:",
                        "",
                        QuestManager.hasItem(player, BUCKET_OF_MILK, "Bucket of milk"),
                        QuestManager.hasItem(player, BOWL_OF_WATER, "Bowl of water"),
                        QuestManager.hasItem(player, EGG, "Egg"),
                        QuestManager.hasItem(player, POT_OF_FLOUR, "Pot of flour"),
                },
                {"",
                        "I have the Dwarven rock cake, I need to go back",
                        "to the feast..",
                },
                {""},
        };
    }

    @Override
    public void getEndDialogue(Player player, int npcId) {
        int id = player.getDialogue().id();

        if (id == 0) {
            boolean hasAle = player.getInventory().contains(ASGOLDIAN_ALE);

            sendDialogue(player, hasAle ? 2 : 11);

            if (hasAle) {
                player.getInventory().delete(ASGOLDIAN_ALE);
                player.getQuest().rohakDrunkness++;

                if (player.getQuest().rohakDrunkness == 3) {
                    increaseStage(player);
                }
            }
        } else if (id == 23) {
            if (player.getInventory().contains(995, 100)) {
                player.getInventory().delete(995, 100);
                sendDialogue(player, 25);
                increaseStage(player);
            } else {
                sendDialogue(player, 26);
            }
        } else if (id == 27) {
            boolean hasAll = player.getInventory().contains(INGREDIENTS);

            sendDialogue(player, hasAll ? 31 : 30);

            if (hasAll) {
                player.getInventory().delete(INGREDIENTS);
            }
        } else if (id == 33) {
            increaseStage(player);

            player.getInventory().add(DWARVEN_ROCK_CAKE);

            player.getPacketSender().sendInterfaceRemoval();
        } else if (id == 35) {
            boolean hasCoins = player.getInventory().contains(995, 1_000_000);

            sendDialogue(player, hasCoins ? 38 : 37);
        } else if (id == 38) {
            player.getInventory().delete(995, 1_000_000);
            player.getInventory().add(DWARVEN_ROCK_CAKE);
            player.getPacketSender().sendInterfaceRemoval();
            player.getPacketSender().sendMessage("You purchase a Dwarven rock cake.");
        } else if(id == 40) {
            boolean hasCake = player.getInventory().contains(DWARVEN_ROCK_CAKE);

            sendDialogue(player, hasCake ? 43 : 42);
        } else if(id == 44) {
            player.getInventory().add(DWARVEN_ROCK_CAKE);
            sendDialogue(player, 46);
        }
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (object.getId()) {
            case 12330:
                QuestManager.sendQuestDisplay(player, quest);
                return true;
        }
        return false;
    }

    @Override
    public boolean handleItemOnObjectInteraction(Player player, Item item, GameObject object) {
        if (object.getId() == 12330) {
            if (item.getId() == DWARVEN_ROCK_CAKE.getId()) {
                if (getStage(player) == 3) {
                    increaseStage(player);
                    player.getInventory().delete(DWARVEN_ROCK_CAKE);
                   RecipeForDisaster.completeAllSubquest(player);
                    QuestManager.complete(player, quest, new String[]{"225,000 Cooking XP","200,000 Slayer skill XP","Increased Culinaromancer's","Chest Access"}, 7510);
                    player.getSkillManager().addFixedDelayedExperience(Skill.COOKING, 225_000);
                    player.getSkillManager().addFixedDelayedExperience(Skill.SLAYER, 200_000);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{4811};
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
