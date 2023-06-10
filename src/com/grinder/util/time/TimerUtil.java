package com.grinder.util.time;

import com.grinder.game.entity.agent.player.Player;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-06-08
 */
public class TimerUtil {

    public static boolean hasActiveFireProtection(final Player player){
        return !player.getCombat().getFireImmunityTimer().finished();
    }

    public static boolean hasActiveSuperFireProtection(final Player player){
        return !player.getCombat().getSuperFireImmunityTimer().finished();
    }

}
