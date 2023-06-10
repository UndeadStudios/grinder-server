package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.commands.Command
import com.grinder.game.model.commands.DeveloperCommand
import com.grinder.game.model.item.container.shop.ShopManager
import com.grinder.util.ShopIdentifiers

/**
 * Clears the general store of all player sold items
 *
 * @author xplicit
 */
class ClearGeneralStoreCommand : DeveloperCommand() {

    override fun getDescription(): String {
        return "Clears the general store player sold items."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        val shop = ShopManager.shops[ShopIdentifiers.GENERAL_STORE]
        var updated = false
        for (item in shop!!.currentStock) {
            if (item != null && shop.getAmount(item.id, true) == 0) {
                shop.removeItem(item.id, item.amount)
                updated = true
            }
        }
        if (updated) {
            ShopManager.refresh(shop)
            player.message("The general store has been cleared of all player sold items.")
        } else {
            player.message("There are no player sold items in the general store to clear.")
        }
    }
}