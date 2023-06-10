package com.grinder.game.content.skill.skillable.impl.agility.obstacle.impl;

import com.grinder.game.content.skill.skillable.impl.agility.obstacle.AgilityObstacle;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.StaticGameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.ForceMovement;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.ForceMovementTask;
import com.grinder.util.ItemID;
import com.grinder.util.ObjectID;

public class GodwarsGrappleRockAgilityObstacle implements AgilityObstacle {

    private Position rockPosition = new Position(2913, 5300, 2);
    private Position otherRockPosition = new Position(2913, 5300, 1);

    public GodwarsGrappleRockAgilityObstacle(Player player) {
        if ((!player.getPosition().isWithinDistance(rockPosition, 20) && !player.getPosition().isWithinDistance(otherRockPosition, 20)) || (EntityExtKt.getBoolean(player, Attribute.DID_FAIL_AGILITY_OBSTACLE, false))) {
            return;
        }

        walkToObject(player);
    }

    private void walkToObject(Player player) {
        if (player.getPosition().getX() < rockPosition.getX()) {

            if (!player.getPosition().sameAs(new Position(2912, 5300, 2))) {
                player.getMotion().enqueuePathToWithoutCollisionChecks(2912, 5300);

                player.BLOCK_ALL_BUT_TALKING = true;
                TaskManager.submit(1, () -> {
                    walkToObject(player);
                });
                return;
            } else {
                execute(player);
            }

        } else {
            if (!player.getPosition().sameAs(new Position(2914, 5300, 1))) {
                player.getMotion().enqueuePathToWithoutCollisionChecks(2914, 5300);

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
                if ((!player.getPosition().isWithinDistance(rockPosition, 20) && !player.getPosition().isWithinDistance(otherRockPosition, 20)) || (EntityExtKt.getBoolean(player, Attribute.DID_FAIL_AGILITY_OBSTACLE, false))) {
                    stop();
                    return;
                }

                if (delay == 0) {
                    if (player.getPosition().sameAs(new Position(2912, 5300, 2))) {
                        cameFromWest = true;

                        if (!EntityExtKt.getBoolean(player, Attribute.GODWARS_ROCK_ROPE, false)) {
                            if (!player.getInventory().contains(954)) {
                                player.sendMessage("You need rope to do that!");
                                stop();
                                return;
                            } else {
                                EntityExtKt.setBoolean(player, Attribute.GODWARS_ROCK_ROPE, true, false);
                                player.getInventory().delete(954, 1);
                            }
                        }
                    }

                    player.performAnimation(new Animation(828));
                    player.playSound(new Sound(2928));

                    EntityExtKt.setBoolean(player, Attribute.STALL_HITS, true, false);
                    player.BLOCK_ALL_BUT_TALKING = true;
                }

                if (delay == 2) {
                    //Create / Change objects
                    if (cameFromWest) {
                        player.moveTo(new Position(2914, 5300, 1));
                    } else {
                        player.moveTo(new Position(2912, 5300, 2));
                    }

                }

                if (delay == 3) {
                    GameObject rope = new StaticGameObject(ObjectID.ROPE_28, new Position(2914, 5300, 1), 10, 2);
                    ObjectManager.add(rope);
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