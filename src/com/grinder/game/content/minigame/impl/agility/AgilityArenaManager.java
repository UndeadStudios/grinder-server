package com.grinder.game.content.minigame.impl.agility;

import com.grinder.game.collision.CollisionManager;
import com.grinder.game.content.miscellaneous.PetHandler;
import com.grinder.game.content.skill.task.SkillMasterType;
import com.grinder.game.content.skill.task.SkillTaskManager;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.StaticGameObjectFactory;
import com.grinder.game.model.*;
import com.grinder.game.model.areas.Area;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.ForceMovementTask;
import com.grinder.net.packet.PacketSender;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;

import java.util.*;

public class AgilityArenaManager extends Area {

    /**
     * A list of players currently in this area.
     */
    public static Set<Player> playersInArea = new HashSet<>();

    private static Position currentDispenser = new Position(2805, 9579, 3);

    public AgilityArenaManager() {
        super(new Boundary(2756, 2811, 9540, 9596));
        start();
    }

    public static void pressedObject(Player player, Position objectPosition) {
        if (currentDispenser.sameAs(objectPosition)) {
            if (player.brimhavenPillarPosition == null) {
                //has not pressed previous pillar
                player.brimhavenPillarPosition = currentDispenser;
                DialogueManager.sendStatement(player, "You get tickets by tagging more than one pillar in a row. @red@Tag the next pillar for a ticket!");
                player.getPacketSender().sendVarbit(5965, 1);
                player.sendMessage("You get tickets by tagging more than one pillar in a row. @red@Tag the next pillar for a ticket!");
            } else if (player.brimhavenPillarPosition.sameAs(currentDispenser)) {
                player.sendMessage("You get tickets by tagging more than one pillar in a row, tag the next pillar!");
            } else {

                // Add items
                int graceAmount = Misc.random(5) + 1;
                player.getInventory().add(new Item(ItemID.MARK_OF_GRACE, graceAmount));
                player.getSkillManager().addExperience(Skill.AGILITY, 50 + Misc.random(350));
                player.sendMessage("@red@You have received " + graceAmount +" mark of grace.");

                // Show dialogue
                DialogueBuilder db = new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER);
                db.setItem(ItemID.MARK_OF_GRACE, 175);
                db.setText("You have received some Agility experience and", "Marks of grace!");
                db.start(player);

                // Send sound
                player.getPacketSender().sendSound(new Sound(Sounds.BRIMHAVEN_DISPENCER_TAG));

                // Roll pet
                PetHandler.onSkill(player, Skill.AGILITY);

                // Skill task
                SkillTaskManager.perform(player, ItemID.MARK_OF_GRACE, graceAmount, SkillMasterType.AGILITY);

                // Attribute saving
                player.getPoints().increase(AttributeManager.Points.BRIMHAVEN_AGILITY_TAGS_COMPLETED, 1);
                player.sendMessage("Your completed Brimhaven Agility Arena tags count: @red@" + player.getPoints().get(AttributeManager.Points.BRIMHAVEN_AGILITY_TAGS_COMPLETED) +"</col>.");

                player.brimhavenPillarPosition = currentDispenser;
            }
        } else {
            player.sendMessage("You can only get a ticket when there is a flashing arrow above the pillar.");
        }

    }

    private void start() {
        TaskManager.submit(new Task(100) {
            //Process Change every minute...
            @Override
            protected void execute() {
                //First loop to check if player got previous dispenser in time..
                for (Player player : playersInArea) {
                    if (player.brimhavenPillarPosition != null) {
                        if (!player.brimhavenPillarPosition.sameAs(currentDispenser)) {
                            player.brimhavenPillarPosition = null;
                            player.getPacketSender().sendVarbit(5965, 0);
                        }
                    }
                }

                currentDispenser = getRandomDispenser();

                //Send flashing arrow to new dispenser.
                for (Player player : playersInArea) {
                    player.getPacketSender().sendPositionalHint(currentDispenser, 2);
                }
            }
        });

        TaskManager.submit(new Task(1) {
            int count = 0;

            @Override
            protected void execute() {
                if (count == 0) {
                    //Create saws from ground moving up..
                    ObjectManager.add(StaticGameObjectFactory.produce(3567, new Position(2788, 9579, 3), 10, 1), true);
                    ObjectManager.add(StaticGameObjectFactory.produce(3567, new Position(2783, 9551, 3), 10, 0), true);
                    ObjectManager.add(StaticGameObjectFactory.produce(3567, new Position(2761, 9584, 3), 10, 0), true);

                }
                if (count == 4) {
                    //Make them saws vanish and remove the object clipping
                    ObjectManager.add(StaticGameObjectFactory.produce(-1, new Position(2788, 9579, 3), 10, 1), true);
                    CollisionManager.clearClipping(new Position(2788, 9579, 3));
                    CollisionManager.clearClipping(new Position(2789, 9579, 3));

                    ObjectManager.add(StaticGameObjectFactory.produce(-1, new Position(2783, 9551, 3), 10, 0), true);
                    CollisionManager.clearClipping(new Position(2783, 9551, 3));
                    CollisionManager.clearClipping(new Position(2783, 9552, 3));

                    ObjectManager.add(StaticGameObjectFactory.produce(-1, new Position(2761, 9584, 3), 10, 0), true);
                    CollisionManager.clearClipping(new Position(2761, 9584, 3));
                    CollisionManager.clearClipping(new Position(2761, 9585, 3));
                }

                for (Player player : playersInArea) {
                    Position playerPosition = player.getPosition();

                    if (count == 0) {
                        if (playerPosition.isWithinDistance(new Position(2788, 9579, 3), 3)) {
                            player.getPacketSender().sendAreaSound(new Position(2788, 9579, 3), 1386, 4);
                        } else if (playerPosition.isWithinDistance(new Position(2784, 9551, 3), 3)) {
                            player.getPacketSender().sendAreaSound(new Position(2784, 9551, 3), 1386, 4);
                        } else if (playerPosition.isWithinDistance(new Position(2761, 9584, 3), 3)) {
                            player.getPacketSender().sendAreaSound(new Position(2761, 9584, 3), 1386, 4);
                        }
                    }
                    if (count < 2) {
                        //Damage player if at position of blade
                        if (playerPosition.sameAs(new Position(2788, 9579, 3)) || playerPosition.sameAs(new Position(2789, 9579, 3)) ||
                                playerPosition.sameAs(new Position(2783, 9551, 3)) || playerPosition.sameAs(new Position(2783, 9552, 3)) ||
                                playerPosition.sameAs(new Position(2761, 9584, 3)) || playerPosition.sameAs(new Position(2761, 9585, 3))) {
                            EntityExtKt.setBoolean(player, Attribute.DOING_BRIMHAVEN_DAMAGE, true, false);
                            player.getMotion().clearSteps();
                            player.BLOCK_ALL_BUT_TALKING = true;

                            int moveX = 0;
                            int moveY = 0;
                            //move west
                            if (playerPosition.getX() == 2789)
                                moveX = 1;

                            //move east
                            if (playerPosition.getX() == 2788)
                                moveX = -1;

                            if (playerPosition.getY() == 9551 || playerPosition.getY() == 9584)
                                moveY = -1;

                            if (playerPosition.getY() == 9552 || playerPosition.getY() == 9585)
                                moveY = 1;

                            int direction = 1;
                            if (moveX == 1)
                                direction = 3;
                            if (moveY == 1)
                                direction = 2;
                            if (moveY == -1)
                                direction = 0;
                            if (player.getForceMovement() == null) {
                                player.sendMessage("You were hit by the saw blade!");
                                Position moveTo = new Position(moveX, moveY, 0);
                                TaskManager.submit(new ForceMovementTask(player, 1,
                                        new ForceMovement(playerPosition.clone(), moveTo, 1, 2,
                                                direction, 819)));
                            }

                        }
                    } else {
                        if (EntityExtKt.getBoolean(player, Attribute.DOING_BRIMHAVEN_DAMAGE, false)) {
                            if (playerPosition.sameAs(new Position(2787, 9579, 3)) || playerPosition.sameAs(new Position(2790, 9579, 3)) ||
                                    playerPosition.sameAs(new Position(2783, 9550, 3)) || playerPosition.sameAs(new Position(2783, 9553, 3)) ||
                                    playerPosition.sameAs(new Position(2761, 9583, 3)) || playerPosition.sameAs(new Position(2761, 9586, 3))) {
                                EntityExtKt.setBoolean(player, Attribute.DOING_BRIMHAVEN_DAMAGE, false, false);
                                player.BLOCK_ALL_BUT_TALKING = false;
                                player.getCombat().queue(new Damage(5, DamageMask.REGULAR_HIT));
                                player.getPacketSender().sendSound(player.getAppearance().isMale() ? (518 + Misc.random(4)) : 509);
                            }
                        }
                    }

                    boolean shouldRun = false;
                    Position moveTo = null;
                    boolean failed = false;
                    int direction = 0;
                    boolean spikeAnimation = true;
                    Position dartsStartPosition = null;
                    Position dartsEndPosition = null;
                    Position spinningBladesPosition = null;
                    int spinningBladesDirection = 0;
                    int levelRequiredMessage = 0;

                    if (playerPosition.sameAs(new Position(2801, 9568, 3)) || playerPosition.sameAs(new Position(2802, 9567, 3)) || playerPosition.sameAs(new Position(2802, 9568, 3)) || playerPosition.sameAs(new Position(2802, 9569, 3)) ||
                            playerPosition.sameAs(new Position(2779, 9557, 3)) || playerPosition.sameAs(new Position(2780, 9556, 3)) || playerPosition.sameAs(new Position(2780, 9557, 3)) || playerPosition.sameAs(new Position(2780, 9558, 3)) ||
                            playerPosition.sameAs(new Position(2779, 9579, 3)) || playerPosition.sameAs(new Position(2780, 9578, 3)) ||playerPosition.sameAs(new Position(2780, 9579, 3)) ||playerPosition.sameAs(new Position(2780, 9580, 3)) ||
                            playerPosition.sameAs(new Position(2801, 9557, 3)) || playerPosition.sameAs(new Position(2802, 9556, 3)) || playerPosition.sameAs(new Position(2802, 9557, 3)) || playerPosition.sameAs(new Position(2802, 9558, 3)) ||
                            playerPosition.sameAs(new Position(2801, 9579, 3)) || playerPosition.sameAs(new Position(2802, 9578, 3)) || playerPosition.sameAs(new Position(2802, 9579, 3)) || playerPosition.sameAs(new Position(2802, 9580, 3)) ||
                            playerPosition.sameAs(new Position(2790, 9557, 3)) || playerPosition.sameAs(new Position(2791, 9556, 3)) || playerPosition.sameAs(new Position(2791, 9557, 3)) || playerPosition.sameAs(new Position(2791, 9558, 3)) ||
                            playerPosition.sameAs(new Position(2779, 9568, 3)) || playerPosition.sameAs(new Position(2780, 9567, 3)) || playerPosition.sameAs(new Position(2780, 9568, 3))  || playerPosition.sameAs(new Position(2780, 9569, 3)) ) {
                        player.cameFromDirection = Direction.EAST;
                    }
                    if (playerPosition.sameAs(new Position(2798, 9568, 3)) || playerPosition.sameAs(new Position(2797, 9567, 3)) || playerPosition.sameAs(new Position(2797, 9568, 3)) || playerPosition.sameAs(new Position(2797, 9569, 3)) ||
                            playerPosition.sameAs(new Position(2776, 9557, 3)) || playerPosition.sameAs(new Position(2775, 9556, 3)) || playerPosition.sameAs(new Position(2775, 9557, 3)) || playerPosition.sameAs(new Position(2775, 9558, 3)) ||
                            playerPosition.sameAs(new Position(2776, 9579, 3)) || playerPosition.sameAs(new Position(2775, 9578, 3)) || playerPosition.sameAs(new Position(2775, 9579, 3)) || playerPosition.sameAs(new Position(2775, 9580, 3)) ||
                            playerPosition.sameAs(new Position(2798, 9557, 3)) || playerPosition.sameAs(new Position(2797, 9556, 3)) || playerPosition.sameAs(new Position(2797, 9557, 3)) || playerPosition.sameAs(new Position(2797, 9558, 3)) ||
                            playerPosition.sameAs(new Position(2798, 9579, 3)) || playerPosition.sameAs(new Position(2797, 9578, 3)) || playerPosition.sameAs(new Position(2797, 9579, 3)) || playerPosition.sameAs(new Position(2797, 9580, 3)) ||
                            playerPosition.sameAs(new Position(2787, 9557, 3)) || playerPosition.sameAs(new Position(2786, 9556, 3)) || playerPosition.sameAs(new Position(2786, 9557, 3)) || playerPosition.sameAs(new Position(2786, 9558, 3)) ||
                            playerPosition.sameAs(new Position(2776, 9568, 3)) || playerPosition.sameAs(new Position(2775, 9567, 3)) || playerPosition.sameAs(new Position(2775, 9568, 3)) || playerPosition.sameAs(new Position(2775, 9569, 3)) ) {
                        player.cameFromDirection = Direction.WEST;
                    }
                    if (playerPosition.sameAs(new Position(2772, 9550, 3)) || playerPosition.sameAs(new Position(2771, 9549, 3)) || playerPosition.sameAs(new Position(2772, 9549, 3)) || playerPosition.sameAs(new Position(2773, 9549, 3)) ||
                            playerPosition.sameAs(new Position(2761, 9572, 3)) || playerPosition.sameAs(new Position(2760, 9571, 3)) || playerPosition.sameAs(new Position(2761, 9571, 3)) || playerPosition.sameAs(new Position(2762, 9571, 3)) ||
                            playerPosition.sameAs(new Position(2772, 9583, 3)) || playerPosition.sameAs(new Position(2771, 9582, 3)) || playerPosition.sameAs(new Position(2772, 9582, 3)) || playerPosition.sameAs(new Position(2773, 9582, 3)) ||
                            playerPosition.sameAs(new Position(2783, 9572, 3)) || playerPosition.sameAs(new Position(2782, 9571, 3)) || playerPosition.sameAs(new Position(2783, 9571, 3)) || playerPosition.sameAs(new Position(2784, 9571, 3)) ||
                            playerPosition.sameAs(new Position(2794, 9572, 3)) || playerPosition.sameAs(new Position(2793, 9571, 3)) || playerPosition.sameAs(new Position(2794, 9571, 3)) || playerPosition.sameAs(new Position(2795, 9571, 3)) ) {
                        player.cameFromDirection = Direction.SOUTH;
                    }
                    if (playerPosition.sameAs(new Position(2772, 9553, 3)) || playerPosition.sameAs(new Position(2771, 9554, 3)) || playerPosition.sameAs(new Position(2772, 9554, 3)) || playerPosition.sameAs(new Position(2773, 9554, 3)) ||
                            playerPosition.sameAs(new Position(2761, 9575, 3)) || playerPosition.sameAs(new Position(2760, 9576, 3)) || playerPosition.sameAs(new Position(2761, 9576, 3)) || playerPosition.sameAs(new Position(2762, 9576, 3)) ||
                            playerPosition.sameAs(new Position(2772, 9586, 3)) || playerPosition.sameAs(new Position(2771, 9587, 3)) || playerPosition.sameAs(new Position(2772, 9587, 3)) || playerPosition.sameAs(new Position(2773, 9587, 3)) ||
                            playerPosition.sameAs(new Position(2783, 9575, 3)) || playerPosition.sameAs(new Position(2782, 9576, 3)) || playerPosition.sameAs(new Position(2783, 9576, 3)) || playerPosition.sameAs(new Position(2784, 9576, 3)) ||
                            playerPosition.sameAs(new Position(2794, 9575, 3)) || playerPosition.sameAs(new Position(2793, 9576, 3)) || playerPosition.sameAs(new Position(2794, 9576, 3)) || playerPosition.sameAs(new Position(2795, 9576, 3)) ) {
                        player.cameFromDirection = Direction.NORTH;
                    }

                    //Check position is at floor spikes
                    if (playerPosition.sameAs(new Position(2800, 9568, 3)) || playerPosition.sameAs(new Position(2778, 9557, 3)) || playerPosition.sameAs(new Position(2778, 9579, 3)) || playerPosition.sameAs(new Position(2800, 9557, 3)) || playerPosition.sameAs(new Position(2800, 9579, 3))
                            || playerPosition.sameAs(new Position(2789, 9557, 3)) || playerPosition.sameAs(new Position(2778, 9568, 3))) {
                        player.getMotion().clearSteps();
                        player.BLOCK_ALL_BUT_TALKING = true;

                        if (playerPosition.sameAs(new Position(2800, 9557, 3)) || playerPosition.sameAs(new Position(2800, 9579, 3)))
                            spikeAnimation = false;

                        if (playerPosition.sameAs(new Position(2778, 9557, 3))) {
                            spinningBladesPosition = new Position(2777, 9556, 3);
                            spinningBladesDirection = 1;
                        }
                        if (playerPosition.sameAs(new Position(2778, 9579, 3))) {
                            spinningBladesPosition = new Position(2777, 9580, 3);
                            spinningBladesDirection = 3;
                        }
                        if (playerPosition.sameAs(new Position(2789, 9557, 3)))
                            if (player.cameFromDirection == Direction.WEST) {
                                dartsStartPosition = new Position(2793, 9557, 3);
                                dartsEndPosition = new Position(2784, 9557, 3);
                            } else {
                                dartsStartPosition = new Position(2784, 9557, 3);
                                dartsEndPosition = new Position(2793, 9557, 3);
                            }

                        if (playerPosition.sameAs(new Position(2778, 9568, 3)))
                            if (player.cameFromDirection == Direction.WEST) {
                                dartsStartPosition = new Position(2782, 9568, 3);
                                dartsEndPosition = new Position(2773, 9568, 3);
                            } else {
                                dartsStartPosition = new Position(2773, 9568, 3);
                                dartsEndPosition = new Position(2782, 9568, 3);
                            }

                        int chance = Misc.random(5);
                        if (player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 20) {
                            levelRequiredMessage = 20;
                            chance = 5;
                        }

                        if (player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 40 && (spinningBladesPosition != null || dartsStartPosition != null)) {
                            levelRequiredMessage = 40;
                            chance = 5;
                        }
                        if (chance >= 4) {
                            //Fail
                            failed = true;
                            if (player.cameFromDirection == Direction.WEST) {
                                moveTo = new Position(-2, 0, 0);
                                direction = 1;
                            } else {
                                moveTo = new Position(1, 0, 0);
                                direction = 3;
                            }
                        } else {
                            //Success
                            if (player.cameFromDirection == Direction.WEST) {
                                moveTo = new Position(1, 0, 0);
                                direction = 1;
                            } else {
                                moveTo = new Position(-2, 0, 0);
                                direction = 3;
                            }
                        }
                        shouldRun = true;

                    }
                    if (playerPosition.sameAs(new Position(2799, 9568, 3)) || playerPosition.sameAs(new Position(2777, 9557, 3)) || playerPosition.sameAs(new Position(2777, 9579, 3)) || playerPosition.sameAs(new Position(2799, 9557, 3)) || playerPosition.sameAs(new Position(2799, 9579, 3))
                            || playerPosition.sameAs(new Position(2788, 9557, 3)) || playerPosition.sameAs(new Position(2777, 9568, 3))) {
                        player.getMotion().clearSteps();
                        player.BLOCK_ALL_BUT_TALKING = true;

                        if (playerPosition.sameAs(new Position(2799, 9557, 3)) || playerPosition.sameAs(new Position(2799, 9579, 3)))
                            spikeAnimation = false;

                        if (playerPosition.sameAs(new Position(2777, 9557, 3))) {
                            spinningBladesPosition = new Position(2777, 9556, 3);
                            spinningBladesDirection = 1;
                        }
                        if (playerPosition.sameAs(new Position(2777, 9579, 3))) {
                            spinningBladesPosition = new Position(2777, 9580, 3);
                            spinningBladesDirection = 3;
                        }

                        if (playerPosition.sameAs(new Position(2788, 9557, 3)))
                            if (player.cameFromDirection == Direction.WEST) {
                                dartsStartPosition = new Position(2793, 9557, 3);
                                dartsEndPosition = new Position(2784, 9557, 3);
                            } else {
                                dartsStartPosition = new Position(2784, 9557, 3);
                                dartsEndPosition = new Position(2793, 9557, 3);
                            }

                        if (playerPosition.sameAs(new Position(2777, 9568, 3)))
                            if (player.cameFromDirection == Direction.WEST) {
                                dartsStartPosition = new Position(2782, 9568, 3);
                                dartsEndPosition = new Position(2773, 9568, 3);
                            } else {
                                dartsStartPosition = new Position(2773, 9568, 3);
                                dartsEndPosition = new Position(2782, 9568, 3);
                            }

                        int chance = Misc.random(5);
                        if (player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 20) {
                            levelRequiredMessage = 20;
                            chance = 5;
                        }
                        if (player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 40 && (spinningBladesPosition != null || dartsStartPosition != null)) {
                            levelRequiredMessage = 40;
                            chance = 5;
                        }
                        if (chance >= 4) {
                            //Fail
                            failed = true;
                            if (player.cameFromDirection == Direction.WEST) {
                                moveTo = new Position(-1, 0, 0);
                                direction = 1;
                            } else {
                                moveTo = new Position(2, 0, 0);
                                direction = 3;
                            }
                        } else {
                            //Success
                            if (player.cameFromDirection == Direction.WEST) {
                                moveTo = new Position(2, 0, 0);
                                direction = 1;
                            } else {
                                moveTo = new Position(-1, 0, 0);
                                direction = 3;
                            }
                        }
                        shouldRun = true;
                    }
                    if (playerPosition.sameAs(new Position(2772, 9552, 3)) || playerPosition.sameAs(new Position(2761, 9574, 3)) || playerPosition.sameAs(new Position(2772, 9585, 3)) || playerPosition.sameAs(new Position(2783, 9574, 3))
                            || playerPosition.sameAs(new Position(2794, 9574, 3))) {
                        player.getMotion().clearSteps();
                        player.BLOCK_ALL_BUT_TALKING = true;

                        if (playerPosition.sameAs(new Position(2772, 9585, 3)))
                            spikeAnimation = false;

                        if (playerPosition.sameAs(new Position(2783, 9574, 3))) {
                            spinningBladesPosition = new Position(2782, 9573, 3);
                            spinningBladesDirection = 2;
                        }

                        if (playerPosition.sameAs(new Position(2794, 9574, 3)))
                            if (player.cameFromDirection == Direction.WEST) {
                                dartsStartPosition = new Position(2794, 9569, 3);
                                dartsEndPosition = new Position(2794, 9578, 3);
                            } else {
                                dartsStartPosition = new Position(2794, 9578, 3);
                                dartsEndPosition = new Position(2794, 9569, 3);
                            }

                        int chance = Misc.random(5);
                        if (player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 20) {
                            levelRequiredMessage = 20;
                            chance = 5;
                        }
                        if (player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 40 && (spinningBladesPosition != null || dartsStartPosition != null)) {
                            levelRequiredMessage = 40;
                            chance = 5;
                        }
                        if (chance >= 4) {
                            //Fail
                            failed = true;
                            if (player.cameFromDirection == Direction.SOUTH) {
                                moveTo = new Position(0, -2, 0);
                                direction = 0;
                            } else {
                                moveTo = new Position(0, 1, 0);
                                direction = 2;
                            }
                        } else {
                            //Success
                            if (player.cameFromDirection == Direction.SOUTH) {
                                moveTo = new Position(0, 1, 0);
                                direction = 0;
                            } else {
                                moveTo = new Position(0, -2, 0);
                                direction = 2;
                            }
                        }
                        shouldRun = true;

                    }
                    if (playerPosition.sameAs(new Position(2772, 9551, 3)) || playerPosition.sameAs(new Position(2761, 9573, 3)) || playerPosition.sameAs(new Position(2772, 9584, 3)) || playerPosition.sameAs(new Position(2783, 9573, 3))
                            || playerPosition.sameAs(new Position(2794, 9573, 3))) {
                        player.getMotion().clearSteps();
                        player.BLOCK_ALL_BUT_TALKING = true;

                        if (playerPosition.sameAs(new Position(2772, 9584, 3)))
                            spikeAnimation = false;

                        if (playerPosition.sameAs(new Position(2783, 9573, 3))) {
                            spinningBladesPosition = new Position(2782, 9573, 3);
                            spinningBladesDirection = 2;
                        }

                        if (playerPosition.sameAs(new Position(2794, 9573, 3)))
                            if (player.cameFromDirection == Direction.WEST) {
                                dartsStartPosition = new Position(2794, 9578, 3);
                                dartsEndPosition = new Position(2794, 9569, 3);
                            } else {
                                dartsStartPosition = new Position(2794, 9569, 3);
                                dartsEndPosition = new Position(2794, 9578, 3);
                            }

                        //Create chance of failing..
                        int chance = Misc.random(5);

                        if (player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 20) {
                            levelRequiredMessage = 20;
                            chance = 5;
                        }
                        if (player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 40 && (spinningBladesPosition != null || dartsStartPosition != null)) {
                            levelRequiredMessage = 40;
                            chance = 5;
                        }
                        if (chance >= 4) {
                            //Fail
                            failed = true;
                            if (player.cameFromDirection == Direction.SOUTH) {
                                moveTo = new Position(0, -1, 0);
                                direction = 0;
                            } else {
                                moveTo = new Position(0, 2, 0);
                                direction = 2;
                            }
                        } else {
                            //Success
                            if (player.cameFromDirection == Direction.SOUTH) {
                                moveTo = new Position(0, 2, 0);
                                direction = 0;
                            } else {
                                moveTo = new Position(0, -1, 0);
                                direction = 2;
                            }
                        }
                        shouldRun = true;
                    }

                    //Get effective final values
                    final boolean getFailed = failed;
                    final Position getMoveTo = moveTo;
                    final int getDirection = direction;
                    final boolean getSpikeAnimation = spikeAnimation;
                    final Position getDartsStartPosition = dartsStartPosition;
                    final Position getDartsEndPosition = dartsEndPosition;
                    final Position getSpinningBladesPosition = spinningBladesPosition;
                    final int getSpinningBladeDirection = spinningBladesDirection;
                    final int getLevelRequiredMessage = levelRequiredMessage;

                    if (shouldRun && !EntityExtKt.getBoolean(player, Attribute.DOING_BRIMHAVEN_DAMAGE, false)) {
                        EntityExtKt.setBoolean(player, Attribute.DOING_BRIMHAVEN_DAMAGE, true, false);

                        TaskManager.submit(new Task(1) {
                            int step = 0;

                            @Override
                            protected void execute() {
                                if (step == 0) {
                                    if (getSpikeAnimation && getSpinningBladesPosition == null && getDartsStartPosition == null) {
                                        player.getPacketSender().sendObjectAnimation(playerPosition.getX(), playerPosition.getY(), 10, 0, new Animation(1111));

                                        if (!getFailed) {
                                            player.playSound(new Sound(1381));
                                        } else {
                                            player.playSound(new Sound(1383));
                                            player.sendMessage("You were hit by the spinning blades!");
                                        }
                                    } else if (getDartsStartPosition == null) {
                                        if (getSpinningBladesPosition == null) {
                                            player.getPacketSender().sendGraphic(new Graphic(271), playerPosition);
                                            player.playSound(new Sound(1379));

                                            if (getFailed)
                                                player.sendMessage("You were hit by some falling rocks!");
                                        } else {
                                            player.getPacketSender().sendObjectAnimation(getSpinningBladesPosition.getX(), getSpinningBladesPosition.getY(), 10, getSpinningBladeDirection, new Animation(1107));
                                            player.playSound(new Sound(1377));
                                            player.playSound(new Sound(2469, 2));

                                            if (getFailed)
                                                player.sendMessage("You were hit by the spinning blades!");
                                        }
                                    } else {
                                        player.getPacketSender().sendProjectile(getDartsStartPosition, getDartsEndPosition, 0, 100, 270, 0, 0, 0, 1);
                                        player.playSound(new Sound(1378));

                                        if (getFailed) {
                                            player.sendMessage("You were hit by some darts, something on them makes you feel dizzy!");
                                            player.getSkillManager().decreaseLevelTemporarily(Skill.AGILITY, 2);
                                        }
                                    }

                                    if (player.getForceMovement() == null) {
                                        Position moveTo = new Position(getMoveTo.getX(), getMoveTo.getY(), 0);
                                        if (getDartsStartPosition == null)
                                            TaskManager.submit(new ForceMovementTask(player, 1,
                                                    new ForceMovement(playerPosition.clone(), moveTo, 1, 20,
                                                            getDirection, getFailed == true ? 1114 : 1115)));
                                        else if (!getFailed)
                                            player.performAnimation(new Animation(1110));
                                    }
                                }
                                if (step == 1) {
                                    if (getLevelRequiredMessage != 0)
                                        player.sendMessage("You need an Agility level of at least " + getLevelRequiredMessage + " to get past this trap!");

                                    if (getDartsStartPosition != null)
                                        player.getMotion().enqueueStep(getMoveTo.getX(), getMoveTo.getY());

                                    if (getFailed) {
                                        player.getCombat().queue(new Damage(4, DamageMask.REGULAR_HIT));
                                        player.getPacketSender().sendSound(player.getAppearance().isMale() ? (518 + Misc.random(4)) : 509);
                                    }
                                    player.BLOCK_ALL_BUT_TALKING = false;
                                    EntityExtKt.setBoolean(player, Attribute.DOING_BRIMHAVEN_DAMAGE, false, false);
                                    stop();
                                }
                                step++;
                            }
                        });
                    }
                }

                count++;

                if (count >= 8)
                    count = 0;
            }

        });
    }

    private Position getRandomDispenser() {
        return AgilityArenaConstants.DISPENSERS[Misc.getRandomExclusive(AgilityArenaConstants.DISPENSERS.length)];
    }

    @Override
    public void enter(Agent agent) {
        if (agent instanceof Player) {

            super.enter(agent);
            final Player player = (Player) agent;

            player.getPacketSender().sendWalkableInterface(8287);
            playersInArea.add(player);

            final PacketSender packetSender = player.getPacketSender();

            packetSender.sendPositionalHint(currentDispenser, 2);

            player.getPacketSender().sendVarbit(5965, 0);

        }
    }

    @Override
    public void leave(Agent agent) {
        if (agent instanceof Player) {
            super.leave(agent);
            final Player player = (Player) agent;
            player.brimhavenPillarPosition = null;
            playersInArea.remove(player);
            player.getPacketSender().sendWalkableInterface(-1);
        }
    }

    @Override
    public void process(Agent agent) {
    }

    @Override
    public boolean canTeleport(Player player) {
        return true;
    }

    @Override
    public boolean canAttack(Agent attacker, Agent target) {
        return false;
    }

    @Override
    public void defeated(Player player, Agent agent) {
    }

    @Override
    public boolean canTrade(Player player, Player target) {
        return true;
    }

    @Override
    public boolean isMulti(Agent agent) {
        return false;
    }

    @Override
    public boolean canEat(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean canDrink(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean dropItemsOnDeath(Player player, Optional<Player> killer) {
        return true;
    }

    @Override
    public boolean handleDeath(Player player, Optional<Player> killer) {
        return true;
    }

    @Override
    public void onPlayerRightClick(Player player, Player rightClicked, int option) {
    }

    @Override
    public boolean handleObjectClick(Player player, GameObject obj, int actionType) {
        return false;
    }

    @Override
    public boolean handleDeath(NPC npc) {
        return true;
    }
}
