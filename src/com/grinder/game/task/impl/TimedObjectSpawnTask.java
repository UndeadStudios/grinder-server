package com.grinder.game.task.impl;

import java.util.Optional;

import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.util.Executable;
import com.grinder.game.task.Task;

/**
 * A {@link Task} implementation which spawns a {@link GameObject}
 * and then despawns it after a period of time.
 *
 * @author Professor Oak
 */
public class TimedObjectSpawnTask extends Task {

    /**
     * The temporary {@link GameObject}.
     * <p>
     * This object will be deregistered once the task has finished executing.
     */
    private final GameObject temp;

    /**
     * The amount of ticks this task has.
     */
    private final int ticks;

    /**
     * The action which should be executed
     * once this task has finished.
     */
    private final Optional<Executable> action;

    /**
     * The current tick counter.
     */
    private int tick = 0;

    /**
     * Constructs this task to spawn an object and then delete it
     * after a period of time.
     *
     * @param temp
     * @param ticks
     */
    public TimedObjectSpawnTask(GameObject temp, int ticks, Optional<Executable> action) {
        super(1, true);
        this.temp = temp;
        this.action = action;
        this.ticks = ticks;
    }

    /**
     * Executes this task.
     */
    @Override
    public void execute() {
        if (tick == 0) {
            ObjectManager.add(temp, true);
        } else if (tick >= ticks) {
            stop();
        }
        tick++;
    }
    @Override
    public void stop() {
        ObjectManager.remove(temp, true);
        action.ifPresent(Executable::execute);
        running = false;
    }
    
}
