package com.grinder.game.model;

import com.google.common.base.Preconditions;

public class ForceMovement {

    private final Position start;
    private final Position end;
    private final int startTick;
    private final int endTick;
    private final int direction;
    private final int animation;

    public ForceMovement(Position start,
                         Position end,
                         int delay,
                         int duration,
                         int direction,
                         int animation
    ) {

//        Preconditions.checkArgument(duration > 0, "Duration must be larger than 0.");
        this.start = start;
        this.end = end;
        this.direction = direction;
        this.animation = animation;
        startTick = delay;
        endTick = delay + duration;
    }

    public Position getStart() {
        return start;
    }

    public Position getEnd() {
        return end;
    }

    public int getStartTick() {
        return startTick;
    }

    public int getEndTick() {
        return endTick;
    }

    public int getDirection() {
        return direction;
    }

    public int getAnimation() {
        return animation;
    }
}
