package com.grinder.game.content.quest.impl;

import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestDialogueLoader;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.NpcID;

import java.util.Optional;


/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class RuneMysteriesQuest extends Quest {

    public RuneMysteriesQuest() {
        super("Rune Mysteries", false, 1, 4);
    }

    private static final Item AIR_TALISMAN = new Item(1438);

    private static final Item RESEARCH_PACKAGE = new Item(290);

    private static final Item NOTES = new Item(291);

    private static void teleportPlayer(Player player, Optional<NPC> optional) {
        optional.get().performAnimation(new Animation(1818));
        optional.get().performGraphic(new Graphic(343));
        player.getPacketSender().sendInterfaceRemoval();
        player.BLOCK_ALL_BUT_TALKING = true;
        TaskManager.submit(new Task(2) {

            int tick = 0;

            @Override
            protected void execute() {
                if (tick == 0) {
                    player.performAnimation(new Animation(1816));
                    player.performGraphic(new Graphic(342));
                }
                if (tick == 1) {
                    player.getPacketSender().sendAnimationReset();
                    player.moveTo(new Position(3113, 9561));
                    player.BLOCK_ALL_BUT_TALKING = false;
                    stop();
                }
                tick++;
            }
        });
    }

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{

                {"<col=010080>Recent research at the @dre@Wizards' Tower<col=010080> has found a way",
                        "<col=010080>to create @dre@Runes<col=010080> for the first time in centuries.",
                        "<col=010080>Assist the head wizard @dre@Sedridor in his research and", "<col=010080>he too may teach you these secrets!"},
                {"", "@dre@Duke Horacio <col=010080>gave me an air talism and asked me to hand it to",
                        "@dre@Sedridor<col=010080> who can be found at the @dre@Wizards Tower",},

                {"", "<col=010080>I have given the @dre@Air Talisman <col=010080>to @dre@Sedridor",
                        "<col=010080>who gave me a @dre@research package</col> to deliver to @dre@Aubury</col>."},


                {"", "@dre@Aubury<col=010080> gave me some notes to bring back to to@dre@ Sedridor</col>."},
                {"",},

        };
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{815, 5034, 11435};
    }

    @Override
    public boolean hasRequirements(Player player) {
        return true;
    }

    @Override
    public DialogueOptions getDialogueOptions(Player player) {
        int stage = getStage(player);
        int dialogue = player.getDialogue().id();
        return new DialogueOptions() {

            @Override
            public void handleOption(Player player, int option) {
                switch (option) {
                    case 1:
                        if (stage == 0) {
                            if (dialogue == 0) {
                                QuestManager.increaseStage(player, quest);
                                QuestDialogueLoader.sendDialogue(player, quest, 5);
                            }
                        } else if (stage == 1) {
                            if (dialogue == 21) {
                                QuestManager.increaseStage(player, quest);
                                QuestDialogueLoader.sendDialogue(player, quest, 28);
                            }
                        } else if (stage == 3) {
                            if (dialogue == 48) {
                                teleportPlayer(player, QuestManager.getNpcById(11435));
                            }
                        }
                        break;

                    case 2:
                        if (stage == 0) {
                            if (dialogue == 0) {
                                QuestDialogueLoader.sendDialogue(player, quest, 9);
                            }
                        }
                        player.getPacketSender().sendInterfaceRemoval();
                        break;

                    case 3:
                        if (stage == 1) {
                            if (dialogue == 10) {
                                QuestDialogueLoader.sendDialogue(player, quest, 14);
                            }
                        } else if (stage == 3) {
                            if (dialogue == 34) {
                                QuestDialogueLoader.sendDialogue(player, quest, 38);
                            }
                        }
                        break;
                }
            }
        };
    }

    @Override
    public boolean hasStartDialogue(Player player, int npcId) {
        return false;
    }

    @Override
    public void getEndDialogue(Player player, int npcId) {
        int id = player.getDialogue().id();

        if (id == 6) {
            increaseStage(player);
            if (player.getInventory().countFreeSlots() == 0) {
                ItemOnGroundManager.register(player, AIR_TALISMAN);
            } else {
                player.getInventory().add(AIR_TALISMAN);
            }
            QuestDialogueLoader.sendDialogue(player, quest, 8);
        } else if (id == 19) {
            if (player.getInventory().contains(AIR_TALISMAN)) {
                player.getInventory().delete(AIR_TALISMAN);
                QuestManager.increaseStage(player, quest);
                QuestDialogueLoader.sendDialogue(player, quest, 21);
                player.getInventory().add(RESEARCH_PACKAGE);
            } else {
                new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.ARCHMAGE_SEDRIDOR_11433)
                        .setText("You don't seem to have the talisman I am looking for...").add(DialogueType.NPC_STATEMENT)
                        .setText("Come back later when you have it!")
                        .add(DialogueType.PLAYER_STATEMENT)
                        .setText("I will be back soon!")
                        .start(player);
            }
        } else if (id == 30) {
            QuestDialogueLoader.sendDialogue(player, quest, 32);
        } else if (id == 34) {
            boolean hasPackage = player.getInventory().contains(RESEARCH_PACKAGE);

            sendDialogue(player, hasPackage ? 38 : 36);
        } else if (id == 39) {
            if (player.getInventory().contains(RESEARCH_PACKAGE)) {
                player.getInventory().delete(RESEARCH_PACKAGE);
                QuestManager.increaseStage(player, quest);
                player.getInventory().add(NOTES);
                QuestDialogueLoader.sendDialogue(player, quest, 41);
            }
        } else if (id == 44) {
            QuestDialogueLoader.sendDialogue(player, quest, 46);
        } else if (id == 47) {
            teleportPlayer(player, QuestManager.getNpcById(11435));
        } else if (id == 50) {
            if (player.getInventory().contains(NOTES)) {
                QuestDialogueLoader.sendDialogue(player, quest, 52);
            } else {
                sendDialogue(player, 96);
            }
        } else if (id == 76) {
            if (player.getInventory().contains(NOTES)) {
                player.getInventory().delete(NOTES);
                QuestDialogueLoader.sendDialogue(player, quest, 78);
            }

        } else if (id == 79) {
            player.getInventory().add(AIR_TALISMAN);
            QuestManager.increaseStage(player, quest);
            QuestManager.complete(player, quest, new String[]{"Access to the Runecrafting skill.", "Air talisman.",
                    "35,000 Runecrafting experience"}, AIR_TALISMAN.getId());
            player.getSkillManager().addFixedDelayedExperience(Skill.RUNECRAFTING, 35_000);
        } else if (id == 83) {
            player.getInventory().add(AIR_TALISMAN);
            sendDialogue(player, 85);
        } else if (id == 88) {
            player.getInventory().add(RESEARCH_PACKAGE);
            sendDialogue(player, 90);
        } else if (id == 93) {
            player.getInventory().add(NOTES);
            sendDialogue(player, 95);
        }
    }


    @Override
    public Position getTeleport() {
        return null;
    }
}