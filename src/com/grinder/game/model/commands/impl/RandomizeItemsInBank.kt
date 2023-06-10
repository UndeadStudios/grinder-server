package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.commands.Command
import com.grinder.game.model.commands.DeveloperCommand
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.bank.Banking

/**
 * Wipes bank and fills it with 250 items each tab.
 */
class RandomizeItemsInBank : DeveloperCommand() {

    override fun getDescription(): String {
        return "Wipes bank and fills with random items."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        if (command.length <= 8) {
            player.message("Wrong usage of the command!")
            return
        }
        Banking.wipe(player)
        var toAdd = parts[1].toInt()
        var Counter = 0
        var tabNumber = 0
        val MAX_COUNT = 2500
        var i = 0
        while (i <= MAX_COUNT) {
            Counter++
            if (Counter == 300) {
                Counter = 0
                player.getBank(tabNumber).refreshItems()
                tabNumber++
                if (tabNumber < player.banks.size) player.currentBankTab = tabNumber else return
            }
            if (toAdd >= 27789) i = 0
            player.getBank(tabNumber).add(Item(toAdd, 9999), false)
            ++toAdd
            i++
        }
        player.message("Items added to the bank!")
    }
}