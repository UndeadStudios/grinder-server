package com.grinder.game.entity.agent.movement;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.movement.pathfinding.PathFinder;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-03-21
 */
public class NPCMotion extends Motion<NPC> {

    public static final int WALKING_AREA_RANGE = -2;
    public static final int ATTACK_RANGE_PREFERED_DISTANCE = -1;
    public static final int DEFAULT_FOLLOW_RANGE = 10;

    private boolean blockRetracing;
    private boolean running;

    public NPCMotion(NPC actor) {
        super(actor);
    }

    @Override
    public void clearSteps() {
        String message = stepCount() > 0 ? "steps" : "";
        super.clearSteps();
        actor.debug("cleared steps "+ message);
    }

    @Override
    public void onTargetOutOfReach(Agent target) {
        actor.debug("onTargetOutofReach is being called!");
        if(actor.isPet()){

            final Player owner = actor.getOwner();

            if(owner != null && owner.isRegistered()){
                actor.moveTo(owner.getPosition().clone().move(0,1));
                actor.resetEntityInteraction();
                resetTargetFollowing();
                followTarget(owner);
                actor.setEntityInteraction(owner);
            }
        } else
            super.onTargetOutOfReach(target);
    }

    @Override
    public boolean isMoving() {
        return notCompleted();
    }

    @Override
    public void traceTo(Position position) {
        if (!blockRetracing) {
            PathFinder.INSTANCE.find(actor, position, true);
            actor.debug("tracing to " + position.getLocalX() + ", " + position.getLocalY() + ", " + position.getZ());
        }
    }

    @Override public boolean runToNextStep() {
        return running;
    }

    public void setCanReTrace(boolean canReTrace) {
        this.blockRetracing = !canReTrace;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
