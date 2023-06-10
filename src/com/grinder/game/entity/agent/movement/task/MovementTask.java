package com.grinder.game.entity.agent.movement.task;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.movement.MovementController;
import com.grinder.util.Priority;

import java.util.Optional;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-03-20
 */
public abstract class MovementTask<T extends Agent> implements MovementController, Comparable<MovementTask> {

    private final Priority priority;

    private boolean finished;

    private Runnable arrivalAction;

    protected final T actor;

    public MovementTask(Priority priority, T actor) {
        this.priority = priority;
        this.actor = actor;
    }

    public void start() { }

    @Override
    public void onMovementSequence() { }

    @Override
    public void sequenceMovement() { }

    public void sequence() { }

    @Override
    public boolean skipNextSequence() {
        return false;
    }

    @Override
    public boolean bypassClippingCheck() {
        return false;
    }

    @Override
    public int compareTo(MovementTask movementTask) {
        return priority.ordinal() - movementTask.priority.ordinal();
    }

    public void cancel(){
        stop();
    }

    public void stop(){
        finished = true;
    }

    public void setArrivalAction(Runnable arrivalAction) {
        this.arrivalAction = arrivalAction;
    }

    public boolean isActive(){
        return !finished;
    }

    public boolean isFinished() {
        return finished;
    }

    public Optional<Runnable> arrivalAction(){
        return Optional.ofNullable(arrivalAction);
    }

}
