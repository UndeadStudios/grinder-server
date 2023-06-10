package com.grinder.game.content.minigame.warriorsguild.rooms.dummy;

import com.grinder.game.entity.agent.player.Player;

/**
 * @author L E G E N D
 */
public enum DummyAttackStyle {
    ACCURATE,
    AGGRESSIVE,
    DEFENSIVE,
    CONTROLLED,
    STAB,
    SLASH,
    CRUSH;


    public boolean isActive(Player player) {
        switch (this) {
            case STAB:
            case SLASH:
            case CRUSH:
                return player.getCombat().getFightType().getMode().ordinal() == ordinal() - 4;
            default:
                return ordinal() == player.getCombat().getFightType().getStyle().ordinal();
        }
    }
}
