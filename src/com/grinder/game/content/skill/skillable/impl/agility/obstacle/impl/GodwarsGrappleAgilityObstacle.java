package com.grinder.game.content.skill.skillable.impl.agility.obstacle.impl;

import com.grinder.game.content.skill.skillable.impl.agility.obstacle.AgilityObstacle;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.movement.pathfinding.PathFinder;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.StaticGameObject;
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

public class GodwarsGrappleAgilityObstacle implements AgilityObstacle {

    private Position rockPosition = new Position(2871, 5270, 2);

    private Item[] crossbows = {new Item(ItemID.BRONZE_CROSSBOW), new Item(ItemID.IRON_CROSSBOW), new Item(ItemID.STEEL_CROSSBOW), new Item(ItemID.MITH_CROSSBOW), new Item(ItemID.ADAMANT_CROSSBOW), new Item(ItemID.RUNE_CROSSBOW),
            new Item(ItemID.DRAGON_HUNTER_CROSSBOW), new Item(ItemID.BLURITE_CROSSBOW), new Item(21902), new Item(ItemID.ARMADYL_CROSSBOW)};

    public GodwarsGrappleAgilityObstacle(Player player) {
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
        if (player.getPosition().getY() > rockPosition.getY()) {

            if (!player.getPosition().sameAs(new Position(2872, 5279, 2))) {
                player.getMotion().enqueuePathToWithoutCollisionChecks(2872, 5279);

                player.BLOCK_ALL_BUT_TALKING = true;
                TaskManager.submit(1, () -> {
                    walkToObject(player);
                });
                return;
            } else {
                execute(player);
            }

        } else {
            if (!player.getPosition().sameAs(new Position(2872, 5269, 2))) {
                player.getMotion().enqueuePathToWithoutCollisionChecks(2872, 5269);

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
            boolean cameFromNorth = false;

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
                    if (player.getPosition().sameAs(new Position(2872, 5279, 2)))
                        cameFromNorth = true;

                    EntityExtKt.setBoolean(player, Attribute.STALL_HITS, true, false);
                    player.BLOCK_ALL_BUT_TALKING = true;
                }

                if (delay == 3) {
                    //Create / Change objects
                    if (cameFromNorth) {

                        player.setShouldNoClip(true);
                        if (!player.shouldSetRunningBack) {
                            player.shouldSetRunningBack = true;
                            player.wasRunningAgility = player.isRunning();
                        }
                        player.setRunning(false);
                        TaskManager.submit(new ForceMovementTask(player, 2,
                                new ForceMovement(new Position(2872, 5279, 2), new Position(0, -10), 4, 60,
                                        2, 6132)));
                    } else {

                        player.setShouldNoClip(true);
                        if (!player.shouldSetRunningBack) {
                            player.shouldSetRunningBack = true;
                            player.wasRunningAgility = player.isRunning();
                        }
                        player.setRunning(false);
                        TaskManager.submit(new ForceMovementTask(player, 2,
                                new ForceMovement(new Position(2872, 5269, 2), new Position(0, 10), 4, 60,
                                        0, 6132)));
                    }

                }

                if (delay == 7) {

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