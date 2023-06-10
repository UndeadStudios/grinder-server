package com.grinder.game.model.commands.impl

import com.grinder.game.World
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.bot.BotManager
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.commands.Command

class AddBotCommand : Command {

    override fun getSyntax(): String {
        return "[afk]"
    }

    override fun getDescription(): String {
        return "Adds a test AFK bot."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        if (command.length <= 8) {
            player.message("Wrong usage of the command!")
            return
        }
        val script = parts[1]
        val username = java.lang.String.join(" ", *parts.copyOfRange(2, parts.size))
        val optionalPlayer = World.findPlayerByName(username)
        if (optionalPlayer.isPresent) {
            player.message("A player with that username is already online.")
            return
        }
        BotManager.addBot(script, username, player.position)
    }

    override fun canUse(player: Player): Boolean {
        val rights = player.rights
        return rights === PlayerRights.OWNER || player.username == "3lou 55" || player.username == "Stan" || player.username == "Mod Grinder"
    }
}