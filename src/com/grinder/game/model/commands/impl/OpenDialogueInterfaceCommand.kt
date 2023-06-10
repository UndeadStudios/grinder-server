package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.commands.DeveloperCommand

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   07/05/2020
 * @version 1.0
 */
class OpenDialogueInterfaceCommand : DeveloperCommand() {

    override fun execute(player: Player, command: String, parts: Array<out String>) {

        val id = parts[1].toIntOrNull()?:return
        val sender = player.packetSender
        sender.sendChatboxInterface(id)

    }
}