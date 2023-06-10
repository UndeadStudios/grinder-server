package com.grinder.game.content.minigame.chamberoxeric.room.vasanistirio;

import com.grinder.game.model.Position;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public enum VasaNistirioCrystal {

    SOUTH_EAST(new Position(3287, 5285)),
    SOUTH_WEST(new Position(3270, 5285)),

    NORTH_WEST(new Position(3270, 5303)),
    NORTH_EAST(new Position(3287, 5303)),

    ;

    public Position position;

    VasaNistirioCrystal(Position position) {
        this.position = position;
    }

    public static final VasaNistirioCrystal[] VALUES = values();
}
