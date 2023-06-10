package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.commands.Command

class CheckKillDeathRatio : Command {
    override fun getSyntax(): String {
        return ""
    }

    override fun getDescription(): String {
        return "Shouts your current kill/death ratio."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        player.packetSender.sendQuickChat("My KDR is " + player.points.kdr)
    }

    override fun canUse(player: Player): Boolean {
        return true
    }
}