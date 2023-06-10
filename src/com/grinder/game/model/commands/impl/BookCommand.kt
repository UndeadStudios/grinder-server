package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.commands.DeveloperCommand

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   28/12/2019
 * @version 1.0
 */
class BookCommand : DeveloperCommand() {

    override fun execute(player: Player, command: String, parts: Array<out String>?) {

        val sender = player.packetSender
        sender.sendInterface(837)
        sender.clearInterfaceText(843, 864)

        val text = arrayOf(
                "BLAAA", "BLALBLBALGD", "BDOSKGPKDS", "boooooo", ":) test"
        )
        text.forEachIndexed { index, string ->
            sender.sendString(834+index, string)
        }

//        sender.sendString()
    }
}