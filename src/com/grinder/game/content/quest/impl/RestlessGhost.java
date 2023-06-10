package com.grinder.game.content.quest.impl;


import com.grinder.game.World;
import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestDialogueLoader;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

import java.util.concurrent.TimeUnit;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class RestlessGhost extends Quest {

    public RestlessGhost() {
        super("The Restless Ghost", false, 1, 5);
    }

    private static final int COFFIN = 2145;

    private static final Item GHOSTSPEAK_AMULET = new Item(552);

    private static final Item GHOST_SKULL = new Item(553);

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{

                {"", "<col=010080>A @dre@ghost<col=010080> is haunting @dre@Lumbridge graveyard. <col=010080>The priest of",
                        "<col=010080>the @dre@Lumbridge<col=010080> church of @dre@Saradomin<col=010080> wants you to find out",
                        "<col=010080>how to get rid of it.",},

                {"", "I have agreed to help father Lawrence to get rid of the ghost",
                        "He said I should go find father Uhrney in the graveyard", "south of the church.",},


                {"", "I spoke to father Lawrence and he gave me a ghostspeak",
                        "amulet, I should go speak to the ghost and find out", "why he's haunting the church.",},

                {"", "The ghost explained why he's been haunting the place",
                        "He believes someone has stolen his head from his body.",
                        "I agreed to find his skull and bring it back to him."},

                {"",
                        "I have found the skull of the ghost and have told him",
                        "He told me that all I need to do is put it in his coffin."
                },
                {""},

        };
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{5038, 923, 922};
    }

    @Override
    public boolean hasRequirements(Player player) {
        return true;
    }

    @Override
    public DialogueOptions getDialogueOptions(Player player) {
        return new DialogueOptions() {
            @Override
            public void handleOption(Player player, int option) {
                switch (option) {
                    case 3:
                        if (quest.getStage(player) == 0) {
                            QuestManager.increaseStage(player, quest);
                            QuestDialogueLoader.sendDialogue(player, quest, 2);
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

        if (id == 8) {
            increaseStage(player);
            player.getPacketSender().sendInterfaceRemoval();
        } else if (id == 41) {
            QuestDialogueLoader.sendDialogue(player, quest, 48);
            player.getInventory().add(GHOSTSPEAK_AMULET);
            QuestManager.increaseStage(player, quest);
        } else if (id == 14) {
            if (!player.getEquipment().contains(GHOSTSPEAK_AMULET)) {
                QuestDialogueLoader.sendDialogue(player, quest, 16);
            } else {
                QuestDialogueLoader.sendDialogue(player, quest, 18);
            }
        } else if (id == 29) {
            QuestManager.increaseStage(player, quest);
            player.getPacketSender().sendInterfaceRemoval();
        } else if (id == 49) {
            if (player.getInventory().contains(GHOST_SKULL)) {
                QuestDialogueLoader.sendDialogue(player, quest, 34);
                QuestManager.increaseStage(player, quest);
            } else {
                QuestDialogueLoader.sendDialogue(player, quest, 32);
            }
        } else if(id == 53) {
            player.getInventory().add(GHOSTSPEAK_AMULET);
            sendDialogue(player, 55);
        }
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        if (type == 1) {
            switch (object.getId()) {
                case 2146:
                    if (quest.getStage(player) == 3) {
                        if (!player.getInventory().contains(553)) {
                            player.getInventory().add(553, 1);
                            player.getPacketSender().sendMessage("You find an interesting skull.. Perhaps it's the one the ghost is looking for!");
                            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(553, 250)
                                    .setText("You find an interesting skull.. Perhaps it's the one", "the ghost is looking for!").start(player);
                        } else {
                            player.sendMessage("You don't find anything of interest.");
                        }
                        return true;
                    }
                    break;
                case COFFIN:
                    if (isCompleted(player)) {
                        player.getPacketSender()
                                .sendMessage("There's a complete skeleton in here. There's no point in disturbing it.");
                    } else if (quest.getStage(player) == 0) {
                        player.getPacketSender().sendMessage(
                                "You search the coffin and find some human remains. There's no point in disturbing them.");
                    } else {
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_COFFIN_USE, 60, TimeUnit.SECONDS, false, false)) {
                            return false;
                        }
                        EntityExtKt.markTime(player, Attribute.LAST_COFFIN_USE);
                        NPC ghost = NPCFactory.INSTANCE.create(922, new Position(3249, 3194));

                        World.getNpcAddQueue().add(ghost);

                        TaskManager.submit(new Task(100) {
                            @Override
                            protected void execute() {
                                World.getNpcRemoveQueue().add(ghost);
                                stop();
                            }
                        });
                    }
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleItemOnObjectInteraction(Player player, Item item, GameObject object) {
        if (item.getId() == GHOST_SKULL.getId()) {
            if (object.getId() == COFFIN) {
                if (quest.getStage(player) == 4) {
                    QuestManager.increaseStage(player, quest);
                    QuestManager.complete(player, quest, new String[]{"75,000 Prayer XP"}, GHOST_SKULL.getId());
                    player.getSkillManager().addFixedDelayedExperience(Skill.PRAYER, 75_000);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Position getTeleport() {
        return null;
    }
}
