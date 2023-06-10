package com.grinder.game.content.collectionLog;

import com.google.common.collect.ImmutableList;

public enum CLTabType {
    BOSSES(64604),
    RAIDS(64605),
    CLUES(64606),
    MINIGAMES(64607),

    OTHER(64608);

    private final int buttonId;

    CLTabType(int buttonId) {
        this.buttonId = buttonId;
    }

    public int getButtonId() {
        return buttonId;
    }

    public static final ImmutableList<CLTabType> COLLECTION_LOG_TABS = ImmutableList.copyOf(CLTabType.values());

    public static ImmutableList<CLData> getTabData(CLTabType tab) {
        switch (tab) {
            case RAIDS:
                return CLData.LOG_DATA_RAIDS;
            case CLUES:
                return CLData.LOG_DATA_CLUES;
            case MINIGAMES:
                return CLData.LOG_DATA_MINIGAMES;
            case OTHER:
                return CLData.LOG_DATA_OTHER;
            default:
                return CLData.LOG_DATA_BOSSES;
        }
    }
}