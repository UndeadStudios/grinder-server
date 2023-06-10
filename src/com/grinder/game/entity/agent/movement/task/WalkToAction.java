package com.grinder.game.entity.agent.movement.task;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.LineOfSight;
import com.grinder.game.entity.agent.combat.event.impl.TargetIsOutOfReach;
import com.grinder.game.entity.agent.movement.StepQueue;
import com.grinder.game.entity.agent.movement.pathfinding.PathFinder;
import com.grinder.game.entity.agent.movement.pathfinding.target.TargetStrategy;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Executable;
import com.grinder.util.Priority;
import com.grinder.util.timing.TimerKey;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface.HALBERD;

/**
 * This class handles the execution of an action
 * once we're close enough to the given position.
 * Used for things such as clicking on entities.
 */
public class WalkToAction<T extends Agent> extends MovementTask<T> {

    private final Set<Policy> policies;
    private final int distance;
    private final int actionDelay;

    public WalkToAction(T agent, Agent target, Executable finalizedTask, Policy... policies) {
        this(agent, target, 0, 0, finalizedTask, policies);
    }

    public WalkToAction(T agent, Object target, int actionDelay, Executable finalizedTask, Policy... policies) {
        this(agent, target, 0, actionDelay, finalizedTask, policies);
    }

    public WalkToAction(T agent, Object target, int distance, int actionDelay, Executable finalizedTask, Policy... policies) {
        this(agent, PathFinder.INSTANCE.getStrategy(target), distance, actionDelay, finalizedTask, policies);
    }

    public WalkToAction(T agent, TargetStrategy strategy, int actionDelay, Executable finalizedTask, Policy... policies) {
        this(agent, strategy, 0, actionDelay, finalizedTask, policies);
    }

    public WalkToAction(T agent, TargetStrategy strategy, int distance, int actionDelay, Executable finalizedTask, Policy... policies) {
        super(Priority.HIGH, agent);
        this.actionDelay = actionDelay;
        this.finalizedAction = finalizedTask;
        this.policies = new HashSet<>(policies.length);
        Collections.addAll(this.policies, policies);
        this.strategy = strategy;
        this.distance = distance;
    }

    @Override
    public void start() {
        calculate();
    }

    private void calculate() {
        if (inDistance()) {
            execute();
            return;
        }

        if (actor.isPlayer()) {
            Player player = actor.getAsPlayer();
            if (player.getTimerRepository().has(TimerKey.FREEZE)) {
                player.sendMessage("A magical spell has made you unable to move.");
                player.setPositionToFace(strategy.getTile());
                player.getCombat().reset(false);
                player.setEntityInteraction(null);
                player.getMotion().clearSteps();
                player.getMotion().cancelTask();
                player.getPacketSender().sendMinimapFlagRemoval();
                player.getPacketSender().sendSound(Sounds.FROZEN_CANT_MOVE);
                player.getMotion().skipNextSequence();
                return;
            }
        }

        if (hasPolicy(Policy.RECALCULATE_IF_TARGET_MOVES)) {
            lastPosition = strategy.getTile();
        }
        destination = PathFinder.INSTANCE.find(actor, strategy);
        if (actor.isPlayer()) {
            Player player = actor.getAsPlayer();
            player.getPacketSender().sendMinimapFlag(destination);
/*            if (player.getRights() == PlayerRights.DEVELOPER) {
                Iterator<StepQueue.Step> it = actor.getMotion().steps.iterator();
                Position start = actor.getPosition().clone();
                while (it.hasNext()) {
                    StepQueue.Step step = it.next();
                    start.add(step.getDirection().getX(), step.getDirection().getY());
                    ((Player) actor).getPacketSender().sendGraphic(new Graphic(187), start.clone());
                }
            }*/
        }
        executeIfInDistance(false);
    }

    /**
     * The destination the game character will move to.
     */
    private Position destination;

    private Position lastPosition = null;

    private final TargetStrategy strategy;

    /**
     * The action a player must execute upon reaching said destination.
     */
    private final Executable finalizedAction;

    private boolean executed = false;

    /**
     * Executes the action if distance is correct
     */
    @Override
    public void sequenceMovement() {
        if (actor == null) {
            return;
        }

        if (!actor.isRegistered() || executed) {
            actor.getMotion().cancelTask();
            return;
        }

        if (!actor.getMotion().canMove() || (actor.isPlayer() && ((Player) actor).busy())) {
            actor.getMotion().cancelTask();
            return;
        }

        if (hasPolicy(Policy.RECALCULATE_IF_TARGET_MOVES)) {
            recalculateIfTargetMoves();
        }

        executeIfInDistance(false);
    }

    public void recalculateIfTargetMoves() {
        if (lastPosition != null && strategy.getTile() != lastPosition) {
            calculate();
        }
    }

    private boolean inDistance() {
        if (hasPolicy(Policy.EXECUTE_ON_PARTIAL) && actor.getPosition().equals(destination)) {
            return true;
        }
        int distance = this.distance;
        if (actor.getMotion().isMoving()) {
            distance++;
            if (actor.getMotion().isRunning()) {
                distance += 2;
            }
        }
        Position target = strategy.getTile();
        if (hasPolicy(Policy.EXECUTE_WHEN_IN_DISTANCE) && distance >= 0 && actor.getPosition().isWithinDistance(target.getX(), target.getY(), target.getZ(), distance)) {
            if (hasPolicy(Policy.EXECUTE_ON_LINE_OF_SIGHT)) {
                if (LineOfSight.withinSight(actor, target, strategy.getWidth(), strategy.getHeight(), distance, checkWalls())) {
                    return hasPolicy(Policy.ALLOW_UNDER) || !actor.isUnder(strategy.getTile(), strategy.getWidth(), strategy.getHeight());
                }
                return false;
            }
            return hasPolicy(Policy.ALLOW_UNDER) || !actor.isUnder(strategy.getTile(), strategy.getWidth(), strategy.getHeight());
        }
        if (hasPolicy(Policy.EXECUTE_ON_LINE_OF_SIGHT) && LineOfSight.withinSight(actor, target, strategy.getWidth(), strategy.getHeight(), distance, checkWalls())) {
            return hasPolicy(Policy.ALLOW_UNDER) || !actor.isUnder(strategy.getTile(), strategy.getWidth(), strategy.getHeight());
        }
        if (inDistanceUnreachable())
            return true;
        return strategy.reached(actor);
    }

    private boolean checkWalls() {
        if (actor.getCombat().isMeleeAttack()) {
            if (actor.getCombat().uses(HALBERD)) {
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean inDistanceUnreachable() {
        /*
         * Custom way to check inDistance if object is unreachable
         */
        if (actor.isWithinDistance(strategy.getTile(), 2)) {
            Position checkPosition = new Position(3318, 3166, 1);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                return true;
            }
            checkPosition = new Position(3349, 2970, 1);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                return true;
            }

            checkPosition = new Position(3356, 2978, 1);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                return true;
            }

            checkPosition = new Position(3359, 2996, 2);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 3359 && actor.getY() == 2995)
                    return true;
            }

            checkPosition = new Position(3363, 3000, 2);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 3362 && actor.getY() == 3002)
                    return true;
            }

            checkPosition = new Position(3363, 2976, 1);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 3362 && actor.getY() == 2977)
                    return true;
            }

            checkPosition = new Position(2804, 9584, 3);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 2804 && actor.getY() == 9582)
                    return true;
            }

            checkPosition = new Position(2806, 9585, 3);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 2806 && actor.getY() == 9587)
                    return true;
            }

            checkPosition = new Position(2767, 9567, 3);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 2769 && actor.getY() == 9567)
                    return true;
            }

            checkPosition = new Position(2766, 9569, 3);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 2764 && actor.getY() == 9569)
                    return true;
            }

            checkPosition = new Position(3206, 9572, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 3204 && actor.getY() == 9572)
                    return true;
            }

            checkPosition = new Position(3206, 9572, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 3208 && actor.getY() == 9572)
                    return true;
            }

            checkPosition = new Position(2684, 9548, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 2682 && actor.getY() == 9548)
                    return true;
            }

            checkPosition = new Position(2688, 9547, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 2690 && actor.getY() == 9547)
                    return true;
            }

            checkPosition = new Position(2695, 9531, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 2695 && actor.getY() == 9533)
                    return true;
            }

            checkPosition = new Position(2696, 9527, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 2697 && actor.getY() == 9525)
                    return true;
            }

            checkPosition = new Position(2863, 2976, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 2863 && actor.getY() == 2974)
                    return true;
            }

            checkPosition = new Position(2863, 2974, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 2863 && actor.getY() == 2976)
                    return true;
            }

            checkPosition = new Position(2514, 3617, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 2514 && actor.getY() == 3619)
                    return true;
            }

            checkPosition = new Position(2514, 3619, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 2514 && actor.getY() == 3617)
                    return true;
            }

            checkPosition = new Position(2514, 3613, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 2514 && actor.getY() == 3615)
                    return true;
            }

            checkPosition = new Position(2514, 3615, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 2514 && actor.getY() == 3613)
                    return true;
            }

            checkPosition = new Position(2518, 3611, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 2516 && actor.getY() == 3611)
                    return true;
            }

            checkPosition = new Position(2516, 3611, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 2518 && actor.getY() == 3611)
                    return true;
            }

            checkPosition = new Position(2522, 3600, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 2522 && actor.getY() == 3602)
                    return true;
            }

            checkPosition = new Position(2522, 3602, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 2522 && actor.getY() == 3600)
                    return true;
            }

            checkPosition = new Position(2522, 3595, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 2522 && actor.getY() == 3597)
                    return true;
            }

            checkPosition = new Position(2522, 3597, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 2522 && actor.getY() == 3595)
                    return true;
            }

            checkPosition = new Position(3220, 10086 , 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 3220 && actor.getY() == 10088 || actor.getX() == 3220 && actor.getY() == 10084)
                    return true;
            }

            checkPosition = new Position(3241, 10145 , 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 3239 && actor.getY() == 10145 || actor.getX() == 3243 && actor.getY() == 10145)
                    return true;
            }

            checkPosition = new Position(3200, 10136 , 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 3202 && actor.getY() == 10136 || actor.getX() == 3198 && actor.getY() == 10136)
                    return true;
            }

            checkPosition = new Position(3202, 10196 , 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 3200 && actor.getY() == 10196 || actor.getX() == 3204 && actor.getY() == 10196)
                    return true;
            }

            checkPosition = new Position(3180, 10209 , 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 3180 && actor.getY() == 10211 || actor.getX() == 3180 && actor.getY() == 10207)
                    return true;
            }

        }
        if (actor.isWithinDistance(strategy.getTile(), 3)) {
            Position checkPosition = new Position(3191, 3415, 1);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                return true;
            }

            checkPosition = new Position(3367, 2977, 1);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                return true;
            }

        }
        if (actor.isWithinDistance(strategy.getTile(), 6)) {
            Position checkPosition = new Position(3252, 3179, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 3246 && actor.getY() == 3179)
                    return true;
            }

        }

        if (actor.isWithinDistance(strategy.getTile(), 6)) {
            Position checkPosition = new Position(3252, 3179, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 3259 && actor.getY() == 3179)
                    return true;
            }

        }

        if (actor.isWithinDistance(strategy.getTile(), 6)) {
            Position checkPosition = new Position(3252, 3179, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 3258 && actor.getY() == 3176)
                    return true;
            }

        }

        /*
        * Mutant tarn passage entrance to fix "I can't reach that!"
         */
        if (actor.isWithinDistance(strategy.getTile(), 2)) {
            Position checkPosition = new Position(3186, 4631, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 3186 && actor.getY() == 4632)
                    return true;
            }
        }
        if (actor.isWithinDistance(strategy.getTile(), 2)) {
            Position checkPosition = new Position(3186, 4611, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 3186 && actor.getY() == 4612)
                    return true;
            }
        }
        if (actor.isWithinDistance(strategy.getTile(), 2)) {
            Position checkPosition = new Position(3186, 4627, 0);
            if (strategy.getTile().getX() == checkPosition.getX() && strategy.getTile().getY() == checkPosition.getY()) {
                if (actor.getX() == 3186 && actor.getY() == 4626)
                    return true;
            }
        }
        return false;
    }

    private void executeIfInDistance(boolean force) {
        if (inDistance()) {
            execute();
        } else {
            checkUnreachable(force);
        }
    }

    private void checkUnreachable(boolean force) {
        if (force || destination == null || actor.getPosition().equals(destination)) {

            executed = true;
            Agent target = actor.getCombat().getTarget();
            if (target != null) {
                actor.getCombat().submit(new TargetIsOutOfReach(target));
            }

            actor.messageIfPlayer("I can't reach that!");
            actor.getMotion().clearSteps();
            actor.getMotion().cancelTask();

            if (actor.isNpc())
                actor.setPositionToFace(strategy.getTile());
            TaskManager.submit(1, () -> {
                if (!hasPolicy(Policy.NO_RESET_ENTITY_INTERACTION_ON_UNREACHABLE)) {
                    if (actor.getInteractingEntity() != null)
                        actor.setEntityInteraction(null);
                }
            });
        }
    }


    /**
     * Executes the action associated with this class.
     */
    private void execute() {
        if (!hasPolicy(Policy.NO_RESET_ENTITY_INTERACTION_ON_EXECUTION)) {
            actor.setEntityInteraction(null);
        }

        //actor.getMotion().clearSteps();
        actor.getMotion().removeTask(this);
        executed = true;
        if (finalizedAction != null) {
            if (actionDelay == 0 || !actor.getMotion().isMoving())
                finalizedAction.execute();
            else
                TaskManager.submit(actionDelay, finalizedAction::execute);
        }
    }

    private boolean hasPolicy(Policy policy) {
        return policies != null && policies.contains(policy);
    }

    public enum Policy {
        RECALCULATE_IF_TARGET_MOVES,
        EXECUTE_WHEN_IN_DISTANCE,
        NO_RESET_ENTITY_INTERACTION_ON_EXECUTION,
        NO_RESET_ENTITY_INTERACTION_ON_UNREACHABLE,
        EXECUTE_ON_PARTIAL,
        EXECUTE_ON_LINE_OF_SIGHT,
        ALLOW_UNDER
    }
}