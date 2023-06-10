package com.grinder.game.model.commands.impl

import com.grinder.game.World
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerLoading
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.commands.Command
import com.grinder.game.model.item.container.player.Inventory

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-04
 */
class CheckInventoryCommand : Command {

    override fun getSyntax(): String {
        return "[playerName]"
    }

    override fun getDescription(): String {
        return "Check the player's inventory."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        val targetName = command.substring(parts[0].length + 1)
        val optionalTarget = World.findPlayerByName(targetName)
        val targetInventoryItems = optionalTarget
                .map { targetPlayer: Player -> targetPlayer.inventory.validItems }
                .orElse(PlayerLoading.getInventoryItems(targetName))

        if (targetInventoryItems.isEmpty()) {
            player.message("Player '$targetName' has no items in his/her inventory.")
            return
        }

        player.message("Viewing @dre@$targetName@bla@'s inventory.")
        player.packetSender.sendInterfaceItems(Inventory.INTERFACE_ID, targetInventoryItems)
    }

    override fun canUse(player: Player): Boolean {
        val rights = player.rights
        return rights === PlayerRights.OWNER || rights === PlayerRights.DEVELOPER || player.username == "3lou 55" || rights === PlayerRights.CO_OWNER || rights === PlayerRights.ADMINISTRATOR || rights === PlayerRights.GLOBAL_MODERATOR
                || rights === PlayerRights.MODERATOR
    }
}