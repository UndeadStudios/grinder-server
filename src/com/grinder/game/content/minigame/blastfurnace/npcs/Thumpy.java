package com.grinder.game.content.minigame.blastfurnace.npcs;

import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.model.Animation;
import com.grinder.game.model.FacingDirection;
import com.grinder.game.model.Position;
import com.grinder.game.task.TaskManager;

/**
 * @author L E G E N D
 * @date 2/22/2021
 * @time 1:22 AM
 * @discord L E G E N D#4380
 */
public final class Thumpy extends BlastFurnaceNpc {

    public Thumpy(int id, Position position) {
        super(id, position);
    }

    public void repair() {
        if(getArea() != null){
            getMotion().enqueuePathToWithoutCollisionChecks(getX() + 1, getY());
            resetFace();
            TaskManager.submit(2, () -> performAnimation(new Animation(898)));
            TaskManager.submit(6, () -> {
                getMotion().enqueuePathToWithoutCollisionChecks(getX() - 1, getY());
                resetFace();
            });
        }

    }

    private void resetFace() {
        setFace(FacingDirection.SOUTH);
    }
}
