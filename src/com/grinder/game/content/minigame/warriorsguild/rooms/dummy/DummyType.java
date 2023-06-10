package com.grinder.game.content.minigame.warriorsguild.rooms.dummy;

import com.grinder.util.ObjectID;

/**
 * @author L E G E N D
 */
public enum DummyType {
    BROWN(ObjectID.DUMMY_ACCURATE_BROWN, DummyAttackStyle.ACCURATE),
    GRAY(ObjectID.DUMMY_SLASH_GRAY, DummyAttackStyle.SLASH),
    RED(ObjectID.DUMMY_AGGRESSIVE_RED, DummyAttackStyle.AGGRESSIVE),
    WHITE(ObjectID.DUMMY_CONTROLLED_WHITE, DummyAttackStyle.CONTROLLED),
    BLUE(ObjectID.DUMMY_CRUSH_BLUE, DummyAttackStyle.CRUSH),
    GREEN(ObjectID.DUMMY_STAB_GREEN, DummyAttackStyle.STAB),
    YELLOW(ObjectID.DUMMY_DEFENSIVE_YELLOW, DummyAttackStyle.DEFENSIVE);

    private final int objectId;
    private final DummyAttackStyle attackStyle;

    DummyType(int objectId, DummyAttackStyle attackStyle) {
        this.objectId = objectId;
        this.attackStyle = attackStyle;
    }

    public int getObjectId() {
        return objectId;
    }

    public DummyAttackStyle getAttackStyle() {
        return attackStyle;
    }
}
