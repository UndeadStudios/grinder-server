package com.grinder.game.model.passages;

/**
 * @author L E G E N D
 */
public enum PassageState {
    OPENED,
    CLOSED;

    public PassageState opposite() {
        return this == OPENED ? CLOSED : OPENED;
    }
}
