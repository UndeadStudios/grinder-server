package com.grinder.game.content.skill.skillable.impl.agility.obstacle.impl;

import com.grinder.game.content.skill.skillable.impl.agility.obstacle.AgilityObstacle;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.StaticGameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ObjectID;

public class GodwarsBridgeAgilityObstacle implements AgilityObstacle {

    private Position rockPosition = new Position(2885, 5332, 2);
    private Position otherRockPosition = new Position(2885, 5345, 2);

    public GodwarsBridgeAgilityObstacle(Player player) {
        if ((!player.getPosition().isWithinDistance(rockPosition, 20) && !player.getPosition().isWithinDistance(otherRockPosition, 20)) || (EntityExtKt.getBoolean(player, Attribute.DID_FAIL_AGILITY_OBSTACLE, false))) {
            return;
        }

        walkToObject(player);
    }

    private void walkToObject(Player player) {
        if (player.getPosition().getY() < 5338) {

            if (!player.getPosition().sameAs(new Position(2885, 5332, 2))) {
                player.getMotion().enqueuePathToWithoutCollisionChecks(2885, 5332);

                player.BLOCK_ALL_BUT_TALKING = true;
                TaskManager.submit(1, () -> {
                    walkToObject(player);
                });
                return;
            } else {
                execute(player);
            }

        } else {
            if (!player.getPosition().sameAs(new Position(2885, 5345, 2))) {
                player.getMotion().enqueuePathToWithoutCollisionChecks(2885, 5345);

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
            boolean cameFromSouth = false;

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
                    if (player.getPosition().sameAs(new Position(2885, 5332, 2))) {
                        cameFromSouth = true;
                        player.getMotion().enqueuePathToWithoutCollisionChecks(2885, 5333);
                    } else {
                        player.getMotion().enqueuePathToWithoutCollisionChecks(2885, 5344);
                    }

                    EntityExtKt.setBoolean(player, Attribute.STALL_HITS, true, false);
                    player.BLOCK_ALL_BUT_TALKING = true;
                    player.getPacketSender().sendFadeScreen("", 2, 4);
                }

                if (delay == 2) {
                    //Create / Change objects
                    if (cameFromSouth) {
                        player.moveTo(new Position(2885, 5334, 2));
                    } else {
                        player.moveTo(new Position(2885, 5343, 2));
                    }
                    player.performGraphic(new Graphic(68));
                    player.playSound(new Sound(2929));
                    player.performAnimation(new Animation(772));
                }

                if (delay == 3) {
                    if (cameFromSouth) {
                        player.moveTo(new Position(2885, 5345, 2));
                    } else {
                        player.moveTo(new Position(2885, 5332, 2));
                    }
                }

                if (delay == 4) {
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