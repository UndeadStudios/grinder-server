package com.grinder.game.model.commands.impl

import com.grinder.game.content.skill.skillable.impl.Thieving
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.commands.DeveloperCommand
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.bank.BankUtil
import com.grinder.util.Misc

class TestCrystalChestCommand : DeveloperCommand() {

    override fun getSyntax(): String {
        return "[amount]"
    }

    override fun getDescription(): String {
        return "Opens x amount of crystal chests."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {

        player.message("Adding " + parts[1].toInt() + " Crystal chest rewards!")

        for (amount2 in 0 until parts[1].toInt()) {
            val item1 = Thieving.CRYSTAL_CHEST_LOOT[Misc.getRandomInclusive(Thieving.CRYSTAL_CHEST_LOOT.size - 1)]
            var item2 = -1
            item2 = if (Misc.getRandomInclusive(4) == 1) { // You only have 1/4 chance of getting a second good item from the chest
                Thieving.CRYSTAL_CHEST_LOOT[Misc.getRandomInclusive(Thieving.CRYSTAL_CHEST_LOOT.size - 1)]
            } else {
                Thieving.NOOBISH_ITEMS[Misc.getRandomInclusive(Thieving.NOOBISH_ITEMS.size - 1)]
            }
            val moneyRandom = 150000 + Misc.getRandomInclusive(500000) // 3rd item is always cash bonus reward
            BankUtil.addToBank(player, Item(item1, 1))
            BankUtil.addToBank(player, Item(item2, 1))
            BankUtil.addToBank(player, Item(995, moneyRandom))
        }
    }
}