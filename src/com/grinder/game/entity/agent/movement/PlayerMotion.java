package com.grinder.game.entity.agent.movement;

import com.grinder.game.content.skill.skillable.impl.hunter_new.Hunter;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.movement.pathfinding.PathFinder;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-03-21
 */
public class PlayerMotion extends Motion<Player> {

    public static final int MAX_RUN_ENERGY = 10000;

    public PlayerMotion(Player actor) {
        super(actor);
    }

    @Override
    public void onTargetOutOfReach(Agent target) {
        super.onTargetOutOfReach(target);
        actor.debug("You can't reach that!");
    }

    @Override
    public boolean runToNextStep() {
        return actor.isRunning();
    }

    @Override
    void postSequence() {
        Hunter.clearTraps(actor, false);
    }

    @Override
    void postSequencedRunStep() {
        actor.drainEnergy();

        if (actor.getRunEnergy() == 0) {
            actor.setRunning(false);
            actor.getPacketSender().sendRunStatus();
        }
    }

    @Override
    public void traceTo(Position targetPosition) {
        if (bypassClippingCheck())
            enqueuePathToWithoutCollisionChecks(targetPosition.getX(), targetPosition.getY());
        else if (PathFinder.INSTANCE.find(actor, targetPosition, true) == null) {
            System.err.println("[PlayerMotion]: could not generate path to " + targetPosition + " from " + actor.getPosition() + " for player " + actor.getUsername());
            if (actor.isPlayer()) {
                actor.setPositionToFace(targetPosition);
                actor.getMotion().reset();
                actor.getMotion().resetTargetFollowing();
                actor.getPacketSender().sendMinimapFlagRemoval();
                actor.sendMessage("I can't reach that!");
            } else if (actor.isNpc()) {
                actor.setPositionToFace(targetPosition);
                actor.getMotion().reset();
                actor.getMotion().resetTargetFollowing();
            }
        }
    }

    public void reset() {
        steps.clear();
    }
}
