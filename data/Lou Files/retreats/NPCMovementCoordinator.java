package com.grinder.game.entity.agent.movement;

import com.grinder.game.entity.agent.movement.pathfinding.PathFinder;
import com.grinder.game.entity.agent.movement.task.MovementTask;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Position;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import kotlin.random.Random;

import static com.grinder.game.entity.agent.movement.NPCMovementCoordinator.GoalState.*;

/**
 * Will make all {@link NPC}s set to coordinate, pseudo-randomly move within a
 * specified radius of their original position.
 * <p>
 * TODO: integrate this class in {@link NPCMotion} and use specialised {@link MovementTask} for the specific guided movement tasks.
 *
 * @author lare96
 * @author Stan van der Bend
 */
public class NPCMovementCoordinator {

    // the actor NPC whose movement is being coordinated.
    private final NPC actor;

    // the current goal state of the NPC.
    private GoalState goalState;

    // the radius in which a NPC can freely walk randomly from it's home location.
    private int radius;

    private int failureCount = 0;

    private Position retreatPosition;

    public boolean canBeAttacked = true;

    public NPCMovementCoordinator(NPC actor) {
        this.actor = actor;
        this.goalState = WALK_IN_RADIUS;
    }

    public boolean canRetreat() {
        return getRetreatPosition() != null;
    }

    public Position getRetreatPosition() {
        return retreatPosition == null ? actor.getSpawnPosition() : retreatPosition;
    }

    public void sequence() {
        if (goalState == WALK_IN_RADIUS && radius == 0) {
            return;
        }

        if (actor.getHitpoints() <= 0) {
            return;
        }

        if (actor.getMotion().isMoving()) {
            failureCount = 0;
        }

        actor.debug("Goal(" + goalState + ")[" + failureCount + "]");

        Position retreatPosition = getRetreatPosition();

        if (goalState != WALK_HOME && actor.getRetreatPolicy() != MonsterRetreatPolicy.NEVER) {
            if (retreatPosition != null && !actor.getPosition().isWithinDistance(retreatPosition, actor.fetchDefinition().getCombatFollowDistance())
            || (actor.getCombat().getTarget() != null && !actor.getPosition().isWithinDistance(actor.getCombat().getTarget().getPosition(), actor.fetchDefinition().getCombatFollowDistance()))) {
                actor.getMotion().cancelTask();
                actor.getCombat().reset(true, true);
                actor.getCombat().resetCombatCoolDown();
                actor.getCombat().resetTarget();
                actor.getCombat().setNegateAggression(true);
                TaskManager.submit(new Task(2) {
                    @Override
                    public void execute() {
                        stop();
                        goalState = WALK_HOME;
                    }
                });
                TaskManager.submit(new Task(5) {
                    @Override
                    public void execute() {
                        stop();
                        if (actor != null)
                        actor.getCombat().setNegateAggression(false);
                        goalState = WALK_IN_RADIUS;
                    }
                });
                goalState = WALK_IN_RADIUS; // Already set by reset (true, true)
            }
        }

        switch (goalState) {
            case WALK_IN_RADIUS:
                if (actor.getInteractingEntity() != null || actor.getCombat().isInCombat() || actor.getCombat().isBeingAttacked()
                        || actor.isDying() || actor.getCombat().hasTarget() || !actor.getMotion().completed()) {
                    return;
                }
                //TODO:UNCOMMENT BELOW WHEN YOU FIX AUTO RETALIATE & AGGRESSION (LEGENDS NOTED THIS)
                if ( /*!actor.getCombat().isInCombat() &&  */actor.getMotion().completed() && Misc.getRandomInclusive(10) <= 1) {
                    PathFinder.INSTANCE.find(actor, createOffsetPositionWithinRadius(), false);
                }
                break;
            case RETREAT_HOME:
            case WALK_HOME:
                if (!canRetreat()) {
                    reset();
                    return;
                }

                if (actor.getMotion().isMoving())
                    return;

                if (actor.getPosition().equals(retreatPosition)) {
                    reset();
                    return;
                }

                if (++failureCount > 15) {
                    failureCount = 0;
                    actor.moveTo(retreatPosition.clone());
                    PlayerUtil.broadcastPlayerDeveloperMessage(actor + " has been moved back to retreat location " + retreatPosition.compactString());
                } else {
                    PathFinder.INSTANCE.find(actor, retreatPosition, failureCount > 1);
                }
                break;
        }
    }

    private Position createOffsetPositionWithinRadius() {
        int spawnX = actor.getSpawnPosition().getX();
        int spawnY = actor.getSpawnPosition().getY();
        int spawnZ = actor.getSpawnPosition().getZ();
        int offsetX = Random.Default.nextInt(-radius, radius);
        int offsetY = Random.Default.nextInt(-radius, radius);
        return new Position(spawnX + offsetX, spawnY + offsetY, spawnZ);
    }

    public boolean isRetreating() {
        return goalState == RETREAT_HOME;
    }

    public void reset() {
        goalState = WALK_IN_RADIUS;
    }

    public enum GoalState {
        WALK_IN_RADIUS,
        WALK_HOME,
        IN_COMBAT,
        RETREAT_HOME
    }

    private boolean isOutOfBounds() {
        if (retreatPosition == null)
            return true;
        return !actor.getPosition().isWithinDistance(retreatPosition, actor.fetchDefinition().getCombatFollowDistance());
    }

    public void retreatHome() {

        goalState = RETREAT_HOME;

        if (!isOutOfBounds() && actor.getAbsoluteWalkableArea() != null && radius != 0) {
            final Position retreat = actor.getAbsoluteWalkableArea().getRandomPosition(1).setZ(actor.getPosition().getZ());
            if (retreat != null)
                retreatPosition = retreat;
        }
    }


    public void setGoalState(GoalState goalState) {
        this.goalState = goalState;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public GoalState getGoalState() {
        return goalState;
    }

    public int getRadius() {
        return radius;
    }

}