package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.commands.Command

class OpenWebMemberBenefits : Command {
    override fun getSyntax(): String {
        return ""
    }

    override fun getDescription(): String {
        return "Opens up member's benefits link."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        player.message("@dre@Opening the member's benefits and features link..")
        player.packetSender.sendURL("https://wiki.grinderscape.org/Main_page/General_guides/Donator_benefits")
    }

    override fun canUse(player: Player): Boolean {
        return true
    }
}