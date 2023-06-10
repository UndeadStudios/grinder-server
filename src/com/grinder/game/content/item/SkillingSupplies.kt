package com.grinder.game.content.item

import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.passedTime
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.attribute.AttributeManager.Points
import com.grinder.game.model.item.Item
import com.grinder.game.model.onFirstInventoryAction
import com.grinder.util.ItemID
import kotlin.random.Random

/**
 * Handles opening of skilling supply boxes.
 */
object SkillingSupplies {

    init {
        // custom item
        onFirstInventoryAction(ItemID.HERB_BOX) {
            if (player.passedTime(Attribute.GENERIC_ACTION, 1, message = false)){
                if (player.checkFreeInventorySlots(23)){
                    if (player.removeInventoryItem(getItem() ?: return@onFirstInventoryAction, -1)){
                        player.addInventoryItems(generateGeneralSkillingSupplyItems())
                        player.playSound(72)
                        player.message("<img=749> You have opened the skilling supplies pack!")
                        player.message("<img=779> You have received 15 bonus skilling points!")
                        player.points.increase(Points.SKILLING_POINTS, 15)
                    }
                }
            }
        }
        onFirstInventoryAction(ItemID.OPENED_HERB_BOX) {
            if (player.passedTime(Attribute.GENERIC_ACTION, 1, message = false)){
                if (player.checkFreeInventorySlots(10)){
                    if (player.removeInventoryItem(getItem() ?: return@onFirstInventoryAction, -1)){
                        player.addInventoryItems(generateHerbalSkillingSupplyItems())
                        player.message("You've opened the herb box.")
                    }
                }
            }
        }
    }

    private fun generateHerbalSkillingSupplyItems() = listOf(
            Item(ItemID.GRIMY_GUAM_LEAF_2, Random.nextInt(50, 85)),
            Item(ItemID.GRIMY_MARRENTILL_2, Random.nextInt(40, 70)),
            Item(ItemID.GRIMY_TARROMIN_2, Random.nextInt(40, 70)),
            Item(ItemID.GRIMY_HARRALANDER_2, Random.nextInt(40, 70)),
            Item(ItemID.GRIMY_RANARR_WEED_2, Random.nextInt(30, 55)),
            Item(ItemID.GRIMY_IRIT_LEAF_2, Random.nextInt(30, 55)),
            Item(ItemID.GRIMY_AVANTOE_2, Random.nextInt(30, 55)),
            Item(ItemID.GRIMY_KWUARM_2, Random.nextInt(25, 45)),
            Item(ItemID.GRIMY_CADANTINE_2, Random.nextInt(25, 45)),
            if (Random.nextBoolean())
                Item(ItemID.GRIMY_DWARF_WEED_2, Random.nextInt(10, 25))
            else
                Item(ItemID.GRIMY_LANTADYME_2, Random.nextInt(10, 25)))

    private fun generateGeneralSkillingSupplyItems() = listOf(
            Item(ItemID.KWUARM_SEED, Random.nextInt(50, 100)),
            Item(ItemID.SNAPDRAGON_SEED, Random.nextInt(50, 100)),
            Item(ItemID.CADANTINE_SEED, Random.nextInt(50, 100)),
            Item(ItemID.LANTADYME_SEED, Random.nextInt(50, 100)),
            Item(ItemID.DWARF_WEED_SEED, Random.nextInt(50, 100)),
            Item(ItemID.TORSTOL_SEED, Random.nextInt(50, 100)),
            Item(ItemID.PURE_ESSENCE_2, Random.nextInt(100, 500)),
            Item(ItemID.UNCUT_SAPPHIRE_2, Random.nextInt(50, 150)),
            Item(ItemID.GRIMY_TORSTOL_2, Random.nextInt(100, 200)),
            Item(ItemID.GRIMY_TOADFLAX_2, Random.nextInt(100, 200)),
            Item(ItemID.GRIMY_SNAPDRAGON_2, Random.nextInt(100, 200)),
            Item(ItemID.GRIMY_LANTADYME_2, Random.nextInt(100, 200)),
            Item(ItemID.UNCUT_DIAMOND_2, Random.nextInt(150, 300)),
            Item(ItemID.UNCUT_DRAGONSTONE_2, Random.nextInt(50, 100)),
            Item(ItemID.RED_DRAGONHIDE_2, Random.nextInt(100, 200)),
            Item(ItemID.BLACK_DRAGONHIDE_2, Random.nextInt(50, 100)),
            Item(ItemID.DRAGON_BONES_2, Random.nextInt(40, 150)),
            Item(ItemID.RAW_SHARK_2, Random.nextInt(100, 300)),
            Item(ItemID.RAW_MANTA_RAY_2, Random.nextInt(50, 100)),
            Item(ItemID.ADAMANTITE_BAR_2, Random.nextInt(50, 250)),
            Item(ItemID.RUNITE_BAR_2, Random.nextInt(50, 150)),
            Item(ItemID.MAGIC_LOGS_2, Random.nextInt(100, 200)),
            Item(ItemID.RUNITE_ORE_2, Random.nextInt(150, 250)))
}