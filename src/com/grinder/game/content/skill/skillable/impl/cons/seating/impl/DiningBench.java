package com.grinder.game.content.skill.skillable.impl.cons.seating.impl;

import com.grinder.game.content.skill.skillable.impl.cons.Construction;
import com.grinder.game.content.skill.skillable.impl.cons.ConstructionUtils;
import com.grinder.game.content.skill.skillable.impl.cons.seating.Seat;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ClippedMapObjects;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Direction;
import com.grinder.game.model.ForceMovement;
import com.grinder.game.model.Position;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.ForceMovementTask;

import java.util.List;

/**
 * @author Simplex
 * @since Apr 10, 2020
 */

public enum DiningBench implements Seat {
    WOODEN_BENCH(13300, 26210, 4089, 4090),
    OAK_BENCH(13301, 26211, 4091, 4092),
    CARVED_OAK_BENCH(13302, 26212, 4093, 4094),
    TEAK_BENCH(13303, 26213, 4095, 4096),
    CARVED_TEAK_BENCH(13304, 26214, 4097, 4098),
    MAHOGANY_BENCH(13305, 26215, 4099, 4100),
    GILDED_BENCH(13306, 26216, 4101, 4102),

    ;

    DiningBench(int benchId, int tempId, int idle, int eat) {
        this.benchId = benchId;
        this.tempId = tempId;
        this.idle = idle;
        this.eat = eat;
    }

    public int benchId;

    int tempId;
    int idle;
    int eat;

    public void sit(Player player, GameObject object) {
        Position pullTo = player.getPosition().copy();

        final Seat seat = this;

        TaskManager.submit(new Task(1, player, true) {
            int cycle = 0;
            @Override
            protected void execute() {
                switch (cycle++) {
                    /*case 0:
                        Direction step = object.getFacing().getForceMovementMask() == 1 || object.getFacing().getForceMovementMask() == 3 ? Direction.NORTH : Direction.WEST;
                        //player.getMotion().enqueueStepsTo(new Position(player.getX() + step.getX(), player.getY() + step.getY()), true);
                        break;*/
                    case 0:
                        player.BLOCK_ALL_BUT_TALKING = true;
                        if(object.getId() != benchId) {
                            player.BLOCK_ALL_BUT_TALKING = false;
                            stop();
                            return;
                        }
                        player.seat = seat;

                        // anims are screwed up, bench disappears after 30 seconds
                        // keep the objects for now
                        // player.getPacketSender().sendObjectRemoval(object);
                        // object.setPosition(player.getPosition().copy());

                        Direction dir = Direction.getDirection(player.getPosition(), object.getPosition().copy());

                        // access to benches is front facing everywhere except throne room
                        dir = Direction.getDirection(player.getPosition(), object.getPosition().copy());
                        if(Construction.getHouseRoomAt(player, player.getPosition()).get().getType() == ConstructionUtils.THRONE_ROOM) {
                            dir = Direction.getDirection(object.getPosition(), player.getPosition().copy());
                        }

                        //player.performAnimation(ConstructionUtils.SIT);

                        TaskManager.submit(
                                new ForceMovementTask(player, 1,
                                        new ForceMovement(player.getPosition().copy(),
                                                object.getPosition().getDelta(player.getPosition()),
                                                25,
                                                5,
                                                dir.getForceMovementMask(),
                                                ConstructionUtils.SIT.getId())));

                        player.setBas(idle);
                        player.updateAppearance();
                        break;
                    case 2:
                        player.performAnimation(new Animation(idle));
                        player.BLOCK_ALL_BUT_TALKING = false;
                        stop();
                        break;
                }
            }
        });
    }


    @Override
    public void stand(Player player) {
    }

    @Override
    public int getEatAnimation(Player player) {
        return eat;
    }

    @Override
    public void restore(Player player) {
        List<GameObject> seat = ClippedMapObjects.getObjectsAt(player.getPosition());
        if (seat.size() > 0) {
            seat.get(0).setId(benchId);
        }
    }
}
