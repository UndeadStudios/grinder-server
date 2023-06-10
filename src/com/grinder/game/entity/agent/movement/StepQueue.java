package com.grinder.game.entity.agent.movement;

import com.grinder.game.collision.CollisionManager;
import com.grinder.game.entity.agent.movement.pathfinding.PathFinder;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Direction;
import com.grinder.game.model.ForceMovement;
import com.grinder.game.model.Position;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-03-21
 */
public abstract class StepQueue {

    private static final Direction[] PREFERRED_SURROUNDING_DIRECTIONS = new Direction[]{Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH};
    private static final int MAXIMUM_SIZE = 100;

    public final Deque<Step> steps = new ArrayDeque<>();

    private Position lastPositionToFace;
    private Direction lastDirectionToFace = Direction.NONE;
    private Position markedDestination;

    public abstract boolean canMove();

    public abstract Position actorPosition();

    public abstract int actorSize();

    public void clearSteps() {
//        Thread.dumpStack();
        steps.clear();
    }

    /**
     * Adds the first step to the queue, attempting to connect the server and client
     * position by looking at the previous queue.
     *
     * @param initialStep The first step.
     */
    public void clearAndEnqueue(Position initialStep) {
        clearSteps();

        enqueueStepsTo(initialStep);
    }

    public void enqueuePathToWithoutCollisionChecks(int x, int y) {
        enqueueStepsTo(new Position(x, y), true);
    }

    public void enqueueStep(int deltaX, int deltaY) {
        enqueueStepsTo(actorPosition().copy().move(deltaX, deltaY));
    }

    public void enqueueStepAwayWithCollisionCheck(){
        for(Direction direction : PREFERRED_SURROUNDING_DIRECTIONS){
            if(!isBlocked(direction)){
                enqueueStep(direction);
                return;
            }
        }
    }

    public void enqueueStepsTo(Position position) {
        enqueueStepsTo(position, false);
    }

    /**
     * Use {@link PathFinder} or {@link Player#setForceMovement(ForceMovement)}
     */
    @Deprecated
    public void enqueueStepsTo(Position position, boolean force) {

        if (!canMove())
            return;

        final Step last = lastNode();

        final int x = position.getX();
        final int y = position.getY();

        final Position lastPosition = Optional.ofNullable(last.position).orElse(actorPosition().clone());
        int deltaX = x - lastPosition.getX();
        int deltaY = y - lastPosition.getY();

        final int totalSteps = Math.max(Math.abs(deltaX), Math.abs(deltaY));

        for (int step = 0; step < totalSteps; step++) {

            if (steps.size() >= MAXIMUM_SIZE)
                return;

            deltaX = deltaX > 0 ? deltaX - 1 : deltaX < 0 ? deltaX + 1 : 0;
            deltaY = deltaY > 0 ? deltaY - 1 : deltaY < 0 ? deltaY + 1 : 0;

            queue(x - deltaX, y - deltaY, position.getZ(), force);
        }
    }

    public void markDestination(Position position){
        markedDestination = position;
    }

    public int stepCount(){
        return steps.size();
    }

    public boolean completed() {
        return steps.size() == 0;
    }

    public boolean notCompleted() {
        return !steps.isEmpty();
    }

    public Position getLastPositionToFace() {
        return lastPositionToFace;
    }

    Optional<Step> poll(){
        return Optional.ofNullable(steps.poll()).filter(node -> node.direction != Direction.NONE);
    }

    public Optional<Step> peek(){
        return Optional.ofNullable(steps.peek());
    }

    private void queue(int x, int y, int heightLevel, boolean force) {
        final Step last = lastNode();
        final Position lastPosition = Optional.ofNullable(last.position).orElse(actorPosition().clone());
        final int deltaX = x - lastPosition.getX();
        final int deltaY = y - lastPosition.getY();
        final Direction direction = Direction.fromDeltas(deltaX, deltaY);

        if (direction != Direction.NONE) {
            steps.add(new Step(new Position(x, y, heightLevel), direction, force));
        }

        lastDirectionToFace = direction;
        lastPositionToFace = (new Position(x + (direction.getDirectionDelta()[0]), y + (direction.getDirectionDelta()[1]), heightLevel));
    }

    public void queueHead(Direction direction) {
        if (direction != Direction.NONE) {
            steps.addFirst(new Step(null, direction, false));
        }
    }

    public void queue(Direction direction, Boolean shouldForce) {
        if (direction != Direction.NONE) {
            steps.add(new Step(null, direction, shouldForce));
        }
    }

    public void setLastDirection(Direction direction) {
        lastDirectionToFace = direction;
    }

    private void enqueueStep(Direction direction){
        enqueueStep(direction.getX(), direction.getY());
    }

    private boolean isBlocked(Direction direction) {

        final Position destination = actorPosition().clone().move(direction);

        if(actorPosition().getZ() == -1 && destination.getZ() == -1)
            return false;

        return !CollisionManager.canMove(actorPosition(), destination, actorSize(), actorSize());
    }

    private Step lastNode() {
        final Position actor = actorPosition().clone();
        return Optional.ofNullable(steps.peekLast()).orElse(new Step(actor, Direction.NONE, false));
    }

    public Position getMarkedDestination() {
        return markedDestination;
    }

    public Direction getLastDirectionToFace() {
        return lastDirectionToFace;
    }

    public static final class Step {

        private final Position position;
        private final Direction direction;
        private final boolean force;

        Step(Position position, Direction direction, boolean force) {
            this.position = position;
            this.direction = direction;
            this.force = force;
        }

        public Position getPosition() {
            return position;
        }

        public Direction getDirection() {
            return direction;
        }

        public boolean isForced() {
            return force;
        }

        @Override
        public String toString() {
            return Step.class.getName() + " [direction=" + direction + ", position=" + position + ", force=" + force + "]";
        }
    }

}
