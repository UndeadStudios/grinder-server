package com.grinder.game.content.item

import com.grinder.game.entity.agent.player.replaceInventoryItem
import com.grinder.game.entity.passedTime
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.item.Item
import com.grinder.game.model.onFirstInventoryAction
import com.grinder.util.ItemID
import kotlin.random.Random

/**
 * Handles the Oyster item, which may give the player a pearl if opened.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   15/01/2021
 */
object Oyster {

    init {
        onFirstInventoryAction(ItemID.OYSTER) {
            if (player.passedTime(Attribute.GENERIC_ACTION, 1, message = false)) {
                val oysterItem = getItem()?:return@onFirstInventoryAction
                val rewardItem = if (Random.nextBoolean())
                    Item(ItemID.OYSTER_PEARL)
                else
                    Item(ItemID.EMPTY_OYSTER)
                player.replaceInventoryItem(oysterItem, rewardItem)
            }
        }
    }
}