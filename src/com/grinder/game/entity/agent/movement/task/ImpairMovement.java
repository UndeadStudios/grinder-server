package com.grinder.game.entity.agent.movement.task;

import com.grinder.game.entity.agent.movement.Motion;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.task.Task;
import com.grinder.util.Misc;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-03-23
 */
public class ImpairMovement extends Task {

    private final Motion motion;

    public ImpairMovement(Motion motion, int seconds) {
        super(Misc.getTicks(seconds));
        this.motion = motion;
        bind(motion);
    }

    @Override
    protected void execute() {
        motion.update(MovementStatus.NONE);
        motion.clearSteps();
        stop();
    }
}
