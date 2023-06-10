package com.grinder.game.content.quest.impl.rfd;

import com.grinder.game.World;
import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.FacingDirection;
import com.grinder.game.model.Position;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class RecipeForDisaster extends Quest {

    private static final int SUB_QUEST_SIZE = 8;

    private static final Item EYE_OF_NEWT = new Item(221);

    private static final Item ALE = new Item(1909);

    private static final Item ROTTEN_TOMATO = new Item(2518);

    private static final Item DIRTY_BLAST = new Item(7497);

    private static final Position CUT_SCENE_COORDS = new Position(1861, 5340);

    private static final int DUKE_HERECIO = 8051;

    private static final Position DUKE_SPAWN = new Position(1864, 5341);

    private static final int OSMAN = 3388;

    private static final Position OSMAN_SPAWN = new Position(1862, 5343);

    private static final int MOUNTAIN_DWARF = 3390;

    private static final Position DWARF_SPAWN = new Position(1862, 5345);

    private static final int PIRATE_PETE = 3389;

    private static final Position PIRATE_SPAWN = new Position(1862, 5347);

    private static final int CULINOROMANCER = 3400;

    private static final Position CULI_SPAWN = new Position(1863, 5341);

    private static final int GIPSY = 5082;

    private static final Position GIPSY_SPAWN = new Position(1866, 5348);

    private static final Position FROZEN_ROOM = new Position(1861, 5316);

    private static final Position KITCHEN = new Position(3207, 3217);

    private static final Animation GIPSY_SPELL = new Animation(811);

    private static final int BENTNOZE = 3392;

    private static final Position BENTNOZE_POS = new Position(1862, 5349);

    private static final int WART_FACE = 3391;

    private static final Position WARTFACE_POS = new Position(1862, 5351);

    private static final int SKARCH = 3398;

    private static final Position SKRACH_POS = new Position(1863, 5352);

    private static final int SAGE = 3393;

    private static final Position SAGE_POS = new Position(1865, 5349);

    private static final int EVIL_DAVE = 3394;

    private static final Position EVIL_DAVE_POS = new Position(1865, 5347);

    private static int SIR_VASE = 3395;

    private static final Position SIR_VASE_POS = new Position(1865, 5345);

    private static final int AWOWOGEI = 3396;

    private static final Position AWOWOGEI_POS = new Position(1865, 5343);

    private static final Item ASGARDIAN_ALE_COST = new Item(995, 200);

    private static final Item ASGOLDIAN_ALE = new Item(7508);

    private static final Position FROZEN_GIPSY_POS = new Position(1866, 5318);
    private static final Position FROZEN_CULI_POS = new Position(1863, 5318);

    private static final Position CULI_BOSS_AREA = new Position(1899, 5365);

    private static final Position BOSS_SPAWN = new Position(1899, 5358);

    private static final Item[] INGREDIENTS = {
            EYE_OF_NEWT, ALE, ROTTEN_TOMATO, DIRTY_BLAST
    };

    private boolean cantEquipGlove(Player p, int id) {
        if (id == 7462) {
            if (!QuestManager.hasCompletedQuest(p, quest.name)) {
                p.sendMessage("You need to complete 'Recipe for Disaster' quest to equip this.");
                return true;
            }
        }

        if (RFDUnlockableGlove.FOR_ID.get(id) == null) {
            return false;
        }

        int completed = QuestManager.getSpecialQuestsCompleted(p);
        int required = RFDUnlockableGlove.FOR_ID.get(id);

        if (completed < required) {
            p.sendMessage("You haven't completed enough Recipe for Disaster subquests. To equip this, you need at least " + required + " subquests.");
            return true;
        }
        return false;
    }

    public static void completeAllSubquest(Player player) {
        if (RecipeForDisaster.hasCompletedAllSubquest(player)) {
            if (QuestManager.getStage(player, "Recipe for Disaster") == 3) {
                QuestManager.increaseStage(player, "Recipe for Disaster");
            }
        }
    }

    private static boolean hasCompletedAllSubquest(Player p) {
        return QuestManager.getSpecialQuestsCompleted(p) == SUB_QUEST_SIZE;
    }

    private void sendUnfreezeCutScene(Player p) {
        p.BLOCK_ALL_BUT_TALKING = true;

        int height = p.getIndex() * 4;

        Position pos = FROZEN_ROOM.clone().transform(0, 0, height);

        p.moveTo(pos);

        final NPC culi = NPCFactory.INSTANCE.create(CULINOROMANCER, FROZEN_CULI_POS.clone().transform(0, 0, height));

        final NPC gipsy = NPCFactory.INSTANCE.create(GIPSY, FROZEN_GIPSY_POS.clone().transform(0, 0, height));

        World.getNpcAddQueue().add(gipsy);
        World.getNpcAddQueue().add(culi);

        TaskManager.submit(new Task() {

            int tick = 0;

            @Override
            protected void execute() {
                switch (tick) {
                    case 0:
                        gipsy.say("Now you've freed them all, I'll restart time so you can deal with the culinaromancer.");
                        break;
                    case 4:
                        p.say("If you're sure..");
                        break;
                    case 8:
                        gipsy.performAnimation(GIPSY_SPELL);
                        gipsy.say("TEMPUS PROCEDIT!");
                        break;
                    case 15:
                        culi.say("What was that?");
                        break;
                    case 18:
                        gipsy.say("We have broken your feeble spell! Begone!");
                        break;
                    case 21:
                        culi.say("You DARED to meddle?");
                        break;
                    case 24:
                        culi.say("But you cannot defeat me! I'LL BE BACK!");
                        break;
                    case 27:
                        World.getNpcRemoveQueue().add(culi);
                        break;

                    case 29:
                        p.say("Hooray - he's gone!");
                        break;
                    case 32:
                        gipsy.say("One day he'll return. You must go through the portal and finish him off.");
                        break;
                    case 34:
                        increaseStage(p);
                        p.moveTo(CUT_SCENE_COORDS);
                        p.BLOCK_ALL_BUT_TALKING = false;
                        stop();
                        break;
                }
                tick++;
            }
        });
    }

    private void spawnGipsy(Player p) {
        int height = p.getIndex() * 4;

        final NPC culi = NPCFactory.INSTANCE.create(CULINOROMANCER, FROZEN_CULI_POS.clone().transform(0, 0, height));

        final NPC gipsy = NPCFactory.INSTANCE.create(GIPSY, FROZEN_GIPSY_POS.clone().transform(0, 0, height));

        World.getNpcAddQueue().add(gipsy);
        World.getNpcAddQueue().add(culi);

        TaskManager.submit(new Task(120) {
            @Override
            protected void execute() {
                stop();
                World.getNpcRemoveQueue().add(culi);
                World.getNpcRemoveQueue().add(gipsy);
            }
        });

    }

    private void sendFreezeCutScene(Player p) {

        p.BLOCK_ALL_BUT_TALKING = true;

        int height = p.getIndex() * 4;

        Position pos = CUT_SCENE_COORDS.clone().transform(0, 0, height);

        p.moveTo(pos);

        final NPC duke = NPCFactory.INSTANCE.create(DUKE_HERECIO, DUKE_SPAWN.clone().transform(0, 0, height));

        final NPC osman = NPCFactory.INSTANCE.create(OSMAN, OSMAN_SPAWN.clone().transform(0, 0, height));

        final NPC dwarf = NPCFactory.INSTANCE.create(MOUNTAIN_DWARF, DWARF_SPAWN.clone().transform(0, 0, height));

        final NPC pete = NPCFactory.INSTANCE.create(PIRATE_PETE, PIRATE_SPAWN.clone().transform(0, 0, height));

        final NPC bentnoze = NPCFactory.INSTANCE.create(BENTNOZE, BENTNOZE_POS.clone().transform(0, 0, height));

        final NPC wartface = NPCFactory.INSTANCE.create(WART_FACE, WARTFACE_POS.clone().transform(0, 0, height));

        final NPC skrach = NPCFactory.INSTANCE.create(SKARCH, SKRACH_POS.clone().transform(0, 0, height));

        final NPC sage = NPCFactory.INSTANCE.create(SAGE, SAGE_POS.clone().transform(0, 0, height));

        final NPC dave = NPCFactory.INSTANCE.create(EVIL_DAVE, EVIL_DAVE_POS.clone().transform(0, 0, height));

        final NPC sir = NPCFactory.INSTANCE.create(SIR_VASE, SIR_VASE_POS.clone().transform(0, 0, height));

        final NPC monkey = NPCFactory.INSTANCE.create(AWOWOGEI, AWOWOGEI_POS.clone().transform(0, 0, height));

        World.getNpcAddQueue().add(duke);

        World.getNpcAddQueue().add(osman);
        World.getNpcAddQueue().add(dwarf);
        World.getNpcAddQueue().add(pete);
        World.getNpcAddQueue().add(bentnoze);
        World.getNpcAddQueue().add(wartface);

        World.getNpcAddQueue().add(skrach);

        World.getNpcAddQueue().add(sage);
        World.getNpcAddQueue().add(dave);
        World.getNpcAddQueue().add(sir);
        World.getNpcAddQueue().add(monkey);

        duke.setFace(FacingDirection.NORTH);
        osman.setFace(FacingDirection.EAST);
        dwarf.setFace(FacingDirection.EAST);
        pete.setFace(FacingDirection.EAST);
        bentnoze.setFace(FacingDirection.NORTH);
        wartface.setFace(FacingDirection.SOUTH);
        skrach.setFace(FacingDirection.SOUTH);
        sage.setFace(FacingDirection.WEST);
        dave.setFace(FacingDirection.WEST);
        sir.setFace(FacingDirection.WEST);
        monkey.setFace(FacingDirection.WEST);

        final NPC culi = NPCFactory.INSTANCE.create(CULINOROMANCER, CULI_SPAWN.clone().transform(0, 0, height));

        final NPC gipsy = NPCFactory.INSTANCE.create(GIPSY, GIPSY_SPAWN.clone().transform(0, 0, height));

        TaskManager.submit(new Task() {
            int tick = 0;

            @Override
            protected void execute() {

                switch (tick) {
                    case 3:
                        duke.say("Welcome, gentleman, to Lumbridge Castle.");
                        break;
                    case 6:
                        duke.say("I welcome Osman, Spymaster for the Emir of Al Kharid.");
                        break;
                    case 8:
                        osman.say("I thank you for your hospitality.");
                        break;
                    case 10:
                        duke.say("I welcome the chief guard of the White Wolf Mountain dwarves.");
                        break;
                    case 12:
                        dwarf.say("The beer is good!");
                        break;
                    case 15:
                        duke.say("I welcome Pirate Pete from Braindeath Island.");
                        break;
                    case 17:
                        pete.say("Your rum's got no flavour!");
                        break;
                    case 20:
                        duke.say("I welcome the chief of the Goblin Village.");
                        break;
                    case 22:
                        bentnoze.say("That me! Give me chair!");
                        break;
                    case 24:
                        wartface.say("No, it me! Chair mine!");
                        break;
                    case 27:
                        duke.say("I welcome Skrach Uglogwee of the Feldip Hills ogres.");
                        break;
                    case 29:
                        skrach.say("Der ogres, dey call me Bone Cruncher.");
                        break;
                    case 32:
                        duke.say("I welcome my neighbour, Phileas the Lumbridge Sage.");
                        break;
                    case 34:
                        sage.say("I didn't have far to travel!");
                        break;
                    case 37:
                        duke.say(" From the town of Edgeville, I welcome Evil Dave.");
                        break;
                    case 39:
                        dave.say("These secret meetings are SOOO EVIL!");
                        break;
                    case 42:
                        duke.say("A hearty welcome to Sir Amik Varze, leader of the White Knights...");
                        break;
                    case 44:
                        sir.say("Do get a move on!");
                        break;
                    case 47:
                        duke.say("...and finally, I welcome the ruler of Ape Atoll, Awowogei.");
                        break;
                    case 49:
                        sage.say("I think he's lost his Amulet of Manspeak.");
                        break;
                    case 52:
                        p.say("Ooh - nice food!");
                        break;
                    case 55:
                        World.getNpcAddQueue().add(culi);

                        culi.say("Aha! I'm BACK!");
                        break;
                    case 58:
                        osman.say("Did we invite you?");
                        break;
                    case 61:
                        culi.say("Hah! Your chef did! And now I'll kill you!");
                        break;
                    case 63:
                        culi.say("MUAHAHAHA!");
                        break;
                    case 65:
                        World.getNpcAddQueue().add(gipsy);
                        break;
                    case 67:
                        gipsy.say("Sorry I am late...");
                        break;
                    case 69:
                        culi.say("I remember you...");
                        break;
                    case 71:
                        gipsy.say("Aaargh!");
                        break;
                    case 73:
                        culi.say("Muahahaha!");
                        break;
                    case 75:
                        gipsy.say("TEMPUS CESSIT!");
                        gipsy.performAnimation(GIPSY_SPELL);
                        break;
                    case 78:
                        gipsy.say(p.getUsername() + ", you must help me defeat this evil!");
                        World.getNpcRemoveQueue().add(duke);
                        World.getNpcRemoveQueue().add(osman);
                        World.getNpcRemoveQueue().add(dwarf);
                        World.getNpcRemoveQueue().add(pete);
                        p.moveTo(FROZEN_ROOM);
                        increaseStage(p);
                        stop();
                        p.BLOCK_ALL_BUT_TALKING = false;
                        break;
                }
                tick++;
            }
        });
    }

    public RecipeForDisaster() {
        super("Recipe for Disaster", false, 1, 14);
    }

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{
                {"",
                        "The cook in Lumbridge has a surprisingly small",
                        "number of ingredients on hand in his kitchen, ",
                        "possibly as a result of an endless stream of ",
                        "newcomers with light fingers, taking anything that",
                        "isn't nailed down from his kitchen.",
                        "Luckily, in the past, a kind Cook's Assistant was",
                        "willing to help him in his culinary duties, but a ",
                        "problem has now appeared that could spell...",
                        "",
                        "a Recipe For Disaster!",
                        "",
                        "You need the following quests completed:",
                        "",
                        hasCompletedQuest(player, "Cook's Assistant"),
                },
                {"",
                        "The cook said he needs these ingredients:",
                        "",
                        QuestManager.hasItem(player, EYE_OF_NEWT, "Eye of newt"),
                        QuestManager.hasItem(player, ALE, "Greenma's Ale"),
                        QuestManager.hasItem(player, ROTTEN_TOMATO, "Rotten tomato"),
                        QuestManager.hasItem(player, DIRTY_BLAST, "Dirty Blast"),
                },
                {"",
                        "The cook said to go through the door to the feast.",
                },
                {"",
                        "I need to free all the members, I should inspect",
                        "them individually."
                },
                {"",
                        "I need to return to the feast..",
                },
                {"",
                        "I have freed all the council members. I now need",
                        "to take on the Culinoromancer!",
                        "",
                        "I need to talk to the Gipsy to continue..",
                },
                {"",
                        "I need to face Agrith Na-Na",
                },
                {"",
                        "I need to face Flambeed",
                },
                {"",
                        "I need to face Karamel",
                },
                {"",
                        "I need to face Dessourt",
                },
                {"",
                        "I need to face the Gelatinnoth Mother",
                },
                {"",
                        "I need to face The Culinaromancer",
                },
                {""},
                {"",
                        "I defeated all the monsters. I need",
                        "to speak to Gipsy again.."
                },
                {""},
        };
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{4626, 1316, 4810, 5082};
    }

    @Override
    public void getEndDialogue(Player player, int npcId) {
        int id = player.getDialogue().id();

        if (id == 39) {
            increaseStage(player);
            sendDialogue(player, 41);
        } else if (id == 50) {
            player.getInventory().add(new Item(995, 100));
            player.getPacketSender().sendInterfaceRemoval();
        } else if (id == 55) {
            boolean hasAll = player.getInventory().contains(INGREDIENTS);

            sendDialogue(player, hasAll ? 58 : 57);
        } else if (id == 61) {
            increaseStage(player);
            player.getPacketSender().sendInterfaceRemoval();
            if (player.getInventory().contains(INGREDIENTS)) {
                player.getInventory().delete(INGREDIENTS);
            }
        } else if (id == 65) {
            if (player.getInventory().contains(ASGARDIAN_ALE_COST)) {
                player.getInventory().delete(ASGARDIAN_ALE_COST);
                sendDialogue(player, 67);
                player.getInventory().add(1905, 1);
            } else {
                player.getPacketSender().sendMessage("You don't have 200 coins to purchase the ale.");
                player.getPacketSender().sendInterfaceRemoval();
            }
        } else if (id == 81) {
            if (!player.getInventory().contains(ASGOLDIAN_ALE)) {
                sendDialogue(player, 83);
            } else {
                sendDialogue(player, 84);
                player.getInventory().delete(ASGOLDIAN_ALE);
            }
        } else if (id == 93) {
            increaseStage(player);
            player.getPacketSender().sendInterfaceRemoval();
        } else if(id == 99) {
            increaseStage(player);
            QuestManager.complete(player, quest, new String[]{"Full Access to", "Culinaromancer's Chest"}, 7462);
        }
    }

    public static void spawnMonster(Player p) {
        int stage = QuestManager.getStage(p, "Recipe for Disaster");

        int monster = RFDBossWave.FOR_ID.get(stage);

        int height = (p.getIndex() * 4) + 2;

        NPC n = NPCFactory.INSTANCE.create(monster, BOSS_SPAWN.clone().transform(0, 0, height));

        p.instancedArea = new RFDFinalBossArea();

        TaskManager.submit(new Task(5 ) {
            @Override
            protected void execute() {
                World.getNpcAddQueue().add(n);
                p.instancedArea.addAgent(n);
                stop();
            }
        });
    }

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int type) {
        switch (npc.getId()) {
            case 5082:
                if (getStage(player) >= 6 && getStage(player) <= 11) {
                    new DialogueBuilder(DialogueType.NPC_STATEMENT)
                            .setText("Are you ready?")
                            .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                            .firstOption("Yes.", player1 -> {
                                int height = (player.getIndex() * 4) + 2;

                                player.moveTo(CULI_BOSS_AREA.clone().transform(0, 0, height));

                                if (player.instancedArea != null) {
                                    player.instancedArea.destroy();
                                }

                                spawnMonster(player);

                                player.getPacketSender().sendInterfaceRemoval();
                            }).secondOption("Maybe later.", player1 -> {
                                player.getPacketSender().sendInterfaceRemoval();
                            }).start(player);
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        if (object.getPosition().sameAs(new Position(3207, 3217))) {
            if (getStage(player) == 2) {
                sendFreezeCutScene(player);
                return true;
            } else if (getStage(player) == 3) {
                player.moveToByFadeScreen(player, FROZEN_ROOM.clone().transform(0, 0, player.getIndex() * 4), "");
                return true;
            } else if (getStage(player) == 4) {
                sendUnfreezeCutScene(player);
                return true;
            } else if (getStage(player) == 5) {
                player.moveToByFadeScreen(player, FROZEN_ROOM.clone().transform(0, 0, player.getIndex() * 4), "");
                spawnGipsy(player);
                return true;
            } else if (getStage(player) >= 6 && !QuestManager.hasCompletedQuest(player, "Recipe for Disaster")) {
                player.moveToByFadeScreen(player, CUT_SCENE_COORDS, "");
                return true;
            }
             return false;
        }
        switch (object.getId()) {
            case 12356:
                player.moveToByFadeScreen(player, CUT_SCENE_COORDS, "");
                return true;
            case 12351:
                player.moveToByFadeScreen(player, KITCHEN, "");
                return true;
            case 57:
                player.moveToByFadeScreen(player, new Position(2876, 9879), "");
                return true;
            case 56:
                player.moveToByFadeScreen(player, new Position(2876, 3482), "");
                return true;
        }
        return false;
    }

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if ((use.getId() == 995 && usedWith.getId() == 1905)
                || use.getId() == 1905 && usedWith.getId() == 995) {
            player.getInventory().delete(995, 1);
            player.getInventory().delete(1905, 1);
            player.getInventory().add(7508, 1);
            player.getPacketSender().sendMessage("You add a coin to the ale and turn it into an Asgoldian Ale.");
            player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleItemInteraction(Player player, Item item, int type) {
        switch (item.getId()) {
            case 7508:
                player.getPacketSender().sendMessage("I shouldn't drink this..");
                return true;
        }
        return false;
    }

    @Override
    public boolean handleEquipItemInteraction(Player player, Item item, int slot) {
        if (!cantEquipGlove(player, item.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean hasRequirements(Player player) {
        if (!QuestManager.hasCompletedQuest(player, "Cook's Assistant")) {
            return false;
        }
        return true;
    }

    @Override
    public Position getTeleport() {
        return null;
    }
}
