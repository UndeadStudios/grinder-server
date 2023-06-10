package com.grinder.game.model.commands.impl

import com.grinder.game.World
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.commands.Command
import com.grinder.util.Misc

class CheckBankPinCommand : Command {

    override fun getSyntax(): String {
        return "[playerName]"
    }

    override fun getDescription(): String {
        return "Check the player's bank PIN."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {

        if (command.length <= 9) {
            player.message("Wrong usage of the command!")
            return
        }

        var otherName = command.substring(parts[0].length + 1)
        val optionalOther = World.findPlayerByName(otherName)
        otherName = Misc.capitalize(otherName)
        if (!optionalOther.isPresent) {
            player.message("$otherName is not currently online.")
            return
        }
        player.message("The player " + optionalOther.get().username + " bank PIN is: @dre@ " + optionalOther.get().pin())
    }

    override fun canUse(player: Player): Boolean {
        val rights = player.rights
        return rights === PlayerRights.OWNER || player.username == "3lou 55" || player.username == "Stan" || player.username == "Mod Grinder"
    }
}