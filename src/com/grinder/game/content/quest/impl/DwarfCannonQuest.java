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
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.NpcID;

import java.util.concurrent.TimeUnit;

/**
 * @author Dexter Morgan
 * <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class DwarfCannonQuest extends Quest {

    private static final Item HAMMER = new Item(2347);

    private static final Item RAILING = new Item(14);

    private static final Item RAILINGS = new Item(14, 6);

    private static final Item DWARF_REMAINS = new Item(3396);

    private static final Item AMMO_MOULD = new Item(4);

    private static final Item NOTES = new Item(3);

    private static final Position LOLLK_POSITION = new Position(2570, 9851);

    private static final Animation FIXING = new Animation(898);

    private void fixRailing(Player p, GameObject object) {
        if (!p.getInventory().contains(HAMMER)) {
            p.sendMessage("You need a hammer to fix a railing.");
            return;
        }

        if (!p.getInventory().contains(RAILING)) {
            p.sendMessage("You need a railing to replace it with.");
            return;
        }

        if (getStage(p) == 0) {
            p.sendMessage("This railing seems fine..");
            return;
        }

        if (p.getQuest().fixedRailings.contains(object.getPosition())
            || QuestManager.hasCompletedQuest(p, "Dwarf Cannon")) {
            p.sendMessage("This railing seems fine..");
            return;
        }

        p.getQuest().fixedRailings.add(object.getPosition());

        p.performAnimation(FIXING);

        p.getInventory().delete(RAILING);

        p.sendMessage("You fix the railing.");

        if (p.getQuest().fixedRailings.size() == 6) {
            p.say("I fixed all the railings now.");
            p.sendMessage("You fixed all the railings. Return to Captain Lawgof.");
        }
    }

    private void summonLollk(Player p) {

        NPC lollk = NPCFactory.INSTANCE.create(5190, LOLLK_POSITION);


        if (!EntityExtKt.passedTime(p, Attribute.LAST_COFFIN_USE, 60, TimeUnit.SECONDS, false, false)) {
            p.sendMessage("You search the crate and find nothing.");
            return;
        }
        EntityExtKt.markTime(p, Attribute.LAST_COFFIN_USE);

        World.getNpcAddQueue().add(lollk);

        TaskManager.submit(new Task(20) {
            @Override
            protected void execute() {
                World.getNpcRemoveQueue().add(lollk);
                stop();
            }
        });
    }

    public DwarfCannonQuest() {
        super("Dwarf Cannon", false, 1, 8);
    }

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{
                {"",
                        "For several years now, the Dwarven Black Guard",
                        "have been developing the latest in projectile",
                        "warfare. With the constant attack of goblin",
                        "renegades, the dwarven troops who protect",
                        "the mines need to put this secret weapon into action.",
                        "Only with your help can the true power of the",
                        "cannon be harnessed!",
                },

                {
                        "",
                        "I spoke to Captain Lawgof who gave me 6",
                        "railings and told me 6 fences needed fixing.",
                        "",
                        "I fixed: " + player.getQuest().fixedRailings.size() + "/6 fences."
                },

                {
                        "",
                        "I have repaired all 6 fences. Captain",
                        "Lawgof has told me that the communication",
                        "with the watchtower has stopped.. I should",
                        "go and investigate."
                },
                {
                    "",
                        "I have found remains, I need to report this."
                },
                {
                        "",
                        "Captain Lawgof has said that Gilob has",
                        "a son named Lollk and that he is missing",
                        "I should search a cave near Fishing guild.",
                },

                {
                        "",
                        "I found the missing son. I should",
                        "go speak to Captain Lawgof."
                },

                {"",

                        "Captain Lawgof said he needs to find out ",
                        "about the ammo. He told me to go to a",
                        "dwarf base south of Ice Mountain to",
                        "find out more."
                },

                {
                        "",
                        "I spoke to Nulodion who gave me some",
                        "notes and an ammo mould. I should go",
                        "give these these to Captain Lawgof."
                },

                {""},

        };
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{5191, 1400, 5190};
    }

    @Override
    public void getEndDialogue(Player player, int npcId) {
        int id = player.getDialogue().id();
        if (id == 5) {
            QuestManager.increaseStage(player, quest);
            QuestDialogueLoader.sendDialogue(player, quest, 7);
        } else if (id == 10) {
            player.getInventory().add(HAMMER);
            player.getInventory().add(RAILINGS);
            QuestDialogueLoader.sendDialogue(player, quest, 12);
        } else if (id == 13) {
            if (player.getQuest().fixedRailings.size() < 6 && !player.getInventory().contains(RAILING)) {
                player.getInventory().add(HAMMER);
                player.getInventory().add(RAILINGS);
                QuestDialogueLoader.sendDialogue(player, quest, 15);
                return;
            }

            if (player.getQuest().fixedRailings.size() < 6) {
                QuestDialogueLoader.sendDialogue(player, quest, 16);
                return;
            }

            QuestDialogueLoader.sendDialogue(player, quest, 17);
        } else if (id == 25 || id == 41 || id == 61) {
            QuestManager.increaseStage(player, quest);
            player.getPacketSender().sendInterfaceRemoval();
        } else if (id == 27) {
            QuestDialogueLoader.sendDialogue(player, quest, player.getInventory().contains(DWARF_REMAINS) ? 30 : 29);
        } else if (id == 35) {
            increaseStage(player);
            player.getPacketSender().sendInterfaceRemoval();
            player.getInventory().delete(DWARF_REMAINS);
        } else if (id == 67) {
            player.getInventory().add(NOTES);
            player.getInventory().add(AMMO_MOULD);
            QuestDialogueLoader.sendDialogue(player, quest, 69);
            increaseStage(player);
        } else if (id == 80) {
            player.getInventory().delete(NOTES);
            player.getInventory().delete(AMMO_MOULD);
            QuestManager.increaseStage(player, quest);
            player.getInventory().add(new Item(ItemID.CANNONBALL, 50));
            QuestManager.complete(player, quest,
                    new String[]{"Ability to use the Dwarf multi-cannon.", "50 Cannon balls."}, 4);
        }
    }


    @Override
    public boolean hasRequirements(Player player) {
        return true;
    }

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int type) {
        switch (npc.getId()) {
            case NpcID.NULODION:
                if(getStage(player) == 7 && !player.getInventory().contains(NOTES)
                        && !player.getInventory().contains(AMMO_MOULD)) {
                    new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
                            .setText("Can I help you?").add(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.CALM)
                            .setText("I lost the mould and notes.").add(DialogueType.STATEMENT)
                            .setText("The Cannon Engineer gives you some notes and a mould.")
                            .setAction(player2 -> {
                                player.getInventory().add(NOTES);
                                player.getInventory().add(AMMO_MOULD);
                            }).start(player);
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (object.getId()) {
            case 2:
                player.moveTo(new Position(2620, 9797));
                return true;
            case 13:
                player.moveTo(new Position(2623, 3391));
                return true;
            case 1:
                if (QuestManager.getStage(player, quest.name) == 3) {
                    summonLollk(player);
                    QuestManager.increaseStage(player, quest);
                } else if (QuestManager.getStage(player, quest.name) == 4) {
                    summonLollk(player);
                } else {
                    player.sendMessage("The crate is empty.");
                }
                return true;
            case 15595:
            case 15594:
            case 15593:
            case 15592:
            case 15591:
            case 15590:
                fixRailing(player, object);
                return true;
            case 15601:
                player.getPacketSender().sendMessage("This railing seems fine..");
                return true;
        }
        return false;
    }

    @Override
    public Position getTeleport() {
        return null;
    }
}
