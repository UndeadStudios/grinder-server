package com.grinder.game.entity.agent.movement.task.impl.npc;

import com.grinder.game.entity.agent.movement.NPCMotion;
import com.grinder.game.entity.agent.movement.task.MovementTask;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.util.Priority;

/**
 * @author Stan van der Bend
 * @since 27-3-19
 */
public class RetreatTask extends MovementTask<NPC> {

    public RetreatTask(NPC actor) {
        super(Priority.HIGH, actor);

        final NPCMotion movement = actor.getMotion();

        movement.clearSteps();
        movement.followTarget(null);
        movement.traceTo(actor.getSpawnPosition());

        actor.getCombat().reset(true);
    }

    @Override
    public void sequenceMovement() {

        final NPCMotion movement = actor.getMotion();

        if(movement.completed())
            stop();
        else {

            if(movement.hasFollowTarget())
                cancel();

        }
    }

    @Override
    public void cancel() {
        super.cancel();
        actor.debug("Canceled retreat task!");
    }

}
