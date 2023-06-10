package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.model.Position
import com.grinder.game.model.commands.Command

class TeleToLocation : Command {

    override fun getSyntax(): String {
        return "[x,y,z]"
    }

    override fun getDescription(): String {
        return "Teleports you to the selected coordinates."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        if (command.length <= 5) {
            player.sendMessage("Wrong usage of the command!")
            return
        }
        val x = parts[1].toInt()
        val y = parts[2].toInt()
        var z = 0
        if (parts.size == 4) {
            z = parts[3].toInt()
        }
        player.moveTo(Position(x, y, z))
    }

    override fun canUse(player: Player): Boolean {
        val rights = player.rights
        return rights === PlayerRights.OWNER || rights === PlayerRights.DEVELOPER || rights === PlayerRights.CO_OWNER
    }
}