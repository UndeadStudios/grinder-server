package com.grinder.game.content.minigame.warriorsguild.drops;

import com.grinder.util.ItemID;

import java.util.Random;

/**
 * @author L E G E N D
 */
public enum Misc {
    FIGHTER_TORSO(ItemID.FIGHTER_TORSO, 120),
    HARDENED_FIGHTER_TORSO(15394, 280),
    ARMY_FIGHTER_TORSO(15396, 650);

    private final int id;
    private final int roll;
    private static final Random random = new Random();

    Misc(int id, int roll) {
        this.id = id;
        this.roll = roll;
    }

    public static int random(int range) {
        return (int) (java.lang.Math.random() * (range + 1));
    }

    public int getId() {
        return id;
    }

    public int getRoll() {
        return roll;
    }
}
