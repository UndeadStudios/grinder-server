package com.grinder.game.content.minigame.chamberoxeric.room.olm;

import com.grinder.game.content.minigame.warriorsguild.drops.Misc;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public enum OlmPhase {

    ACID,

    CRYSTAL,

    FLAME,

    ;

    public static final OlmPhase[] VALUES = values();

    public static OlmPhase getRandom() {
        return VALUES[Misc.random(VALUES.length-1)];
    }
}
