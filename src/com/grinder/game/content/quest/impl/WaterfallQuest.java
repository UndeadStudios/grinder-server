package com.grinder.game.content.quest.impl;


import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.ForceMovement;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.ForceMovementTask;
import com.grinder.util.ItemID;

import java.util.concurrent.TimeUnit;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class WaterfallQuest extends Quest {

    private static final Position CRASH = new Position(2513, 3481);

    private static final Item BOOK_ON_BAXTORIAN = new Item(292);
    private static final Item GLARIALS_PEBBLE = new Item(294);

    private static final Item GLARIALS_AMULET = new Item(295);
    private static final Item GLARIALS_URN = new Item(296);

    private static final Animation CLIMB = new Animation(827);
    private static final Position WATERFALL_POS = new Position(2511, 3463);

    private static final Position WATERFALL_DUNGEON = new Position(2575, 9861);

    private static final Item A_KEY = new Item(298);

    public WaterfallQuest() {
        super("Waterfall Quest", false, 3, 7);
    }

    private void boardRaft(Player p) {
        p.getPacketSender().sendMessage("You board the raft..");
        p.getPacketSender().sendFadeScreen("You crash!", 2, 5);
        p.delayedMoveTo(CRASH, 3);
    }

    private void swimWater(Player p) {
        TaskManager.submit(new ForceMovementTask(p, 1,
                new ForceMovement(p.getPosition().clone(), new Position(0, -9), 1, 130,
                        4, 772)));

        TaskManager.submit(new Task(1) {
            int ticks = 0;
            @Override
            protected void execute() {

                p.performAnimation(new Animation(772));
                ticks++;
                if(ticks == 5) {
                    stop();
                }
            }
        });
    }

    private void climbDeadTree(Player p) {
        p.performAnimation(CLIMB);
        p.delayedMoveTo(WATERFALL_POS, 1);
    }

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{
                {
                        "Investigate the death of elven leaders ",
                        "of old. Search for the elf King Baxtorian's ",
                        "tomb and discover the mysterious hidden ",
                        "treasure of the waterfall. ",
                },
                {"",
                        "Almera asked me to find her son.",
                },
                {"",
                        "I found her son but he doesn't want to return..",
                },
                {"",
                        "I should investigate south from here..",
                },
                {"",
                        "Hadley told me a little about the treasure.",
                        "I should go upstairs and look for a book for",
                        "more information",
                },
                {"",
                        "I found and read the book, I now need to find",
                        "a pebble. The pebble is one step closer to",
                        "the treasure.",
                        "",
                        "Once I have this pebble, I need to find a",
                        "tombstone. If I use the pebble on the",
                        "tombstone, then I should find another clue.",
                },
                {"",
                        "I have fond Glarial's amulet and urn. I",
                        "should now take it to the waterfall dungeon",
                        "and find a statue."
                },
                {""},
        };
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{4181, 4182, 4179};
    }

    @Override
    public boolean hasRequirements(Player player) {
        return true;
    }

    @Override
    public boolean hasStartDialogue(Player player, int npcId) {
        return false;
    }

    @Override
    public void getEndDialogue(Player player, int npcId) {
        int id = player.getDialogue().id();

        if (id == 5 || id == 18 || id == 33 || id == 55) {
            increaseStage(player);
            player.getPacketSender().sendInterfaceRemoval();
        }
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (object.getId()) {
            case 1987:
                boardRaft(player);
                return true;
            case 1989:
                if (getStage(player) == 4) {
                    increaseStage(player);
                }
                player.getInventory().add(BOOK_ON_BAXTORIAN);
                player.getPacketSender().sendMessage("You find a book.");
                return true;
            case 10283:
                swimWater(player);
                return true;
            case 2020:
                climbDeadTree(player);
                return true;
            case 2022:
                player.moveToByFadeScreen(player, new Position(2527, 3413, 0), "You crash!");
                return true;
            case 2010:
                if(getStage(player) == 6) {
                    player.moveTo(WATERFALL_DUNGEON);
                    return true;
                }
                break;
            case 2000:
                    player.moveToByFadeScreen(player, WATERFALL_POS, "");
                    return true;
            case 354:
            case 1990:
                if (!EntityExtKt.passedTime(player, Attribute.LAST_BUSH_PICKUP, 5, TimeUnit.SECONDS, false, true)) {
                    player.sendMessage("The crate is empty.");
                    return true;
                }
                EntityExtKt.markTime(player, Attribute.LAST_BUSH_PICKUP);
                new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(A_KEY.getId(), 250)
                        .setText("You find a key!").start(player);
                player.getPacketSender().sendMessage("You find a key!");
                player.getInventory().add(A_KEY);
                return true;

            case 2014:
                if (getStage(player) == 6) {
                    player.performAnimation(new Animation(862));
                    increaseStage(player);
                    player.BLOCK_ALL_BUT_TALKING = true;
                    TaskManager.submit(new Task(2) {
                        @Override
                        public void execute() {
                            stop();
                            player.BLOCK_ALL_BUT_TALKING = false;
                            player.getInventory().delete(GLARIALS_URN);
                            player.getInventory().delete(GLARIALS_AMULET);
                            player.getInventory().delete(GLARIALS_PEBBLE);

                            QuestManager.complete(player, quest, new String[]{
                                    "237,500 Strength XP",
                                    "237,500 Attack XP",
                                    "Cache of runes",
                                    "40 Mithril seeds",
                                    "a Mystery box."}, GLARIALS_URN.getId());

                            player.getSkillManager().addFixedDelayedExperience(Skill.STRENGTH, 237_500);
                            player.getSkillManager().addFixedDelayedExperience(Skill.ATTACK, 237_500);
                            player.getInventory().add(new Item(ItemID.CACHE_OF_RUNES, 1));
                            player.getInventory().add(new Item(ItemID.MITHRIL_SEEDS, 40));
                            player.getInventory().add(new Item(ItemID.MYSTERY_BOX, 1));
                        }
                    });
                    return true;
                }
                return false;
            }
        return false;
    }

    @Override
    public boolean handleItemOnObjectInteraction(Player player, Item item, GameObject object) {
        if (item.getId() == GLARIALS_PEBBLE.getId()) {
            if (object.getId() == 1992) {
                player.getInventory().add(GLARIALS_AMULET);
                player.getInventory().add(GLARIALS_URN);
                player.getPacketSender().sendMessage("You use the pebble on the tombstone and find an amulet and an urn!");
                if (getStage(player) == 5) {
                    increaseStage(player);
                }
                return true;
            }
            return false;
        }
        if (item.getId() == GLARIALS_URN.getId() || item.getId() == GLARIALS_AMULET.getId()) {
            if (object.getId() == 2006 && object.getPosition().equals(new Position(2565, 9916, 0))) {
                if (!player.getInventory().contains(new Item[]{GLARIALS_AMULET, GLARIALS_URN})) {
                    player.getPacketSender().sendMessage("You need to have a Gllarial's amulet and urn to be able place it over the statue.");
                    return true;
                }
                player.moveToByFadeScreen(player, new Position(2603, 9914), "");
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public Position getTeleport() {
        return null;
    }
}