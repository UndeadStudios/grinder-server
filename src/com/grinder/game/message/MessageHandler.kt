package com.grinder.game.message

import com.grinder.game.entity.agent.player.Player

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   28/11/2019
 * @version 1.0
 */
abstract class MessageHandler<M : Message> {

    abstract fun handle(player: Player, message: M)

}