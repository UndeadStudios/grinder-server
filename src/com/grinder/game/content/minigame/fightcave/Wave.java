package com.grinder.game.content.minigame.fightcave;

import static com.grinder.game.content.minigame.fightcave.Constants.*;

/**
 * @author L E G E N D
 */
public enum Wave {
    _1(PRAYER),
    _2(PRAYER, PRAYER_2),
    _3(RECOIL),
    _4(RECOIL, PRAYER),
    _5(RECOIL, PRAYER, PRAYER_2),
    _6(RECOIL, RECOIL_2),

    _7(RANGER),
    _8(RANGER, PRAYER),
    _9(RANGER, PRAYER, PRAYER_2),
    _10(RANGER, RECOIL),
    _11(RANGER, RECOIL, PRAYER),
    _12(RANGER, RECOIL, PRAYER, PRAYER_2),
    _13(RANGER, RECOIL, RECOIL_2),
    _14(RANGER, RANGER_2),

    _15(MELEE),
    _16(MELEE, PRAYER),
    _17(MELEE, PRAYER, PRAYER_2),
    _18(MELEE, RECOIL),
    _19(MELEE, RECOIL, PRAYER),
    _20(MELEE, RECOIL, PRAYER, PRAYER_2),
    _21(MELEE, RECOIL, RECOIL_2),

    _22(MELEE, RANGER),
    _23(MELEE, RANGER, PRAYER),
    _24(MELEE, RANGER, PRAYER, PRAYER_2),
    _25(MELEE, RANGER, RECOIL),
    _26(MELEE, RANGER, RECOIL, PRAYER),
    _27(MELEE, RANGER, RECOIL, PRAYER, PRAYER_2),
    _28(MELEE, RANGER, RECOIL, RECOIL_2),
    _29(MELEE, RANGER, RANGER_2),

    _30(MELEE, MELEE),

    _31(MAGE),
    _32(MAGE, PRAYER),
    _33(MAGE, PRAYER, PRAYER_2),
    _34(MAGE, RECOIL),
    _35(MAGE, RECOIL, PRAYER),
    _36(MAGE, RECOIL, PRAYER, PRAYER_2),
    _37(MAGE, RECOIL, RECOIL_2),

    _38(MAGE, RANGER),
    _39(MAGE, RANGER, PRAYER),
    _40(MAGE, RANGER, PRAYER, PRAYER_2),
    _41(MAGE, RANGER, RECOIL),
    _42(MAGE, RANGER, RECOIL, PRAYER),
    _43(MAGE, RANGER, RECOIL, PRAYER, PRAYER_2),
    _44(MAGE, RANGER, RECOIL, RECOIL_2),
    _45(MAGE, RANGER, RANGER_2),

    _46(MAGE, MELEE),
    _47(MAGE, MELEE, PRAYER),
    _48(MAGE, MELEE, PRAYER, PRAYER_2),
    _49(MAGE, MELEE, RECOIL),
    _50(MAGE, MELEE, RECOIL, PRAYER),
    _51(MAGE, MELEE, RECOIL, PRAYER, PRAYER_2),
    _52(MAGE, MELEE, RECOIL, RECOIL_2),

    _53(MAGE, MELEE, RANGER),
    _54(MAGE, MELEE, RANGER, PRAYER),
    _55(MAGE, MELEE, RANGER, PRAYER, PRAYER_2),
    _56(MAGE, MELEE, RANGER, RECOIL),
    _57(MAGE, MELEE, RANGER, RECOIL, PRAYER),
    _58(MAGE, MELEE, RANGER, RECOIL, PRAYER, PRAYER_2),
    _59(MAGE, MELEE, RANGER, RECOIL, RECOIL_2),
    _60(MAGE, MELEE, RANGER, RANGER_2),

    _61(MAGE, MELEE, MELEE),

    _62(MAGE_ORANGE, MAGE),

    _JAD(JAD);

    private final int[] ids;

    Wave(int... ids) {
        this.ids = ids;
    }

    public int asInt(){
        return ordinal()+1;
    }
    public Wave getNextWave() {
        if (this == _JAD) {
            return _1;
        }
        return values()[ordinal() + 1];
    }

    public int[] getIds() {
        return ids;
    }
}
