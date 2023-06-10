package com.grinder.game.model.commands.impl

import com.grinder.game.content.skill.skillable.impl.Thieving
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.commands.DeveloperCommand
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.bank.BankUtil
import com.grinder.game.model.item.container.bank.Banking
import com.grinder.util.Misc

class AddBonusItems : DeveloperCommand() {

    override fun getSyntax(): String {
        return "[amount]"
    }

    override fun getDescription(): String {
        return "Adds Thieving bonus items rewards to your bank."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {

        val amount = parts[1].toInt()

        player.message("Adding $amount bonus items rewards!")

        Banking.wipe(player);
        for (i in 0 until amount) {
            val reward = Misc.randomElement(Thieving.THIVING_STALL_BONUS_ITEMS)
            BankUtil.addToBank(player, Item(reward, 1))
        }
    }
}