package com.grinder.game.content.item

import com.grinder.game.content.skill.skillable.impl.magic.Teleporting.TeleportLocation
import com.grinder.game.entity.agent.movement.teleportation.TeleportType
import com.grinder.game.entity.agent.player.removeInventoryItem
import com.grinder.game.entity.agent.player.teleport
import com.grinder.game.model.ItemActions
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID

/**
 * Item to teleport to zul-andra, from where zulrah can be fought.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/04/2020
 * @version 1.0
 */
object ZulAndraTeleport {

    init {
        ItemActions.onClick(ItemID.ZUL_ANDRA_TELEPORT){
            if(isInInventory()) {
                if (player.removeInventoryItem(Item(getItemId(), 1), 1))
                    player.teleport(TeleportLocation.ZULRAH.position, TeleportType.SCROLL)
                return@onClick true
            }
            return@onClick false
        }
    }
}