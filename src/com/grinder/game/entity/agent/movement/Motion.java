package com.grinder.game.entity.agent.movement;

import com.grinder.game.collision.Collisions;
import com.grinder.game.collision.TileFlags;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.AgentUtil;
import com.grinder.game.entity.agent.movement.task.ImpairMovement;
import com.grinder.game.entity.agent.movement.task.MovementTask;
import com.grinder.game.entity.agent.movement.task.impl.FollowAgentTask;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.Area;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.task.TaskManager;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * A {@link StepQueue} implementation used to manage the motion of the {@link #actor}.
 *
 * This class controls the {@link #actor} movement and following of an optional {@link #target}.
 * Also, this class provides functionality for executing a (prioritized) {@link MovementTask}.
 *
 * @author Graham Edgecombe
 * @author Gabbe
 * @author Stan van der Bend
 */
public abstract class Motion<T extends Agent> extends StepQueue implements FollowController, MovementController {

    public static final int MAX_TARGET_FOLLOW_DISTANCE = 50;

    final T actor;

    Agent target;

    private MovementTask<T> task = null;
    private MovementStatus status = MovementStatus.NONE;
    private Position previousPosition;
    private int forcedTraceTicks;
    private boolean forcedTracing = false;

    public Position getNextPosition() {
        if (skipNextSequence() || peek().isEmpty()) {
            return actorPosition();
        }
        Step step = peek().get();
        return actorPosition().copy().add(step.getDirection().getX(), step.getDirection().getY());
    }

    /**
     * Creates a walking queue for the specified character.
     *
     * @param actor The character.
     */
    Motion(T actor) {
        this.actor = actor;
    }

    @Override
    public void onMovementSequence() {
        forcedTracing = forcedTraceTicks > 0;
        if (forcedTracing)
            forcedTraceTicks--;
    }

    @Override
    public void onTargetOutOfReach(Agent target) {

        actor.debug("Target is out of reach!");

        if (actor.getCombat().isInCombatWith(target))
            actor.getCombat().reset(true);

        resetTargetFollowing();
        clearSteps();
    }

    @Override
    public boolean skipNextSequence() {
        return task != null && task.skipNextSequence();
    }

    @Override
    public boolean bypassClippingCheck() {
        return task != null && task.bypassClippingCheck();
    }

    @Override
    public boolean canMove() {

        if (forcedTracing)
            return true;

        if (actor.hasPendingTeleportUpdate())
            return false;

        if (movementDisabled())
            return false;

        if (AgentUtil.isFrozen(actor) || AgentUtil.isStunned(actor))
            return false;

        return true;
    }

    public boolean movementDisabled() {
        return !forcedTracing && status == MovementStatus.DISABLED;
    }

    @Override
    public Position actorPosition() {
        return actor.getPosition();
    }

    @Override
    public int actorSize() {
        return actor.getSize();
    }

    /**
     * This function aims is to execute some path-finding algorithm to be executed by the {@link #sequenceNextStep()}.
     *
     * @param targetPosition the {@link Position} this {@link #actor} is aiming to reach.
     */
    public abstract void traceTo(Position targetPosition);

    /**
     * Sequences {@link Motion} of the {@link #actor} given the {@link #actor} is able to move.
     */
    public void sequenceMovement() {

        if (actor.getTeleportPosition() != null){
            final Position teleportPosition = actor.getTeleportPosition();
            removeCollision();
            clearSteps();
            actor.setPosition(teleportPosition.clone());
            actor.setPendingTeleportUpdate(true);
            actor.setTeleportPosition(null);
            updateCollision();
            AreaManager.checkAreaChanged(actor);
            return;
        }

        if (skipNextSequence())
            return;

        if (!canMove())
            return;

        sequenceNextStep();

        if (task != null && !forcedTracing) {
            if (task.isFinished()) {
                cancelTask();
                task = null;
            } else
                task.sequenceMovement();
        }
    }

    public void sequence() {
        if (skipNextSequence())
            return;

        if (!movementDisabled() && !canMove())
            return;

        if (task != null && !forcedTracing)
            task.sequence();
    }

    protected void sequenceNextStep() {
        final Optional<Step> walkNode = peek();

        if (walkNode.isPresent()) {
            final Step walkStep = walkNode.get();
            Position startPosition = actor.getPosition();
            Position previousPosition = actor.getPosition();
            final Direction walkingDirection = walkStep.getDirection();
            if (!walkStep.isForced() && actor.traversal.blocked(startPosition, walkingDirection)) {
                actor.getMotion().clearSteps();
            } else if (walkStep.isForced() || !actor.traversal.blocked(startPosition, walkingDirection)) {
                poll();
                actor.setPosition(actor.getPosition().clone().add(walkStep.getDirection().getX(), walkStep.getDirection().getY()));

                actor.setWalkingDirection(walkingDirection);
                actor.setLastFacingDirection(walkingDirection);

                final Optional<Step> runNode = runToNextStep() ? peek() : Optional.empty();
                if (runNode.isPresent()) {

                    final Step runStep = runNode.get();
                    final Direction runDirection = runStep.getDirection();
                    if (!runStep.isForced() && actor.traversal.blocked(actor.getPosition(), runDirection)) {
                        actor.getMotion().clearSteps();
                    } else if (runStep.isForced() || !actor.traversal.blocked(actor.getPosition(), runDirection)) {
                        final int mask = actor.getWalkingDirection().getClientRunMask(runDirection);

                        if (mask != -1) {

                            poll();

                            previousPosition = actor.getPosition();

                            actor.setPosition(actor.getPosition().clone().add(runStep.getDirection().getX(), runStep.getDirection().getY()));
                            actor.setRunningDirection(runDirection);
                            actor.setLastFacingDirection(runDirection);

                            postSequencedRunStep();
                        }
                    }
                }
                Optional.ofNullable(previousPosition).ifPresent(this::postStepSequence);
                postSequence();
                removeCollision(startPosition);
                updateCollision();
            }
        }
    }

    public void removeCollision() {
        removeCollision(actor.getPosition());
    }

    public void removeCollision(Position position) {
        for (int x = 0; x < actor.getWidth(); x++) {
            for (int y = 0; y < actor.getHeight(); y++) {
                Collisions.remove(position.getX() + x, position.getY() + y, position.getZ(), actor.unpassable ? TileFlags.UNWALKABLE | TileFlags.ENTITY : TileFlags.ENTITY);
            }
        }
    }

    public void updateCollision() {
        updateCollision(actor.getPosition());
    }

    public void updateCollision(Position position) {
        for (int x = 0; x < actor.getWidth(); x++) {
            for (int y = 0; y < actor.getHeight(); y++) {
                Collisions.add(position.getX() + x, position.getY() + y, position.getZ(), actor.unpassable ? TileFlags.UNWALKABLE | TileFlags.ENTITY : TileFlags.ENTITY);
            }
        }
    }

    /**
     * Impairs the movement of the character for the specified amount of time.
     *
     * @param seconds the amount of seconds for which the {@link #actor} is unable to move.
     * @see ImpairMovement
     */
    public void impairMovement(int seconds) {

        update(MovementStatus.DISABLED);
        clearSteps();

        TaskManager.submit(new ImpairMovement(this, seconds));
    }

    public boolean hasTask() {
        return task != null;
    }

    public void cancelTask() {
        if (task != null) {
            task.cancel();
        }
    }

    public void start(@NotNull MovementTask<T> task, boolean force) {
        if (this.task != null) {
            if (force || this.task.compareTo(task) <= 0) {
                cancelTask();
                actor.debug("Starting a new task, canceled old task!");
            } else {
                actor.debug("Could not start new task, old task has priority!");
                return;
            }
        }
        this.task = task;
        this.task.start();
    }

    public <A extends Agent> void removeTask(MovementTask<A> task) {
        if (this.task == task) {
            this.task = null;
        }
    }

    public boolean didSequenceStep() {
        return actor.getWalkingDirection() != Direction.NONE || actor.getRunningDirection() != Direction.NONE;
    }

    public Optional<MovementTask<T>> movementTask() {
        return Optional.ofNullable(task);
    }

    public Motion<T> update(MovementStatus status) {
        this.status = status;
        return this;
    }

    public boolean isFollowing(Agent agent) {
        return target == agent;
    }

    public boolean hasFollowTarget() {
        return target != null;
    }

    public boolean runToNextStep() {
        return actor.getRunningDirection() != Direction.NONE;
    }

    public boolean isRunning() {
        return isMoving() && runToNextStep();
    }

    public boolean isMoving() {
        return actor.getWalkingDirection() != Direction.NONE;
    }

    public Agent getTarget() {
        return target;
    }

    public void setTarget(Agent target) {
        this.target = target;
    }

    public void followTarget(Agent target) {
        if (this.target == target) {
            return;
        }
        followTarget(target, false, false);
    }

    public void followTarget(Agent target, boolean follow, boolean combat) {
        this.target = target;
        if (target == null) {
            cancelTask();
            actor.debug("motion -> set target to null");
        } else {
            start(new FollowAgentTask(actor, target, follow, combat), true);
            actor.debug("motion -> targeting " + target + "");
        }
    }

    void postSequencedRunStep() {
    }

    void postSequence() {

    }

    void postStepSequence(Position previousPosition) {
        setPreviousPosition(previousPosition);
    }

    void setPreviousPosition(Position previousPosition) {
        this.previousPosition = previousPosition;
    }

    public void resetTargetFollowing() {
        target = null;
        actor.setEntityInteraction(null);
        actor.getMotion().followTarget(null);
        actor.debug("motion -> reset target following");
    }

    public void pauseTasks(int pauseTasksTicks) {
        if (pauseTasksTicks > 0)
            forcedTracing = true;
        this.forcedTraceTicks = pauseTasksTicks;
    }
}