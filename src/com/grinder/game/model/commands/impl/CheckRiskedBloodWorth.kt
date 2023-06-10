package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.commands.Command
import com.grinder.game.model.item.BloodItemPrices
import java.text.NumberFormat

class CheckRiskedBloodWorth : Command {
    override fun getSyntax(): String {
        return ""
    }

    override fun getDescription(): String {
        return "Shouts the current value of stuff your carrying."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        val bloodWorth = BloodItemPrices.getItemsWorthInBloodMoney(player)
        player.packetSender.sendQuickChat("I am currently carrying " + NumberFormat.getInstance().format(bloodWorth) + " Blood money worth of items!")
    }

    override fun canUse(player: Player): Boolean {
        return true
    }
}