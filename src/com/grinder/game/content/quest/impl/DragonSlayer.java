package com.grinder.game.content.quest.impl;

import com.grinder.game.World;
import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.NpcID;

import java.util.concurrent.TimeUnit;

/**
 * @author Dexter Morgan
 * <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class DragonSlayer extends Quest {

    private static final Item MELZARS_KEY = new Item(1542);

    private static final Item MAP_OF_CRANDOR = new Item(1538);

    private static final Item BOAT_COST = new Item(995, 2_000);

    private static final Item PLANKS = new Item(960, 3);

    private static final Item STEEL_NAILS = new Item(1539, 90);

    private static final int ELVARG = 6349;

    private static final Position ISLAND_OF_CRANDOR = new Position(2853, 3236);

    private static final Item[] MATERIALS = {
            PLANKS,
            STEEL_NAILS
    };

    private void spawnElvarg(Player p) {
        if (!EntityExtKt.passedTime(p, Attribute.LAST_COFFIN_USE, 120, TimeUnit.SECONDS, false, false)) {
            return;
        }
        EntityExtKt.markTime(p, Attribute.LAST_COFFIN_USE);
        int height = p.getIndex() * 4;

        Position pos = new Position(2847, 9635, height);

        SkillUtil.stopSkillable(p);
        p.BLOCK_ALL_BUT_TALKING = true;
        p.getPacketSender().sendFadeScreen("", 2, 6);
        TaskManager.submit(new Task(4) {
            @Override
            public void execute() {
                stop();
                p.getPacketSender().sendInterfaceRemoval();
                p.BLOCK_ALL_BUT_TALKING = false;
                p.moveTo(pos);
            }
        });

        Position npcPos = new Position(2854, 9639, height);

        NPC n = NPCFactory.INSTANCE.create(ELVARG, npcPos);

        n.setOwner(p);
        World.getNpcAddQueue().add(n);
        n.getMotion().followTarget(p);
        n.setEntityInteraction(p);

        TaskManager.submit(new Task(200) {
            @Override
            protected void execute() {
                if (n.isAlive() || n.isActive())
                    World.getNpcRemoveQueue().add(n);
                stop();
            }
        });
    }

    public DragonSlayer() {
        super("Dragon Slayer", false, 2, 9);
    }

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{
                {"",
                        "Prove yourself a true champion. Kill the mighty dragon",
                        " Elvarg of Crandor and earn the right to buy and wear",
                        "the Rune platebody.",
                        "",
                        QuestManager.hasQuestPoints(player, 26, "26 Quest Points"),
                        "Ability to defeat a level 83 dragon.",
                },
                {"",
                        "The guildmaster told me to go Speak to Oziach in Edgeville.",
                },
                {"",
                        "Oziach is willing to sell me a rune platebody if",
                        "I defeat the Dragon of Crandor. I need to report",
                        "back to the guildmaster.."
                },
                {"",
                        "The guild master said I need to find a map",
                        "that will guide me to the Dragon..",
                        "",
                        "I have:",
                        "",
                        QuestManager.hasItem(player, MAP_OF_CRANDOR, "Map of Crandor"),
                        "",
                        "Once I have the map, I need to find a boat",
                        "and someone to take me..", "Perhaps I need to talk to someone in Port sarim."
                },
                {"",
                        "The ship I purchased needs fixing.",
                        "The sailor said to bring him",
                        "3 planks and 90 steel naills..",
                },
                {"",
                        "I have fixed my ship. I need to",
                        "find a captain that will take me",
                        "to the island.."
                },
                {"",
                        "I have found a captain to take me,",
                        "he said he will meet me at the ship..",
                },
                {
                        "",
                        "I have to kill Elvarg. After, I need",
                        "to go speak to Oziach.."
                },
                {"", "I have slain the dragon of Crandor, Elvarg."},
                {""},
        };
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{814, 822, 819, 4280};
    }

    @Override
    public void getEndDialogue(Player player, int npcId) {
        int id = player.getDialogue().id();

        if (id == 4) {
            increaseStage(player);
            sendDialogue(player, 6);
        } else if (id == 20) {
            increaseStage(player);
            player.getPacketSender().sendInterfaceRemoval();
        } else if (id == 41) {
            increaseStage(player);
            player.getInventory().add(MELZARS_KEY);
            sendDialogue(player, 43);
        } else if (id == 47) {
            sendDialogue(player, 49);
        } else if (id == 51) {
            boolean hasCoins = player.getInventory().contains(BOAT_COST);

            sendDialogue(player, hasCoins ? 54 : 53);

            if(hasCoins) {
                player.getInventory().delete(BOAT_COST);
                increaseStage(player);
            }
        } else if (id == 58) {
            player.getInventory().delete(BOAT_COST);
            increaseStage(player);
            player.getPacketSender().sendInterfaceRemoval();
        } else if (id == 60) {
            boolean hasMaterial = player.getInventory().contains(MATERIALS);

            sendDialogue(player, hasMaterial ? 63 : 62);
        } else if (id == 63) {
            player.getInventory().delete(MATERIALS);
            increaseStage(player);
            sendDialogue(player, 65);
        } else if (id == 84) {
            increaseStage(player);
            if (player.getInventory().contains(MAP_OF_CRANDOR))
            player.getInventory().delete(MAP_OF_CRANDOR);
            sendDialogue(player, 86);
        } else if (id == 90) {
            increaseStage(player);
            QuestManager.complete(player, quest, new String[]{"300,000 Attack XP",
                    "300,000 Strength XP",
                    "300,000 Defence XP",
                    "Ability to equip Rune and",
                    "Dragon Platebodies.",}, 1127);
            player.getSkillManager().addFixedDelayedExperience(Skill.ATTACK, 300_000);
            player.getSkillManager().addFixedDelayedExperience(Skill.STRENGTH, 300_000);
            player.getSkillManager().addFixedDelayedExperience(Skill.DEFENCE, 300_000);
        } else if (id == 92) {
            boolean hasKey = player.getInventory().contains(MELZARS_KEY);

            sendDialogue(player, hasKey ? 95 : 94);
        } else if (id == 94) {
            player.getInventory().add(MELZARS_KEY);
            sendDialogue(player, 97);
        }
    }

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int type) {
        switch (npc.getId()) {
            case NpcID.GUILDMASTER:
                if (QuestManager.hasCompletedQuest(player, quest.name)) { // Give key dialogue
                    new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
                            .setText("Can I help you champion?").add(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.CALM)
                            .setText("I lost the maze key.", "Can I get another one please?").add(DialogueType.STATEMENT)
                            .setText("The Guildmaster hands you with a brand new maze key.")
                            .setAction(player2 -> {
                                player.getInventory().add(MELZARS_KEY);
                            }).start(player);
                    return true;
                } else { // Regular dialogue
                    new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
                            .setText("Hello, " + player.getUsername() +". What can I do for you?").add(DialogueType.PLAYER_STATEMENT)
                            .setText("Nothing thanks, I'm just looking around..")
                            .start(player);
                    return true;
                }
        }
        return false;
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {

        if (player.getPosition().sameAs(new Position(2937, 3252))
                    || player.getPosition().sameAs(new Position(2923, 9654))
                    || player.getPosition().sameAs(new Position(2926, 9649))
                    || player.getPosition().sameAs(new Position(2930, 9640))) {
            player.sendMessage("The door is locked from this side.");
            player.playSound(new Sound(Sounds.USE_KEY_ON_LOCKED_DOOR));
            return true;
        }
        switch (object.getId()) {
            case 2603:
                if (!player.getInventory().contains(MAP_OF_CRANDOR)) {
                    player.getInventory().add(MAP_OF_CRANDOR);
                    player.getPacketSender().sendMessage("You find a map piece!");
                    new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(1538, 250)
                            .setText("You find a map piece!").start(player);
                    return true;
                }
            case 2593:
                if (getStage(player) == 7) {

                    SkillUtil.stopSkillable(player);
                    player.BLOCK_ALL_BUT_TALKING = true;
                    player.getPacketSender().sendFadeScreen("Travelling to Crandor!", 2, 5);
                    TaskManager.submit(new Task(3) {
                        @Override
                        public void execute() {
                            stop();
                            player.getPacketSender().sendInterfaceRemoval();
                            player.BLOCK_ALL_BUT_TALKING = false;
                            player.moveTo(ISLAND_OF_CRANDOR);
                        }
                    });
                    return true;
                }
                break;
            case 25161:
                if (getStage(player) == 7) {
                    spawnElvarg(player);
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public boolean handleItemInteraction(Player player, Item item, int type) {
        switch (item.getId()) {
            case ItemID.CRANDOR_MAP:
                player.getPacketSender().sendMessage("I should deliver this to a pirate in Port Sarim.");
                return true;
        }
        return false;
    }

    @Override
    public boolean handleNpcDeath(Player player, NPC npc) {
        if (npc.getId() == ELVARG && getStage(player) == 7) {
            new DialogueBuilder(DialogueType.STATEMENT)
                    .setText("I have defeated Elvarg! I should report it to Oziach", "as soon as possible!").start(player);
            increaseStage(player);
        }
        return false;
    }

    @Override
    public boolean hasRequirements(Player player) {
        return true;
    }

    @Override
    public Position getTeleport() {
        return null;
    }
}
