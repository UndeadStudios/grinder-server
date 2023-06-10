package com.grinder.game.content.item

import com.grinder.game.entity.agent.player.itemStatement
import com.grinder.game.entity.agent.player.playSound
import com.grinder.game.entity.agent.player.setInventoryItem
import com.grinder.game.model.ItemActions
import com.grinder.game.model.item.Item
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID

object RingOfWealthScroll {

    private val ringPairs = mapOf(
            ItemID.RING_OF_WEALTH to ItemID.RING_OF_WEALTH_I_,
            ItemID.RING_OF_WEALTH_1_ to ItemID.RING_OF_WEALTH_I1_,
            ItemID.RING_OF_WEALTH_2_ to ItemID.RING_OF_WEALTH_I2_,
            ItemID.RING_OF_WEALTH_3_ to ItemID.RING_OF_WEALTH_I3_,
            ItemID.RING_OF_WEALTH_4_ to ItemID.RING_OF_WEALTH_I4_,
            ItemID.RING_OF_WEALTH_5_ to ItemID.RING_OF_WEALTH_I5_)

    init {
        ItemActions.onItemOnItem(ItemID.RING_OF_WEALTH_SCROLL) {
            val otherId = getOtherItemId(ItemID.RING_OF_WEALTH_SCROLL)
            val imbuedId = ringPairs[otherId]?:return@onItemOnItem false
            player.inventory.delete(ItemID.RING_OF_WEALTH_SCROLL, 1);
            player.setInventoryItem(getSlot(imbuedId), Item(imbuedId, 1))
            player.itemStatement(imbuedId, 200, "You have imbued your Ring of Wealth.")
            player.playSound(Sounds.USING_LAMP_REWARD)
            return@onItemOnItem true
        }
    }
}