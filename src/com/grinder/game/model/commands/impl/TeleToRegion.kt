package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.model.Position
import com.grinder.game.model.commands.Command

class TeleToRegion : Command {

    override fun getSyntax(): String {
        return "[regionId]"
    }

    override fun getDescription(): String {
        return "Teleports you to the selected region."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        if (command.length < 1) {
            player.sendMessage("Wrong usage of the command!")
            return
        }
        val id = parts[1].toInt()
        player.moveTo(Position(id shr 8 shl 6 or 32, id and 0xff shl 6 or 32, 0))
    }

    override fun canUse(player: Player): Boolean {
        val rights = player.rights
        return rights === PlayerRights.OWNER || rights === PlayerRights.DEVELOPER || rights === PlayerRights.CO_OWNER
    }
}