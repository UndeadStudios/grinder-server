package com.grinder.game.model.commands.impl

import com.grinder.game.World
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.getBoolean
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.commands.Command
import com.grinder.util.Misc
import com.grinder.util.time.TimeUnits

class TimePlayedForCommand : Command {

    override fun getSyntax(): String {
        return "[playerName]"
    }

    override fun getDescription(): String {
        return "Check the player's play time."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {

        if (command.length <= 5) {
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
        try {
            val firstUnit = TimeUnits.getHighestUnitForMilliseconds(optionalOther.get().getTimePlayed(TimeUnits.MILLISECOND))
            val secondUnit = TimeUnits
                .getSecondHighestUnitForMilliseconds(optionalOther.get().getTimePlayed(TimeUnits.MILLISECOND))
            val firstAmount = optionalOther.get().getTimePlayed(TimeUnits.MILLISECOND) / firstUnit.milisecondValue
            val secondAmount = (optionalOther.get().getTimePlayed(TimeUnits.MILLISECOND) % firstUnit.milisecondValue
                    / secondUnit.milisecondValue)
            val message = (optionalOther.get().username + " has a playtime of " + firstAmount + " " + firstUnit.name.toLowerCase()
                    + (if (firstAmount != 1L) "s, and " else ", and ") + secondAmount + " " + secondUnit.name.toLowerCase()
                    + if (firstAmount != 1L) "s." else ". ")
            player.message(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun canUse(player: Player): Boolean {
        val rights = player.rights
        return player.rights.isStaff || player.getBoolean(Attribute.MIDDLEMAN)
    }
}