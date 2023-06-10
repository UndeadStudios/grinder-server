package com.grinder.game.content.minigame.chamberoxeric.room.vespula;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public enum VespulaState {

    AIR_BORNE(7530),

    GROUND(7532),

    ENRANGED(7531),

    ;

    public int id;

    VespulaState(int id) {
        this.id = id;
    }
}
