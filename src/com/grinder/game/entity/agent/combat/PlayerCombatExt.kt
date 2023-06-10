package com.grinder.game.entity.agent.combat

import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.EffectTimer

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   11/04/2020
 * @version 1.0
 */

fun PlayerCombat.isTeleblocked(message: Boolean = false): Boolean
{
    if (!teleBlockTimer.finished()){
        if (message)
            actor.message("A magical spell is blocking you from teleporting.", 1)
        return true
    }
    return false
}
fun PlayerCombat.removeTeleblock(update: Boolean = true) {
    teleBlockTimer.stop()
    if(update)
        actor.packetSender.sendEffectTimer(0, EffectTimer.TELE_BLOCK)
}