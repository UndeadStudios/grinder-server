package com.grinder.game.content.item

import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler
import com.grinder.game.entity.agent.movement.teleportation.TeleportType
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.removeInventoryItem
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Position
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.item.Item
import com.grinder.game.model.onFirstInventoryAction
import com.grinder.util.ItemID

/**
 * Handles the slayers enchantment item, which can teleport a player.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   15/01/2021
 */
object SlayerEnchantment {

    init {
        onFirstInventoryAction(ItemID.SLAYERS_ENCHANTMENT){
            if (player.wildernessLevel > 19) {
                player.message("You can't use this item in Wilderness above level 20.")
                return@onFirstInventoryAction
            }
            if (player.passedTime(Attribute.GENERIC_ACTION, 1, message = false)) {
                val slayerEnchantment = Item(getItemId(), 1)
                if (player.removeInventoryItem(slayerEnchantment)) {
                    TeleportHandler.teleport(player, Position(2196, 3247), TeleportType.TELE_TAB, false, true)
                }
            }
        }
    }
}