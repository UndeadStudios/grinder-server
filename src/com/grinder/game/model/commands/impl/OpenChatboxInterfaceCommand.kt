package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.model.commands.Command
import com.grinder.game.model.commands.DeveloperCommand

class OpenChatboxInterfaceCommand : DeveloperCommand() {

    override fun getSyntax(): String {
        return "[id]"
    }

    override fun getDescription(): String {
        return "Opens chatbox interface id."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        player.packetSender.sendChatboxInterface(parts[1].toInt())
    }
}