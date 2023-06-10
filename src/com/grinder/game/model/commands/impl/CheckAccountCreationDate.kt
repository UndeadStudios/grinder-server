package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.commands.Command

class CheckAccountCreationDate : Command {

    override fun getSyntax(): String {
        return ""
    }

    override fun getDescription(): String {
        return "Shouts the date you started playing."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        val string = "I started playing on " + player.welcome.welcome.fullDate + "."
        player.say(string)
        player.sendMessage(string)
    }

    override fun canUse(player: Player): Boolean {
        return true
    }
}