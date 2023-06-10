package com.grinder.game.entity.agent.movement;

import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.movement.pathfinding.PathFinder;
import com.grinder.game.entity.agent.movement.task.MovementTask;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.aggression.MonsterAggressionProcess;
import com.grinder.game.entity.agent.npc.monster.boss.Boss;
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinion;
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.areas.instanced.PestControlArea;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;
import kotlin.random.Random;

import java.util.concurrent.TimeUnit;

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

    private final MonsterAggressionProcess aggressionProcess = new MonsterAggressionProcess();

    // the actor NPC whose movement is being coordinated.
    private final NPC actor;

    // the current goal state of the NPC.
    private GoalState goalState;

    // the radius in which a NPC can freely walk randomly from it's home location.
    private int radius;

    private int failureCount = 0;

    private int leaveAloneCounter = 0;

    private Position retreatPosition;

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
            leaveAloneCounter = 0;
        }

        actor.debug("Goal(" + goalState + ")[" + failureCount + "]");

        Position retreatPosition = getRetreatPosition();

        /*if (goalState != WALK_HOME) {
            if (retreatPosition != null && !actor.getPosition().isWithinDistance(retreatPosition, actor.fetchDefinition().getCombatFollowDistance())) {
                actor.getMotion().cancelTask();
                actor.getCombat().reset(true, true);
                actor.setEntityInteraction(null);
                goalState = WALK_HOME;
            }
        }*/

//        if (goalState != WALK_HOME && goalState != RETREAT_HOME && actor.getCombat().hasTarget() && !actor.getCombat().isUnderAttack() && !actor.getCombat().isBeingAttacked()
//                && !MonsterAggressionProcess.Companion.isBandit(actor) && !MonsterAggressionProcess.Companion.isGodWarsMinion(actor)
//                && !AreaManager.inWilderness(actor) && !(actor.getArea() instanceof PestControlArea) && actor.fetchDefinition().getRespawn() > 0
//                && actor.fetchDefinition().doesRetreat()
//                && canRetreat() && actor.fetchDefinition().isAttackable() && !(actor instanceof Boss) && !(actor instanceof BossMinion) && actor.fetchDefinition().getId() != NpcID.AL_KHARID_WARRIOR) {
//            leaveAloneCounter++;
//            if (leaveAloneCounter >= 15) {
//                final NPCMotion npcMotion = actor.getMotion();
//                actor.setEntityInteraction(null);
//                npcMotion.cancelTask();
//                npcMotion.traceTo(retreatPosition);
//                npcMotion.setCanReTrace(true);
//                actor.getCombat().reset(true, false);
//                actor.getCombat().resetCombatCoolDown();
//                actor.ifPlayer(aggressionProcess::temporarilyIgnore);
//                goalState = WALK_HOME;
//            }
//        }

        if (retreatPosition != null && goalState != WALK_HOME && goalState != RETREAT_HOME && actor.fetchDefinition().isAttackable()/* && !(actor.getAsNpc() instanceof Boss)*/) {
            final int combatFollowDistance = actor.fetchDefinition().getCombatFollowDistance();
            final boolean strayedTooFar = !actor.getPosition().isWithinDistance(retreatPosition, combatFollowDistance + (actor.getAsNpc() instanceof Boss ? 4 : 2));
            final boolean targetStrayedTooFar = actor.getCombat().findCurrentTarget()
                    .map(target -> !target.getPosition().isWithinDistance(retreatPosition, combatFollowDistance + (actor.getAsNpc() instanceof Boss ? 6 : 4)))
                    .orElse(false);
            if (strayedTooFar || targetStrayedTooFar) {
                actor.getCombat()
                        .findCurrentTarget()
                        .flatMap(Agent::getAsOptionalPlayer)
                        .ifPresent(player ->
                                player.sendDevelopersMessage("2: Actor is not in " +
                                        "combat distance {" + combatFollowDistance + "}, " +
                                        "retreating to {" + retreatPosition.compactString() + "}, "+
                                        "strayedTooFar = "+strayedTooFar+", "+
                                        "targetStrayedTooFar = "+targetStrayedTooFar+""));
                final NPCMotion npcMotion = actor.getMotion();
                npcMotion.cancelTask();
                npcMotion.traceTo(retreatPosition);
                npcMotion.setCanReTrace(true);
                actor.getCombat().reset(true, false);
                actor.getCombat().resetCombatCoolDown();
                goalState = WALK_HOME;
            }
        }

        switch (goalState) {
            case WALK_IN_RADIUS:
                if (actor.getPosition().equals(retreatPosition) || (actor.getInteractingEntity() != null && actor.fetchDefinition().isAttackable())) {
                    actor.resetEntityInteraction();
                }
                if (actor.getInteractingEntity() != null || actor.getCombat().isInCombat() || actor.getCombat().isBeingAttacked()
                        || actor.isDying() || actor.getCombat().hasTarget() || actor.getMotion().isMoving() || actor.getMotion().notCompleted()) {

                    return;
                }
                // Random walking
                if (actor.getMotion().completed() && Misc.getRandomInclusive(10) <= 1) {
                  //  if (EntityExtKt.passedTime(actor, Attribute.LAST_RANDOM_MOVEMENT, 5, TimeUnit.SECONDS, false, true)) {
                        PathFinder.INSTANCE.find(actor, createOffsetPositionWithinRadius(), false);
                  //  }
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
                    if (++failureCount > 15) {
                        failureCount = 0;
                        actor.moveTo(retreatPosition.clone());
                        PlayerUtil.broadcastPlayerDeveloperMessage(actor + " has been moved back to retreat location " + retreatPosition.compactString());
                    }
                    return;
                }

                if (++failureCount > 15) {
                    failureCount = 0;
                    actor.moveTo(retreatPosition.clone());
                    PlayerUtil.broadcastPlayerDeveloperMessage(actor + " has been moved back to retreat location " + retreatPosition.compactString());
                } else {
                    PathFinder.INSTANCE.find(actor, retreatPosition, failureCount > 3);
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

        retreatPosition = actor.getSpawnPosition();
        if (!isOutOfBounds() && actor.getAbsoluteWalkableArea() != null && radius != 0) {
            final Position retreat = actor.getAbsoluteWalkableArea().getRandomPosition(1).setZ(actor.getPosition().getZ());
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