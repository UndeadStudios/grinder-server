package com.grinder.game.model;

import com.grinder.util.Priority;

/**
 * This file manages an entity's animation which should be performed.
 *
 * @author relex lawl
 */
public class Animation {

    public static final Animation DEFAULT_RESET_ANIMATION = new Animation(65535);
    public static final Animation AFK_ANIMATION = new Animation(4117);

    private final int id;

    private final int delay;

    private final int blockNextAnimationTicks;

    private final Priority priority;

    /**
     * Animation constructor for entity to perform.
     *
     * @param id    The id of the animation entity should perform.
     * @param delay The delay which to wait before entity performs animation.
     * @param priority The priority of the animation entity should perform.
     */
    public Animation(int id, int delay, int blockNextAnimationTicks, Priority priority) {
        this.id = id;
        this.delay = delay;
        this.priority = priority;
        this.blockNextAnimationTicks = blockNextAnimationTicks;
    }

    public Animation(int id, int delay, Priority priority) {
        this(id, delay, 0, priority);
    }

    public Animation(int id, Priority priority) {
        this(id, 0, priority);
    }

    public Animation(int id, int delay) {
        this(id, delay, Priority.LOW);
    }

    public Animation(int id) {
        this(id, 0);
    }

    public int getId() {
        return id;
    }

    public int getDelay() {
        return delay;
    }

    public int getBlockNextAnimationTicks() {
        return blockNextAnimationTicks;
    }

    public Priority getPriority() {
        return priority;
    }

}
