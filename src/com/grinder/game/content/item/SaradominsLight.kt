package com.grinder.game.content.item

import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.setBoolean
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.areas.godwars.GodwarsArea
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.item.Item
import com.grinder.game.model.onFirstInventoryAction
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID

/**
 * Handles saradomin's light item effect.
 */
object SaradominsLight {

    init {
        onFirstInventoryAction(ItemID.SARADOMINS_LIGHT) {
            if (player.getBoolean(Attribute.CONSUMED_SARADOMIN_LIGHT)) {
                player.message("You already have consumed a Saradomin's light and the effect is passive.")
                return@onFirstInventoryAction
            }
            if (player.removeInventoryItem(Item(getItemId()))) {
                player.openInterface(18681)
                player.setBoolean(Attribute.CONSUMED_SARADOMIN_LIGHT, true)
                player.block()
                TaskManager.submit(4) {
                    player.unblock()
                    player.removeInterfaces()
                    player.message("You submit to the light of Saradomin. Zamorak's darkness will henceforth have no effect on you.")
                    if (AreaManager.GOD_WARS_AREA.contains(player)){
                        player.packetSender.sendWalkableInterface(-1)
                        GodwarsArea.sendInterface(player)
                    }
                }
            }
        }
    }
}