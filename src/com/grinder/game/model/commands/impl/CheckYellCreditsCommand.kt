package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.getInt
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.commands.Command

class CheckYellCreditsCommand : Command {

    override fun getSyntax(): String {
        return ""
    }

    override fun getDescription(): String {
        return "Sends the amount of yell credits on your account."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        val credits = player.getInt(Attribute.YELL_CREDITS)
        val noun = if(credits == 1) "credit" else "credits"
        player.message("You have $credits $noun on your account.")
    }

    override fun canUse(player: Player): Boolean {
        return true
    }
}