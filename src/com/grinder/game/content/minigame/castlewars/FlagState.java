package com.grinder.game.content.minigame.castlewars;

public enum FlagState {
    SAFE(0), TAKEN(1), DROPPED(2);

    private final int stateID;

    private FlagState(int stateID) {
        this.stateID = stateID;
    }

    public int getStateID() {
        return stateID;
    }
}
