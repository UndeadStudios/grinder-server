package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.commands.DeveloperCommand

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/12/2019
 * @version 1.0
 */
class ScreenFadeCommand : DeveloperCommand() {

    override fun execute(player: Player?, command: String?, parts: Array<out String>?) {
        player!!.packetSender.sendFadeScreen("test", 2, 6)
    }
}