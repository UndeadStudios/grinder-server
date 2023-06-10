package com.grinder.game.model.commands.impl

import com.grinder.game.World
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.commands.Command

class CheckPlayersOnline : Command {

    override fun getSyntax(): String {
        return ""
    }

    override fun getDescription(): String {
        return "Shows the current number of online player's."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
/*        var playerCount = World.players.size()
        if (playerCount > 80) {
            playerCount = (playerCount * 1.6).toInt()
        } else if (playerCount > 40) {
            playerCount = (playerCount * 1.4).toInt()
        } else if (playerCount >= 20) {
            playerCount = (playerCount * 1.2).toInt()
        }*/
        var playerCount = PlayerUtil.transformPlayerCount();
        player.message("There are currently $playerCount players online.")
    }

    override fun canUse(player: Player): Boolean {
        return true
    }
}