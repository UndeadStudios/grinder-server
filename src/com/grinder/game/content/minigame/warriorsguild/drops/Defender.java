package com.grinder.game.content.minigame.warriorsguild.drops;

import com.grinder.game.definition.ItemDefinition;

import static com.grinder.util.ItemID.*;

/**
 * @author L E G E N D
 */
public enum Defender {
    BRONZE(BRONZE_DEFENDER, 10),
    IRON(IRON_DEFENDER, 20),
    STEEL(STEEL_DEFENDER, 30),
    BLACK(BLACK_DEFENDER, 40),
    MITHRIL(MITHRIL_DEFENDER, 50),
    ADAMANT(ADAMANT_DEFENDER, 55),
    RUNE(RUNE_DEFENDER, 60),
    DRAGON(DRAGON_DEFENDER, 150),
    AVERNIC_HILT(AVERNIC_DEFENDER_HILT, 250);

    private final int id;
    private final int roll;

    Defender(int id, int roll) {
        this.id = id;
        this.roll = roll;
    }

    public int getId() {
        return id;
    }

    public int getRoll() {
        return roll;
    }

    public String getName() {
        return ItemDefinition.forId(id).getName();
    }

    public Defender getNext() {
        if (this == AVERNIC_HILT) {
            return this;
        }
        return Defender.values()[ordinal() + 1];
    }

    public Defender getPrevious() {
        if (this == BRONZE) {
            return BRONZE;
        }
        return Defender.values()[ordinal() - 1];
    }

    public static Defender forId(int id) {
        for (var defender : values()) {
            if (defender.getId() == id) {
                return defender;
            }
        }
        return null;
    }
}
