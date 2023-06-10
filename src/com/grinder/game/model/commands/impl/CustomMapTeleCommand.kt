package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.model.Position
import com.grinder.game.model.commands.Command

class CustomMapTeleCommand : Command {

    override fun getSyntax(): String {
        return "[mapId]"
    }

    override fun getDescription(): String {
        return "Teleports you to the custom map ids."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        if (command.length <= 3) {
            player.sendMessage("Wrong usage of the command!")
            return
        }
        val mapId = parts[1].toInt()
        when {
            mapId == 1 -> player.moveTo(Position(2832, 4132, 0)) // F16
            mapId == 2 -> player.moveTo(Position(2280, 2911, 0)) // F16
            mapId == 3 -> player.moveTo(Position(2651, 4621, 0)) // RS
            mapId == 4 -> player.moveTo(Position(2852, 2586, 0)) // Stable (Casino)
            mapId == 5 -> player.moveTo(Position(2397, 2590, 0)) // Stable (AFK Island)
            mapId == 6 -> player.moveTo(Position(2272, 2596, 0)) // Stable (Boss Dungeon)
            mapId == 7 -> player.moveTo(Position(2145, 2587, 0)) // Stable (Member Zone)
            mapId == 8 -> player.moveTo(Position(2270, 2916, 0)) // Stable (Lava Island)
            mapId == 9 -> player.moveTo(Position(2961, 2602, 0)) // Stable (Pk Zone)
            mapId == 10 -> player.moveTo(Position(1, 1, 0))
            mapId == 11 -> player.moveTo(Position(1, 1, 0))
            mapId == 12 -> player.moveTo(Position(1, 1, 0))
            mapId == 13 -> player.moveTo(Position(1, 1, 0))
            mapId == 14 -> player.moveTo(Position(1, 1, 0))
            mapId == 15 -> player.moveTo(Position(1, 1, 0))
        }
    }

    override fun canUse(player: Player): Boolean {
        val rights = player.rights
        return rights === PlayerRights.OWNER || rights === PlayerRights.DEVELOPER
    }
}