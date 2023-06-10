package com.grinder.game.content.item

import com.grinder.game.content.cluescroll.scroll.ScrollConstants
import com.grinder.game.content.item.degrading.DegradableType.Companion.forItem
import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.removeInterfaces
import com.grinder.game.model.ButtonActions
import com.grinder.game.model.item.Item

object ItemDestruction {

    init {
        ButtonActions.onClick(14175) {
            val item = player.destroyItem
            val slot = player.inventory.getSlot(item)
            val amount = player.inventory.getAmountForSlot(slot)
            val itemName = ItemDefinition.forId(item)
            player.removeInterfaces()
            if (item != -1)
                player.inventory.delete(Item(item, amount), slot, true)
            player.message("You have successfully destroyed " + itemName.name + " x " + amount + ".")
            val manager = player.clueScrollManager

            forItem(item)?.let {
                player.itemDegradationManager.repair(item)
            }

            when (item) {
                ScrollConstants.ITEM_EASY_SCROLL -> manager.easyScroll = null
                ScrollConstants.ITEM_MEDIUM_SCROLL -> manager.mediumScroll = null
                ScrollConstants.ITEM_HARD_SCROLL -> manager.hardScroll = null
                ScrollConstants.ITEM_ELITE_SCROLL -> manager.eliteScroll = null
                LootingBag.LOOTING_BAG -> {
                    player.lootingBag.container.resetItems()
                    player.message("Your Looting bag items has disappeared.")
                }
            }
        }
    }
}