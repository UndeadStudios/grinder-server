package com.grinder.game.content.quest.impl;

import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestDialogueLoader;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.game.model.item.Item;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class DoricsQuest extends Quest {

    public DoricsQuest() {
        super("Doric's Quest", false, 3, 2);
    }

    private static final Item CLAY = new Item(434, 6);

    private static final Item COPPER_ORE = new Item(436, 4);

    private static final Item IRON_ORE = new Item(440, 2);

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{{"", "@dre@Doric the dwarf<col=010080> is happy to let you use his anvils",
                "<col=010080>but first he would like you to run an errand for him."},

                {"", "Doric has requested that I bring him:",

                        QuestManager.hasItem(player, CLAY, "6 clay"),
                        QuestManager.hasItem(player, COPPER_ORE, "4 copper ore"),
                        QuestManager.hasItem(player, IRON_ORE, "2 iron ore"), "",

                },

                {"I have brough the items Doric has requested. He has",
                        "rewarded me and allowed me to use his anvil."},};
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{3893};
    }

    @Override
    public boolean hasRequirements(Player player) {
        return true;
    }

    @Override
    public DialogueOptions getDialogueOptions(Player player) {
        DialogueOptions option = null;
        int dialogue = player.getDialogue().id();
        switch (getStage(player)) {
            case 0:
                option = new DialogueOptions() {
                    @Override
                    public void handleOption(Player player, int option) {
                        switch (option) {
                            case 1:
                                if (quest.getStage(player) == 0) {
                                    if (dialogue == 0) {
                                        QuestDialogueLoader.sendDialogue(player, quest, 2);
                                    } else if (dialogue == 2) {
                                        QuestManager.increaseStage(player, quest);
                                        QuestDialogueLoader.sendDialogue(player, quest, 5);
                                    }
                                }
                                break;
                            case 2:
                                if (quest.getStage(player) == 1) {
                                    if (dialogue == 5) {
                                        QuestDialogueLoader.sendDialogue(player, quest, 8);
                                    }
                                }
                                break;
                        }
                    }
                };
                break;
        }
        return option;
    }

    @Override
    public boolean hasStartDialogue(Player player, int npcId) {
        return false;
    }

    @Override
    public void getEndDialogue(Player player, int npcId) {
        int id = player.getDialogue().id();

        if(id == 3) {
            increaseStage(player);
            sendDialogue(player, 5);
        } else
        if (id == 14) {
            if (player.getInventory().contains(new Item[]{CLAY, COPPER_ORE, IRON_ORE})) {
                player.getInventory().delete(new Item[]{CLAY, COPPER_ORE, IRON_ORE});
                QuestManager.increaseStage(player, quest);
                QuestManager.complete(player, quest,
                        new String[]{"5,000 Mining XP", "Access to Doric's anvil!"}, 1269);
                player.getSkillManager().addFixedDelayedExperience(Skill.MINING, 5_000);
            }
        } else if (id == 9) {
            QuestDialogueLoader.sendDialogue(player, quest,
                    player.getInventory().contains(new Item[]{CLAY, COPPER_ORE, IRON_ORE}) ? 12 : 11);
        }
    }

    @Override
    public Position getTeleport() {
        return null;
    }
}
