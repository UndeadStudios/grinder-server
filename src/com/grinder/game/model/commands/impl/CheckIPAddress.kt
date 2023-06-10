package com.grinder.game.model.commands.impl

import com.grinder.game.World
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.commands.Command
import com.grinder.util.Misc

class CheckIPAddress : Command {

    override fun getSyntax(): String {
        return "[playerName]"
    }

    override fun getDescription(): String {
        return "Check the player's IP address."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        var targetName = command.substring(parts[0].length + 1)
        val optionalTarget = World.findPlayerByName(targetName)
        targetName = Misc.capitalize(targetName)
        if (optionalTarget.isEmpty) {
            player.message("$targetName is not currently online.")
            return
        }
        if (optionalTarget.get().username.toLowerCase() == "3lou 55" || optionalTarget.get().username.toLowerCase() == "mod grinder") {
            player.message("Your command has been denied by the server.")
            return
        }
        player.message("The player " + optionalTarget.get().username + " is connected from the IP address " + optionalTarget.get().hostAddress)
    }

    override fun canUse(player: Player): Boolean {
        val rights = player.rights
        return rights === PlayerRights.OWNER || rights === PlayerRights.DEVELOPER || rights === PlayerRights.CO_OWNER || rights === PlayerRights.ADMINISTRATOR/*
                || player.username == "Hellmage"*/
    }
}