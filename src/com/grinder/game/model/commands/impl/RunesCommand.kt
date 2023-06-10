package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.model.commands.Command
import com.grinder.game.model.commands.DeveloperCommand
import com.grinder.game.model.item.Item

class RunesCommand : DeveloperCommand() {

    override fun getDescription(): String {
        return "Adds 1,000 runes of all kinds."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        val runes = intArrayOf(554, 555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 21880)
        for (rune in runes) {
            player.inventory.add(Item(rune, 1000), false)
        }
        player.inventory.refreshItems()
    }
}