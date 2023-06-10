package com.grinder.game.model.commands.impl

import com.grinder.game.World
import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.commands.Command
import com.grinder.game.model.item.container.player.SafeDeposit
import com.grinder.util.Misc

class CheckDepositCommand : Command {
    override fun getSyntax(): String {
        return "[playerName]"
    }

    override fun getDescription(): String {
        return "Check the player's deposit box."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        val targetName = command.substring(parts[0].length + 1)
        val optionalTarget = World.findPlayerByName(targetName)
        var safeDeposit: SafeDeposit? = null

        if (optionalTarget.isPresent) {
            safeDeposit = optionalTarget.get().safeDeposit
        } else if (PlayerSaving.playerExists(targetName)) {
            safeDeposit = PlayerLoading.getDepositContainers(targetName)
        }

        if (safeDeposit != null) {
            player.status = PlayerStatus.NONE
            player.packetSender.sendString(SafeDeposit.SLOTS_STRING_ID, safeDeposit.validItems.size.toString() + " / " + safeDeposit.capacity())
            player.packetSender.sendItemContainer(safeDeposit, SafeDeposit.ITEM_CONTAINER_ID)
            player.packetSender.sendInterface(SafeDeposit.INTERFACE_ID)
            player.message("Viewing @dre@" + Misc.formatPlayerName(targetName) + "@bla@'s deposit's box.")
        }
    }

    override fun canUse(player: Player): Boolean {
        val rights = player.rights
        return rights === PlayerRights.OWNER || rights === PlayerRights.DEVELOPER || player.username == "3lou 55" || player.username.toLowerCase() == "isaac" || rights === PlayerRights.ADMINISTRATOR || rights === PlayerRights.GLOBAL_MODERATOR
                 || rights === PlayerRights.CO_OWNER || rights === PlayerRights.MODERATOR
    }
}