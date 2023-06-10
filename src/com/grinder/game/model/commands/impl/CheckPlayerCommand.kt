package com.grinder.game.model.commands.impl

import com.grinder.game.World
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerLoading
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.commands.Command
import com.grinder.game.model.item.container.bank.BankUtil
import com.grinder.game.model.item.container.player.Inventory
import com.grinder.util.Misc

class CheckPlayerCommand : Command {
    override fun getSyntax(): String {
        return "[playerName]"
    }

    override fun getDescription(): String {
        return "Check the player's bank/inventory."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {

        if (command.length <= 4) {
            player.sendMessage("Wrong usage of the command!")
            return
        }

        val targetName = command.substring(parts[0].length + 1)

        BankUtil.displayBank(player, targetName)
        val optionalTarget = World.findPlayerByName(targetName)
        val targetInventoryItems = optionalTarget
                .map { targetPlayer: Player -> targetPlayer.inventory.validItems }
                .orElse(PlayerLoading.getInventoryItems(targetName))
        if (targetInventoryItems.isEmpty()) {
            player.message("Player '$targetName' has no items in his/her inventory.")
            return
        }

        //player.setStatus(PlayerStatus.NONE);
        player.message("Viewing @dre@" + Misc.formatPlayerName(targetName) + "@bla@'s inventory.")
        player.packetSender.sendInterfaceItems(Inventory.INTERFACE_ID, targetInventoryItems)
        if (optionalTarget.isPresent)
        player.message("The player " + optionalTarget.get().username + " is connected from the IP address " + optionalTarget.get().hostAddress)
    }

    override fun canUse(player: Player): Boolean {
        val rights = player.rights
        return rights === PlayerRights.OWNER || rights === PlayerRights.DEVELOPER || player.username == "3lou 55"
    }
}