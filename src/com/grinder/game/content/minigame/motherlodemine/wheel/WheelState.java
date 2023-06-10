package com.grinder.game.content.minigame.motherlodemine.wheel;

import com.grinder.util.ObjectID;

/**
 * @author L E G E N D
 * @date 2/11/2021
 * @time 2:00 AM
 * @discord L E G E N D#4380
 */
public enum WheelState {
    BROKEN(ObjectID.WATER_WHEEL_9, ObjectID.BROKEN_STRUT),
    RUNNING(ObjectID.WATER_WHEEL_8, ObjectID.STRUT);

    WheelState(int id, int strutId) {
        this.id = id;
        this.strutId = strutId;
    }

    private final int id;
    private final int strutId;

    public int getId() {
        return id;
    }

    public int getStrutId() {
        return strutId;
    }
}
