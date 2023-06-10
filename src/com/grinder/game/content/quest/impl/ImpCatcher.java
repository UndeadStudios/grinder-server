package com.grinder.game.content.quest.impl;

import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestDialogueLoader;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.util.ItemID;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class ImpCatcher extends Quest {

    public ImpCatcher() {
        super("Imp Catcher", false, 1, 2);
    }

    private static final Item RED_BEAD = new Item(1470);

    private static final Item YELLOW_BEAD = new Item(1472);

    private static final Item BLACK_BEAD = new Item(1474);

    private static final Item WHITE_BEAD = new Item(1476);

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{{"@dre@The Wizard Grayzag<col=010080> has summoned hundreds of",
                "<col=010080>little @dre@imps. <col=010080>They have stolen a lot of things",
                "<col=010080>belonging to the @dre@Wizard Mizgog<col=010080> including his", "@dre@magic beads</col>."},

                {"", "I have agreed to help Wizard Mizgog retrieve", " his magicial beads. I still need:", "",
                        QuestManager.hasItem(player, RED_BEAD, "1 red bead"),
                        QuestManager.hasItem(player, YELLOW_BEAD, "1 yellow bead"),
                        QuestManager.hasItem(player, WHITE_BEAD, "1 white bead"),
                        QuestManager.hasItem(player, BLACK_BEAD, "1 black bead"),},

                {"", "I have helped Wizard Mizgog and he has rewarded me."},

        };
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{7746};
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
                                        QuestDialogueLoader.sendDialogue(player, quest, 3);
                                    } else if (dialogue == 3) {
                                        QuestManager.increaseStage(player, quest);
                                        QuestDialogueLoader.sendDialogue(player, quest, 10);
                                    }
                                }
                                break;
                            case 2:
                                QuestDialogueLoader.sendDialogue(player, quest, 5);
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
        if (id == 8) {
            increaseStage(player);
            sendDialogue(player, 10);
        } else if (id == 17) {
            if (player.getInventory().contains(new Item[]{RED_BEAD, YELLOW_BEAD, WHITE_BEAD, BLACK_BEAD})) {
                player.getInventory().delete(new Item[]{RED_BEAD, YELLOW_BEAD, WHITE_BEAD, BLACK_BEAD});
                QuestManager.increaseStage(player, quest);
                QuestManager.complete(player, quest,
                        new String[]{"15,000 Magic XP", "Amulet of accuracy."}, 1478);
                player.getSkillManager().addFixedDelayedExperience(Skill.MAGIC, 15_000);
                ItemContainerUtil.addOrDrop(player.getInventory(),player, new Item(ItemID.AMULET_OF_ACCURACY, 1));
            }
        } else if (id == 13) {
            if (player.getInventory().contains(new Item[]{RED_BEAD, YELLOW_BEAD, WHITE_BEAD, BLACK_BEAD})) {
                QuestDialogueLoader.sendDialogue(player, quest, 15);
            } else {
                sendDialogue(player, 20);
            }
        }
    }

    @Override
    public Position getTeleport() {
        return null;
    }
}
