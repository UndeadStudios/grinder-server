package com.grinder.game.content.minigame.blastfurnace.npcs;

import com.grinder.game.model.Animation;
import com.grinder.game.model.FacingDirection;
import com.grinder.game.model.Position;
import com.grinder.game.task.TaskManager;

/**
 * @author L E G E N D
 * @date 2/22/2021
 * @time 4:36 AM
 * @discord L E G E N D#4380
 */
public final class Numpty extends BlastFurnaceNpc {

    private static Position first;
    private static Position second;

    public Numpty(int id, Position position) {
        super(id, position);
        first = position;
        second = first.transform(-4, 0, 0);
    }

    public void repair() {
        if (getArea() != null) {
            var newPos = getPosition().equals(first) ? second : first;
            getMotion().enqueuePathToWithoutCollisionChecks(newPos.getX(), newPos.getY());
            TaskManager.submit(4, () -> {
                setFace(FacingDirection.NORTH);
                performAnimation(new Animation(2108));
            });
        }
    }
}
