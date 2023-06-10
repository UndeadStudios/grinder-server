package com.grinder.game.content.quest.impl;

import com.grinder.game.World;
import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;
import com.grinder.util.ObjectID;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class GertrudesCat extends Quest {

    private static final Item SEASONED_SARDINE = new Item(1552);

    private static final Item BUCKET_OF_MILK = new Item(1927);

    public GertrudesCat() {
        super("Gertrude's Cat", false, 1, 6);
    }

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{
                {
                    "",
                        "Gertrude has lost her cat Fluffs and desperately",
                        "wants to find her. Can you help bring her home?"
                },

                {
                    "",
                        "Gertrude said she lost her cat Fluffs.. She said",
                        "that I should go speak to her two children, Shilop",
                        "and Wilough in Varrock Square."
                },
                {
                    "",
                        "I spoke to the two boys and they told me about",
                        "a secret playground, perhaps they were talking",
                        "about the Lumber Yard north-east of Varrock..",
                        "",
                        "It's a cat so perhaps I should bring some cat items:",
                        "",
                        QuestManager.hasItem(player, SEASONED_SARDINE, "Seasoned sardine"),
                        QuestManager.hasItem(player, BUCKET_OF_MILK, "Bucket of milk"),
                },
                {
                    "",
                        "I have given the sardine to the cat, I should",
                        "now try the milk..",

                },
                {
                    "",
                        "The sardine and milk didn't work, I wonder",
                        "why it doesn't want to leave.."
                },
                {
                    "",
                        "I found a kitten! They both returned home",
                        "I should speak to Gertrude now.."
                },
                {""},
        };
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[] {3528, 3501, 3503};
    }

    @Override
    public boolean hasRequirements(Player player) {
        return true;
    }

    @Override
    public void getEndDialogue(Player player, int npcId) {
        int id = player.getDialogue().id();

        if(id == 5) {
            increaseStage(player);
            sendDialogue(player, 7);
        } else if(id == 17) {
            if(player.getInventory().contains(new Item(995, 100))) {
                player.getInventory().delete(new Item(995, 100));
                increaseStage(player);
                sendDialogue(player, 19);
            } else {
                sendDialogue(player, 20);
            }
        } else if(id == 22) {
            player.getPacketSender().sendInterfaceRemoval();
        } else if(id == 27) {
            increaseStage(player);
            QuestManager.complete(player,quest, new String[]{"A pet Kitten."}, 1555);
            player.getInventory().add(1555, 1);
        }
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (object.getId()) {
            case ObjectID.BARREL_12:
                if (getStage(player) == 2 || getStage(player) == 3 || getStage(player) == 4) {
                    player.performAnimation(new Animation(827));
                    player.sendMessage("You search the " + object.getDefinition().name.toLowerCase() + "...");
                    player.BLOCK_ALL_BUT_TALKING = true;
                    TaskManager.submit(new Task(3) {
                        @Override
                        public void execute() {
                            stop();
                            new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("Wow! Seasoned sardine?!", "What would I do with that?")
                                    .setExpression(DialogueExpression.CURIOUS)
                                    .start(player);
                            player.getPacketSender().sendSound(2584);
                            ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.SEASONED_SARDINE, 1));
                            player.BLOCK_ALL_BUT_TALKING = false;
                        }
                    });
                    return true;
                } else {
                    player.sendMessage("You search the barrel and find nothing of interest.");
                    return true;
                }
        }
        return false;
    }

    @Override
    public boolean handleItemOnEntityInteraction(Player player, Item item, Agent entity) {
        if(entity.isNpc()) {
            NPC npc = entity.getAsNpc();

            if(npc.getId() == NpcID.GERTRUDES_CAT_3497) {
                if(getStage(player) == 2) {
                    if(item.getId() == SEASONED_SARDINE.getId()) {

                        player.performAnimation(new Animation(827));
                        if (Misc.random(3) == Misc.random(3)) {
                            npc.say("Meow!");
                            increaseStage(player);
                        } else {
                            npc.say("Meow..Mmmmmmmore!");
                            player.sendMessage("It looks like cat seems to be pleased, and is asking for more fish.");
                        }
                        player.getInventory().delete(SEASONED_SARDINE);
                        player.sendMessage("You feed the cat the seasoned sardine...");
                        return true;
                    }
                } else if(getStage(player) == 3) {
                    if(item.getId() == BUCKET_OF_MILK.getId()) {
                        player.performAnimation(new Animation(827));
                        player.sendMessage("You feed the cat the bucket of milk...");
                        player.getInventory().delete(BUCKET_OF_MILK);
                        increaseStage(player);
                        npc.say("Meow!");
                        return true;
                    }
                } else if(getStage(player) == 4) {
                    if(item.getId() == ItemID.FLUFFS_KITTEN) {


                        player.getInventory().delete(ItemID.FLUFFS_KITTEN, 1);

                        increaseStage(player);
                        player.getPacketSender().sendMessage("Fluffs and her kitten run home!");

                        new DialogueBuilder(DialogueType.STATEMENT)
                                .setText("Fluffs and her kitten run home!")
                                .setExpression(DialogueExpression.CURIOUS)
                                .start(player);

                        World.getNpcRemoveQueue().add(npc);

                        TaskManager.submit(new Task(15) {
                            @Override
                            protected void execute() {
                                stop();
                                World.getNpcAddQueue().add(npc);
                            }
                        });
                    }
                    return true;
                }

                if(item.getId() == SEASONED_SARDINE.getId() || item.getId() == ItemID.BUCKET_OF_MILK) {
                    player.sendMessage("The cat seems to be already full.");
                    return true;
                }


            }
        }
        return false;
    }

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int type) {
        switch (npc.getId()) {
            case NpcID.GERTRUDES_CAT_3497:
                npc.say("Hiss!");
                player.getPacketSender().sendMessage("I shouldn't do that..");
                return true;
            case NpcID.CRATE:
                if (getStage(player) == 4) {
                    if (!player.getInventory().contains(ItemID.FLUFFS_KITTEN)) {

                        player.sendMessage("You search the crate...");
                        new DialogueBuilder(DialogueType.STATEMENT)
                                .setText("You search the crate...")
                                .start(player);

                        player.BLOCK_ALL_BUT_TALKING = true;

                        TaskManager.submit(new Task(3) {
                            @Override
                            public void execute() {
                                stop();

                                if (player.getInventory().isFull()) {
                                    player.sendMessage("You find something! You're unable to fit into your inventory to see what it is...");
                                    return;
                                }

                                new DialogueBuilder(DialogueType.STATEMENT)
                                        .setText("You find a small lost kitten!")
                                        .start(player);

                                player.getInventory().add(new Item(ItemID.FLUFFS_KITTEN));

                                player.getPacketSender().sendSound(2584);
                                player.BLOCK_ALL_BUT_TALKING = false;
                            }
                        });
                        return true;
                    } else {
                        player.sendMessage("The crate is empty.");
                        return true;
                    }
                } else {
                    player.sendMessage("The crate is empty.");
                    return true;
                }
        }
        return false;
    }

    @Override
    public Position getTeleport() {
        return null;
    }
}
