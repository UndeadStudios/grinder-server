package com.grinder.game.model.commands.impl

import com.grinder.game.content.miscellaneous.RecentUpdates
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.commands.Command

class CheckUpdatesCommand : Command {

    override fun getSyntax(): String {
        return ""
    }

    override fun getDescription(): String {
        return "Shows the latest server updates panel."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        RecentUpdates.display(player)
    }

    override fun canUse(player: Player): Boolean {
        return true
    }
}