package com.grinder.game.content.skill.skillable.impl.agility.obstacle.impl;

import com.grinder.game.content.skill.skillable.impl.agility.Agility;
import com.grinder.game.content.skill.skillable.impl.agility.obstacle.AgilityObstacle;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.EntityType;
import com.grinder.game.entity.agent.movement.pathfinding.PathFinder;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.StaticGameObject;
import com.grinder.game.entity.updating.block.BasicAnimationSet;
import com.grinder.game.model.*;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.ForceMovementTask;
import com.grinder.util.ItemID;
import com.grinder.util.ObjectID;

import java.util.Optional;

public class LumbridgeGrappleAgilityObstacle implements AgilityObstacle {

    private Position rockPosition = new Position(3252, 3179);

    private Item[] crossbows = {new Item(ItemID.BRONZE_CROSSBOW), new Item(ItemID.IRON_CROSSBOW), new Item(ItemID.STEEL_CROSSBOW), new Item(ItemID.MITH_CROSSBOW), new Item(ItemID.ADAMANT_CROSSBOW), new Item(ItemID.RUNE_CROSSBOW),
            new Item(ItemID.DRAGON_HUNTER_CROSSBOW), new Item(ItemID.BLURITE_CROSSBOW), new Item(21902), new Item(ItemID.ARMADYL_CROSSBOW)};

    public LumbridgeGrappleAgilityObstacle(Player player) {
        if (!player.getPosition().isWithinDistance(rockPosition, 20) || (EntityExtKt.getBoolean(player, Attribute.DID_FAIL_AGILITY_OBSTACLE, false))) {
            return;
        }

        if (player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 8) {
            DialogueManager.sendStatement(player, "You need at least 8 Agility to use this shortcut.");
            return;
        }
        if (player.getSkillManager().getCurrentLevel(Skill.STRENGTH) < 19) {
            DialogueManager.sendStatement(player, "You need at least 19 Strength to use this shortcut.");
            return;
        }
        if (player.getSkillManager().getCurrentLevel(Skill.RANGED) < 37) {
            DialogueManager.sendStatement(player, "You need at least 37 Range to use this shortcut.");
            return;
        }

        if (!player.getEquipment().contains(new Item(ItemID.MITH_GRAPPLE_2))) {
            player.sendMessage("You need a Mithril grapple tipped bolt with a rope to do that.");
            return;
        }

        boolean hasCrossbow = false;
        for (int i = 0; i< crossbows.length; i++) {
            if (player.getEquipment().contains(crossbows[i])) {
                hasCrossbow = true;
            }
        }
        if (!hasCrossbow) {
            player.sendMessage("You need a crossbow equipped to do that.");
            return;
        }

        walkToObject(player);
    }

    private void walkToObject(Player player) {
        if (player.getPosition().getX() < rockPosition.getX()) {

            if (!player.getPosition().sameAs(new Position(3246, 3179))) {
                player.getMotion().enqueuePathToWithoutCollisionChecks(3246, 3179);

                player.BLOCK_ALL_BUT_TALKING = true;
                TaskManager.submit(1, () -> {
                    walkToObject(player);
                });
                return;
            } else {
                execute(player);
            }

        } else {
            if (!player.getPosition().sameAs(new Position(3259, 3179))) {
                player.getMotion().enqueuePathToWithoutCollisionChecks(3259, 3179);

                player.BLOCK_ALL_BUT_TALKING = true;
                TaskManager.submit(1, () -> {
                    walkToObject(player);
                });
                return;
            } else {
                execute(player);
            }
        }
    }

    @Override
    public void execute(Player player) {
        player.setPositionToFace(rockPosition);

        TaskManager.submit(new Task(1, true) {

            int delay = 0;
            boolean cameFromWest = false;

            @Override
            protected void execute() {
                /*
                 * To far
                 */
                if (!player.getPosition().isWithinDistance(rockPosition, 20) || (EntityExtKt.getBoolean(player, Attribute.DID_FAIL_AGILITY_OBSTACLE, false))) {
                    stop();
                    return;
                }

                if (delay == 0) {
                    player.performAnimation(new Animation(4230));
                    player.playSound(new Sound(2928));
                    if (player.getPosition().sameAs(new Position(3246, 3179)))
                        cameFromWest = true;

                    EntityExtKt.setBoolean(player, Attribute.STALL_HITS, true, false);
                    player.BLOCK_ALL_BUT_TALKING = true;
                }

                if (delay == 3) {
                    //Create / Change objects
                    if (cameFromWest) {
                        GameObject strongYew = new StaticGameObject(ObjectID.STRONG_YEW_2, new Position(3244, 3179, 0), 10, 0);
                        ObjectManager.add(strongYew);
                        GameObject grappleRope = new StaticGameObject(17034, new Position(3246, 3179, 0), 22, 0);
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3247, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3248, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3249, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3250, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3251, 3179, 0));
                        ObjectManager.add(grappleRope);

                        player.setShouldNoClip(true);
                        if (!player.shouldSetRunningBack) {
                            player.shouldSetRunningBack = true;
                            player.wasRunningAgility = player.isRunning();
                        }
                        player.setRunning(false);
                        PathFinder.INSTANCE.find(player, new Position(3248, 3179), false);
                    } else {
                        GameObject strongYew = new StaticGameObject(ObjectID.STRONG_TREE_2, new Position(3260, 3178, 0), 10, 2);
                        ObjectManager.add(strongYew);
                        GameObject grappleRope = new StaticGameObject(17034, new Position(3254, 3179, 0), 22, 0);
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3255, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3256, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3257, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3258, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3259, 3179, 0));
                        ObjectManager.add(grappleRope);

                        player.setShouldNoClip(true);
                        if (!player.shouldSetRunningBack) {
                            player.shouldSetRunningBack = true;
                            player.wasRunningAgility = player.isRunning();
                        }
                        player.setRunning(false);
                        PathFinder.INSTANCE.find(player, new Position(3258, 3179), false);
                    }

                }

                if (delay == 4) {
                    if (cameFromWest) {
                        player.moveTo(new Position(3248, 3179));

                        TaskManager.submit(new ForceMovementTask(player, 2,
                                new ForceMovement(new Position(3248, 3179), new Position(4, 1), 4, 80,
                                        1, 4466)));

                        player.performGraphic(new Graphic(68));
                        player.playSound(new Sound(2929));
                    } else {
                        player.moveTo(new Position(3258, 3179));

                        TaskManager.submit(new ForceMovementTask(player, 2,
                                new ForceMovement(new Position(3258, 3179), new Position(-5, 0), 4, 130,
                                        3, 4467)));

                        player.performGraphic(new Graphic(68));
                        player.playSound(new Sound(2929));
                    }
                }

                if (delay == 7) {
                    PathFinder.INSTANCE.find(player, new Position(3253, 3180), false);
                }

                if (delay == 8) {
                    if (cameFromWest) {
                        PathFinder.INSTANCE.find(player, new Position(3253, 3179), false);
                    } else {
                        PathFinder.INSTANCE.find(player, new Position(3252, 3180), false);
                    }
                }

                if (delay == 10) {
                    if (cameFromWest) {
                        player.setPositionToFace(new Position(3255, 3179));
                    }
                }

                if (delay == 11) {
                    player.performAnimation(new Animation(4230));
                    player.playSound(new Sound(2928));
                }

                if (delay == 13) {
                    //Create / Change objects
                    if (cameFromWest) {
                        GameObject strongYew = new StaticGameObject(ObjectID.STRONG_TREE_3, new Position(3260, 3178, 0), 10, 2);
                        ObjectManager.add(strongYew);
                        GameObject grappleRope = new StaticGameObject(17034, new Position(3254, 3179, 0), 22, 0);
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3255, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3256, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3257, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3258, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3259, 3179, 0));
                        ObjectManager.add(grappleRope);

                    } else {
                        GameObject strongYew = new StaticGameObject(ObjectID.STRONG_YEW_3, new Position(3244, 3179, 0), 10, 0);
                        ObjectManager.add(strongYew);
                        GameObject grappleRope = new StaticGameObject(17034, new Position(3246, 3179, 0), 22, 0);
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3247, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3248, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3249, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3250, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3251, 3179, 0));
                        ObjectManager.add(grappleRope);
                    }

                }

                if (delay == 14) {
                    if (cameFromWest) {
                        TaskManager.submit(new ForceMovementTask(player, 3,
                                new ForceMovement(new Position(3253, 3179), new Position(5, 0), 5, 130,
                                        1, 4467)));
                    } else {
                        TaskManager.submit(new ForceMovementTask(player, 3,
                                new ForceMovement(new Position(3252, 3180), new Position(-4, -1), 5, 80,
                                        3, 4466)));
                    }
                    player.performGraphic(new Graphic(68));
                    player.playSound(new Sound(2929));
                }

                if (delay == 17) {
                    if (cameFromWest) {
                        GameObject strongYew = new StaticGameObject(ObjectID.STRONG_YEW, new Position(3244, 3179, 0), 10, 0);
                        ObjectManager.add(strongYew);

                        //Removes Ropes
                        GameObject grappleRope = new StaticGameObject(17079, new Position(3246, 3179, 0), 22, 0);
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3247, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3248, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3249, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3250, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3251, 3179, 0));
                        ObjectManager.add(grappleRope);
                    } else {
                        GameObject strongYew = new StaticGameObject(ObjectID.STRONG_TREE, new Position(3260, 3178, 0), 10, 2);
                        ObjectManager.add(strongYew);

                        //Removes Ropes
                        GameObject grappleRope = new StaticGameObject(17079, new Position(3254, 3179, 0), 22, 0);
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3255, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3256, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3257, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3258, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3259, 3179, 0));
                        ObjectManager.add(grappleRope);
                    }
                }

                if (delay == 19) {
                    if (cameFromWest) {
                        PathFinder.INSTANCE.find(player, new Position(3259, 3179), false);
                    } else {
                        PathFinder.INSTANCE.find(player, new Position(3246, 3179), false);
                    }
                }

                if (delay == 23) {
                    if (cameFromWest) {
                        GameObject strongYew = new StaticGameObject(ObjectID.STRONG_TREE, new Position(3260, 3178, 0), 10, 2);
                        ObjectManager.add(strongYew);

                        //Removes Ropes
                        GameObject grappleRope = new StaticGameObject(17079, new Position(3254, 3179, 0), 22, 0);
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3255, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3256, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3257, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3258, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3259, 3179, 0));
                        ObjectManager.add(grappleRope);
                    } else {
                        GameObject strongYew = new StaticGameObject(ObjectID.STRONG_YEW, new Position(3244, 3179, 0), 10, 0);
                        ObjectManager.add(strongYew);

                        //Removes Ropes
                        GameObject grappleRope = new StaticGameObject(17079, new Position(3246, 3179, 0), 22, 0);
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3247, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3248, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3249, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3250, 3179, 0));
                        ObjectManager.add(grappleRope);
                        grappleRope.setPosition(new Position(3251, 3179, 0));
                        ObjectManager.add(grappleRope);
                    }

                    EntityExtKt.setBoolean(player, Attribute.STALL_HITS, false, false);
                    PlayerExtKt.unblock(player, true, true);
                    PlayerExtKt.resetInteractions(player, true, false);
                    player.getClickDelay().reset();
                    player.performAnimation(Animation.DEFAULT_RESET_ANIMATION);
                    player.resetBas();
                    player.updateAppearance();
                    player.setShouldNoClip(false);
                    player.BLOCK_ALL_BUT_TALKING = false;

                    if (player.shouldSetRunningBack) {
                        player.setRunning(player.wasRunningAgility);
                        player.shouldSetRunningBack = false;
                    }

                    stop();
                }

                /*
                 * Started
                 */
                delay++;
            }
        });
    }

}