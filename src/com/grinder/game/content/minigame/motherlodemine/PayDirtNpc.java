package com.grinder.game.content.minigame.motherlodemine;

import com.grinder.game.World;
import com.grinder.game.content.minigame.motherlodemine.sack.Sack;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.NpcID;

/**
 * @author L E G E N D
 * @date 2/11/2021
 * @time 9:44 AM
 * @discord L E G E N D#4380
 */
public final class PayDirtNpc extends NPC {

    private static final Position PAY_DIRT_STARTING_POSITION = new Position(3748, 5671, 0);
    private static final Position PAY_DIRT_END_POSITION = new Position(3748, 5660, 0);

    private Task task;

    public PayDirtNpc(int id, Position position) {
        super(id, position);
    }

    public void startMoving() {
        if (task != null) {
            return;
        }
        TaskManager.submit(1, () -> TaskManager.submit(task = new Task(1, false) {
            @Override
            protected void execute() {
                if (!isActive()){
                    stop();
                    return;
                }
                getMotion().enqueuePathToWithoutCollisionChecks(PAY_DIRT_END_POSITION.getX(), getY() - 1);
                if (getPosition().equals(PAY_DIRT_END_POSITION)) {
                    onArrival();
                    stop();
                }
            }
        }));
    }

    public void stopMoving() {
        if (task != null) {
            task.stop();
            task = null;
        }
    }

    public void onArrival() {
        Sack.fromMachineToSack(getOwner());
    }

    public static PayDirtNpc create(Player player) {
        var npc = new PayDirtNpc(NpcID.PAYDIRT, PAY_DIRT_STARTING_POSITION);
        npc.setOwner(player);
        World.getNpcAddQueue().add(npc);
        return npc;
    }
}
