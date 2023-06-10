package com.grinder.game.content.item

import com.grinder.util.ItemID

/**
 * Contains item ids that should always be kept on death.
 */
object ItemsKeptOnDeath {

    private val keptOnDeathItemIds = intArrayOf(
            ItemID.IRONMAN_HELM,
            ItemID.IRONMAN_PLATEBODY,
            ItemID.IRONMAN_PLATELEGS,
            ItemID.ULTIMATE_IRONMAN_HELM,
            ItemID.ULTIMATE_IRONMAN_PLATEBODY,
            ItemID.ULTIMATE_IRONMAN_PLATELEGS,
            ItemID.HARDCORE_IRONMAN_HELM,
            ItemID.HARDCORE_IRONMAN_PLATEBODY,
            ItemID.HARDCORE_IRONMAN_PLATELEGS,
            ItemID.GROUP_IRON_HELM,
            ItemID.GROUP_IRON_PLATEBODY,
            ItemID.GROUP_IRON_PLATELEGS,
            ItemID.GROUP_IRON_BRACERS,
            13319,
            ItemID.ROTTEN_POTATO,
            ItemID.HARDCORE_GROUP_IRON_HELM,
            ItemID.HARDCORE_GROUP_IRON_PLATEBODY,
            ItemID.HARDCORE_GROUP_IRON_PLATELEGS,
            ItemID.HARDCORE_GROUP_IRON_BRACERS,
            24384, // Trophy
            24382, // Trophy
            24380, // Trophy
            24378, // Trophy
            24376, // Trophy
            24374, // Trophy
            24372 // Trophy
    )

    /**
     * Check if items with the argued [itemId] should be kept on death.
     */
	@JvmStatic
	fun isKeptOnDeath(itemId: Int) = keptOnDeathItemIds.contains(itemId)
}