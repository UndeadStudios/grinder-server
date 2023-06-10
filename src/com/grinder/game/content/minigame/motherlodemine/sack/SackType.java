package com.grinder.game.content.minigame.motherlodemine.sack;

/**
 * @author L E G E N D
 * @date 2/11/2021
 * @time 2:35 PM
 * @discord L E G E N D#4380
 */
public enum SackType {
    NORMAL(81),
    UPGRADED(162),
    MEMBER(243);

    SackType(int size) {
        this.size = size;
    }

    private final int size;

    public int getSize() {
        return size;
    }
}
