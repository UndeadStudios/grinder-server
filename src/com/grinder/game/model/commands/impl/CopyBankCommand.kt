package com.grinder.game.model.commands.impl

import com.grinder.game.World
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.commands.DeveloperCommand
import com.grinder.game.model.item.container.bank.BankConstants

class CopyBankCommand : DeveloperCommand() {

    override fun getSyntax(): String {
        return "[playerName]"
    }

    override fun getDescription(): String {
        return "Copies the bank of another player."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        val player2 = command.substring(parts[0].length + 1)

        World.findPlayerByName(player2).ifPresent {
            for (i in 0 until BankConstants.TOTAL_BANK_TABS) {
                if (player.getBank(i) != null) {
                    player.getBank(i).resetItems()
                }
            }
            for (i in 0 until BankConstants.TOTAL_BANK_TABS) {
                if (it.getBank(i) != null) {
                    for (item in it.getBank(i).validItems) {
                        player.getBank(i).add(item, false)
                    }
                }
            }
        }
    }
}