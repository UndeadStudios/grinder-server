package com.grinder.net.packet.impl;

import com.grinder.Server;
import com.grinder.game.World;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.dueling.DuelRule;
import com.grinder.game.content.item.MorphItems;
import com.grinder.game.content.minigame.MinigameManager;
import com.grinder.game.content.minigame.castlewars.CastleWars;
import com.grinder.game.content.minigame.impl.agility.AgilityArenaManager;
import com.grinder.game.content.minigame.impl.inferno.InfernoManager;
import com.grinder.game.content.minigame.pestcontrol.PestControl;
import com.grinder.game.content.minigame.warriorsguild.WarriorsGuild;
import com.grinder.game.content.miscellaneous.TravelSystem;
import com.grinder.game.content.miscellaneous.spirittree.SpiritTree;
import com.grinder.game.content.miscellaneous.spirittree.SpiritTreeData;
import com.grinder.game.content.object.*;
import com.grinder.game.content.object.climbing.ClimbObjectActions;
import com.grinder.game.content.pvm.MonsterKillTracker;
import com.grinder.game.content.pvp.WildernessScoreBoard;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.content.quest.impl.WitchsPotion;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.impl.Smithing.Bar;
import com.grinder.game.content.skill.skillable.impl.Smithing.EquipmentMaking;
import com.grinder.game.content.skill.skillable.impl.Thieving.StallThieving;
import com.grinder.game.content.skill.skillable.impl.agility.Agility;
import com.grinder.game.content.skill.skillable.impl.agility.obstacle.impl.*;
import com.grinder.game.content.skill.skillable.impl.cooking.Cooking;
import com.grinder.game.content.skill.skillable.impl.crafting.Pottery;
import com.grinder.game.content.skill.skillable.impl.crafting.SpinningWheel;
import com.grinder.game.content.skill.skillable.impl.crafting.Weaving;
import com.grinder.game.content.skill.skillable.impl.hunter.birdhouse.BirdHouseActions;
import com.grinder.game.content.skill.skillable.impl.magic.SpellCasting;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.content.skill.skillable.impl.runecrafting.abyss.Rift;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.definition.ObjectDefinition;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.movement.task.WalkToAction;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Color;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.game.entity.object.ClippedMapObjects;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.message.decoder.ObjectActionMessageDecoder;
import com.grinder.game.message.impl.ObjectActionMessage;
import com.grinder.game.model.*;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.areas.impl.BossInstances;
import com.grinder.game.model.areas.impl.WildernessArea;
import com.grinder.game.model.areas.instanced.BryophytaCave;
import com.grinder.game.model.areas.instanced.OborCave;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.interfaces.dialogue.*;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.model.item.container.shop.ShopManager;
import com.grinder.game.model.passages.Passage;
import com.grinder.game.model.passages.PassageCategory;
import com.grinder.game.model.passages.PassageManager;
import com.grinder.game.model.passages.PassageMode;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.ForceMovementTask;
import com.grinder.game.task.impl.TimedObjectReplacementTask;
import com.grinder.net.packet.PacketConstants;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.net.packet.interaction.PacketInteractionManager;
import com.grinder.util.*;
import com.grinder.util.debug.DebugManager;
import com.grinder.util.time.TimeUnits;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.grinder.util.ObjectID.*;

/**
 * This packet listener is called when a player clicked on a game object.
 *
 * @author relex lawl
 */
public class ObjectActionPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {

        final ObjectActionMessage message = ObjectActionMessageDecoder.Companion.decode(packetOpcode, packetReader);
        final int objectId = message.getObjectId();
        final int objectX = message.getX();
        final int objectY = message.getY();

        if (player == null || player.getHitpoints() <= 0)
            return;

        if(player.isTeleporting() && player.getTeleportingType() == TeleportType.HOME)
            player.stopTeleporting();

        if (player.busy())
            return;
        if (player.BLOCK_ALL_BUT_TALKING)
            return;
        if (player.isInTutorial())
            return;
        if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD)
            return;
        if (!MorphItems.INSTANCE.notTransformed(player, "", false, true))
            return;
        if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false) )
            return;
        if (!EntityExtKt.passedTime(player, Attribute.LAST_ACTION_BUTTON, 600, TimeUnit.MILLISECONDS, false, false))
            return;

        final Position position = new Position(objectX, objectY, player.getPosition().getZ());

        final ObjectDefinition objectDefinition = ObjectDefinition.forId(objectId);

        if (objectDefinition == null) {
            Server.getLogger().info("ObjectDefinition for Object {id = "+objectId+"} was not found.");
            return;
        }

        Optional<GameObject> object = World.findObject(player, objectId, position);

        if (object.isEmpty()) {
            Server.getLogger().info("Object {id = "+objectId+", name = '"+objectDefinition.name+"'} does not exist at "+position.compactString()+", "+player+" is at "+player.getPosition().compactString()+"!");
            return;
        }

        player.getPacketSender().sendInterfaceRemoval();

        player.setEntityInteraction(null);


        final ObjectActions.ObjectActionDetails actionDetails = new ObjectActions.ObjectActionDetails(objectId, objectX, objectY, packetOpcode, object.get());

        // TODO: Convert old click methods to new system
        switch (packetOpcode) {
            case PacketConstants.OBJECT_FIRST_CLICK_OPCODE:
                if(!ObjectActions.INSTANCE.handleClick(player, actionDetails, ObjectActions.ClickAction.Type.FIRST_OPTION, false))
                    firstClick(player, objectX, objectY, objectId, position, object.get(), objectDefinition, false);
                break;
            case PacketConstants.OBJECT_SECOND_CLICK_OPCODE:
                if(!ObjectActions.INSTANCE.handleClick(player, actionDetails, ObjectActions.ClickAction.Type.SECOND_OPTION, false))
                    secondClick(player, objectId, position, object.get(), objectDefinition, false);
                break;
            case PacketConstants.OBJECT_THIRD_CLICK_OPCODE:
                if(!ObjectActions.INSTANCE.handleClick(player, actionDetails, ObjectActions.ClickAction.Type.THIRD_OPTION, false))
                    thirdClick(player, objectId, position, object.get(), objectDefinition, false);
                break;
            case PacketConstants.OBJECT_FOURTH_CLICK_OPCODE:
                if(!ObjectActions.INSTANCE.handleClick(player, actionDetails, ObjectActions.ClickAction.Type.FOURTH_OPTION, false))
                    fourthClick(player, objectId, position, object.get(), objectDefinition, false);
                break;
            case PacketConstants.OBJECT_FIFTH_CLICK_OPCODE:
                if(!ObjectActions.INSTANCE.handleClick(player, actionDetails, ObjectActions.ClickAction.Type.FIFTH_OPTION, false))
                    fifthClick(player, objectId, position, object.get(), objectDefinition, false);
                break;
        }
    }

    /**
     * Handles the first click option on an object.
     */
    private static void firstClick(Player player, int x, int y, int id, Position position, GameObject object, ObjectDefinition def, boolean executeImmediately) {

        player.sendDevelopersMessage("First click object: " + object);

        final Executable objectClickAction = new Executable() {
            @Override
            public void execute() {

                onExecutableStart(player, object, def);

                DebugManager.debug(player, "object-option", "1: "+object.getId()+", pos: "+object.getPosition().toString());

                if(PacketInteractionManager.handleObjectInteraction(player, object, 1)) {
                    return;
                }

                // Areas
                if (player.getArea() != null) {
                    if (player.getArea().handleObjectClick(player, object, 1)) {
                        return;
                    }
                }

                if (player.getLocalObject(object.getId(), new Position(object.getX(), object.getY(), player.getPosition().getZ())).isEmpty() && (!ClippedMapObjects.exists(object) || !ObjectManager.existsAt(object.getId(), object.getPosition()))) {
                    return;
                }


                if(PassageManager.handle(player,object))
                    return;

                if(PestControl.handleObject(player,object, 1))
                    return;

                if(Rift.Companion.handleClick(object.getId(), player)) {
                    return;
                }

                if (SkillUtil.startSkillable(player, object, 1))
                    return;

                if (InfernoManager.clickObject(player, object)) {
                    return;
                }
                if (Agility.handleObstacle(player, object)) {
                    return;
                }
                if (Cooking.useCookingObject(player, object)) {
                    return;
                }

                if (player.getFarming().handleObjectOptions(new Position(x, y))) {
                    return;
                }
                if (object.getId() == ObjectID.BROKEN_RAFT) {
                    LumbridgeGrappleAgilityObstacle lumbridgeGrappleAgilityObstacle = new LumbridgeGrappleAgilityObstacle(player);
                    return;
                }
                if (object.getId() == ObjectID.PILLAR_63) {
                    GodwarsGrappleAgilityObstacle godwarsGrappleAgilityObstacle = new GodwarsGrappleAgilityObstacle(player);
                    return;
                }
                if (object.getId() == ObjectID.GODWARS_ROPE_ROCK) {
                    GodwarsGrappleRockAgilityObstacle godwarsGrappleRockAgilityObstacle = new GodwarsGrappleRockAgilityObstacle(player);
                    return;
                }
                if (object.getId() == ObjectID.GODWARS_ROPE_ROCK_2) {
                    GodwarsGrappleRock2AgilityObstacle godwarsGrappleRock2AgilityObstacle = new GodwarsGrappleRock2AgilityObstacle(player);
                    return;
                }
                if (object.getId() == ICE_BRIDGE) {
                    GodwarsBridgeAgilityObstacle godwarsBridgeAgilityObstacle = new GodwarsBridgeAgilityObstacle(player);
                    return;
                }

                if (CastleWars.processClick(player, id, x, y, object.getPosition().getZ(), object.getFace(), object.getObjectType(), 1)) {
                    return;
                }

                if (object.getId() == 32153) {
                    Position playerPosition = player.getPosition();
                    if (playerPosition.getY() <= 5080 && playerPosition.getY() >= 5071) {
                        if (playerPosition.getX() == 1562) {
                            player.getMotion().enqueuePathToWithoutCollisionChecks(1560, playerPosition.getY());
                        } else if (playerPosition.getX() == 1560) {
                            player.getMotion().enqueuePathToWithoutCollisionChecks(1562, playerPosition.getY());
                        } else if (playerPosition.getX() == 1573) {
                            player.getMotion().enqueuePathToWithoutCollisionChecks(1575, playerPosition.getY());
                        } else if (playerPosition.getX() == 1575) {
                            player.getMotion().enqueuePathToWithoutCollisionChecks(1573, playerPosition.getY());
                        }
                    }

                    if (playerPosition.getX() >= 1545 && playerPosition.getX() <= 1589) {
                        if (playerPosition.getY() == 5089) {
                            player.getMotion().enqueuePathToWithoutCollisionChecks(playerPosition.getX(), 5087);
                        } else if (playerPosition.getY() == 5087) {
                            player.getMotion().enqueuePathToWithoutCollisionChecks(playerPosition.getX(), 5089);
                        }
                    }
                }

                if (def != null) {

                    String name = def.getName();

                    if (name == null) {
                        if(def.transforms != null) {
                            for (int transformedId : def.transforms) {
                                ObjectDefinition transformed = ObjectDefinition.forId(transformedId);
                                if (transformed == null)
                                    continue;
                                if (transformed.name != null && transformed.actions != null) {
                                    name = transformed.name;
                                    break;
                                }
                            }
                        }
                    }

                    if(name == null)
                        return;

                    name = name.toLowerCase();

                    if (name.contains("deposit box")) {
                    	player.getSafeDeposit().open();
                    	return;
                    }
                }

                if (id == TICKET_DISPENSER || id == TICKET_DISPENSER_2) {
                    AgilityArenaManager.pressedObject(player, position);
                    return;
                }

                if (ClimbObjectActions.handleClimbObject(player, object, def, position, 0))
                    return;

                switch (id) {
                    case WitchsPotion.CAULDRON:
                        WitchsPotion.drinkFromCauldron(player);
                        break;
                    case 15597:
                        player.sendMessage("This cannon is broken and needs to be repaired.");
                    break;
                  /*  case 26198:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 2, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        EntityExtKt.markTime(player, Attribute.LAST_ACTION_BUTTON);
                        if (PlayerExtKt.tryRandomEventTrigger(player, 1.3F))
                            return;
                        if (player.getInventory().countFreeSlots() <= 3) {
                            player.sendMessage("You must have at least 3 free inventory slots before doing so.");
                            return;
                        }
                        // Face stall..
                        player.setPositionToFace(object.getPosition());
                        player.performAnimation(new Animation(832, 5));
                        // Play sound
                        player.getPacketSender().sendSound(Sounds.STEAL_STALL);
                        // Reset flag
                        player.getPacketSender().sendMinimapFlagRemoval();
                        TaskManager.submit(new Task(1) {
                            @Override
                            protected void execute() {
                                stop();
                                AchievementManager.processFor(AchievementType.NEED_FOOD, player);
                                // Add experience..
                                player.getSkillManager().addExperience(Skill.THIEVING,
                                        (int) (33 * 1.3));
                                SkillTaskManager.perform(player, object.getId(), 1, SkillMasterType.THIEVING);
                                PetHandler.onSkill(player, Skill.THIEVING);

                                player.getPoints().increase(AttributeManager.Points.STALL_STEALS, 1); // Increase points

                                if ((player.getPoints().get(AttributeManager.Points.STALL_STEALS) % 100) == 0) {
                                    player.sendMessage("Total stall count: @red@" + player.getPoints().get(AttributeManager.Points.STALL_STEALS) + "</col>.");
                                }

                                if (Misc.random(2) == 1) {
                                    player.getInventory().add(new Item(CAKE_2, Misc.random(5)));
                                } else {
                                    player.getInventory().add(new Item(CHOCOLATE_CAKE_2, Misc.random(5)));
                                }
                                player.getInventory().add(new Item(995, 25_000 + Misc.random(100_000)));
                                player.getInventory().add(new Item(13307, 50 + Misc.random(25)));
                            }
                        });
                        break;*/
                    case 34582:
                        player.sendMessage("The chest looks empty from the inside.");
                        break;
                    case 42934:
                    case 12348:
                    case 1524:
                    case 1521:
                        player.getPacketSender().sendMessage("This door is currently locked.", 1000);
                        player.getPacketSender().sendSound(Sounds.USE_KEY_ON_LOCKED_DOOR);
                        break;
                    case 39620:
                        player.getPacketSender().sendMessage("This anvil cannot be used until it's fixed.", 1000);
                        break;
                    case POTTERY_OVEN:
                    case POTTERY_OVEN_2:
                    case POTTERY_OVEN_3:
                        Pottery.openPotteryOvenInterface(player);
                        break;
                    case SOUL_ALTAR:
                        player.sendMessage("You don't have enough dark fragments to bind into the temple.");
                        break;
                    case 32153:
                    //if (object.getId() == 32153) {
                        Position playerPosition = player.getPosition();
                        if (playerPosition.getY() <= 5080 && playerPosition.getY() >= 5071) {
                            if (playerPosition.getX() == 1562) {
                                player.getMotion().enqueuePathToWithoutCollisionChecks(1560, playerPosition.getY());
                            } else if (playerPosition.getX() == 1560) {
                                player.getMotion().enqueuePathToWithoutCollisionChecks(1562, playerPosition.getY());
                            } else if (playerPosition.getX() == 1573) {
                                player.getMotion().enqueuePathToWithoutCollisionChecks(1575, playerPosition.getY());
                            } else if (playerPosition.getX() == 1575) {
                                player.getMotion().enqueuePathToWithoutCollisionChecks(1573, playerPosition.getY());
                            }
                        }

                        if (playerPosition.getX() >= 1545 && playerPosition.getX() <= 1589) {
                            if (playerPosition.getY() == 5089) {
                                player.getMotion().enqueuePathToWithoutCollisionChecks(playerPosition.getX(), 5087);
                            } else if (playerPosition.getY() == 5087) {
                                player.getMotion().enqueuePathToWithoutCollisionChecks(playerPosition.getX(), 5089);
                            }
                        }
                    //}
                        break;
                    case TRAWLER_NET_26:
                        player.sendMessage("You search the net and find nothing of interest.");
                        break;
                    case LOGS: // Lumbridge take-axe near chickens
                        new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("I better not be touching something that isn't for me.")
                                .setExpression(DialogueExpression.THINKING)
                                .start(player);
                        break;
                    case HAND_HOLDS:
                        player.performAnimation(new Animation(828, 25));
                        player.BLOCK_ALL_BUT_TALKING = true;
                        TaskManager.submit(new Task(2) {
                            @Override
                            public void execute() {
                                stop();
                                player.BLOCK_ALL_BUT_TALKING = false;
                                player.moveTo(new Position(2824, 3120, 0));
                            }
                        });
                        break;
                    case 13477:
                        DialogueManager.sendStatement(player, "This fishing spot seems to be empty with no fishes.");
                        break;
                    case BOULDER_20: //godwars Boulder
                        if (player.getSkillManager().getCurrentLevel(Skill.STRENGTH) < 60) {
                            DialogueManager.sendStatement(player, "You need at least 60 Strength to use this shortcut.");
                            return;
                        }
                        player.BLOCK_ALL_BUT_TALKING = true;
                        if (player.getPosition().getY() <= 3717) {
                            if (!player.getPosition().sameAs(new Position(2898, 3715, 0)))
                                player.moveTo(new Position(2898, 3715, 0));


                            TaskManager.submit(new Task(1, true) {
                                int delay = 0;
                                @Override
                                public void execute() {
                                    if (delay == 0) {
                                        player.setPositionToFace(new Position(2898, 3720, 0));
                                        //player.getPacketSender().sendObjectAnimation(2898, 3716, 10, 0, new Animation(5197));
                                    }
                                    if (delay == 2) {
                                        TaskManager.submit(new ForceMovementTask(player, 6,
                                                new ForceMovement(new Position(2898, 3715, 0), new Position(0, 4), 6, 360,
                                                        0, 6983)));
                                        player.playSound(new Sound(3855));
                                    }
                                    if (delay == 3) {
                                        ObjectManager.add(DynamicGameObject.createPublic(BOULDER_21, new Position(2898, 3716)), true);
                                    }

                                    if (delay == 14) {
                                        ObjectManager.add(DynamicGameObject.createPublic(BOULDER_20, new Position(2898, 3716)), true);
                                        player.BLOCK_ALL_BUT_TALKING = false;
                                        stop();
                                    }
                                    delay++;
                                }
                            });

                        } else {
                            if (!player.getPosition().sameAs(new Position(2898, 3719, 0)))
                                player.moveTo(new Position(2898, 3719, 0));


                            TaskManager.submit(new Task(1, true) {
                                int delay = 0;
                                @Override
                                public void execute() {
                                    if (delay == 0) {
                                        player.setPositionToFace(new Position(2898, 3720, 0));
                                        //player.getPacketSender().sendObjectAnimation(2898, 3716, 10, 0, new Animation(5197));
                                    }
                                    if (delay == 2) {
                                        TaskManager.submit(new ForceMovementTask(player, 6,
                                                new ForceMovement(new Position(2898, 3719, 0), new Position(0, -4), 6, 360,
                                                        2, 6984)));
                                        player.playSound(new Sound(3855));
                                    }
                                    if (delay == 3) {
                                        ObjectManager.add(DynamicGameObject.createPublic(BOULDER_21, new Position(2898, 3716)), true);
                                    }

                                    if (delay == 14) {
                                        ObjectManager.add(DynamicGameObject.createPublic(BOULDER_20, new Position(2898, 3716)), true);
                                        player.BLOCK_ALL_BUT_TALKING = false;
                                        stop();
                                    }
                                    delay++;
                                }
                            });
                        }
                        break;

                    case LITTLE_CRACK:

                        player.BLOCK_ALL_BUT_TALKING = true;
                        TaskManager.submit(new Task(1, true) {
                            int delay = 0;

                            @Override
                            public void execute() {
                                if (delay == 0) {
                                    player.performAnimation(new Animation(7041));
                                    player.getPacketSender().sendInterface(18460);
                                }
                                if (delay == 3) {
                                    if (player.getPosition().sameAs(new Position(2899, 3713, 0))) {
                                        player.setPositionToFace(new Position(2904, 3721, 0));
                                        player.moveTo(new Position(2904, 3720, 0));
                                    } else {
                                        player.setPositionToFace(new Position(2898, 3713, 0));
                                        player.moveTo(new Position(2899, 3713, 0));
                                    }
                                }
                                if (delay == 5) {
                                    player.getPacketSender().sendInterface(65535);
                                    player.BLOCK_ALL_BUT_TALKING = false;
                                    stop();
                                }
                                delay++;
                            }
                        });

                        break;

                    case 15765:
                        player.sendMessage("This trapdoor seems locked from the inside.");
                        break;
                    case 30380: // Lizardman caves
                        player.moveTo(new Position(1305, 9973, 0));
                        break;
                    case 30381: // Lizardman caves
                        player.moveTo(new Position(1309, 3573, 0));
                        break;
                    case 29681: // Redwood platinum member island
                    case 29682:
                        //Determines up or down
                        int z = object.getId() == 29681 ? 2 : 1;

                        //Just a random range to lively things up ;p
                        int x = 1999 + Misc.random(1);
                        //Determines north or south
                        int y = object.getY() > 3704 ? 3706 : 3703;

                        //Copied the Pipe Animation - Will look into the actual animation
                        player.performAnimation(new Animation(749, 2));

                        String direction = z == 2 ? "up" : "down";
                        player.sendMessage("You climb " + direction + " the tree!");

                        player.BLOCK_ALL_BUT_TALKING = true;

                        player.setTeleportingTask(new Task(2) {
                            @Override
                            protected void execute() {
                                player.moveTo(new Position(x, y, z));
                                player.BLOCK_ALL_BUT_TALKING = false;
                                stop();
                            }
                        });
                        TaskManager.submit(player.getTeleportingTask());
                        break;
                    case 3831:
                    case 40417:
                        player.performAnimation(new Animation(827));

                        player.BLOCK_ALL_BUT_TALKING = true;

                        player.setTeleportingTask(new Task(2) {
                            @Override
                            protected void execute() {
                                player.moveTo(new Position(2900, 4449, 0));
                                player.BLOCK_ALL_BUT_TALKING = false;
                                player.sendMessage("You climb down the ladder!");
                                stop();
                            }
                        });
                        TaskManager.submit(player.getTeleportingTask());
                        break;
                    case 31989: // Vorkath boat
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 2, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        EntityExtKt.markTime(player, Attribute.LAST_ACTION_BUTTON);
                        DialogueManager.start(player, 2669);
                        player.setDialogueOptions(new DialogueOptions() {
                            @Override
                            public void handleOption(Player player, int option) {
                                switch (option) {
                                    case 1:
                                        player.getPacketSender().sendInterface(3281);
                                        player.BLOCK_ALL_BUT_TALKING = true;
                                        TaskManager.submit(new Task(4) {
                                            @Override
                                            public void execute() {
                                                player.getPacketSender().sendMessage("The boat crashed during the sail! You're glad to be okay.");
                                                player.getPacketSender().sendInterfaceRemoval();
                                                player.BLOCK_ALL_BUT_TALKING = false;
                                                player.moveTo(new Position(2643, 3710, 0));
                                                stop();
                                            }
                                        });
                                        break;
                                    case 2:
                                        player.getPacketSender().sendInterfaceRemoval();
                                        break;
                                }
                            }
                        });
                        break;
                    case ObjectID.ROCKS_121:
                        player.sendMessage("I must be very careful and not go down on these rocks.");
                        break;
                    case HAY_BALE:
                    case HAY_BALES:
                    case HAY_BALE_2:
                    case HAY_BALES_2:
                    case HAY_BALE_3:
                    case HAY_BALES_3:
                    case HAY_BALES_4:
                    case HAY_BALES_5:
                    case HAYSTACK:
                    case HAYSTACK_2:
                    case HAYSTACK_3:
                    case HAYSTACK_4:
                        player.performAnimation(new Animation(827));
                        player.sendMessage("You search the " + object.getDefinition().name.toLowerCase() + "...");
                        player.BLOCK_ALL_BUT_TALKING = true;
                        TaskManager.submit(new Task(3) {
                            @Override
                            public void execute() {
                                stop();
                                if (Misc.random(10) <= 2) {
                                    new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                            .setText("Wow! A needle!", "Now what are the chances of finding that?")
                                            .setExpression(DialogueExpression.CURIOUS)
                                            .start(player);
                                    player.getPacketSender().sendSound(2584);
                                    ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.NEEDLE, 1));
                                } else if (Misc.random(10) <= 2) {
                                    player.getCombat().queue(new Damage(1, DamageMask.REGULAR_HIT));
                                    player.getPacketSender().sendSound(player.getAppearance().isMale() ? (518 + Misc.random(4)) : 509);
                                    player.sendMessage("You accidently hurt yourself while searching.");
                                } else {
                                    player.sendMessage("You find nothing of interest.");
                                }
                                    player.BLOCK_ALL_BUT_TALKING = false;
                            }
                        });
                        break;
                    case ObjectID.ROCKS_14:
                    case ObjectID.ROCKS_13:
                        ClippedMapObjects.findObject(id, position).ifPresent(gameObject -> {

                            player.getMotion().clearSteps();

                            if (!player.getEquipment().containsAny(ItemID.ROCK_CLIMBING_BOOTS, ItemID.ROCK_CLIMBING_BOOTS_2)) {
                                player.sendMessage("You need some better boots to climb over these rocks!");
                                return;
                            }

                            final Direction facing = gameObject.getFacing();
                            final Direction diff = player.getDirection(position);

                            if (diff.isPerpendicular()) {

                                if (player.getForceMovement() == null && player.getClickDelay().elapsed(2000)) {
                                    final Position position1 = player.getPosition().copy();

                                    final Position delta = new Position(diff.getX() * 3, diff.getY() * 3);

                                    TaskManager.submit(new ForceMovementTask(player, 3, new ForceMovement(position1, delta, 0, 90, diff == Direction.NORTH || diff == Direction.SOUTH ? (delta.getY() > 0 ? 0 : 2)
                                            : (delta.getX() > 0 ? 5 : 7), id == ObjectID.ROCKS_14 ? 6131 : 3064)));
                                    TaskManager.submit(new Task(3) {
                                        @Override
                                        protected void execute() {
                                            player.say("Ouch!");
                                            player.performAnimation(new Animation(3103));
                                            stop();
                                        }
                                    });
//
                                    player.getClickDelay().reset();
                                }
                            }

                        });
                        break;
                    case LADDER_135:
                        player.sendMessage("I cannot climb down here.");
                    break;
                    case ObjectID.ROCKS_12:
                        ClippedMapObjects.findObject(id, position).ifPresent(gameObject -> {

                            player.getMotion().clearSteps();

                            if (!player.getEquipment().containsAny(ItemID.ROCK_CLIMBING_BOOTS, ItemID.ROCK_CLIMBING_BOOTS_2)) {
                                player.sendMessage("You need some better boots to climb over these rocks!");
                                return;
                            }

                            final Direction facing = gameObject.getFacing();
                            final Direction diff = player.getDirection(position);

                            if (diff.isPerpendicular()) {

                                if (player.getForceMovement() == null && player.getClickDelay().elapsed(2000)) {
                                    final Position position1 = player.getPosition().copy();

                                    final Position delta = new Position(diff.getX() * 2, diff.getY() * 2);

                                    TaskManager.submit(new ForceMovementTask(player, 2, new ForceMovement(position1, delta, 0, 90, diff == Direction.NORTH || diff == Direction.SOUTH ? (delta.getY() > 0 ? 0 : 2)
                                            : (delta.getX() > 0 ? 5 : 7), 5038)));
//
                                    player.getClickDelay().reset();
                                }
                            }

                        });
                        break;
                    case ObjectID.STILE_2:
                        ClippedMapObjects.findObject(id, position).ifPresent(gameObject -> {

                            player.getMotion().clearSteps();

                            if (player.getForceMovement() == null && player.getClickDelay().elapsed(2000)) {
                                final Position position1 = player.getPosition().copy();

                                final Direction sideNS = (position1.getY() <= 3562 ? Direction.SOUTH : Direction.NORTH);

                                final Position[] delta = {new Position(0, sideNS.getOpposite().getY())};

                                final int walkX = Integer.compare(2817, position1.getX());
                                final int walkY = sideNS == Direction.SOUTH ? Integer.compare(3562, position1.getY()) : Integer.compare(3563, position1.getY());

                                if (walkX != 0 || walkY != 0) {
                                    player.getMotion().enqueuePathToWithoutCollisionChecks(walkX, walkY);
                                    TaskManager.submit(new Task(2, false) {
                                        @Override
                                        protected void execute() {
                                            delta[0] = new Position(0, player.getPosition().getY() <= 3562 ? 1 : -1);
                                            player.setEntityInteraction(gameObject);
                                            TaskManager.submit(new ForceMovementTask(player, 2, new ForceMovement(player.getPosition().clone(), delta[0], 0, 70, delta[0].getY() > 0 ? 0 : 2, 840)));
                                            stop();
                                        }
                                    });
                                } else
                                    TaskManager.submit(new ForceMovementTask(player, 2, new ForceMovement(new Position(0, player.getPosition().getY() <= 3562 ? 1 : -1), delta[0], 0, 70, delta[0].getY() > 0 ? 0 : 2, 840)));
//
                                player.getClickDelay().reset();
                            }
                        });
                        break;

                    case MAGICAL_ANIMATOR:
                        WarriorsGuild.handleAnimator(player);
                        break;
                    case HANDHOLDS_42009:
                        Direction climb = object.getPosition().getY() == 3687 ? Direction.WEST : Direction.EAST;
                        TaskManager.submit(new ScaleCliffsideObstacle(player,
                                Direction.getDirection(player.getPosition(), object.getPosition()) == climb,
                                6, climb));
                        break;
                    case HANDHOLDS:
                        Direction climbDir = object.getPosition().getY() == 3690 ? Direction.EAST : Direction.WEST;
                        TaskManager.submit(new ScaleCliffsideObstacle(player,
                                Direction.getDirection(player.getPosition(), object.getPosition()) == climbDir,
                                6, climbDir));
                        break;
                    case 8720:
                    case POLL_BOOTH_14:
                    case 26818:
                    case 26813:
                    case 26814:
                    case 26815:
                    case 28617:
                    case 26816:
                    case 26809:
                    case 26820:
                    case 32138:
                    case 32649:
                        player.sendMessage("There are no running polls at the moment.");
                        break;
                    case 42967:
                        if (player.getX() >= object.getX()) {
                            player.sendMessage("You sense a strange energy preventing you from going through this portal.");
                            return;
                        }
                        new DialogueBuilder(DialogueType.STATEMENT).setText("Only the bravest warriors should enter this chamber.").
                                add(DialogueType.OPTION).firstOption("I dare.", (p) -> {
                            p.moveTo(new Position(object.getX(), p.getY()));
                            p.getPacketSender().sendInterfaceRemoval();
                        }).secondOption("Erm.. let's do some skilling instead...", (p) -> {
                            p.getPacketSender().sendInterfaceRemoval();
                        }).start(player);
                        break;
                    case 34727:
                        TeleportHandler.teleport(player, new Position(3089, 3469, 0), TeleportType.NORMAL, false, true);
                        break;
                    case 34733:
                        new DialogueBuilder(DialogueType.STATEMENT).setText("A challenge awaits you, do you have what it takes?.").
                                add(DialogueType.OPTION).firstOption("Yes, this halloween is mine!", (p) -> {
                            TeleportHandler.teleport(player, new Position(2720 + Misc.random(1), 4315, 1), TeleportType.MAGE_OF_ZAMORAK, false, true);
                            p.getPacketSender().sendInterfaceRemoval();
                        }).secondOption("No please I'll stick with the candy.", (p) -> {
                            p.getPacketSender().sendInterfaceRemoval();
                        }).start(player);
                        break;
                    case 40383:
                    case 40384:
                    case 40385:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 2, TimeUnit.SECONDS, false, false)) {
                            return;
                        }
                        if (player.getInventory().countFreeSlots() < 1) {
                            DialogueManager.sendStatement(player, "You need to have a free inventory slots to take any candy.");
                            return;
                        }
                        EntityExtKt.markTime(player, Attribute.LAST_PRAY);
                        player.performAnimation(new Animation(2142));
                        player.BLOCK_ALL_BUT_TALKING = true;
                        TaskManager.submit(new Task(2) {
                            @Override
                            public void execute() {
                                player.BLOCK_ALL_BUT_TALKING = false;
                                player.getPacketSender().sendMessage("You take a candy.");
                                player.getInventory().add(new Item(24980 + Misc.random(8), 1));
                                player.getPacketSender().sendSound(Sounds.PICKUP_ITEM);
                                stop();
                            }
                        });
                        break;
                    case SPADE:
                        player.performAnimation(new Animation(2142));
                        player.BLOCK_ALL_BUT_TALKING = true;
                        TaskManager.submit(new Task(2) {
                            @Override
                            public void execute() {
                                player.BLOCK_ALL_BUT_TALKING = false;
                                player.getPacketSender().sendMessage("You take a spade.");
                                player.getInventory().add(new Item(ItemID.SPADE, 1));
                                player.getPacketSender().sendSound(Sounds.PICKUP_ITEM);
                                stop();
                            }
                        });
                        break;
                    case CADAVA_BUSH:
                    case CADAVA_BUSH_2:
                    case CADAVA_BUSH_3:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_BUSH_PICKUP, 5, TimeUnit.SECONDS, false, true)) {
                            player.sendMessage("The bush appears to be empty.");
                            return;
                        }

                        if (Misc.random(5) == 1) {
                            EntityExtKt.markTime(player, Attribute.LAST_BUSH_PICKUP);
                        }

                        player.performAnimation(new Animation(2281));
                        player.BLOCK_ALL_BUT_TALKING = true;
                        TaskManager.submit(new Task(2) {
                            @Override
                            public void execute() {
                                player.BLOCK_ALL_BUT_TALKING = false;
                                player.getPacketSender().sendMessage("You pick red berries from the bush.");
                                player.getInventory().add(new Item(ItemID.CADAVA_BERRIES, 1));
                                player.getPacketSender().sendSound(Sounds.PICKUP_ITEM);
                                stop();
                            }
                        });
                        break;
                    case REDBERRY_BUSH:
                    case REDBERRY_BUSH_2:
                    case REDBERRY_BUSH_3:
                    case REDBERRY_BUSH_4:
                    case REDBERRY_BUSH_5:
                    case REDBERRY_BUSH_6:
                    case REDBERRY_BUSH_7:
                    case REDBERRY_BUSH_8:
                    case REDBERRY_BUSH_9:
                    case REDBERRY_BUSH_10:
                    case REDBERRY_BUSH_11:
                    case REDBERRY_BUSH_12:
                    case REDBERRY_BUSH_13:
                    case REDBERRY_BUSH_14:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_BUSH_PICKUP, 5, TimeUnit.SECONDS, false, true)) {
                            player.sendMessage("The bush appears to be empty.");
                            return;
                        }

                        if (Misc.random(5) == 1) {
                            EntityExtKt.markTime(player, Attribute.LAST_BUSH_PICKUP);
                        }

                        player.performAnimation(new Animation(2281));
                        player.BLOCK_ALL_BUT_TALKING = true;
                        TaskManager.submit(new Task(2) {
                            @Override
                            public void execute() {
                                player.BLOCK_ALL_BUT_TALKING = false;
                                player.getPacketSender().sendMessage("You pick red berries from the bush.");
                                player.getInventory().add(new Item(ItemID.REDBERRIES, 1));
                                player.getPacketSender().sendSound(Sounds.PICKUP_ITEM);
                                stop();
                            }
                        });
                        break;
                    case WALL_5:
                        player.sendMessage("I need more strength to be able to push through this door.");
                        break;
                    case ANNAKARL_PORTAL:
                        if (TeleportHandler.checkReqs(player, new Position(3285, 3943, 0), true, false, player.getSpellbook().getTeleportType())) {
                            TeleportHandler.teleport(player, new Position(3285, 3943, 0),
                                    player.getSpellbook().getTeleportType(), true, true);
                        }
                        break;

                    case 31626:
                        player.performAnimation(new Animation(2796));
                        player.getPacketSender().sendSound(2495);
                        player.BLOCK_ALL_BUT_TALKING = true;
                        TaskManager.submit(new Task(2) {
                            @Override
                            public void execute() {
                                player.performAnimation(Animation.DEFAULT_RESET_ANIMATION);
                                player.moveTo(new Position(1936, 9009, 1));
                                player.BLOCK_ALL_BUT_TALKING = false;
                                stop();
                            }
                        });
                        break;

                    case GANGPLANK:
                    case GANGPLANK_2:
                    case GANGPLANK_3:
                    case GANGPLANK_4:
                    case GANGPLANK_5:
                    case GANGPLANK_6:
                    case GANGPLANK_7:
                    case GANGPLANK_8:
                    case GANGPLANK_9:
                    case GANGPLANK_10:
                    case GANGPLANK_11:
                    case GANGPLANK_12:
                    case GANGPLANK_13:
                    case GANGPLANK_14:
                    case GANGPLANK_15:
                    case GANGPLANK_16:
                    case GANGPLANK_17:
                    case GANGPLANK_18:
                    case GANGPLANK_19:
                    case GANGPLANK_20:
                    case GANGPLANK_21:
                    case GANGPLANK_22:
                    case GANGPLANK_23:
                    case GANGPLANK_24:
                    case GANGPLANK_25:
                    case GANGPLANK_26:
                    case GANGPLANK_27:
                    case GANGPLANK_28:
                    case GANGPLANK_29:
                    case GANGPLANK_30:
                    case GANGPLANK_31:
                    case GANGPLANK_32:
                    case GANGPLANK_33:
                    case GANGPLANK_34:
                    case GANGPLANK_35:
                    case GANGPLANK_36:
                    case GANGPLANK_37:
                    case GANGPLANK_38:
                    case GANGPLANK_39:
                    case GANGPLANK_40:
                    case GANGPLANK_41:
                    case GANGPLANK_42:
                    case GANGPLANK_43:
                    case GANGPLANK_44:
                    case GANGPLANK_45:
                    case GANGPLANK_46:
                    case GANGPLANK_47:
                    case GANGPLANK_48:
                    case GANGPLANK_49:
                    case GANGPLANK_50:
                    case GANGPLANK_51:
                    case GANGPLANK_52:
                    case GANGPLANK_53:
                    case GANGPLANK_54:
                    case GANGPLANK_55:
                    case GANGPLANK_56:
                    case GANGPLANK_57:
                    case GANGPLANK_58:
                    case GANGPLANK_59:
                        player.sendMessage("You are not allowed to cross on this plank.");
                        break;

                    case 31558:
                        player.moveTo(new Position(3126, 3833));
                        break;

                    case 31557:
                        player.moveTo(new Position(3075, 3653));
                        break;

                    case 31556:
                        player.moveTo(new Position(3241, 10234));
                        break;

                    case 31555:
                        player.moveTo(new Position(3196, 10056));
                        break;
                    case PORTAL_61:
                        MinigameManager.leaveMinigame(player, MinigameManager.publicMinigame);
                        break;
                    case TRAWLER_NET:
                        player.sendMessage("I don't have the necessary tools to fix this.");
                        break;
                    case ODD_LOOKING_WALL_7:
                        player.sendMessage("I am going to need a tool for this perhaps..");
                        break;
                    case COFFIN:
                        player.getPacketSender().sendMessage("I better not be touching this coffin..", 1000);
                        break;
                    case BALANCING_ROPE_4:
                        player.sendMessage("You can't use the balancing rope from this side.");
                        break;
                    case CRUMBLING_WALL:
                    case CRUMBLING_WALL_2:
                        player.sendMessage("You cannot climb that from this side.");
                        break;
                    case OBSTACLE_PIPE_8:
                    case OBSTACLE_PIPE_9:
                    case OBSTACLE_PIPE_10:
                    case OBSTACLE_PIPE_11:
                        player.sendMessage("You can't enter the pipe from this side.");
                        break;
                    case OBSTACLE_NET_3:
                    case OBSTACLE_NET_4:
                    case OBSTACLE_NET_5:
                        player.sendMessage("You can't do that from here.");
                        break;
                    case ROPESWING_2:
                    case ROPESWING_3:
                    case ROPESWING_4:
                    case ROPESWING_5:
                    case ROPESWING_6:
                    case ROPESWING_7:
                        player.sendMessage("You can't do that from here.");
                        break;
                    case 14245:
                    case 14246:
                        player.sendMessage("The gate is broken and must be repaired.");
                        break;
                    case OLD_WALL:
                        new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("Perhaps I should not be touching this wall as", "it might just fall off.")
                                .setExpression(DialogueExpression.ANNOYED)
                                .start(player);
                        break;
                    case GATE_10:
                    case GATE_31:
                    case GATE_32:
                        player.getPacketSender().sendMessage("This gate is currently locked.", 1000);
                        player.getPacketSender().sendSound(Sounds.USE_KEY_ON_LOCKED_DOOR);
                        break;
                    case BURNT_CHEST:
                    case CHEST_16:
                        player.getPacketSender().sendMessage("The chest is locked.", 1000);
                        player.getPacketSender().sendSound(Sounds.USE_KEY_ON_LOCKED_DOOR);
                        break;
                    case PORTCULLIS_4:
                        TravelSystem.INSTANCE.fadeTravelAction(player, true, true, "You managed to escape and get out.", 2, 6, 4, () -> {
                            player.moveTo(new Position(2873, 9847, 0));
                            player.sendMessage("The gate locks shut behind you.");
                            player.getPacketSender().sendSound(70);
                            return null;
                        });
                    break;
                    case 34514:
                        TravelSystem.INSTANCE.fadeTravelAction(player, true, true, "", 2, 3, 2, () -> {
                            player.moveTo(new Position(1311, 3807, 0));
                            return null;
                        });
                        break;
                    case 34359:
                    case 34513:
                        player.setPositionToFace(new Position(1309, 3807, 0));
                        player.performAnimation(new Animation(2140));
                        player.getPacketSender().sendAreaPlayerSound(1539, 3, 1, 0);

                        TravelSystem.INSTANCE.fadeTravelAction(player, true, true, "", 2, 3, 2, () -> {
                            player.moveTo(new Position(1311, 10188, 0));
                            player.getPacketSender().sendAreaPlayerSound(1541, 3, 1, 0);
                            return null;
                        });
                        break;
                    case MANHOLE_3:
                        player.getPacketSender().sendMessage("The manhole seems to be tightly locked.", 1000);
                        player.getPacketSender().sendSound(Sounds.USE_KEY_ON_LOCKED_DOOR);
                        break;
                    case JUNGLE_PLANT_20:
                        player.sendMessage("Looks like something is hidden inside of this plant...");
                        break;
                    case DARK_HOLE:
                        //player.sendMessage("I'm not going to risk it going down there.");
                        player.moveTo(new Position(3169, 9571, 0));
                        break;
                    case SNOW:
                    case SNOW_2:
                    case SNOW_3:
                    case SNOW_6:
                    case SNOW_7:
                    case SNOW_8:
                    case SNOW_9:
                    case SNOW_10:
                    case SNOW_11:
                    case SNOW_13:
                    case SNOW_14:
                    case SNOW_15:
                    case SNOW_12:
                    case SNOW_4:
                    case SNOW_5:
                        if (!player.getInventory().canHold(new Item(10501, 1))) {
                            player.sendMessage("You don't have enough inventory space.");
                            return;
                        }
                        player.performAnimation(new Animation(5054, 25));
                        player.BLOCK_ALL_BUT_TALKING = true;
                        TaskManager.submit(new Task(8) {
                            @Override
                            public void execute() {
                                player.sendMessage("@red@You gather some snowballs!");
                                player.getInventory().add(new Item(10501, 1 + Misc.random(3)));
                                player.performAnimation(Animation.DEFAULT_RESET_ANIMATION);
                                player.BLOCK_ALL_BUT_TALKING = false;
                                stop();
                            }
                        });
                        break;
                    case AQUARIUM:
                        player.sendMessage("You can't fish here, you don't have a Tiny Net.");
                        break;
                    case ENTRANCE_3:
                    case ENTRANCE_5:
                        if (!player.getArea().equals(AreaManager.DUEL_ARENA)) {
                            return;
                        }
                        if (player.getDueling().getRules()[DuelRule.NO_FORFEIT.ordinal()]) {
                            player.getPacketSender().sendMessage("You can't forfeit from this duel!", 1000);
                            return;
                        }
                        if (player.busy()) {
                            player.sendMessage("You can't forfeit at this moment!");
                            return;
                        }
                        if (player.getCombat().getOpponent() != null || player.getDueling().getInteract() != null) {

                            if (player.getDueling().getInteract().getHitpoints() <= 0 || player.getDueling().getInteract().isDying()
                                    || player.getHitpoints() <= 0 || player.isDying()) {
                                player.getPacketSender().sendMessage("You can't forfeit at this moment!", 1000);
                                return;
                            }
                        }
                        if (player.getStatus() == PlayerStatus.DUELING) {
                            player.setStatus(PlayerStatus.NONE);
                        }
                        if (player.getDueling().inDuel()) {
                            player.getDueling().loseDuel();
                        }
                        player.moveTo(new Position(3361 + Misc.getRandomInclusive(10), 3275, 0));
                        player.getPacketSender().sendMessage("You have forfeit from the duel!");
                        break;
                    case 9370:
                        player.sendMessage("This portal is currently disabled.");
                        break;

                    case STONE_4:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        player.getMotion().clearSteps();
                        player.getPacketSender().sendMinimapFlagRemoval();
                        EntityExtKt.markTime(player, Attribute.LAST_ACTION_BUTTON);
                        if (player.getPosition().getY() <= 10063)
                            player.moveTo(new Position(1613, 10070, 2));
                        else if (player.getPosition().getY() >= 10063)
                            player.moveTo(new Position(1610, 10061, 0));
                break;
                    case PASSAGEWAY_10: // Tarn Caves
                        player.moveTo(new Position(3186, 4626, 0));
                        break;
                    case PASSAGEWAY_6: // Tarn Caves
                        player.moveTo(new Position(3186, 4632, 0));
                        break;
                    case PASSAGEWAY_4: // Tarn Caves
                        player.moveTo(new Position(3185, 4601, 0));
                        break;
                    case PASSAGEWAY_84: // Tarn Caves
                        player.moveTo(new Position(3186, 4612, 0));
                        break;
                    case PASSAGEWAY_12: // Tarn Cave Entrance
                        BossInstances.Companion.instanceDialogue(player, new Position(3147, 4644), BossInstances.MUTANT_TARN, true);
                        //player.moveTo(new Position(3147, 4644, 0));
                        break;
                    case 30236: // CHASM OF FIRE ENTRANCE
                        player.performAnimation(new Animation(827));
                        TaskManager.submit(new Task(2) {
                            @Override
                            public void execute() {
                                player.moveTo(new Position(1435, 10077, 3));
                                stop();
                            }
                        });
                        break;
                    case ALTAR_52: // ALTAR CATACOBMS OF KOUREND DOWN
                        player.performAnimation(new Animation(828));
                        TaskManager.submit(new Task(2) {
                            @Override
                            public void execute() {
                                player.moveTo(new Position(1639, 3673, 0));
                                stop();
                            }
                        });
                        break;
                    case STATUE_91: // STATUE CATACOBMS OF KOUREND UP
                        player.performAnimation(new Animation(827));
                        TaskManager.submit(new Task(2) {
                            @Override
                            public void execute() {
                                player.moveTo(new Position(1665, 10048, 0));
                                stop();
                            }
                        });
                        break;
                    case CRACK_10: // Crevice catacombs of kourend
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        player.getMotion().clearSteps();
                        player.getPacketSender().sendMinimapFlagRemoval();
                        EntityExtKt.markTime(player, Attribute.LAST_ACTION_BUTTON);
                        player.getMotion().update(MovementStatus.DISABLED);
                        player.performAnimation(new Animation(2240));
                        player.getPacketSender().sendMessage("You squeeze through the crevice..");
                        TaskManager.submit(new Task(1) {
                            @Override
                            public void execute() {
                                stop();
                                if (player.getPosition().getX() == 1646) {
                                    player.moveTo(new Position(1648, 10009, 0));
                                } else if (player.getPosition().getX() == 1648) {
                                    player.moveTo(new Position(1646, 10000, 0));
                                } else if (player.getPosition().getX() == 1716) {
                                    player.moveTo(new Position(1706, 10078, 0));
                                } else {
                                    player.moveTo(new Position(1716, 10056, 0));
                                }
                                player.getMotion().update(MovementStatus.NONE);
                            }
                        });
                        break;
                    case 32211:
                    case 32212:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        if (player.getPosition().getZ() == 1) {
                            player.moveTo(new Position(player.getPosition().getX(), 5325, 0));
                        } else {
                            player.moveTo(new Position(player.getPosition().getX(), 5321, 1));
                        }

                        break;
                    case BANK_SAFE:
                        player.getSafeDeposit().open();
                        break;
                    case CASTLE_DOOR_6:
                    case CASTLE_DOOR:
                        player.getPacketSender().sendMessage("The castle door is locked.", 1000);
                        player.getPacketSender().sendSound(Sounds.USE_KEY_ON_LOCKED_DOOR);
                        break;
                    case GATE_11:
                    case GATE_66:
                    case GATE_67:
                    case ARENA_ENTRANCE:
                    case ARENA_ENTRANCE_2:
                    case ICE_GATE:
                    case ICE_GATE_2:
                    case ICE_GATE_3:
                    case ICE_GATE_4:
                    case MAGIC_DOOR_2:
                    case MAGIC_DOOR_3:
                    case MAGIC_DOOR_4:
                    case MAGIC_DOOR_5:
                    case MAGIC_DOOR_6:
                    case MAGIC_DOOR_7:
                    case MAGIC_DOOR_8:
                    case MAGIC_DOOR_9:
                    case MAGIC_DOOR_10:
                    case MAGIC_DOOR_11:
                    case DOOR_46:
                        player.getPacketSender().sendMessage("The gate is locked.", 1000);
                        player.getPacketSender().sendSound(Sounds.USE_KEY_ON_LOCKED_DOOR);
                        break;
                    case ROCKS_29:
                        player.playSound(new Sound(Sounds.DOOR_STUCK));
                        player.sendMessage("This door seems to be stuck.");
                        break;
                    case SNOWY_BIRD_HIDE:
                        DialogueManager.sendStatement(player, "I don't think I can fit inside..");
                        break;
                    case COAL_TRUCK:
                    case 26461:
                        player.getPacketSender().sendMessage("The door is locked.", 1000);
                        player.getPacketSender().sendSound(Sounds.USE_KEY_ON_LOCKED_DOOR);
                        break;
                    case 6919:
                    case 6920:
                        player.getPacketSender().sendMessage("The door is tightly locked.", 1000);
                        player.getPacketSender().sendSound(Sounds.USE_KEY_ON_LOCKED_DOOR);
                        break;
                    case 33262:
                        player.sendMessage("I must be very careful and not go down a smelly hole.");
                        break;
                    case 33261:
                        player.sendMessage("The steps are too obstructed for me to be able to climb.");
                        break;
                    case 5977:
                    case 5978:
                        player.sendMessage("I should refrain from passing through the flames.");
                        break;
                    case WARNING_SIGN_4:
                        DialogueManager.sendStatement(player, "The warning sign shows: BE CAREFUL! DO NOT ENTER!");
                        break;
                    case TELEKINETIC_TELEPORT:
                    case ENCHANTERS_TELEPORT:
                    case ALCHEMISTS_TELEPORT:
                    case GRAVEYARD_TELEPORT:
                        DialogueManager.sendStatement(player, "The portals are not yet activated.");
                        break;
                    case SCOREBOARD_3:
                        WildernessScoreBoard.open(player, WildernessScoreBoard.ScoreBoard.TODAY, 0);
                        break;
                    case BANANA_TREE:
                    case BANANA_TREE_2:
                    case BANANA_TREE_3:
                    case BANANA_TREE_4:
                    case BANANA_TREE_5:
                    case BANANA_TREE_6:
                    case BANANA_TREE_7:
                    case BANANA_TREE_8:
                    case BANANA_TREE_9:
                    case BANANA_TREE_10:
                    case BANANA_TREE_13:
                    case BANANA_TREE_12:
                    case BANANA_TREE_14:
                    case BANANA_TREE_15:
                    case BANANA_TREE_16:
                    case BANANA_TREE_17:
                    case BANANA_TREE_18:
                    case BANANA_TREE_19:
                    case BANANA_TREE_20:
                    case BANANA_TREE_21:
                    case BANANA_TREE_22:
                    case BANANA_TREE_23:
                    case BANANA_TREE_24:
                    case BANANA_TREE_25:
                    case BANANA_TREE_26:
                    case BANANA_TREE_27:
                    case BANANA_TREE_28:
                    case BANANA_TREE_29:
                    case BANANA_TREE_30:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 2, TimeUnit.SECONDS, false, false)) {
                            return;
                        }
                        if (player.getInventory().countFreeSlots() < 1) {
                            DialogueManager.sendStatement(player, "You need to have a free inventory slots to pickup from the tree.");
                            return;
                        }
                        EntityExtKt.markTime(player, Attribute.LAST_PRAY);
                        player.performAnimation(new Animation(2281));
                        player.BLOCK_ALL_BUT_TALKING = true;
                        TaskManager.submit(new Task(3) {
                            @Override
                            public void execute() {
                                player.BLOCK_ALL_BUT_TALKING = false;
                                player.getPacketSender().sendMessage("You pick a banana from the palm.");
                                player.getInventory().add(new Item(1963, 1));
                                player.getPacketSender().sendSound(Sounds.PICKUP_ITEM);
                                stop();
                            }
                        });
                        break;
                    case STRANGE_ALTAR:
                        player.sendMessage("I don't think the gods will be pleased watching me doing this...");
                        break;
                    case CHAOS_ALTAR_3:
                    case BANDOS_ALTAR:
                    case ARMADYL_ALTAR:
                    case ZAMORAK_ALTAR:
                    case SARADOMIN_ALTAR:
                    case ECTOFUNTUS:
                    case 28566:
                    case 31624:
                        if (EntityExtKt.passedTime(player, Attribute.LAST_GOD_ALTAR, 60, TimeUnit.SECONDS, false, false)) {
                            if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager()
                                    .getMaxLevel(Skill.PRAYER) + 3) {
                                player.performAnimation(new Animation(645));
                                player.getSkillManager().setCurrentLevel(Skill.PRAYER,
                                        player.getSkillManager().getMaxLevel(Skill.PRAYER) + 3, true);
                                player.getPacketSender().sendMessage("You recharge your Prayer points with god's enchancement.");
                                player.getPoints().increase(AttributeManager.Points.RECHARGED_PRAYER_TIMES, 1); // Increase points
                                EntityExtKt.markTime(player, Attribute.LAST_GOD_ALTAR);
                                player.getPacketSender().sendSound(Sounds.PRAY_ALTAR);
                            } else {
                                player.getPacketSender().sendMessage("You already have full prayer points.");
                                player.getPacketSender().sendSound(Sounds.PRAYER_UNAVAILABLE_SOUND);
                                return;
                            }
                        } else {
                            player.getPacketSender().sendMessage("You can only use this altar once every 60 seconds.");
                            return;
                        }
                        break;
                    case 42965:
                        if (EntityExtKt.passedTime(player, Attribute.LAST_GOD_ALTAR, 60, TimeUnit.SECONDS, false, false)) {
                            if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager()
                                    .getMaxLevel(Skill.PRAYER) + 3) {
                                player.performAnimation(new Animation(645));
                                player.getSkillManager().setCurrentLevel(Skill.PRAYER,
                                        player.getSkillManager().getMaxLevel(Skill.PRAYER) + 3, true);
                                player.getPacketSender().sendMessage("You recharge your Prayer points with god's enchancement.");
                                player.getPoints().increase(AttributeManager.Points.RECHARGED_PRAYER_TIMES, 1); // Increase points
                                EntityExtKt.markTime(player, Attribute.LAST_GOD_ALTAR);
                                player.getPacketSender().sendSound(Sounds.PRAY_ALTAR);
                            } else {
                                player.getPacketSender().sendMessage("You already have full prayer points.");
                                player.getPacketSender().sendSound(Sounds.PRAYER_UNAVAILABLE_SOUND);
                            }
                            player.restoreRegularAttributes();
                            PrayerHandler.resetAll(player);
                            player.getPacketSender().sendSound(Sounds.HEALED_BY_NURSE);
                        } else {
                            player.getPacketSender().sendMessage("You can only use this altar once every 60 seconds.");
                        }
                        break;
                    case ALTAR_43:
                    case ALTAR_45: // Gilded altar
                        if (EntityExtKt.passedTime(player, Attribute.LAST_GOD_ALTAR, 60, TimeUnit.SECONDS, false, false)) {
                            if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager()
                                    .getMaxLevel(Skill.PRAYER) + 5) {
                                player.performAnimation(new Animation(645));
                                player.getSkillManager().setCurrentLevel(Skill.PRAYER,
                                        player.getSkillManager().getMaxLevel(Skill.PRAYER) + 5, true);
                                player.getPacketSender().sendMessage("@yel@Your Prayer points are massively enchanted by the Gilded Altar.");
                                player.getPoints().increase(AttributeManager.Points.RECHARGED_PRAYER_TIMES, 1); // Increase points
                                EntityExtKt.markTime(player, Attribute.LAST_GOD_ALTAR);
                                player.getPacketSender().sendSound(Sounds.PRAY_ALTAR);
                            } else {
                                player.getPacketSender().sendMessage("You already have full prayer points.");
                                player.getPacketSender().sendSound(Sounds.PRAYER_UNAVAILABLE_SOUND);
                                return;
                            }
                        } else {
                            player.getPacketSender().sendMessage("You can only use the Gilded altar once every 60 seconds.");
                            return;
                        }
                        break;
                    case ROPE_17:
                        player.performAnimation(new Animation(828));
                        TaskManager.submit(new Task(1) {
                            @Override
                            public void execute() {
                                player.moveTo(new Position(2997, 3376, 0));
                                stop();
                            }
                        });
                        break;
                    case FANCY_JEWELLERY_BOX:
                        JewelleryStandTeleport.send(player);
                        break;
                    case GATE_181:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 2, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        if (player.getPosition().getY() == 3944) {
                            player.moveTo(new Position(3184, 3945, 0));
                            return;
                        }
                        if (player.getPosition().getY() == 3495 && !player.getInventory().contains(new Item(995, 2_500_000))) {
                            DialogueManager.sendStatement(player, "You must have at least 2,500,000 coins to enter the Wilderness resource area.");
                            return;
                        }
                        DialogueManager.start(player, 2632);
                        player.setDialogueOptions(new DialogueOptions() {
                            @Override
                            public void handleOption(Player player, int option) {
                                switch (option) {
                                    case 1:
                                        if (!player.getInventory().contains(new Item(995, 2_500_000))) {
                                            DialogueManager.sendStatement(player, "You must have at least 2,500,000 coins to enter the Wilderness resource area.");
                                            return;
                                        }
                                        player.getInventory().delete(995, 2_500_000);
                                        player.moveTo(new Position(3184, 3944, 0));
                                        player.sendMessage("@red@You receieve 25% bonus experience when skilling in the Wilderness resource area.");
                                        player.sendMessage("@red@Please be aware of PKer's!");
                                        player.getPacketSender().sendInterfaceRemoval();
                                        break;
                                    case 2:
                                        player.getPacketSender().sendInterfaceRemoval();
                                        break;
                                }
                            }
                        });
                        break;
                    case 21246:
                        player.sendMessage("The snow patch looks very volatile to touch.");
                        break;
                    case CAVE_EXIT_16:
                        final int kc = player.getAttributes().numInt(Attribute.KAMIL_MINION_KILL_COUNT);
                        if (kc <= 9 && !PlayerUtil.isMember(player)) {
                            DialogueManager.sendStatement(player, "You must have at least 10 minion kills to enter the boss cave!");
                            return;
                        } else if (kc <= 4 && PlayerUtil.isMember(player)) {
                            DialogueManager.sendStatement(player, "You must have at least 5 minion kills to enter the boss cave!");
                            return;
                        }
                        DialogueManager.start(player, 2569);
                        player.setDialogueOptions(new DialogueOptions() {
                            @Override
                            public void handleOption(Player player, int option) {
                                switch (option) {
                                    case 1:
                                        player.getAttributes().numAttr(Attribute.KAMIL_MINION_KILL_COUNT, 0).setValue(0);
                                        player.moveTo(new Position(2629, 4013, 1));
                                        player.getPacketSender().sendInterfaceRemoval();
                                        break;
                                    case 2:
                                        player.getPacketSender().sendInterfaceRemoval();
                                        break;
                                }
                            }
                        });
                        break;
                    case 14837:
                    case 14838:
                        player.sendMessage("You have nothing placed in to check.");
                        break;
                    case LADDER_188:
                        new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("I don't think my hands will fit on here...").setExpression(DialogueExpression.CURIOUS)
                                .start(player);
                        break;
                    case STEPS:
                        if (player.getPosition().getY() == 9555) {
                            player.moveTo(new Position(player.getPosition().getX(), player.getPosition().getY() + 2));
                        } else {
                            player.moveTo(new Position(player.getPosition().getX(), player.getPosition().getY() - 2));
                        }
                        break;
                    case STAIRCASE_141: // Falador Party room west staircase
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        DialogueManager.start(player, 2571);
                        player.setDialogueOptions(new DialogueOptions() {
                            @Override
                            public void handleOption(Player player, int option) {
                                switch (option) {
                                    case 1:
                                        player.moveTo(new Position(3039, 3383, 1));
                                        player.getPacketSender().sendInterfaceRemoval();
                                        break;
                                    case 2:
                                        player.sendMessage("I cannot climb down here.");
                                        player.getPacketSender().sendInterfaceRemoval();
                                        break;
                                }
                            }
                        });
                        break;
                    case STAIRCASE_142: // Falador Party room east staircase
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        DialogueManager.start(player, 2571);
                        player.setDialogueOptions(new DialogueOptions() {
                            @Override
                            public void handleOption(Player player, int option) {
                                switch (option) {
                                    case 1:
                                        player.moveTo(new Position(3052, 3383, 1));
                                        player.getPacketSender().sendInterfaceRemoval();
                                        break;
                                    case 2:
                                        player.sendMessage("I cannot climb down here.");
                                        player.getPacketSender().sendInterfaceRemoval();
                                        break;
                                }
                            }
                        });
                        break;
                    case LADDER_29:
                    case STAIRCASE_105:
                    case LADDER_196:
                    case LADDER_169:
                    case STAIRCASE_66:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        DialogueManager.start(player, 2571);
                        player.setDialogueOptions(new DialogueOptions() {
                            @Override
                            public void handleOption(Player player, int option) {
                                switch (option) {
                                    case 1:
                                        TeleportHandler.teleport(player,
                                                new Position(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ() + 1),
                                                TeleportType.LADDER_UP, false, true);
                                        player.getPacketSender().sendInterfaceRemoval();
                                        break;
                                    case 2:
                                        TeleportHandler.teleport(player,
                                                new Position(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ() - 1),
                                                TeleportType.LADDER_DOWN, false, true);
                                        player.getPacketSender().sendInterfaceRemoval();
                                        break;
                                }
                            }
                        });
                        break;
                    case STAIRCASE_8:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        DialogueManager.start(player, 2571);
                        player.setDialogueOptions(new DialogueOptions() {
                            @Override
                            public void handleOption(Player player, int option) {
                                switch (option) {
                                    case 1:
                                        player.moveTo(new Position(player.getPosition().getX(), 3446, 2));
                                        player.getPacketSender().sendInterfaceRemoval();
                                        break;
                                    case 2:
                                        TeleportHandler.teleport(player,
                                                new Position(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ() - 1),
                                                TeleportType.LADDER_DOWN, false, true);
                                        player.getPacketSender().sendInterfaceRemoval();
                                        break;
                                }
                            }
                        });
                        break;
                    case STAIRCASE_106:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        TeleportHandler.teleport(player,
                                new Position(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ() - 1),
                                TeleportType.LADDER_DOWN, false, true);
                        break;
                    case VINE_24:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        TeleportHandler.teleport(player,
                                new Position(player.getPosition().getX() - 3, player.getPosition().getY(), player.getPosition().getZ() + 2),
                                TeleportType.LADDER_UP, false, true);
                        break;
                    case VINE_25:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        TeleportHandler.teleport(player,
                                new Position(player.getPosition().getX() + 4, player.getPosition().getY(), player.getPosition().getZ() - 2),
                                TeleportType.LADDER_DOWN, false, true);
                        break;
                    case CRATE_24:
                    case CRATE_178:
                        ShopManager.open(player, 39);
                        break;
                    case PIT_3: // Elf Island Boss Dungeon
                        DialogueManager.sendStatement(player, "A new boss pit will be released here soon! Stay tuned!");
                        break;
                    case BARRIER_MINIGAME:
                        if (Server.isUpdating()) {
                            player.sendMessage("You cannot enter while the server is being updated, come back later.");
                            return;
                        }
                        if (!player.getInventory().isEmpty() || !player.getEquipment().isEmpty()) {
                            player.sendMessage("You must have no equipment and no items in your inventory to pass through this barrier.");
                            return;
                        }
                        if (player.getSkillManager().calculateCombatLevel() < 70) {
                            player.sendMessage("You must have at least a combat level of 70 to enter into this Minigame!");
                            return;
                        }
                        /*if (player.getGameMode().isAnyIronman()) {
                            player.sendMessage("You stand solid as an Iron Man thus it is not a good idea to enter to this Minigame!");
                            return;
                        }*/
                        if (player.getGameMode().isSpawn()) {
                            player.sendMessage("It is not a good idea to enter to this Minigame in spawn game mode!");
                            return;
                        }/*
                        if (player.getUsername().equals("Mod Hellmage")) {
                            player.sendMessage("Your account is not allowed to enter into this Minigame.");
                            return;
                        }*/
                        if (player.getCurrentPet() != null) {
                            player.sendMessage("You're not allowed to bring any pets into this Minigame.");
                            return;
                        }
                        player.BLOCK_ALL_BUT_TALKING = true;
                                if (player.getPosition().getY() >= 3501) {
                                    player.getMotion().enqueuePathToWithoutCollisionChecks(player.getPosition().getX(), player.getPosition().getY() - 2);
                                } else {
                                    player.getMotion().enqueuePathToWithoutCollisionChecks(player.getPosition().getX(), player.getPosition().getY() + 2);
                                }
                        TaskManager.submit(new Task(2) {
                            @Override
                            public void execute() {

                                stop();
                                player.BLOCK_ALL_BUT_TALKING = false;
                            }
                        });
                        break;

                    case CAVE_EXIT: // Corp
                        player.sendMessage("The cave exit is too small to pass through.");
                        break;

                    case STAIRCASE_3:
                        player.moveTo(new Position(3417, 3541, 2));
                        break;

                    case SPARKLING_POOL:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        player.getMotion().clearSteps();
                        player.getPacketSender().sendMinimapFlagRemoval();
                        EntityExtKt.markTime(player, Attribute.LAST_ACTION_BUTTON);
                        player.getMotion().update(MovementStatus.DISABLED);
                        player.getPacketSender().sendSound(Sounds.STEP_INTO_POOL);
                        TaskManager.submit(new Task(3) {
                            @Override
                            public void execute() {
                                player.performAnimation(new Animation(746));
                                final Position crossDitch = new Position(0,
                                        player.getPosition().getY() < 4718 ? 2 : 2);
                                TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(
                                        player.getPosition().clone(), crossDitch, 0, 70, 0, 746)));
                                stop();
                            }
                        });
                        TaskManager.submit(new Task(6) {
                            @Override
                            public void execute() {
                                player.performAnimation(new Animation(773));
                                DialogueManager.sendStatement(player, "You fell into the swirling pool!");
                                player.performGraphic(new Graphic(68));
                                stop();
                            }
                        });
                        TaskManager.submit(new Task(9) {
                            @Override
                            public void execute() {
                                player.moveTo(new Position(2509, 4689, 0));
                                player.getMotion().update(MovementStatus.NONE);
                                player.performAnimation(new Animation(1116));
                                stop();
                            }
                        });
                        break;
                    case SPARKLING_POOL_2:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        player.getMotion().clearSteps();
                        player.getPacketSender().sendMinimapFlagRemoval();
                        EntityExtKt.markTime(player, Attribute.LAST_ACTION_BUTTON);
                        player.getMotion().update(MovementStatus.DISABLED);
                        player.getPacketSender().sendSound(Sounds.STEP_INTO_POOL);
                        TaskManager.submit(new Task(3) {
                            @Override
                            public void execute() {
                                player.performAnimation(new Animation(746));
                                final Position crossDitch = new Position(0,
                                        player.getPosition().getY() < 4718 ? -2 : -2);
                                TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(
                                        player.getPosition().clone(), crossDitch, 0, 70, 2, 746)));
                                stop();
                            }
                        });
                        TaskManager.submit(new Task(6) {
                            @Override
                            public void execute() {
                                player.performAnimation(new Animation(773));
                                DialogueManager.sendStatement(player, "You fell into the swirling pool!");
                                player.performGraphic(new Graphic(68));
                                stop();
                            }
                        });
                        TaskManager.submit(new Task(9) {
                            @Override
                            public void execute() {
                                player.moveTo(new Position(2542, 4718, 0));
                                player.getMotion().update(MovementStatus.NONE);
                                player.performAnimation(new Animation(1116));
                                stop();
                            }
                        });
                        break;
                    case CREVICE_16:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, false)) {
                            return;
                        }
                        if (!PlayerUtil.isMember(player)) {
                            DialogueManager.sendStatement(player, "<img=745> You must have the Ruby members rank or higher to use this Agility shortcut.");
                            return;
                        }
                        player.getMotion().clearSteps();
                        player.getPacketSender().sendMinimapFlagRemoval();
                        EntityExtKt.markTime(player, Attribute.LAST_ACTION_BUTTON);
                        player.getMotion().update(MovementStatus.DISABLED);
                        player.performAnimation(new Animation(2240));
                        player.getPacketSender().sendMessage("You squeeze through the crevice..");
                        TaskManager.submit(new Task(2) {
                            @Override
                            public void execute() {
                                if (player.getPosition().getX() == 3500 && player.getPosition().getY() == 9510) {
                                    TaskManager.submit(new ForceMovementTask(player, 1,
                                            new ForceMovement(player.getPosition().clone(), new Position(6, -4), 3, 5,
                                                    2, 2590)));
                                }
                                if (player.getPosition().getX() == 3506 && player.getPosition().getY() == 9506) {
                                    TaskManager.submit(new ForceMovementTask(player, 1,
                                            new ForceMovement(player.getPosition().clone(), new Position(0, -1), 2, 2,
                                                    2, 2240)));
                                    stop();
                                }

                                if (player.getPosition().getX() == 3506 && player.getPosition().getY() == 9505) {
                                    TaskManager.submit(new ForceMovementTask(player, 1,
                                            new ForceMovement(player.getPosition().clone(), new Position(-5, 5), 3, 5,
                                                    2, 2590)));
                                }
                                if (player.getPosition().getX() == 3501 && player.getPosition().getY() == 9510) {
                                    TaskManager.submit(new ForceMovementTask(player, 1,
                                            new ForceMovement(player.getPosition().clone(), new Position(-1, 0), 2, 2,
                                                    3, 2240)));
                                    stop();
                                }
                                player.getMotion().update(MovementStatus.NONE);
                            }
                        });
                        break;
                    case CRACK_6:
                    case 21800:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 3, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        player.getMotion().clearSteps();
                        player.getPacketSender().sendMinimapFlagRemoval();
                        EntityExtKt.markTime(player, Attribute.LAST_ACTION_BUTTON);
                        player.getMotion().update(MovementStatus.DISABLED);
                        player.performAnimation(new Animation(2240));
                        player.getPacketSender().sendMessage("You try to squeeze through the crack..");
                        TaskManager.submit(new Task(2) {
                            @Override
                            public void execute() {
                                player.getMotion().update(MovementStatus.NONE);
                                DialogueManager.sendStatement(player, "This crack doesn't look like it leads to anywhere. Try finding another one.");
                                stop();
                            }
                        });
                        break;
                    case SMOKY_CAVE:
                        if (EquipmentUtil.isSmokeProtect(player.getEquipment())) {
                            player.moveTo(new Position(2404, 9415, 0));
                        } else {
                            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                    .setNpcChatHead(NpcID.BREIVE)
                                    .setText("Hey you don't " + Color.COOL_BLUE.wrap("*cough*") + " wanna go in there",
                                            "without " + Color.COOL_BLUE.wrap("*cough*") +" some kind of protection from the smoke.",
                                            "Your lungs aren't as tough as " + Color.COOL_BLUE.wrap("*wheeze*") + " mine.")
                                    .add(DialogueType.OPTION).setOptionTitle("Choose an Option.")
                                    .firstOption("Enter anyway.", player -> {
                                        player.moveTo(new Position(2404, 9415, 0));
                                        player.getPacketSender().sendInterfaceRemoval();
                                    }).addCancel("Stay outside.").start(player);
                        }
                        break;
                    case CREVICE:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        player.moveTo(new Position(2412, 3060, 0));
                        break;
                    case CREVICE_2:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 3, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        EntityExtKt.setBoolean(player, Attribute.SMOKE_BOSS_WARNING, true, true);
                        if (player.getAttributes().bool(Attribute.SMOKE_BOSS_WARNING)) {
                            new DialogueBuilder(DialogueType.STATEMENT).setStatementTitle("WARNING!")
                                    .setText("This is the lair of the Smoke Devil boss.", "Are you sure you want to enter?")
                                    .add(DialogueType.OPTION).setOptionTitle("Enter the boss area?")
                                    .firstOption("Yes.", player -> {
                                        player.moveTo(new Position(2376, 9452, 0));
                                        player.getPacketSender().sendInterfaceRemoval();
                                    }).secondOption("Yes, and don't warn me again.", player2 -> {
                                        EntityExtKt.setBoolean(player, Attribute.SMOKE_BOSS_WARNING, false, true);
                                        player.getMotion().clearSteps();
                                        player.getPacketSender().sendMinimapFlagRemoval();
                                        player.getPacketSender().sendInterfaceRemoval();
                                        EntityExtKt.markTime(player, Attribute.LAST_ACTION_BUTTON);
                                        player.getMotion().update(MovementStatus.DISABLED);
                                        player.performAnimation(new Animation(2240));
                                        TaskManager.submit(new Task(2) {
                                            @Override
                                            public void execute() {
                                                player.getMotion().update(MovementStatus.NONE);
                                                player.moveTo(new Position(2376, 9452, 0));
                                                stop();
                                            }
                                        });
                                    })
                                    .addCancel("No.").start(player);
                        } else {
                            player.getMotion().clearSteps();
                            player.getPacketSender().sendMinimapFlagRemoval();
                            EntityExtKt.markTime(player, Attribute.LAST_ACTION_BUTTON);
                            player.getMotion().update(MovementStatus.DISABLED);
                            player.performAnimation(new Animation(2240));
                            TaskManager.submit(new Task(2) {
                                @Override
                                public void execute() {
                                    player.getMotion().update(MovementStatus.NONE);
                                    player.moveTo(new Position(2376, 9452, 0));
                                    stop();
                                }
                            });
                        }
                        break;

                    case CREVICE_3:
                        player.getMotion().clearSteps();
                        player.getPacketSender().sendMinimapFlagRemoval();
                        EntityExtKt.markTime(player, Attribute.LAST_ACTION_BUTTON);
                        player.getMotion().update(MovementStatus.DISABLED);
                        player.performAnimation(new Animation(2240));
                        TaskManager.submit(new Task(2) {
                            @Override
                            public void execute() {
                                player.getMotion().update(MovementStatus.NONE);
                                player.moveTo(new Position(2379, 9452, 0));
                                stop();
                            }
                        });
                        break;
                    case CRACK_2:
                    case CRACK_3:
                    case CRACK_4:
                    case CRACK_5:
                        if (!QuestManager.hasCompletedQuest(player, "Monkey Madness")) {
                            player.sendMessage("You need to complete " +
                                    "the quest 'Monkey Madness' quest to enter through this crack.");
                            return;
                        }
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 3, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        player.getMotion().clearSteps();
                        player.getPacketSender().sendMinimapFlagRemoval();
                        EntityExtKt.markTime(player, Attribute.LAST_ACTION_BUTTON);
                        player.getMotion().update(MovementStatus.DISABLED);
                        player.performAnimation(new Animation(2240));
                        player.getPacketSender().sendMessage("You try to squeeze through the crack..");
                        TaskManager.submit(new Task(2) {
                            @Override
                            public void execute() {
                                player.getMotion().update(MovementStatus.NONE);
                                stop();
                                Position pos = null;
                                if (player.getPosition().getX() == 2691)
                                    pos = (new Position(2690, 9163, 1));
                                else if (player.getPosition().getX() == 2696)
                                    pos = (new Position(2696, 9212, 1));
                                else if (player.getPosition().getX() == 2692)
                                    pos = (new Position(2690, 9163, 1));
                                else if (player.getPosition().getX() == 2739)
                                    pos = (new Position(2741, 9205, 1));
                                else if (player.getPosition().getX() == 2736)
                                    pos = (new Position(2736, 9159, 1));

                                if (pos != null) {
                                    BossInstances.Companion.instanceDialogue(player, pos, BossInstances.JUNGLE_DEMON, true);
                                } else {
                                    player.getPacketSender().sendMessage("You were able find a way out.");
                                }
                            }
                        });
                        break;
                    case STAIRCASE:
                        player.moveTo(new Position(3433, 3538, 1));
                        break;
                    case GIFT_OF_PEACE: // Level 1
                        if (AreaManager.inside(player.getPosition(), new Boundary(1903, 1912, 5218, 5227))) { // Stronghold reward
                            player.getMotion().clearSteps();
                            player.getPacketSender().sendMinimapFlagRemoval();
                            EntityExtKt.markTime(player, Attribute.LAST_PRAY);
                            player.performAnimation(new Animation(881, 25));
                            player.getMotion().update(MovementStatus.DISABLED);
                            TaskManager.submit(new Task(2) {
                                @Override
                                public void execute() {
                                    stop();
                                    player.getMotion().update(MovementStatus.NONE);
                                    if (player.getPoints().get(AttributeManager.Points.OPENED_GIFT_OF_PEACE) > 0) {
                                        DialogueManager.sendStatement(player, "You have already claimed your reward.");
                                        return;
                                    }
                                    if (player.getTimePlayed(TimeUnits.HOUR) < 1) {
                                        DialogueManager.sendStatement(player, "You must have at least a play time of 1 hour to open this box.");
                                        return;
                                    }
                                    AchievementManager.processFor(AchievementType.GIFT_OF_PEACE, player);
                                    player.getPoints().increase(AttributeManager.Points.OPENED_GIFT_OF_PEACE);
                                    ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.BLOOD_MONEY, 1_000));
                                    ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.COINS, 1_000_000));
                                    Logging.log("Strongholdboxes", "" + player.getUsername() + " has opened the Gift of peace.");
                                    PlayerUtil.broadcastMessage("<img=767> " + PlayerUtil.getImages(player) + "" + player.getUsername() +" has just opened the Gift of peace from Stronghold of Security!");
                                    if(DiscordBot.ENABLED)
                                        DiscordBot.INSTANCE.sendServerLogs(player.getUsername() + " has opened the Gift of peace.");

                                    player.performGraphic(new Graphic(436));
                                    player.restoreRegularAttributes();
                                    PrayerHandler.resetAll(player);
                                }
                            });
                        }
                        break;
                    case GRAIN_OF_PLENTY: // Level 2
                            if (AreaManager.inside(player.getPosition(), new Boundary(2017, 2024, 5211, 5219))) { // Stronghold reward
                                player.getMotion().clearSteps();
                                player.getPacketSender().sendMinimapFlagRemoval();
                                EntityExtKt.markTime(player, Attribute.LAST_PRAY);
                                player.performAnimation(new Animation(881, 25));
                                player.getMotion().update(MovementStatus.DISABLED);
                                TaskManager.submit(new Task(2) {
                                    @Override
                                    public void execute() {
                                        stop();
                                        player.getMotion().update(MovementStatus.NONE);
                                        if (player.getPoints().get(AttributeManager.Points.OPENED_GRAIN_OF_PLENTY) > 0) {
                                            DialogueManager.sendStatement(player, "You have already claimed your reward.");
                                            return;
                                        }
                                        if (player.getTimePlayed(TimeUnits.HOUR) < 1) {
                                            DialogueManager.sendStatement(player, "You must have at least a play time of 1 hour to open this box.");
                                            return;
                                        }
                                        if (player.getAchievements().getProgress()[AchievementType.SELF_SECURE.ordinal()] == 0) {
                                            DialogueManager.sendStatement(player, "You must have a bank PIN setup before opening this box.");
                                            return;
                                        }
                                        AchievementManager.processFor(AchievementType.GRAIN_OF_PLENTY, player);
                                        player.getPoints().increase(AttributeManager.Points.OPENED_GRAIN_OF_PLENTY);
                                        ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.BLOOD_MONEY, 2_500));
                                        ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.COINS, 2_000_000));
                                        Logging.log("Strongholdboxes", "" + player.getUsername() + " has opened the Grain of plenty.");
                                        PlayerUtil.broadcastMessage("<img=767> " + PlayerUtil.getImages(player) + "" + player.getUsername() +" has just opened the Grain of plenty from Stronghold of Security!");
                                        player.performGraphic(new Graphic(436));
                                        player.restoreRegularAttributes();
                                        PrayerHandler.resetAll(player);
                                    }
                                });
                        }
                        break;
                    case BOX_OF_HEALTH: // Level 3
                        if (AreaManager.inside(player.getPosition(), new Boundary(2141, 2150, 5276, 5284))) { // Stronghold reward
                            player.getMotion().clearSteps();
                            player.getPacketSender().sendMinimapFlagRemoval();
                            EntityExtKt.markTime(player, Attribute.LAST_PRAY);
                            player.performAnimation(new Animation(881, 25));
                            player.getMotion().update(MovementStatus.DISABLED);
                            TaskManager.submit(new Task(2) {
                                @Override
                                public void execute() {
                                    stop();
                                    player.getMotion().update(MovementStatus.NONE);
                                    if (player.getPoints().get(AttributeManager.Points.OPENED_BOX_OF_HEALTH) > 0) {
                                        DialogueManager.sendStatement(player, "You have already claimed your reward.");
                                        return;
                                    }
                                    if (player.getTimePlayed(TimeUnits.HOUR) < 1) {
                                        DialogueManager.sendStatement(player, "You must have at least a play time of 1 hour to open this box.");
                                        return;
                                    }
                                    if (player.getAchievements().getProgress()[AchievementType.SELF_SECURE.ordinal()] == 0) {
                                        DialogueManager.sendStatement(player, "You must have a bank PIN setup before opening this box.");
                                        return;
                                    }
                                    AchievementManager.processFor(AchievementType.BOX_OF_HEALTH, player);
                                    player.getPoints().increase(AttributeManager.Points.OPENED_BOX_OF_HEALTH);
                                    ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.BLOOD_MONEY, 3_500));
                                    ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.COINS, 3_000_000));
                                    Logging.log("Strongholdboxes", "" + player.getUsername() + " has opened the Box of health.");
                                    PlayerUtil.broadcastPlayerStaffMessage("" + player.getUsername() + " has opened the Box of health in the Stronghold security.");
                                    PlayerUtil.broadcastMessage("<img=767> " + PlayerUtil.getImages(player) + "" + player.getUsername() +" has just opened the Box of health from Stronghold of Security!");
                                    player.performGraphic(new Graphic(436));
                                    player.restoreRegularAttributes();
                                    PrayerHandler.resetAll(player);
                                }
                            });
                        } else {
                            if (EntityExtKt.passedTime(player, Attribute.LAST_REFRESH, 1, TimeUnit.MINUTES, false, true)) {
                                DialogueManager.sendStatement(player, "You feel slightly refreshed.");
                                player.performAnimation(new Animation(7305));
                                player.performGraphic(new Graphic(436));
                                player.restoreRegularAttributes();
                                PrayerHandler.resetAll(player);
                                player.getPacketSender().sendSound(Sounds.HEALED_BY_NURSE);
                            } else {
                                player.getPacketSender()
                                        .sendMessage("You must wait one minute before being able to use it again.");
                                return;
                            }
                        }
                        break;
                    case CRADLE_OF_LIFE: // Level 4
                            if (AreaManager.inside(player.getPosition(), new Boundary(2342, 2348, 5208, 5216))) { // Stronghold reward
                                player.getMotion().clearSteps();
                                player.getPacketSender().sendMinimapFlagRemoval();
                                EntityExtKt.markTime(player, Attribute.LAST_PRAY);
                                player.performAnimation(new Animation(881, 25));
                                player.getMotion().update(MovementStatus.DISABLED);
                                TaskManager.submit(new Task(2) {
                                    @Override
                                    public void execute() {
                                        stop();
                                        player.getMotion().update(MovementStatus.NONE);
                                        if (player.getPoints().get(AttributeManager.Points.OPENED_CRADLE_OF_LIFE) > 0) {
                                            DialogueManager.sendStatement(player, "You have already claimed your reward.");
                                            return;
                                        }
                                        if (player.getTimePlayed(TimeUnits.HOUR) < 1) {
                                            DialogueManager.sendStatement(player, "You must have at least a play time of 1 hour to open this box.");
                                            return;
                                        }
                                        if (player.getAchievements().getProgress()[AchievementType.SELF_SECURE.ordinal()] == 0) {
                                            DialogueManager.sendStatement(player, "You must have a bank PIN setup before opening this box.");
                                            return;
                                        }
                                        new DialogueBuilder(DialogueType.STATEMENT)
                                                .setText("As your hand touches the cradle, you hear a voice in your head of a", "million dead adventurers...")
                                                .add(DialogueType.STATEMENT)
                                                .setText("Congratulations! You have successfully navigated the Stronghold of", "Security and learned to secure your account. You have", "received a small reward for this. Remember to keep your account", "secure in the future!")
                                                .add(DialogueType.ITEM_STATEMENT_NO_HEADER)
                                                .setItem(ItemID.BLOOD_MONEY, 200)
                                                .setText("You manage to grab some Blood money and Coins", "as a reward of achievement.")
                                                .start(player);
                                        AchievementManager.processFor(AchievementType.CRADLE_OF_LIFE, player);
                                        player.getPoints().increase(AttributeManager.Points.OPENED_CRADLE_OF_LIFE);
                                        ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.BLOOD_MONEY, 10_000));
                                        ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.COINS, 5_000_000));
                                        PlayerUtil.broadcastMessage("<img=767> " + PlayerUtil.getImages(player) + "" + player.getUsername() +" has just opened the Cradle of life from Stronghold of Security!");
                                        Logging.log("Strongholdboxes", "" + player.getUsername() + " has opened the Cradle of life.");
                                        PlayerUtil.broadcastPlayerStaffMessage("" + player.getUsername() + " has opened the Cradle of life in the Stronghold security.");
                                        player.performGraphic(new Graphic(436));
                                        player.restoreRegularAttributes();
                                        PrayerHandler.resetAll(player);
                                    }
                                });
                        }
                        break;

                    case 7054: // Stronghold security sign level 4
                                DialogueManager.sendStatement(player, "The notice reads: Congratulations adventurer, if you have made it up until this part! Good luck!");
                        break;
                    case 26741:
                        String playersfunpk = AreaManager.getPlayersInBoundaries(AreaManager.FREE_PVP_ARENA.boundaries()) != 1 ? "players" : "player";
                        player.getPacketSender().sendMessage("There is currently " + AreaManager.getPlayersInBoundaries(AreaManager.FREE_PVP_ARENA.boundaries()) + " " + playersfunpk + " in the area.");
                        break;
                    case CREVICE_9:
                        if (EquipmentUtil.isSmokeProtect(player.getEquipment())) {
                            player.moveTo(new Position(3205, 9379, 0));
                        } else {
                            new DialogueBuilder(DialogueType.STATEMENT)
                                    .setText("The crevice appears to be smoky and it is not advised to go", "in there without some kind of protection from the smoke.")
                                    .add(DialogueType.OPTION).setOptionTitle("Choose an Option.")
                                    .firstOption("Enter anyway.", player -> {
                                        player.performAnimation(new Animation(2240));
                                        player.getPacketSender().sendInterfaceRemoval();
                                        TaskManager.submit(new Task(3, player, false) {
                                            @Override
                                            protected void execute() {
                                                player.moveTo(new Position(3205, 9379, 0));
                                                player.performAnimation(new Animation(2242));
                                                TaskManager.submit(new Task(2) {
                                                    @Override
                                                    public void execute() {
                                                        player.performAnimation(new Animation(-1));
                                                        stop();
                                                    }
                                                });
                                                stop();
                                            }
                                        });
                                    }).addCancel("Stay outside.").start(player);
                        }
                        break;
                    case ROCKS_64:
                        player.performAnimation(new Animation(827));

                        TaskManager.submit(new Task(2, player, false) {
                            @Override
                            protected void execute() {
                                player.moveTo(new Position(2857, 9569, 0));
                                stop();
                            }
                        });
                        break;
                    case WALL_9: // Karamja dungeon wall
                        if (player.getPosition().sameAs(new Position(2836, 9599))) {
                            player.moveTo(new Position(2836, 9600, 0));
                        } else if (player.getPosition().sameAs(new Position(2836, 9600))) {
                            player.moveTo(new Position(2836, 9599, 0));
                        }
                        break;
                    case LOOSE_RAILING:
                        if (player.getPosition().sameAs(new Position(2662, 3500))) {
                            player.moveTo(new Position(2661, 3500, 0));
                        } else if (player.getPosition().sameAs(new Position(2661, 3500))) {
                            player.moveTo(new Position(2662, 3500, 0));
                        }
                        break;

                    case BROKEN_FENCE_2:
                        player.getMotion().clearSteps();
                        if (player.getForceMovement() == null && player.getClickDelay().elapsed(2000)) {
                            final Position crossDitch = new Position(0,
                                    player.getPosition().getY() < 3493 ? 2 : -2);
                            TaskManager.submit(
                                    new ForceMovementTask(player, 2, new ForceMovement(player.getPosition().clone(),
                                            crossDitch, 0, 70, crossDitch.getY() == 2 ? 0 : 2, 6132)));
                            player.getPacketSender().sendSound(Sounds.DITCH_JUMP);
                            player.getClickDelay().reset();
                        }
                        break;
                    case STEPPING_STONE_9:
                        if (player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 74) {
                            DialogueManager.sendStatement(player, "You need an Agility level of at least 74 to pass through this obstacle.");
                            return;
                        }
                        player.getMotion().clearSteps();
                        if (player.getPosition().getY() >= 3810) {
                            player.getMotion().clearSteps();
                            if (player.getForceMovement() == null && player.getClickDelay().elapsed(2000)) {
                                player.BLOCK_ALL_BUT_TALKING = true;
                                final Position crossDitch = new Position(0,
                                        player.getPosition().getY() < 3810 ? 2 : -2);
                                TaskManager.submit(
                                        new ForceMovementTask(player, 2, new ForceMovement(player.getPosition().clone(),
                                                crossDitch, 0, 70, crossDitch.getY() == 2 ? 0 : 2, 6132)));
                                player.getPacketSender().sendSound(Sounds.DITCH_JUMP);
                                player.getClickDelay().reset();
                            }
                            TaskManager.submit(new Task(3, player, false) {
                                @Override
                                protected void execute() {
                                    final Position crossDitch = new Position(0,
                                            player.getPosition().getY() < 3807 ? 1 : -1);
                                    TaskManager.submit(
                                            new ForceMovementTask(player, 2, new ForceMovement(player.getPosition().clone(),
                                                    crossDitch, 0, 70, crossDitch.getY() == 2 ? 0 : 2, 6132)));
                                    player.BLOCK_ALL_BUT_TALKING = false;
                                    player.getClickDelay().reset();
                                    player.getPacketSender().sendSound(Sounds.DITCH_JUMP);
                                    stop();
                                }
                            });
                        } else {
                            player.getMotion().clearSteps();
                            if (player.getForceMovement() == null && player.getClickDelay().elapsed(2000)) {
                                player.BLOCK_ALL_BUT_TALKING = true;
                                final Position crossDitch = new Position(0,
                                        player.getPosition().getY() < 3810 ? 1 : 1);
                                TaskManager.submit(
                                        new ForceMovementTask(player, 2, new ForceMovement(player.getPosition().clone(),
                                                crossDitch, 0, 70, crossDitch.getY() == 1 ? -1 : -1, 6132)));
                                player.getPacketSender().sendSound(Sounds.DITCH_JUMP);
                                player.getClickDelay().reset();
                            }
                            TaskManager.submit(new Task(3, player, false) {
                                @Override
                                protected void execute() {
                                    final Position crossDitch = new Position(0,
                                            player.getPosition().getY() < 3807 ? 2 : 2);
                                    TaskManager.submit(
                                            new ForceMovementTask(player, 2, new ForceMovement(player.getPosition().clone(),
                                                    crossDitch, 0, 70, crossDitch.getY() == 2 ? 0 : 2, 6132)));
                                    player.BLOCK_ALL_BUT_TALKING = false;
                                    player.getClickDelay().reset();
                                    stop();
                                }
                            });
                        }
                        break;
                    case SMOKEY_WELL:
                        player.moveTo(new Position(3206, 9379, 0));
                        break;
                    case ROPE_7:
                        player.moveTo(new Position(3309, 2962, 0));
                        break;
                    case ROPE_BRIDGE_8:
                        if (player.getPosition().getX() == 2355)
                            player.moveTo(new Position(2355, 3848, 0));
                        break;
                    case ROPE_BRIDGE_9:
                        player.moveTo(new Position(2355, 3839, 0));
                        break;
                    case ROPE_BRIDGE_6:
                        player.moveTo(new Position(2314, 3848, 0));
                        break;
                    case ROPE_BRIDGE_2:
                        player.moveTo(new Position(2317, 3832, 0));
                        break;
                    case ROPE_BRIDGE_3:
                        player.moveTo(new Position(2317, 3823, 0));
                        break;
                    case ROPE_BRIDGE_7:
                        player.moveTo(new Position(2314, 3839, 0));
                        break;
                    case ROPE_BRIDGE_4:
                        player.moveTo(new Position(2343, 3829, 0));
                        break;
                    case ROPE_BRIDGE_5:
                        player.moveTo(new Position(2343, 3820, 0));
                        break;
                    case ROCKSLIDE_2:
                        if (player.getPosition().getY() <= 3657) {
                            player.performAnimation(new Animation(1115));
                            TaskManager.submit(new Task(1, player, false) {
                                @Override
                                protected void execute() {
                                    player.moveTo(new Position(2761, 3660, 0));
                                    stop();
                                }
                            });
                        } else if (player.getPosition().getY() >= 3660) {
                            player.performAnimation(new Animation(1115));
                            TaskManager.submit(new Task(1, player, false) {
                                @Override
                                protected void execute() {
                                    player.moveTo(new Position(2760, 3657, 0));
                                    stop();
                                }
                            });
                        }
                        break;
                    case WEB:
                    case WEB_2:
                    case WEB_3:
                    case WEB_4:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, false)) {
                            return;
                        }
                        if (player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == -1) {
                            player.sendMessage("Only a sharp blade can cut through this sticky web.");
                            return;
                        }
                        int chance = 0;
                        if (ItemDefinition.forId(player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId()).getBonuses()[1] >= 1) {
                            chance = 20;
                        }
                        if (player.getCombat().getWeapon().getWeaponInterface() != WeaponInterface.LONGSWORD &&
                                player.getCombat().getWeapon().getWeaponInterface() != WeaponInterface.SCIMITAR &&
                                player.getCombat().getWeapon().getWeaponInterface() != WeaponInterface.ARCLIGHT &&
                                player.getCombat().getWeapon().getWeaponInterface() != WeaponInterface.SWORD) {
                            chance = 80;
                        }
                        if (ItemDefinition.forId(player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId()).getBonuses()[1] >= 100
                                || player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == 3981
                                || player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == 13108
                                || player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == 13109
                                || player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == 13110
                                || player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == 13111) {
                            chance = 100;
                        }
                        if (ItemDefinition.forId(player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId()).getBonuses()[1] < 1 ||
                                player.getCombat().getWeapon().getWeaponInterface() == WeaponInterface.CROSSBOW ||
                                player.getCombat().getWeapon().getWeaponInterface() == WeaponInterface.BOXING ||
                                player.getCombat().getWeapon().getWeaponInterface() == WeaponInterface.BLOWPIPE ||
                                player.getCombat().getWeapon().getWeaponInterface() == WeaponInterface.SHORTBOW ||
                                player.getCombat().getWeapon().getWeaponInterface() == WeaponInterface.LONGBOW ||
                                player.getCombat().getWeapon().getWeaponInterface() == WeaponInterface.KARILS_CROSSBOW ||
                                player.getCombat().getWeapon().getWeaponInterface() == WeaponInterface.CRYSTALBOW ||
                                player.getCombat().getWeapon().getWeaponInterface() == WeaponInterface.BOFA ||
                                player.getCombat().getWeapon().getWeaponInterface() == WeaponInterface.SNOWBALL ||
                                player.getCombat().getWeapon().getWeaponInterface() == WeaponInterface.JAVELIN ||
                                player.getCombat().getWeapon().getWeaponInterface() == WeaponInterface.OBBY_RINGS ||
                                player.getCombat().getWeapon().getWeaponInterface() == WeaponInterface.DART ||
                                player.getCombat().getWeapon().getWeaponInterface() == WeaponInterface.KNIFE ||
                                player.getCombat().getWeapon().getWeaponInterface() == WeaponInterface.UNARMED ||
                                player.getCombat().getWeapon().getWeaponInterface() == WeaponInterface.THROWNAXE ||
                                player.getCombat().getWeapon().getWeaponInterface() == WeaponInterface.DORGRESHUUN) {
                            player.sendMessage("Only a sharp blade can cut through this sticky web.");
                            return;
                        }
                        player.performAnimation(new Animation(player.getAttackAnim()));
                        player.getPacketSender().sendSound(Sounds.SLASH_WEB);
                        int finalChance = chance;
                        TaskManager.submit(new Task(1) {
                            @Override
                            protected void execute() {
                                if (Misc.random(100) <= finalChance) {
                                    TaskManager.submit(new TimedObjectReplacementTask(
                                            object,
                                            DynamicGameObject.createPublic(734, object.getPosition(), object.getObjectType(), object.getFace()),
                                            30));
                                    player.sendMessage("You slash through the web!");
                                } else {
                                    player.sendMessage("You fail to cut through the web.");
                                }
                                stop();
                            }
                        });
                        EntityExtKt.markTime(player, Attribute.LAST_PRAY);
                        break;
                    case GATE_81:
                    case GATE_82:
                        if (player.getPosition().getX() <= 3304) {
                            BossInstances.Companion.skeletonHellhoundDialogue(player, new Position(player.getPosition().getX() + 1, player.getPosition().getY(), 0), true);
                        } else if (player.getPosition().getX() >= 3305) {
                            player.moveTo(new Position(player.getPosition().getX() - 1, player.getPosition().getY(), 0));
                        } else {
                            player.getPacketSender().sendMessage("This gate is currently locked.", 1000);
                            player.getPacketSender().sendSound(Sounds.USE_KEY_ON_LOCKED_DOOR);
                        }
                    break;
                    //case 18620:
                    //case 11455:
                    case 1815: // Wilderness lever
                                TeleportHandler.teleportByLever(player, object,
                                        new Position(3093, 3482, 0), false, false);
                        break;
                    case LEVER_8: // Wilderness lever
                                if (!player.getCombat().getTeleBlockTimer().finished()) {
                                    player.getPacketSender()
                                            .sendMessage("A magical spell is blocking you from teleporting.", 1000);
                                    return;
                                }
                                TeleportHandler.teleportByLever(player, object, new Position(3154, 3923), true, false);
                        break;
                    case ICY_CAVERN:
                    case ICY_CAVERN_2:
                        if (player.getPosition().getY() <= 9557) {
                            player.moveTo(new Position(3056, 9562, 0));
                        } else {
                            new DialogueBuilder(DialogueType.STATEMENT)
                                    .setText("STOP! The creatures in this cave are VERY dangerous. Are you", "sure you want to enter?")
                                    .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                                    .firstOption("Yes, I'm not afraid of death.", player2 -> {
                                        player.moveTo(new Position(3056, 9555, 0));
                                        player.getPacketSender().sendInterfaceRemoval();
                                        return;
                                    })
                                    .addCancel("No thanks, I don't want to die.").start(player);
                        }
                        break;
                    case STRANGE_FLOOR:
                        if (player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 80) {
                            DialogueManager.sendStatement(player, "You need an Agility level of at least 80 to pass through this obstacle.");
                            return;
                        }
                        player.getMotion().clearSteps();
                        if (player.getForceMovement() == null && player.getClickDelay().elapsed(2000)) {
                            final Position crossDitch = new Position(player.getPosition().getX() < 2880 ? 2 : -2,
                                    0);
                            TaskManager.submit(
                                    new ForceMovementTask(player, 2, new ForceMovement(player.getPosition().clone(),
                                            crossDitch, 0, 70, crossDitch.getX() == 2 ? 1 : 3, 6132)));
                            player.getPacketSender().sendSound(Sounds.DITCH_JUMP);
                            player.getClickDelay().reset();
                        }
                        break;
                    case LOOSE_RAILING_4:
                        if (player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 63) {
                            DialogueManager.sendStatement(player, "You need an Agility level of at least 63 to pass through this obstacle.");
                            return;
                        }
                        player.getMotion().clearSteps();
                        player.getSkillManager().addExperience(Skill.AGILITY, 10);
                        if (player.getForceMovement() == null && player.getClickDelay().elapsed(2000)) {
                            final Position crossDitch = new Position(player.getPosition().getX() < 2936 ? 1 : -1,
                                    0);
                            TaskManager.submit(
                                    new ForceMovementTask(player, 3, new ForceMovement(player.getPosition().clone(),
                                            crossDitch, 0, 70, crossDitch.getX() == 2 ? 1 : 3, 754)));
                            player.getClickDelay().reset();
                        }
                        break;
                    case LOOSE_RAILING_3:
                        if (player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 63) {
                            DialogueManager.sendStatement(player, "You need an Agility level of at least 63 to pass through this obstacle.");
                            return;
                        }
                        player.getMotion().clearSteps();
                        player.getSkillManager().addExperience(Skill.AGILITY, 10);
                        if (player.getForceMovement() == null && player.getClickDelay().elapsed(2000)) {
                            final Position crossDitch = new Position(player.getPosition().getX() < 2523 ? 1 : -1,
                                    0);
                            TaskManager.submit(
                                    new ForceMovementTask(player, 3, new ForceMovement(player.getPosition().clone(),
                                            crossDitch, 0, 70, crossDitch.getX() == 2 ? 3 : 1, 754)));
                            player.getClickDelay().reset();
                        }
                        break;
                    case STRANGE_FLOOR_2:
                        if (player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 48) {
                            DialogueManager.sendStatement(player, "You need an Agility level of at least 48 to pass through this obstacle.");
                            return;
                        }
                        player.getMotion().clearSteps();
                        if (player.getPosition().getX() > 2772) {
                            if (player.getForceMovement() == null && player.getClickDelay().elapsed(2000)) {
                                final Position crossDitch = new Position(player.getPosition().getX() < 2774 ? 2 : -2,
                                        0);
                                TaskManager.submit(
                                        new ForceMovementTask(player, 2, new ForceMovement(player.getPosition().clone(),
                                                crossDitch, 0, 70, crossDitch.getX() == 2 ? 1 : 3, 6132)));
                                player.getPacketSender().sendSound(Sounds.DITCH_JUMP);
                                player.getClickDelay().reset();
                            }
                        } else {
                            if (player.getForceMovement() == null && player.getClickDelay().elapsed(2000)) {
                                final Position crossDitch = new Position(player.getPosition().getX() < 2769 ? 2 : -2,
                                        0);
                                TaskManager.submit(
                                        new ForceMovementTask(player, 2, new ForceMovement(player.getPosition().clone(),
                                                crossDitch, 0, 70, crossDitch.getX() == 2 ? 1 : 3, 6132)));
                                player.getPacketSender().sendSound(Sounds.DITCH_JUMP);
                                player.getClickDelay().reset();
                            }
                        }
                        break;
                    case STEPS_9:
                    case STEPS_10:
                        if (player.getPosition().getX() == 2880) {
                            player.moveTo(new Position(2883, 9825, 0));
                        } else if (player.getPosition().getX() == 2883) {
                            player.moveTo(new Position(2880, 9825, 1));
                        } else if (player.getPosition().getX() == 2906) {
                            player.moveTo(new Position(2903, 9813, 1));
                        } else if (player.getPosition().getX() == 2903) {
                            player.moveTo(new Position(2906, 9813, 0));
                        }
                        break;
                    case ROCKS:
                    case ROCKS_67:
                        DialogueManager.sendStatement(player, "The rocks are too high to climb!");
                        break;
                    case LOG_BALANCE_18:
                    case LOG_BALANCE_19:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, false)) {
                            return;
                        }
                        if (player.getSkillManager().getMaxLevel(Skill.AGILITY) < 52) {
                            player.sendMessage("You must have a level of 52 Agility to cross this obstacle.");
                            return;
                        }
                        player.BLOCK_ALL_BUT_TALKING = true;
                        TaskManager.submit(new Task(5) {
                            @Override
                            public void execute() {
                                player.BLOCK_ALL_BUT_TALKING = false;
                                stop();
                            }
                        });
                        EntityExtKt.markTime(player, Attribute.LAST_PRAY);
                        player.getMotion().clearSteps();
                        player.setEntityInteraction(null);
                        if (player.getPosition().getX() <= 2682) {
                            player.getMotion().enqueuePathToWithoutCollisionChecks(player.getPosition().getX() + 5, player.getPosition().getY());
                        } else {
                            player.getMotion().enqueuePathToWithoutCollisionChecks(player.getPosition().getX() - 5, player.getPosition().getY());
                        }
                        player.getClickDelay().reset();
                        break;
                    case STEPPING_STONE_31:
                    case STEPPING_STONE_32:
                        if (player.getPosition().getY() == 9557) {
                            player.moveTo(new Position(2649, 9562, 0));
                        } else {
                            player.moveTo(new Position(2647, 9557, 0));
                        }
                        break;
                    case KBD_LADDER_DOWN:
                        TeleportHandler.teleport(player, new Position(3069, 10255), TeleportType.LADDER_DOWN,
                                false, false);
                        break;
                    case CAVE_EXIT_20:
                        player.moveTo(new Position(2862, 9572, 0));
                        break;
                    case HOLE_40:
                        player.moveTo(new Position(2833, 9656, 0));
                        break;
                    case 29486:
                    case 29487:
                        if (player.getArea() != null && player.getArea().handleObjectClick(player, object, 0))
                            return;
                        OborCave.enterLair(player);
                        break;
                    case 32534:
                        if (player.getArea() != null && player.getArea().handleObjectClick(player, object, 0))
                            return;
                        BryophytaCave.enterBryophytaCaveLair(player);
                        break;
                    case ROSES:
                    case ROSES_2:
                    case ROSES_3:
                    case ROSES_4:
                    case ROSES_5:
                        if (player.getInventory().countFreeSlots() < 1) {
                            DialogueManager.sendStatement(player, "You need to have a free inventory slots to pickup the roses.");
                            return;
                        }
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 2, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        player.performAnimation(new Animation(827));
                        TaskManager.submit(new Task(2) {
                            @Override
                            public void execute() {
                                player.getPacketSender().sendMessage("You pickup the the rose seeds.");
                                player.getInventory().add(new Item(5097, 1));
                                player.getPacketSender().sendSound(Sounds.PICKUP_ITEM);
                                stop();
                            }
                        });
                        break;
                    case WALL_161:
                        if (player.getPosition().getX() < 2847) {
                            if (player.getInventory().containsAny(ItemID.ANCESTRAL_KEY)) {
                                player.getMotion().clearSteps();
                                if (player.getForceMovement() == null && player.getClickDelay().elapsed(2000)) {
                                    final Position crossDitch = new Position(player.getPosition().getX() < 2847 ? 2 : -2, 0);
                                    TaskManager.submit(new ForceMovementTask(player, 2, new ForceMovement(player.getPosition().clone(), crossDitch, 0, 70, crossDitch.getX() == 2 ? 1 : 3, 6132)));
                                    player.getInventory().delete(ItemID.ANCESTRAL_KEY, 1);
                                    player.getPacketSender().sendSound(Sounds.DITCH_JUMP);
                                    player.getClickDelay().reset();
                                }
                            } else {
                                player.getPacketSender().sendMessage("You need an Ancestral key to jump over the spiky wall.", 1000);
                                return;
                            }
                        } else {
                            player.getMotion().clearSteps();
                            if (player.getForceMovement() == null && player.getClickDelay().elapsed(2000)) {
                                final Position crossDitch = new Position(player.getPosition().getX() >= 2847 ? -2 : 2, 0);
                                TaskManager.submit(new ForceMovementTask(player, 2, new ForceMovement(player.getPosition().clone(), crossDitch, 0, 70, crossDitch.getX() == 2 ? 1 : 3, 6132)));
                                player.getClickDelay().reset();
                            }
                        }
                        break;
                    case STAIRCASE_97: // Yanille entrance and falador mine
                        if (player.getWildernessLevel() >= 40) {
                            player.sendMessage("It looks like a dead end over here.");
                            return;
                        }
                        if (player.getPosition().getY() >= 3370) {
                            player.moveTo(new Position(3058, player.getPosition().getY() + 6400));
                        } else {
                            if (player.getInventory().contains(ItemID.KEY_4)) {
                                if (!QuestManager.hasCompletedQuest(player, "Dragon Slayer")) {
                                    player.sendMessage("You need to complete " +
                                            "the quest 'Dragon Slayer' quest to use this ladder.");
                                    return;
                                }
                                player.getInventory().delete(ItemID.KEY_4, 1);
                                player.performAnimation(new Animation(806));
                                DialogueManager.sendStatement(player, "You threw the Orange key into the dark leading stairs and something magical happens..");
                                TravelSystem.INSTANCE.fadeTravelAction(player, true, true, "", 2, 6, 5, () -> {
                                    player.moveTo(new Position(2601, 9479, 0));
                                    DialogueManager.sendStatement(player, "You find yourself in a dark leading dungeon where mysterious monsters dwell.");
                                    return null;
                                });
                            } else {
                                if (!QuestManager.hasCompletedQuest(player, "Dragon Slayer")) {
                                    player.sendMessage("You need to complete " +
                                            "the quest 'Dragon Slayer' quest to use this ladder.");
                                    return;
                                }
                                player.getPacketSender().sendMessage("You need an Orange key from Dad to use the dungeon stairs.").sendMessage("It is commonly heard that Dad dwells west of Yanille.", 1000);
                                return;
                            }
                        }
                        break;

                    case MAGIC_PORTAL:
                    case MAGIC_PORTAL_3:
                        player.moveTo(new Position(2703, 3406, 0));
                        break;
                    case STAIRCASE_90:
                        if (player.getInventory().contains(ItemID.KEY_5)) {
                            player.getInventory().delete(ItemID.KEY_5, 1);
                            player.performAnimation(new Animation(806));
                            DialogueManager.sendStatement(player, "You threw the Yellow key into the dead end stairs...");
                            TravelSystem.INSTANCE.fadeTravelAction(player, true, true, "", 2, 6, 5, () -> {
                                player.moveTo(new Position(2613, 9522 + Misc.getRandomInclusive(2), 0));
                                DialogueManager.sendStatement(player, "You wake up in a tiny tunnel sort of dungeon..");
                                return null;
                            });
                        } else {
                            player.getPacketSender().sendMessage("You need a Yellow key from Fire giants to use the dead end dungeon stairs.", 1000);
                            return;
                        }
                        break;
                    case FURNACE_17:
                        for (Bar bar : Bar.values()) {
                            player.getPacketSender().sendInterfaceModel(bar.getFrame(), bar.getBar(), 150);
                        }
                        player.getPacketSender().sendChatboxInterface(2400);
                        break;
                    case 23564:
                        player.performAnimation(new Animation(6131));
                        DialogueManager.sendStatement(player, "You start climbing the pile of rubble.");
                        TravelSystem.INSTANCE.fadeTravelAction(player, true, true, "", 2, 6, 5, () -> {
                            player.moveTo(new Position(2620 + Misc.getRandomInclusive(1), 9496, 0));
                            DialogueManager.sendStatement(player, "You find yourself safe near a dead end dungeon stairs.");
                            player.resetAnimation();
                            return null;
                        });
                        break;
                    case KBD_LADDER_UP:
                        TeleportHandler.teleport(player, new Position(3017, 3850), TeleportType.LADDER_UP, false, false);
                        break;
                    case STAIRS_122:
                        player.moveTo(new Position(1768, 5366, 1));
                        break;
                    case STAIRS_123:
                        player.moveTo(new Position(1744, 5321, 1));
                        break;
                    case STAIRS_124:
                        if (player.getPosition().getX() == 1744)
                            player.moveTo(new Position(1744, 5325, 0));
                        else
                            player.moveTo(new Position(1772, 5366, 0));
                        break;
                    case STAIRS_125:
                        player.moveTo(new Position(1778, 5343, 1));
                        break;
                    case STAIRS_126:
                        player.moveTo(new Position(1778, 5346, 0));
                        break;
                    case STEPS_8:
                        if (player.getPosition().getY() == 9991)
                            player.moveTo(new Position(2703, 9989, 0));
                        else
                            player.moveTo(new Position(2703, 9991, 0));
                        break;
                    case ENTRANCE_11:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        player.getMotion().clearSteps();
                        player.getPacketSender().sendMinimapFlagRemoval();
                        EntityExtKt.markTime(player, Attribute.LAST_ACTION_BUTTON);
                        player.performAnimation(new Animation(827));
                        player.getMotion().update(MovementStatus.DISABLED);
                        TaskManager.submit(new Task(1) {
                            @Override
                            public void execute() {
                                player.moveTo(new Position(1859, 5243, 0));
                                player.getMotion().update(MovementStatus.NONE);
                                stop();
                            }
                        });
                        break;
                    case BONE_CHAIN:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        player.getMotion().clearSteps();
                        player.getPacketSender().sendMinimapFlagRemoval();
                        EntityExtKt.markTime(player, Attribute.LAST_ACTION_BUTTON);
                        player.performAnimation(new Animation(828));
                        player.getMotion().update(MovementStatus.DISABLED);
                        TaskManager.submit(new Task(1) {
                            @Override
                            public void execute() {
                                player.moveTo(new Position(3081, 3421, 0));
                                player.getMotion().update(MovementStatus.NONE);
                                stop();
                            }
                        });
                        break;
                    case SKELETON_73:
                        DialogueManager.sendStatement(player, "I'm not going to put my hands in this dirty thing.");
                        break;
                    case DICE:
                    case DICE_3:
                    case DICE_4:
                        DialogueManager.sendStatement(player, "You roll the dice and yield: @red@" + Misc.random(100) + "</col>.");
                        break;
                    case COINS:
                        new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("Perhaps I should not get myself into trouble.")
                                .setExpression(DialogueExpression.THINKING).start(player);
                        break;
                    case SACK:
                    case SACKS_2:
                    case SACKS_3:
                    case SACKS_4:
                    case SACKS_5:
                    case SACKS_6:
                    case SACKS_7:
                    case SACKS_8:
                    case SACKS_9:
                    case SACKS_10:
                    case SACKS_11:
                    case SACKS_12:
                    case SACKS_13:
                    case SACKS_14:
                    case SACKS_15:
                    case SACKS_16:
                    case SACKS_17:
                        player.sendMessage("There's nothing interesting in these sacks.");
                        break;
                    case DEAD_EXPLORER:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, false)) {
                            return;
                        }
                        if (player.getPosition().getY() == 5241) {
                            player.getMotion().clearSteps();
                            player.getPacketSender().sendMinimapFlagRemoval();
                            EntityExtKt.markTime(player, Attribute.LAST_PRAY);
                            player.performAnimation(new Animation(881, 25));
                            player.getMotion().update(MovementStatus.DISABLED);
                            TaskManager.submit(new Task(3) {
                                @Override
                                public void execute() {
                                    DialogueManager.sendStatement(player,
                                            "You search the dead explorer, but nothing valuable has been found.");
                                    player.getMotion().update(MovementStatus.NONE);
                                    stop();
                                }
                            });
                        }
                        break;
                    case GATE_OF_WAR:
                    case GATE_OF_WAR_2:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        /*
                         * if (System.currentTimeMillis() - c.logoutDelay <
                         * 5000) { // Resets the npc trying to attack you
                         * when you move into a door, like rs.
                         * Server.npcHandler.removeAllAttackables2(); }
                         */
                        player.getMotion().clearSteps();
                        player.getPacketSender().sendMinimapFlagRemoval();
                        player.performAnimation(new Animation(2246, 15));
                        player.getMotion().update(MovementStatus.DISABLED);
                        player.getPacketSender().sendMessage("You try to open the door..");
                        player.getPacketSender().sendSound(Sounds.OPEN_BANK_BOOTH);
                        TaskManager.submit(new Task(2) {
                            @Override
                            public void execute() {
                                if (player.getPosition().getY() == 5238
                                        || player.getPosition().getY() == 5235
                                        && player.getPosition().getX() != 1889
                                        && player.getPosition().getX() != 1888
                                        && player.getPosition().getX() != 1886
                                        && player.getPosition().getX() != 1885
                                        || player.getPosition().getY() == 5222
                                        || (player.getPosition().getY() == 5212
                                        && player.getPosition().getX() != 1896
                                        && player.getPosition().getX() != 1897
                                        && player.getPosition().getX() != 1894
                                        && player.getPosition().getX() != 1893
                                        && player.getPosition().getX() != 1890
                                        && player.getPosition().getX() != 1889)
                                        || player.getPosition().getY() == 5209
                                        && player.getPosition().getX() != 1889
                                        && player.getPosition().getX() != 1890
                                        || player.getPosition().getY() == 5198
                                        || (player.getPosition().getY() == 5195
                                        && player.getPosition().getX() != 1876
                                        && player.getPosition().getX() != 1877)
                                        || player.getPosition().getY() == 5191
                                        || player.getPosition().getY() == 5194
                                        || player.getPosition().getY() == 5206
                                        || player.getPosition().getY() == 5230
                                        || player.getPosition().getY() == 5207
                                        && player.getPosition().getX() != 1911
                                        || player.getPosition().getY() == 5204
                                        && player.getPosition().getX() != 1903
                                        && player.getPosition().getX() != 1906
                                        && player.getPosition().getX() != 1904
                                        && player.getPosition().getX() != 1907
                                        || player.getPosition().getY() == 5211
                                        || player.getPosition().getY() == 5233
                                        || player.getPosition().getY() == 5225) { // Increases
                                    // ur
                                    // absY
                                    // by
                                    // 1
                                    player.moveTo(new Position(player.getPosition().getX(),
                                            player.getPosition().getY() + 1, player.getPosition().getZ()));
                                } else if (player.getPosition().getY() == 5239
                                        && player.getPosition().getX() != 1875
                                        && player.getPosition().getX() != 1876
                                        && player.getPosition().getX() != 1878
                                        && player.getPosition().getX() != 1879
                                        || player.getPosition().getY() == 5223
                                        || player.getPosition().getY() == 5236
                                        && player.getPosition().getX() != 1888
                                        && player.getPosition().getX() != 1889
                                        && player.getPosition().getX() != 1886
                                        && player.getPosition().getX() != 1885
                                        || (player.getPosition().getY() == 5213
                                        && player.getPosition().getX() != 1897
                                        && player.getPosition().getX() != 1896
                                        && player.getPosition().getX() != 1894
                                        && player.getPosition().getX() != 1893)
                                        || player.getPosition().getY() == 5210
                                        || player.getPosition().getY() == 5199
                                        || player.getPosition().getY() == 5196
                                        || player.getPosition().getY() == 5192
                                        || (player.getPosition().getY() == 5195
                                        && (player.getPosition().getX() == 1876
                                        || player.getPosition().getX() == 1877))
                                        || player.getPosition().getY() == 5208
                                        || player.getPosition().getY() == 5229
                                        || player.getPosition().getY() == 5231
                                        || player.getPosition().getY() == 5205
                                        || player.getPosition().getY() == 5212
                                        && player.getPosition().getX() != 1893
                                        && player.getPosition().getX() != 1894
                                        && player.getPosition().getX() != 1896
                                        && player.getPosition().getX() != 1897
                                        || player.getPosition().getY() == 5209
                                        || player.getPosition().getY() == 5234
                                        || player.getPosition().getY() == 5223
                                        || (player.getPosition().getY() == 5207
                                        && player.getPosition().getX() == 1911
                                        || player.getPosition().getY() == 5226
                                        && player.getPosition().getX() != 1864
                                        && player.getPosition().getX() != 1868
                                        && player.getPosition().getX() != 1867)) { // Decreases
                                    // ur
                                    // absY
                                    // by
                                    // 1
                                    player.moveTo(new Position(player.getPosition().getX(),
                                            player.getPosition().getY() - 1, player.getPosition().getZ()));
                                } else if (player.getPosition().getX() == 1864
                                        || player.getPosition().getX() == 1866
                                        || (player.getPosition().getX() == 1867
                                        && (player.getPosition().getY() == 5227
                                        || player.getPosition().getY() == 5226
                                        && player.getPosition().getX() != 1867
                                        && player.getPosition().getX() != 1868
                                        || player.getPosition().getX() != 1878
                                        && player.getPosition().getX() != 1879)
                                        && player.getPosition().getY() != 5218
                                        && player.getPosition().getY() != 5217)
                                        || player.getPosition().getX() == 1869
                                        || player.getPosition().getX() == 1878
                                        && player.getPosition().getY() != 5226
                                        && player.getPosition().getY() != 5223
                                        && player.getPosition().getY() != 5222
                                        || player.getPosition().getX() == 1881
                                        || player.getPosition().getX() == 1896
                                        || player.getPosition().getX() == 1893
                                        || player.getPosition().getX() == 1903
                                        || player.getPosition().getX() == 1906
                                        || player.getPosition().getX() == 1888
                                        || player.getPosition().getX() == 1907
                                        && player.getPosition().getY() != 5204
                                        && player.getPosition().getY() != 5203
                                        || player.getPosition().getX() == 1885
                                        || player.getPosition().getX() == 1875
                                        || player.getPosition().getX() == 1883
                                        || player.getPosition().getX() == 1886
                                        && player.getPosition().getY() != 5235
                                        && player.getPosition().getY() != 5236) { // Increases
                                    // your
                                    // absX
                                    // by
                                    // 1
                                    player.moveTo(new Position(player.getPosition().getX() + 1,
                                            player.getPosition().getY(), player.getPosition().getZ()));
                                } else if (player.getPosition().getX() == 1865
                                        || player.getPosition().getX() == 1868
                                        || player.getPosition().getX() == 1867
                                        || player.getPosition().getX() == 1870
                                        || player.getPosition().getX() == 1879
                                        && player.getPosition().getY() != 5226
                                        && player.getPosition().getY() != 5223
                                        && player.getPosition().getY() != 5222
                                        || player.getPosition().getX() == 1880
                                        || player.getPosition().getX() == 1908
                                        || player.getPosition().getX() == 1889
                                        || player.getPosition().getX() == 1897
                                        || player.getPosition().getX() == 1894
                                        || player.getPosition().getX() == 1904
                                        && player.getPosition().getY() != 5230
                                        && player.getPosition().getY() != 5231
                                        && player.getPosition().getY() != 5233
                                        && player.getPosition().getY() != 5234
                                        || player.getPosition().getX() == 1907
                                        || player.getPosition().getX() == 1886
                                        && player.getPosition().getY() != 5243
                                        && player.getPosition().getY() != 5244
                                        || player.getPosition().getX() == 1876
                                        || player.getPosition().getX() == 1884
                                        || player.getPosition().getX() == 1887
                                        || player.getPosition().getX() == 1882) { // Decreases
                                    // your
                                    // absX
                                    // by
                                    // 1
                                    player.moveTo(new Position(player.getPosition().getX() - 1,
                                            player.getPosition().getY(), player.getPosition().getZ()));
                                }
                                player.getPacketSender().sendSound(Sounds.STRONGHOLD_SECURITY_DOOR_OPENING);
                                player.getPacketSender()
                                        .sendMessage("The magical door opens and teleports you further.");
                                player.getMotion().update(MovementStatus.NONE);
                                stop();
                            }
                        });
                        break;
                    case RICKETY_DOOR:
                    case RICKETY_DOOR_2:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        player.getMotion().clearSteps();
                        player.getPacketSender().sendMinimapFlagRemoval();
                        player.performAnimation(new Animation(2246, 15));
                        player.getMotion().update(MovementStatus.DISABLED);
                        player.getPacketSender().sendMessage("You try to open the door..");
                        player.getPacketSender().sendSound(Sounds.OPEN_BANK_BOOTH);
                        TaskManager.submit(new Task(2) {
                            @Override
                            public void execute() {
                                if ((player.getPosition().getY() == 5239 && player.getPosition().getX() != 2026
                                        && player.getPosition().getX() != 2027)
                                        || player.getPosition().getY() == 5224
                                        || player.getPosition().getY() == 5227
                                        && (player.getPosition().getX() != 2019
                                        && player.getPosition().getX() != 2018
                                        && player.getPosition().getX() != 2016
                                        && player.getPosition().getX() != 2015)
                                        || player.getPosition().getY() == 5238
                                        && player.getPosition().getX() != 2005
                                        && player.getPosition().getX() != 2006
                                        || player.getPosition().getY() == 5241
                                        || (player.getPosition().getY() == 5242
                                        && player.getPosition().getX() != 2026
                                        && player.getPosition().getX() != 2027)
                                        || player.getPosition().getY() == 5196
                                        && (player.getPosition().getX() != 2031
                                        && player.getPosition().getX() != 2032)
                                        || player.getPosition().getY() == 5193
                                        || player.getPosition().getY() == 5191
                                        || (player.getPosition().getY() == 5194
                                        && player.getPosition().getX() == 2004)
                                        || (player.getPosition().getY() == 5194
                                        && player.getPosition().getX() == 2005)
                                        || player.getPosition().getY() == 5234
                                        || (player.getPosition().getY() == 5237
                                        && player.getPosition().getX() != 2044
                                        && player.getPosition().getX() != 2045)
                                        || player.getPosition().getY() == 5236
                                        && (player.getPosition().getX() == 2044
                                        || player.getPosition().getX() == 2045)
                                        || player.getPosition().getY() == 5203
                                        && (player.getPosition().getX() == 2036
                                        || player.getPosition().getX() == 2037)
                                        || player.getPosition().getY() == 5200
                                        && (player.getPosition().getX() == 2036
                                        || player.getPosition().getX() == 2037)
                                        || ((player.getPosition().getY() == 5194
                                        || player.getPosition().getY() == 5197)
                                        && (player.getPosition().getX() == 2045
                                        || player.getPosition().getX() == 2046))
                                        || (player.getPosition().getY() == 5195
                                        && (player.getPosition().getX() == 2031
                                        || player.getPosition().getX() == 2032)
                                        || (player.getPosition().getY() == 5198
                                        && (player.getPosition().getX() == 2031
                                        || player.getPosition().getX() == 2032)))
                                        || player.getPosition().getY() == 5207
                                        || player.getPosition().getY() == 5210
                                        || player.getPosition().getY() == 5202
                                        || player.getPosition().getY() == 5199) { // Increases
                                    // ur
                                    // absY
                                    // by
                                    // +1
                                    player.moveTo(new Position(player.getPosition().getX(),
                                            player.getPosition().getY() + 1, player.getPosition().getZ()));
                                } else if (player.getPosition().getY() == 5240
                                        || player.getPosition().getY() == 5238
                                        || player.getPosition().getY() == 5225
                                        || player.getPosition().getY() == 5228
                                        && (player.getPosition().getX() != 2019
                                        && player.getPosition().getX() != 2016
                                        && player.getPosition().getX() != 2018
                                        && player.getPosition().getX() != 2015)
                                        || player.getPosition().getY() == 5339
                                        || player.getPosition().getY() == 5242
                                        || player.getPosition().getY() == 5243
                                        || (player.getPosition().getY() == 5240
                                        && (player.getPosition().getX() == 2026
                                        || player.getPosition().getX() == 2027))
                                        || player.getPosition().getY() == 5197
                                        || player.getPosition().getY() == 5194
                                        && (player.getPosition().getX() != 2045
                                        && player.getPosition().getX() != 2046)
                                        || player.getPosition().getY() == 5192
                                        || player.getPosition().getY() == 5195
                                        && (player.getPosition().getX() != 2031
                                        && player.getPosition().getX() != 2032)
                                        || player.getPosition().getY() == 5235
                                        || player.getPosition().getY() == 5239
                                        || player.getPosition().getY() == 5237
                                        && (player.getPosition().getX() == 2044
                                        || player.getPosition().getX() == 2045)
                                        || player.getPosition().getY() == 5204
                                        && (player.getPosition().getX() == 2036
                                        || player.getPosition().getX() == 2037)
                                        || player.getPosition().getY() == 5201
                                        && (player.getPosition().getX() == 2036
                                        || player.getPosition().getX() == 2037)
                                        || (player.getPosition().getY() == 5198
                                        || player.getPosition().getY() == 5195
                                        && (player.getPosition().getX() == 2045
                                        || player.getPosition().getX() == 2046))
                                        || (player.getPosition().getY() == 5199
                                        && (player.getPosition().getX() == 2031
                                        || player.getPosition().getX() == 2032)
                                        || (player.getPosition().getY() == 5196
                                        && (player.getPosition().getX() == 2031
                                        || player.getPosition().getX() == 2032)))
                                        || player.getPosition().getY() == 5208
                                        || player.getPosition().getY() == 5211
                                        || player.getPosition().getY() == 5203
                                        || player.getPosition().getY() == 5200) { // Decreases
                                    // ur
                                    // absY
                                    // by
                                    // -1
                                    player.moveTo(new Position(player.getPosition().getX(),
                                            player.getPosition().getY() - 1, player.getPosition().getZ()));
                                } else if (player.getPosition().getX() == 1864
                                        || player.getPosition().getX() == 2042
                                        || player.getPosition().getX() == 2039
                                        || player.getPosition().getX() == 2036
                                        && player.getPosition().getY() != 5203
                                        && player.getPosition().getY() != 5200
                                        || player.getPosition().getX() == 2033
                                        && (player.getPosition().getY() == 5185
                                        || player.getPosition().getY() == 5186)
                                        || (player.getPosition().getX() == 2018
                                        || player.getPosition().getX() == 2015)
                                        && (player.getPosition().getY() == 5228
                                        || player.getPosition().getY() == 5227)
                                        || player.getPosition().getX() == 1996
                                        || player.getPosition().getX() == 1999
                                        || player.getPosition().getX() == 2005
                                        || player.getPosition().getX() == 2018
                                        || player.getPosition().getX() == 2008) { // Increases
                                    // your
                                    // absX
                                    // by
                                    // +1
                                    player.moveTo(new Position(player.getPosition().getX() + 1,
                                            player.getPosition().getY(), player.getPosition().getZ()));
                                } else if (player.getPosition().getX() == 1865
                                        || player.getPosition().getX() == 2043
                                        || player.getPosition().getX() == 2037
                                        && player.getPosition().getY() != 5204
                                        && player.getPosition().getY() != 5201
                                        || player.getPosition().getX() == 2040
                                        || player.getPosition().getX() == 2034
                                        && (player.getPosition().getY() == 5185
                                        || player.getPosition().getY() == 5186)
                                        || (player.getPosition().getX() == 2019
                                        || player.getPosition().getX() == 2016)
                                        && (player.getPosition().getY() == 5228
                                        || player.getPosition().getY() == 5227)
                                        || player.getPosition().getX() == 1997
                                        || player.getPosition().getX() == 2000
                                        || player.getPosition().getX() == 2006
                                        || player.getPosition().getX() == 2009) { // Decreases
                                    // your
                                    // absX
                                    // by
                                    // -1
                                    player.moveTo(new Position(player.getPosition().getX() - 1,
                                            player.getPosition().getY(), player.getPosition().getZ()));
                                }
                                player.getPacketSender().sendSound(Sounds.STRONGHOLD_SECURITY_DOOR_OPENING);
                                player.getPacketSender()
                                        .sendMessage("The magical door opens and teleports you further.");
                                player.getMotion().update(MovementStatus.NONE);
                                stop();
                            }
                        });
                        break;
                    case OOZING_BARRIER:
                    case OOZING_BARRIER_2:
                    case OOZING_BARRIER_3:
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        player.getMotion().clearSteps();
                        player.getPacketSender().sendMinimapFlagRemoval();
                        player.performAnimation(new Animation(2246, 15));
                        player.getMotion().update(MovementStatus.DISABLED);
                        player.getPacketSender().sendMessage("You try to open the door..");
                        player.getPacketSender().sendSound(Sounds.OPEN_BANK_BOOTH);
                        TaskManager.submit(new Task(2) {
                            @Override
                            public void execute() {
                                if (player.getPosition().getY() == 5256 || player.getPosition().getY() == 5259
                                        || player.getPosition().getY() == 5278
                                        && (player.getPosition().getX() == 2133
                                        || player.getPosition().getX() == 2132)
                                        || player.getPosition().getY() == 5281
                                        && (player.getPosition().getX() == 2132
                                        || player.getPosition().getX() == 2133)
                                        || player.getPosition().getY() == 5295
                                        && (player.getPosition().getY() == 2130
                                        || player.getPosition().getX() == 2131)
                                        || player.getPosition().getY() == 5292
                                        && (player.getPosition().getX() == 2130
                                        || player.getPosition().getX() == 2131)
                                        || player.getPosition().getY() == 5298
                                        && (player.getPosition().getX() == 2148
                                        || player.getPosition().getX() == 2149)
                                        || player.getPosition().getY() == 5301
                                        && (player.getPosition().getX() == 2148
                                        || player.getPosition().getX() == 2149)
                                        || player.getPosition().getY() == 5296
                                        && (player.getPosition().getX() == 2167
                                        || player.getPosition().getX() == 2168)
                                        || player.getPosition().getY() == 5293
                                        && (player.getPosition().getX() == 2167
                                        || player.getPosition().getX() == 2168)
                                        || player.getPosition().getY() == 5261
                                        && (player.getPosition().getX() == 2166
                                        || player.getPosition().getX() == 2167)
                                        || player.getPosition().getY() == 5258
                                        && (player.getPosition().getX() == 2166
                                        || player.getPosition().getX() == 2167)
                                        || player.getPosition().getY() == 5289
                                        && (player.getPosition().getX() == 2162
                                        || player.getPosition().getX() == 2163)
                                        || player.getPosition().getY() == 5286
                                        && (player.getPosition().getX() == 2162
                                        || player.getPosition().getX() == 2163)
                                        || player.getPosition().getY() == 5277
                                        && (player.getPosition().getX() == 2163
                                        || player.getPosition().getX() == 2164)
                                        || player.getPosition().getY() == 5274
                                        && (player.getPosition().getX() == 2163
                                        || player.getPosition().getX() == 2164)
                                        || player.getPosition().getY() == 5285
                                        && (player.getPosition().getX() == 2155
                                        || player.getPosition().getX() == 2156)
                                        || player.getPosition().getY() == 5288
                                        && (player.getPosition().getX() == 2155
                                        || player.getPosition().getX() == 2156)) { // Increases
                                    // ur
                                    // absY
                                    // by
                                    // +1
                                    player.moveTo(new Position(player.getPosition().getX(),
                                            player.getPosition().getY() + 1, player.getPosition().getZ()));
                                } else if (player.getPosition().getY() == 5257
                                        || player.getPosition().getY() == 5260
                                        || player.getPosition().getY() == 5279
                                        && (player.getPosition().getX() == 2133
                                        || player.getPosition().getX() == 2132)
                                        || player.getPosition().getY() == 5282
                                        && (player.getPosition().getX() == 2132
                                        || player.getPosition().getX() == 2133)
                                        || player.getPosition().getY() == 5296
                                        && (player.getPosition().getX() == 2130
                                        || player.getPosition().getX() == 2131)
                                        || player.getPosition().getY() == 5293
                                        && (player.getPosition().getX() == 2130
                                        || player.getPosition().getX() == 2131)
                                        || player.getPosition().getY() == 5299
                                        && (player.getPosition().getX() == 2148
                                        || player.getPosition().getX() == 2149)
                                        || player.getPosition().getY() == 5302
                                        && (player.getPosition().getX() == 2148
                                        || player.getPosition().getX() == 2149)
                                        || player.getPosition().getY() == 5297
                                        && (player.getPosition().getX() == 2167
                                        || player.getPosition().getX() == 2168)
                                        || player.getPosition().getY() == 5294
                                        && (player.getPosition().getX() == 2167
                                        || player.getPosition().getX() == 2168)
                                        || player.getPosition().getY() == 5262
                                        && (player.getPosition().getX() == 2166
                                        || player.getPosition().getX() == 2167)
                                        || player.getPosition().getY() == 5259
                                        && (player.getPosition().getX() == 2166
                                        || player.getPosition().getX() == 2167)
                                        || player.getPosition().getY() == 5290
                                        && (player.getPosition().getX() == 2162
                                        || player.getPosition().getX() == 2163)
                                        || player.getPosition().getY() == 5287
                                        && (player.getPosition().getX() == 2162
                                        || player.getPosition().getX() == 2163)
                                        || player.getPosition().getY() == 5278
                                        && (player.getPosition().getX() == 2163
                                        || player.getPosition().getX() == 2164)
                                        || player.getPosition().getY() == 5275
                                        && (player.getPosition().getX() == 2163
                                        || player.getPosition().getX() == 2164)
                                        || player.getPosition().getY() == 5286
                                        && (player.getPosition().getX() == 2155
                                        || player.getPosition().getX() == 2156)
                                        || player.getPosition().getY() == 5289
                                        && (player.getPosition().getX() == 2155
                                        || player.getPosition().getX() == 2156)) { // Decreases
                                    // ur
                                    // absY
                                    // by
                                    // -1
                                    player.moveTo(new Position(player.getPosition().getX(),
                                            player.getPosition().getY() - 1, player.getPosition().getZ()));
                                } else if (player.getPosition().getX() == 2126
                                        && (player.getPosition().getY() == 5287
                                        || player.getPosition().getY() == 5288)
                                        || player.getPosition().getX() == 2123
                                        && (player.getPosition().getY() == 5287
                                        || player.getPosition().getY() == 5288)
                                        || player.getPosition().getX() == 2137
                                        && (player.getPosition().getY() == 5294
                                        || player.getPosition().getY() == 5295)
                                        || player.getPosition().getX() == 2140
                                        && (player.getPosition().getY() == 5294
                                        || player.getPosition().getY() == 5295)
                                        || player.getPosition().getX() == 2170
                                        && (player.getPosition().getY() == 5271
                                        || player.getPosition().getY() == 5272)
                                        || player.getPosition().getX() == 2167
                                        && (player.getPosition().getY() == 5271
                                        || player.getPosition().getY() == 5272)
                                        || player.getPosition().getX() == 2156
                                        && (player.getPosition().getY() == 5263
                                        || player.getPosition().getY() == 5264)
                                        || player.getPosition().getX() == 2153
                                        && (player.getPosition().getY() == 5263
                                        || player.getPosition().getY() == 5264)
                                        || player.getPosition().getX() == 2140
                                        && (player.getPosition().getY() == 5262
                                        || player.getPosition().getY() == 5263)
                                        || player.getPosition().getX() == 2137
                                        && (player.getPosition().getY() == 5262
                                        || player.getPosition().getY() == 5263)
                                        || player.getPosition().getX() == 2152
                                        && (player.getPosition().getY() == 5291
                                        || player.getPosition().getY() == 5292)
                                        || player.getPosition().getX() == 2148
                                        && (player.getPosition().getY() == 5291
                                        || player.getPosition().getY() == 5292)) { // Increases
                                    // your
                                    // absX
                                    // by
                                    // +1
                                    player.moveTo(new Position(player.getPosition().getX() + 1,
                                            player.getPosition().getY(), player.getPosition().getZ()));
                                } else if (player.getPosition().getX() == 2127
                                        && (player.getPosition().getY() == 5287
                                        || player.getPosition().getY() == 5288)
                                        || player.getPosition().getX() == 2124
                                        && (player.getPosition().getY() == 5287
                                        || player.getPosition().getY() == 5288)
                                        || player.getPosition().getX() == 2138
                                        && (player.getPosition().getY() == 5294
                                        || player.getPosition().getY() == 5295)
                                        || player.getPosition().getX() == 2141
                                        && (player.getPosition().getY() == 5294
                                        || player.getPosition().getY() == 5295)
                                        || player.getPosition().getX() == 2171
                                        && (player.getPosition().getY() == 5271
                                        || player.getPosition().getY() == 5272)
                                        || player.getPosition().getX() == 2168
                                        && (player.getPosition().getY() == 5271
                                        || player.getPosition().getY() == 5272)
                                        || player.getPosition().getX() == 2157
                                        && (player.getPosition().getY() == 5263
                                        || player.getPosition().getY() == 5264)
                                        || player.getPosition().getX() == 2154
                                        && (player.getPosition().getY() == 5263
                                        || player.getPosition().getY() == 5264)
                                        || player.getPosition().getX() == 2141
                                        && (player.getPosition().getY() == 5262
                                        || player.getPosition().getY() == 5263)
                                        || player.getPosition().getX() == 2138
                                        && (player.getPosition().getY() == 5262
                                        || player.getPosition().getY() == 5263)
                                        || player.getPosition().getX() == 2153
                                        && (player.getPosition().getY() == 5291
                                        || player.getPosition().getY() == 5292)
                                        || player.getPosition().getX() == 2149
                                        && (player.getPosition().getY() == 5291
                                        || player.getPosition().getY() == 5292)) { // Decreases
                                    // your
                                    // absX
                                    // by
                                    // -1
                                    player.moveTo(new Position(player.getPosition().getX() - 1,
                                            player.getPosition().getY(), player.getPosition().getZ()));
                                }
                                player.getPacketSender().sendSound(Sounds.STRONGHOLD_SECURITY_DOOR_OPENING);
                                player.getPacketSender()
                                        .sendMessage("The magical door opens and teleports you further.");
                                player.getMotion().update(MovementStatus.NONE);
                                stop();
                            }
                        });
                        break;
                    case AGED_LOG:
                        player.getPacketSender().sendMessage("The log is broken and can't be used now.", 1000);
                        break;
                    case 32057:
                    case 34586:
                    case 34585:
                    case 34581:
                    case 34584:
                    case 34662:
                        BrimstoneChest.openBrimstoneChest(player);
                        break;
                    case 34831:
                        LarransSmallChest.openLarransSmallChest(player);
                        break;
                    case 34832:
                        LarransLargeChest.openLarransLargeChest(player);
                        break;
                    case MANHOLE:
                        player.sendMessage("It appears to be locked from the inside.");
                        break;
                    case 34587:
                        player.getPacketSender().sendMessage("The machine is broken and can't be used now.", 1000);
                        break;
                    case KBD_ENTRANCE_LEVER:
                        if (!player.getCombat().getTeleBlockTimer().finished()) {
                            player.getPacketSender()
                                    .sendMessage("A magical spell is blocking you from teleporting.", 1000);
                            return;
                        }
                        TeleportHandler.teleportByLever(player, object, new Position(2271, 4680), false, false);
                        break;
                    case LEVER_41:
                        if (!player.getCombat().getTeleBlockTimer().finished()) {
                            player.getPacketSender()
                                    .sendMessage("A magical spell is blocking you from teleporting.", 1000);
                            return;
                        }
                        TeleportHandler.teleportByLever(player, object, new Position(3106, 3951), false, false);
                        break;
                    case LEVER_42:
                        if (!player.getCombat().getTeleBlockTimer().finished()) {
                            player.getPacketSender()
                                    .sendMessage("A magical spell is blocking you from teleporting.", 1000);
                            return;
                        }
                        TeleportHandler.teleportByLever(player, object, new Position(3105, 3956), false, false);
                        break;
                    case LEVER_39:
                        if (!player.getCombat().getTeleBlockTimer().finished()) {
                            player.getPacketSender()
                                    .sendMessage("A magical spell is blocking you from teleporting.", 1000);
                            return;
                        }
                        TeleportHandler.teleportByLever(player, object, new Position(3090, 3956), true, false);
                        break;
                    case LEVER_38:
                        if (!player.getCombat().getTeleBlockTimer().finished()) {
                            player.getPacketSender()
                                    .sendMessage("A magical spell is blocking you from teleporting.", 1000);
                            return;
                        }
                        TeleportHandler.teleportByLever(player, object, new Position(2539, 4712), false, false);
                        break;
                    case LEVER_54:
                        if (!player.getCombat().getTeleBlockTimer().finished()) {
                            player.getPacketSender()
                                    .sendMessage("A magical spell is blocking you from teleporting.", 1000);
                            return;
                        }
                        TeleportHandler.teleportByLever(player, object, new Position(3154, 3923), true, false);
                        break;
                    case KBD_EXIT_LEVER:
                        TeleportHandler.teleportByLever(player, object, new Position(3067, 10253), false, false);
                        break;
                    case AN_EXPERIMENTAL_ANVIL:
                    case ANVIL_2:
                    case ANVIL_3:
                    case ANVIL_4:
                    case ANVIL_5:
                    case ANVIL_6:
                        EquipmentMaking.openInterface(player);
                        break;
                    case ANVIL:
                        if (!QuestManager.hasCompletedQuest(player, "Doric's Quest")) {
                            player.sendMessage("You must complete the quest 'Doric's Quest' to be able to use this anvil.");
                            return;
                        }
                        EquipmentMaking.openInterface(player);
                        break;
                    case 27258:
                        player.sendMessage("The tunnel is too small for you to enter.");
                        break;
                    case ALTAR:
                    case ALTAR_2:
                    case ALTAR_3:
                    case ALTAR_4:
                    case ALTAR_5:
                    case ALTAR_6:
                    case ALTAR_7:
                    case ALTAR_8:
                    case ALTAR_9:
                    case ALTAR_10:
                    case ALTAR_11:
                    case ALTAR_12:
                    case ALTAR_13:
                    case ALTAR_14:
                    case ALTAR_15:
                    case ALTAR_16:
                    case ALTAR_17:
                    case ALTAR_18:
                    case ALTAR_19:
                    case ALTAR_20:
                    case ALTAR_21:
                    case ALTAR_22:
                    case ALTAR_23:
                    case ALTAR_24:
                    case ALTAR_25:
                    case ALTAR_26:
                    case ALTAR_27:
                    case ALTAR_28:
                    case ALTAR_29:
                    case ALTAR_30:
                    case ALTAR_31:
                    case ALTAR_32:
                    case ALTAR_33:
                    case ALTAR_34:
                    case ALTAR_35:
                    case ALTAR_36:
                    case ALTAR_37:
                    case ALTAR_38:
                    case ALTAR_39:
                    case ALTAR_40:
                    case ALTAR_41:
                    case ALTAR_42:
                    case ALTAR_44:
                    case CHAOS_ALTAR_2:
                    case SHRINE:
                    case 29941:
                    case ALTAR_OF_GUTHIX:
                        if (EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 2, TimeUnit.SECONDS, false, true)) {
                            if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager()
                                    .getMaxLevel(Skill.PRAYER)) {
                                player.performAnimation(new Animation(645));
                                player.getSkillManager().setCurrentLevel(Skill.PRAYER,
                                        player.getSkillManager().getMaxLevel(Skill.PRAYER), true);
                                player.getPacketSender().sendMessage("You recharge your Prayer points.");
                                player.getPoints().increase(AttributeManager.Points.RECHARGED_PRAYER_TIMES, 1); // Increase points
                                player.getPacketSender().sendSound(Sounds.PRAY_ALTAR);
                                EntityExtKt.markTime(player, Attribute.LAST_PRAY);
                            } else {
                                EntityExtKt.markTime(player, Attribute.LAST_PRAY);
                                player.getPacketSender().sendMessage("You already have full prayer points.");
                                player.getPacketSender().sendSound(Sounds.PRAYER_UNAVAILABLE_SOUND);
                                return;
                            }
                        }
                        break;
                    case STATUE_OF_SARADOMIN:
                    case STATUE_OF_SARADOMIN_2:
                    case STATUE_OF_SARADOMIN_3:
                        if (EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 2, TimeUnit.SECONDS, false, true)) {
                            player.performAnimation(new Animation(645));
                            player.getPacketSender().sendMessage("You receive the Saradomin's blessings!");
                            player.getPacketSender().sendSound(Sounds.PRAY_ALTAR);
                        } else {
                            return;
                        }
                        break;
                    case STATUE_OF_ZAMORAK:
                    case STATUE_OF_ZAMORAK_2:
                        if (EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 2, TimeUnit.SECONDS, false, true)) {
                            player.performAnimation(new Animation(645));
                            player.getPacketSender().sendMessage("You receive the Zamorak's blessings!");
                            player.getPacketSender().sendSound(Sounds.PRAY_ALTAR);
                        } else {
                            return;
                        }
                        break;
                    case STATUE_OF_GUTHIX:
                        if (EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 2, TimeUnit.SECONDS, false, true)) {
                            player.performAnimation(new Animation(645));
                            player.getPacketSender().sendMessage("You receive the Guthix's blessings!");
                            player.getPacketSender().sendSound(Sounds.PRAY_ALTAR);
                        } else {
                            return;
                        }
                        break;
                    case DITCH_PORTAL:
                        player.getPacketSender().sendMessage("You're teleported to the Wilderness ditch.");
                        player.moveTo(new Position(3087, 3520));
                        break;
                    case WILDERNESS_DITCH:
                        player.getMotion().clearSteps();
                        if (player.getForceMovement() == null && player.getClickDelay().elapsed(2000)) {

                            final boolean fromWild = player.getPosition().getY() >= 3522;

                            final Position crossDitch = new Position(0,fromWild ? -3 : 3);

                            EntityExtKt.setBoolean(player, Attribute.STALL_HITS, true, false);
                            TaskManager.submit(new ForceMovementTask(player, 3,
                                    new ForceMovement(player.getPosition().clone(), crossDitch, 0, 70, crossDitch.getY() == 3 ? 0 : 2, 6132)){
                                @Override
                                protected void execute() {
                                    super.execute();
                                    if(!fromWild)
                                        WildernessArea.onCrossDitch(player);
                                    EntityExtKt.setBoolean(player, Attribute.STALL_HITS, false, false);
                                }
                            });

                            player.getPacketSender().sendSound(Sounds.DITCH_JUMP);
                            player.getClickDelay().reset();
                        }
                        break;
                    case ANCIENT_ALTAR:
                        if (player.getSpellbook().equals(MagicSpellbook.NORMAL)) {
                            player.getPacketSender().sendInterfaceRemoval();
                            MagicSpellbook.changeSpellbook(player, MagicSpellbook.ANCIENT);
                            DialogueManager.sendStatement(player, "You have switched to ancient magicks spell book.");
                        } else {
                            player.getPacketSender().sendInterfaceRemoval();
                            MagicSpellbook.changeSpellbook(player, MagicSpellbook.NORMAL);
                            DialogueManager.sendStatement(player, "You have switched to modern spell book.");
                        }
                        break;
                    case MAGICAL_ALTAR:
                        DialogueManager.start(player, 8);
                        player.setDialogueOptions(new DialogueOptions() {
                            @Override
                            public void handleOption(Player player, int option) {
                                switch (option) {
                                    case 1: // Normal spellbook option
                                        player.getPacketSender().sendInterfaceRemoval();
                                        MagicSpellbook.changeSpellbook(player, MagicSpellbook.NORMAL);
                                        // DialogueManager.sendStatement(player,
                                        // "You have switched to modern spell
                                        // book.");
                                        break;
                                    case 2: // Ancient spellbook option
                                        player.getPacketSender().sendInterfaceRemoval();
                                        MagicSpellbook.changeSpellbook(player, MagicSpellbook.ANCIENT);
                                        // DialogueManager.sendStatement(player,
                                        // "You have switched to ancient magicks
                                        // spell book.");
                                        break;
                                    case 3: // Lunar spellbook option
                                        player.getPacketSender().sendInterfaceRemoval();
                                        MagicSpellbook.changeSpellbook(player, MagicSpellbook.LUNAR);
                                        // DialogueManager.sendStatement(player,
                                        // "You have switched to lunar spell
                                        // book.");
                                        break;
                                    case 4: // Cancel option
                                        player.getPacketSender().sendInterfaceRemoval();
                                        break;
                                }
                            }
                        });
                        break;
                    case NETTLES:
                    case NETTLES_2:
                    case NETTLES_3:
                    case NETTLES_4:
                    case NETTLES_5:
                    case NETTLES_6:
                    case NETTLES_7:
                        if (player.getInventory().countFreeSlots() < 1) {
                            DialogueManager.sendStatement(player, "You need to have a free inventory slots to pickup the nettles.");
                            return;
                        }
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 2, TimeUnit.SECONDS, false, true)) {
                            return;
                        }
                        player.performAnimation(new Animation(827));
                        TaskManager.submit(new Task(2) {
                            @Override
                            public void execute() {
                                player.getPacketSender().sendMessage("You pickup some nettles.");
                                player.getInventory().add(new Item(4241, 1));
                                player.getPacketSender().sendSound(Sounds.PICKUP_ITEM);
                                stop();
                            }
                        });
                        break;
                    case ORNATE_REJUVENATION_POOL:
                        if (EntityExtKt.passedTime(player, Attribute.LAST_REFRESH, 1, TimeUnit.MINUTES, false, true)) {
                            // player.getPacketSender().sendMessage("You
                            // feel slightly refreshed.");
                            DialogueManager.sendStatement(player, "You feel slightly refreshed.");
                            player.performGraphic(new Graphic(436));
                            player.restoreRegularAttributes();
                            PrayerHandler.resetAll(player);
                            player.getPacketSender().sendSound(Sounds.HEALED_BY_NURSE);
                        } else {
                            player.getPacketSender()
                                    .sendMessage("You must wait one minute before being able to use it again.");
                            return;
                        }
                        break;

                    default:
                        if (def.getName() == null) {
                            player.sendMessage("Nothing interesting happens.");
                            return;
                        }
                        if ((def.getName().toLowerCase().contains("ladder")
                                || def.getName().toLowerCase().equals("vine")
                                || def.getName().toLowerCase().contains("staircase")
                                || def.getName().toLowerCase().equals("stairs"))) {
                            return;
                        } else if (SearchObjectActions.INSTANCE.isSearchable(def)) {
                            SearchObjectActions.INSTANCE.handle(player, object, null);
                            return;
                        }
                        player.getPacketSender().sendMessage("Nothing interesting happens.", 1000);
                        break;

                }
            }
        };

        if (executeImmediately)
            objectClickAction.execute();
        else {
            preStartWalkTask(player);
            if (partial(object)) {
                player.setWalkToTask(new WalkToAction<>(player, object, 1, objectClickAction, WalkToAction.Policy.EXECUTE_ON_PARTIAL));
            } else {
                player.setWalkToTask(new WalkToAction<>(player, object, 2, objectClickAction));
            }
        }
    }

    private static boolean partial(GameObject object) {
        Optional<Passage> passage = PassageManager.findOrPredict(object);
        if (passage.isPresent() && passage.get().getMode() == PassageMode.FORCE && (passage.get().getCategory() == PassageCategory.DOOR || passage.get().getCategory() == PassageCategory.GATE)) {
            return true;
        }
        if (object.getId() == ObjectID.BROKEN_RAFT) {
            return true;
        }
        return false;
    }


    /**
     * Handles the second click option on an object.
     */
    private static void secondClick(final Player player, int id, Position position, GameObject object, ObjectDefinition def, boolean executeImmediately) {

        player.sendDevelopersMessage("Second click object: " + object);

        final Executable objectClickAction = () -> {

            ObjectActions.INSTANCE.faceObj(player, object, def);

            DebugManager.debug(player, "object-option", "2: "+object.getId()+", pos: "+object.getPosition().toString());

            if(PacketInteractionManager.handleObjectInteraction(player, object, 2)) {
                return;
            }

            if (player.getArea() != null) {
                if (player.getArea().handleObjectClick(player, object, 2)) {
                    return;
                }
            }

            if (player.getLocalObject(object.getId(), new Position(object.getX(), object.getY(), player.getPosition().getZ())).isEmpty() && (!ClippedMapObjects.exists(object) || !ObjectManager.existsAt(object.getId(), object.getPosition()))) {
                return;
            }

            if(PassageManager.handle(player,object,2))
                return;

            if (player.getClueScrollManager().handleObjectAction(2, position))
                return;

            if (SkillUtil.startSkillable(player, object, 2))
                return;

            // Check thieving..
            if (StallThieving.init(player, object)) {
                return;
            }

            if (ClimbObjectActions.handleClimbObject(player, object, def, position, 1))
                return;

            if (SpiritTreeData.Companion.fromId(id) != null) {
                SpiritTreeData spiritTreeData = SpiritTreeData.Companion.fromId(id);
                SpiritTree.Companion.handleTeleports(player, spiritTreeData.getSpiritTreeTeleportData());
                return;
            }

            if (CastleWars.processClick(player, id, object.getPosition().getX(), object.getPosition().getY(), object.getPosition().getZ(), object.getFace(), object.getObjectType(), 2)) {
                return;
            }

            if(PestControl.handleObject(player,object, 2))
                return;

            switch (id) {
                case SMOKY_CAVE:
                    player.moveTo(new Position(2404, 9415, 0));
                    break;
                case CREVICE_2:
                    int playerCount = AreaManager.getPlayersInBoundaries(new Boundary(2348, 2376, 9436, 9460));
                    if(playerCount == 0)
                        player.sendMessage("You look inside the crevice and see no adventurers inside the cave.");
                    else
                        player.sendMessage("You look inside the crevice and see " + playerCount + " adventurer" + (playerCount == 1 ? "" : "s") + " inside the cave.");
                    break;
                case LOOM:
                case LOOM_2:
                    Weaving.showWeavingInterface(player);
                    break;
                case 42965:
                    TeleportHandler.teleportNoReq(player, new Position(2903, 5202).randomize(3), TeleportType.NORMAL, false, false);
                    break;
                case ROCKS_4:
                    player.moveTo(new Position(2830, 9522, 0));
                    break;
                case PALM_TREE:
                    player.performAnimation(new Animation(834, 25));
                    player.sendMessage("You search the " + object.getDefinition().name.toLowerCase() + "...");
                    player.BLOCK_ALL_BUT_TALKING = true;
                    TaskManager.submit(new Task(2) {
                        @Override
                        public void execute() {
                            stop();
                            player.sendMessage("You don't find anything to pick.");
                            player.BLOCK_ALL_BUT_TALKING = false;
                        }
                    });
                    break;
                case 31681:
                    player.sendMessage("You need a Brittle key to enter from this entrance.");
                    break;
                case 32534:
                    player.sendMessage("Your Bryophyta kill count is: @red@" + MonsterKillTracker.getKillsCount(player, NpcID.BRYOPHYTA) + "</col>.");
                    break;
                case WARDROBE_3:
                case SHELVES:
                case SHELVES_2:
                case SHELVES_3:
                case SHELVES_4:
                case SHELVES_5:
                case SHELVES_6:
                case SHELVES_7:
                case SHELVES_8:
                case SHELVES_9:
                case SHELVES_10:
                case SHELVES_11:
                case SHELVES_12:
                case SHELVES_13:
                case SHELVES_14:
                case SHELVES_15:
                case SHELVES_16:
                case SHELVES_17:
                case SHELVES_18:
                case SHELVES_19:
                case SHELVES_20:
                case SHELVES_21:
                case SHELVES_22:
                case SHELVES_23:
                case SHELVES_24:
                case OLD_BOOKSHELF:
                case OLD_BOOKSHELF_2:
                case OLD_BOOKSHELF_3:
                case OLD_BOOKSHELF_4:
                case OLD_BOOKSHELF_5:
                case OLD_BOOKSHELF_6:
                case OLD_BOOKSHELF_7:
                case OLD_BOOKSHELF_8:
                case OLD_BOOKSHELF_9:
                case OLD_BOOKSHELF_10:
                case OLD_BOOKSHELF_11:
                case OLD_BOOKSHELF_12:
                case OLD_BOOKSHELF_13:
                case OLD_BOOKSHELF_14:
                case OLD_BOOKSHELF_15:
                case OLD_BOOKSHELF_16:
                case OLD_BOOKSHELF_17:
                case OLD_BOOKSHELF_18:
                case OLD_BOOKSHELF_19:
                case OLD_BOOKSHELF_20:
                case OLD_BOOKSHELF_21:
                case OLD_BOOKSHELF_22:
                case OLD_BOOKSHELF_23:
                case OLD_BOOKSHELF_24:
                case OLD_BOOKSHELF_25:
                case OLD_BOOKSHELF_26:
                case OLD_BOOKSHELF_27:
                case OLD_BOOKSHELF_28:
                case OLD_BOOKSHELF_29:
                case OLD_BOOKSHELF_30:
                case OLD_BOOKSHELF_31:
                case OLD_BOOKSHELF_32:
                case OLD_BOOKSHELF_33:
                case OLD_BOOKSHELF_34:
                case OLD_BOOKSHELF_35:
                case OLD_BOOKSHELF_36:
                case OLD_BOOKSHELF_37:
                case OLD_BOOKSHELF_38:
                case OLD_BOOKSHELF_39:
                case OLD_BOOKSHELF_40:
                case OLD_BOOKSHELF_41:
                case OLD_BOOKSHELF_42:
                case OLD_BOOKSHELF_43:
                case OLD_BOOKSHELF_44:
                case OLD_BOOKSHELF_45:
                case OLD_BOOKSHELF_46:
                case OLD_BOOKSHELF_47:
                case OLD_BOOKSHELF_48:
                    player.sendMessage("You search the " + object.getDefinition().name.toLowerCase() + "...");
                    player.BLOCK_ALL_BUT_TALKING = true;
                    TaskManager.submit(new Task(1) {
                        @Override
                        public void execute() {
                            stop();
                            player.BLOCK_ALL_BUT_TALKING = false;
                            player.sendMessage("You find nothing of interest.");
                        }
                    });
                    break;
                case CREVICE_16:
                    player.sendMessage("You listen carefully and hear some squeaking noises inside.");
                    break;
                case 9345:
                case 9380:
                case 19333:
                case 19223:
                    player.sendMessage("The trap doesn't seem to be triggered and is working.");
                    break;
                case 27785:
                    player.sendMessage("The statue reads the names of heroes that are left missing from Kourend town.");
                    break;
/*                case PORTAL_51:
                    DialogueManager.sendStatement(player, "Construction will be avaliable in the future.");
                    break;*/
                case 26760:
                    if (player.getPosition().getY() <= 3944) {
                        player.moveTo(new Position(3184, 3945, 0));
                        return;
                    }
                    if (!player.getInventory().contains(new Item(ItemID.COINS, 2_500_000))) {
                        DialogueManager.sendStatement(player, "You must have at least 2,500,000 coins to enter the Wilderness resource area.");
                        return;
                    }
                    player.getInventory().delete(ItemID.COINS, 2_500_000);
                    player.moveTo(new Position(3184, 3944, 0));
                    player.sendMessage("@red@You receieve 25% bonus experience when skilling in the Wilderness resource area.");
                    player.sendMessage("@red@Please be aware of PKer's!");
                    break;

                case LADDER_37:
                    if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                        return;
                    }
                    TeleportHandler.teleport(player,
                            new Position(2674, 10096, 0),
                            TeleportType.LADDER_DOWN, false, true);
                    break;
                case STAIRCASE_141: // Falador Party room west staircase
                    if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                        return;
                    }
                    player.moveTo(new Position(3039, 3383, 1));
                    break;
                case STAIRCASE_142: // Falador Party room east staircase
                    if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                        return;
                    }
                    player.moveTo(new Position(3052, 3383, 1));
                    break;
                case LADDER_29:
                case 16672:
                case 16684:
                case 12537:
                    TeleportHandler.teleport(player,
                            new Position(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ() + 1),
                            TeleportType.LADDER_UP, false, true);
                    break;
                case 26366:
                    player.moveTo(new Position(2862, 5354, 2));
                    player.getPacketSender().sendMessage("You have been teleported outside the boss chamber.");
                    break;
                case 26365:
                    player.moveTo(new Position(2839, 5292, 2));
                    player.getPacketSender().sendMessage("You have been teleported outside the boss chamber.");
                    break;
                case 26363:
                    player.moveTo(new Position(2925, 5336, 2));
                    player.getPacketSender().sendMessage("You have been teleported outside the boss chamber.");
                    break;
                case 26364:
                    player.moveTo(new Position(2910, 5264, 0));
                    player.getPacketSender().sendMessage("You have been teleported outside the boss chamber.");
                    break;
                case FURNACE:
                case FURNACE_2:
                case FURNACE_3:
                case FURNACE_4:
                case FURNACE_5:
                case FURNACE_6:
                case FURNACE_7:
                case FURNACE_8:
                case FURNACE_9:
                case FURNACE_10:
                case FURNACE_11:
                case FURNACE_12:
                case FURNACE_13:
                case FURNACE_14:
                case FURNACE_15:
                case FURNACE_16:
                case FURNACE_17:
                case FURNACE_18:
                case FURNACE_19:
                case 11978:
                    for (Bar bar : Bar.values()) {
                        player.getPacketSender().sendInterfaceModel(bar.getFrame(), bar.getBar(), 150);
                    }
                    player.getPacketSender().sendChatboxInterface(2400);
                    break;
                case SPINNING_WHEEL:
                case SPINNING_WHEEL_2:
                case SPINNING_WHEEL_3:
                case SPINNING_WHEEL_4:
                case SPINNING_WHEEL_5:
                case SPINNING_WHEEL_6:
                case SPINNING_WHEEL_7:
                    SpinningWheel.openInterface(player);
                    break;
                case CABBAGE:
                case CABBAGE_2:
                    if (player.getInventory().countFreeSlots() < 1) {
                        DialogueManager.sendStatement(player, "You need to have a free inventory slots to pickup the cabbage.");
                        return;
                    }
                    if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 2, TimeUnit.SECONDS, false, true)) {
                        return;
                    }
                    player.performAnimation(new Animation(827));
                    TaskManager.submit(new Task(2) {
                        @Override
                        public void execute() {
                            player.getPacketSender().sendMessage("You pickup the cabbage.");
                            player.getInventory().add(new Item(1965, 1));
                            player.getPacketSender().sendSound(Sounds.PICKUP_ITEM);
                            stop();
                        }
                    });
                    break;
                case MAGICAL_ALTAR:
                    player.getPacketSender().sendInterfaceRemoval();
                    MagicSpellbook.changeSpellbook(player, MagicSpellbook.NORMAL);
                    break;
                default:
                    if (def.getName() == null) {
                        player.sendMessage("Nothing interesting happens.");
                        return;
                    } else if (SearchObjectActions.INSTANCE.isSearchable(def)) {
                        SearchObjectActions.INSTANCE.handle(player, object, null);
                        return;
                    }
                    player.getPacketSender().sendMessage("Nothing interesting happens.", 1000);
                    break;
            }

            EntityExtKt.markTime(player, Attribute.LAST_ACTION_BUTTON);
        };

        if (executeImmediately)
            objectClickAction.execute();
        else {
            preStartWalkTask(player);
            if (partial(object)) {
                player.setWalkToTask(new WalkToAction<>(player, object, 1, objectClickAction, WalkToAction.Policy.EXECUTE_ON_PARTIAL));
            } else {
                player.setWalkToTask(new WalkToAction<>(player, object, 1, objectClickAction));
            }
        }
    }

    /**
     * Handles the third click option on an object.
     */
    private static void thirdClick(Player player, int id, Position position, GameObject object, ObjectDefinition def, boolean executeImmediately) {

        player.sendDevelopersMessage("Third click object: " + object);

        final Executable objectClickAction = () -> {
            onExecutableStart(player, object, def);

            DebugManager.debug(player, "object-option", "3: "+object.getId()+", pos: "+object.getPosition().toString());

            if(PacketInteractionManager.handleObjectInteraction(player, object, 3)) {
                return;
            }

            if (player.getLocalObject(object.getId(), new Position(object.getX(), object.getY(), player.getPosition().getZ())).isEmpty() && (!ClippedMapObjects.exists(object) || !ObjectManager.existsAt(object.getId(), object.getPosition()))) {
                return;
            }

            if (player.getArea() != null && player.getArea().handleObjectClick(player, object, 3))
                return;

            if(PassageManager.handle(player,object,3))
                return;

            if (player.getClueScrollManager().handleObjectAction(3, position))
                return;

            if (ClimbObjectActions.handleClimbObject(player, object, def, position, 2))
                return;

            if(PestControl.handleObject(player,object, 3))
                return;

            switch (id) {
/*                case PORTAL_51:
                    DialogueManager.sendStatement(player, "Construction will be avaliable in the future.");
                    break;*/
                case MAGICAL_ALTAR:
                    player.getPacketSender().sendInterfaceRemoval();
                    MagicSpellbook.changeSpellbook(player, MagicSpellbook.ANCIENT);
                    break;
                case 42967:
                    player.sendMessage("There are currently " + AreaManager.getPlayersInArena(AreaManager.NEX_CHAMBER) + " player(s) inside!");
                    break;
                case 23609:
                    player.sendMessage("You need a rope to climb down this hole!");
                    break;
                case 10230:
                    player.sendMessage("You try to look deep down and see a huge shadow of a big fly.");
                    break;
                case WARDROBE_3:
                    player.sendMessage("I better not let anyone see me do this in a very spooky place.");
                    break;
                case LADDER_37:
                    if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                        return;
                    }
                    TeleportHandler.teleport(player,
                            new Position(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ() + 1),
                            TeleportType.LADDER_UP, false, true);
                    break;
                case STAIRCASE_141: // Falador Party room west staircase
                case STAIRCASE_142: // Falador Party room east staircase
                    player.sendMessage("I cannot climb down here.");
                    break;
                case LADDER_29:
                case 16672:
                case 16684:
                case 12537:
                    if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
                        return;
                    }
                    TeleportHandler.teleport(player,
                            new Position(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ() - 1),
                            TeleportType.LADDER_DOWN, false, true);
                    break;
                default:
                    player.getPacketSender().sendMessage("Nothing interesting happens.", 1000);
                    break;
            }
        };

        if (executeImmediately)
            objectClickAction.execute();
        else {
            preStartWalkTask(player);
            if (partial(object)) {
                player.setWalkToTask(new WalkToAction<>(player, object, 1, objectClickAction, WalkToAction.Policy.EXECUTE_ON_PARTIAL));
            } else {
                player.setWalkToTask(new WalkToAction<>(player, object, 1, objectClickAction));
            }
        }
    }

    /**
     * Handles the fourth click option on an object.
     */
    private static void fourthClick(Player player, final int id, Position position, GameObject object, ObjectDefinition def, boolean executeImmediately) {

        player.sendDevelopersMessage("Fourth click object: " + object);

        final Executable objectClickAction = () -> {
            onExecutableStart(player, object, def);

            DebugManager.debug(player, "object-option", "4: "+object.getId()+", pos: "+object.getPosition().toString());

            if(PacketInteractionManager.handleObjectInteraction(player, object, 4)) {
                return;
            }

            // Areas
            if (player.getArea() != null) {
                if (player.getArea().handleObjectClick(player, object, 4)) {
                    return;
                }
            }

            if (!ClippedMapObjects.exists(object) || !ObjectManager.existsAt(object.getId(), object.getPosition())) {
                return;
            }
            if(PassageManager.handle(player,object,4))
                return;

            if (player.getClueScrollManager().handleObjectAction(4, position))
                return;

            switch (id) {
/*                case PORTAL_51:
                    DialogueManager.sendStatement(player, "Construction will be available in the future.");
                    break;*/
                case MAGICAL_ALTAR:
                    player.getPacketSender().sendInterfaceRemoval();
                    MagicSpellbook.changeSpellbook(player, MagicSpellbook.LUNAR);
                    player.setSpellbook(player.getSpellbook());
                    SpellCasting.setSpellToCastAutomatically(player, null);
                    break;
                default:
                    player.getPacketSender().sendMessage("Nothing interesting happens.", 1000);
                    break;
            }

            switch (id) {
/*                case PORTAL_51:
                    DialogueManager.sendStatement(player, "Construction will be available in the future.");
                    break;*/
                case MAGICAL_ALTAR:
                    player.getPacketSender().sendInterfaceRemoval();
                    MagicSpellbook.changeSpellbook(player, MagicSpellbook.LUNAR);
                    player.setSpellbook(player.getSpellbook());
                    SpellCasting.setSpellToCastAutomatically(player, null);
                    break;
                default:
                    player.getPacketSender().sendMessage("Nothing interesting happens.", 1000);
                    break;
            }
        };

        if (executeImmediately)
            objectClickAction.execute();
        else {
            preStartWalkTask(player);
            if (partial(object)) {
                player.setWalkToTask(new WalkToAction<>(player, object, 1, objectClickAction, WalkToAction.Policy.EXECUTE_ON_PARTIAL));
            } else {
                player.setWalkToTask(new WalkToAction<>(player, object, 1, objectClickAction));
            }
        }
    }

    /**
     * Handles the fourth click option on an object.
     */
    private static void fifthClick(Player player, final int id, Position position, GameObject object, ObjectDefinition def, boolean executeImmediately) {

        player.sendDevelopersMessage("Fifth click object: " + object);

        final Executable objectClickAction = () -> {
            onExecutableStart(player, object, def);

            DebugManager.debug(player, "object-option", "5: " + object.getId() + ", pos: " + object.getPosition().toString());

            if (PacketInteractionManager.handleObjectInteraction(player, object, 5)) {
                return;
            }

            // Areas
            if (player.getArea() != null) {
                if (player.getArea().handleObjectClick(player, object, 5)) {
                    return;
                }
            }

            if (!ClippedMapObjects.exists(object) || !ObjectManager.existsAt(object.getId(), object.getPosition())) {
                return;
            }

            switch(id) {
                default:
                    player.getPacketSender().sendMessage("Nothing interesting happens.", 1000);
                    break;
            }
        };

        if (executeImmediately)
            objectClickAction.execute();
        else {
            preStartWalkTask(player);
            if (partial(object)) {
                player.setWalkToTask(new WalkToAction<>(player, object, 1, objectClickAction, WalkToAction.Policy.EXECUTE_ON_PARTIAL));
            } else {
                player.setWalkToTask(new WalkToAction<>(player, object, 1, objectClickAction));
            }
        }
    }

    public static void onExecutableStart(Player player, GameObject object, ObjectDefinition def) {
        EntityExtKt.markTime(player, Attribute.LAST_ACTION_BUTTON);
        ObjectActions.INSTANCE.faceObj(player, object, def);
    }

    public static void preStartWalkTask(Player player) {
        SkillUtil.stopSkillable(player);
        player.getCombat().reset(false);
    }

    public static void handleActionDefault(ObjectActions.ClickAction clickAction, boolean executeImmediately) {
        final Player player = clickAction.getPlayer();
        final int x = clickAction.getX();
        final int y = clickAction.getY();
        final GameObject gameObject = clickAction.getObject();
        final ObjectDefinition definition = gameObject.getDefinition();
        final Position position = new Position(x, y, player.getPosition().getZ());
        switch (clickAction.getType()){
            case FIRST_OPTION: firstClick(player, x, y, gameObject.getId(), position, gameObject, definition, executeImmediately); break;
            case SECOND_OPTION: secondClick(player, gameObject.getId(), position, gameObject, definition, executeImmediately); break;
            case THIRD_OPTION: thirdClick(player, gameObject.getId(), position, gameObject, definition, executeImmediately); break;
            case FOURTH_OPTION: fourthClick(player, gameObject.getId(), position, gameObject, definition, executeImmediately); break;
        }
    }
}
