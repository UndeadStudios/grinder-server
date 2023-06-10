package com.grinder.game.content.item

import com.grinder.game.entity.agent.player.addInventoryItems
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.removeInventoryItem
import com.grinder.game.model.item.Item
import com.grinder.game.model.onFirstInventoryAction
import com.grinder.util.ItemID

/**
 * Handles the opening of various item packs.
 */
object ItemPacks {

    private val packs = mapOf(
        ItemID.SWAMP_TOAD
                to (listOf(Item(ItemID.TOADS_LEGS, 1))
                to "You carefully remove the toad's legs."),
        ItemID.EMPTY_JUG_PACK
                to (listOf(Item(ItemID.JUG_2, 100))
                to "You open the empty jug pack."),
        ItemID.EMPTY_VIAL_PACK
                to (listOf(Item(ItemID.EMPTY_VIAL_NOTED, 100))
                to "You open the empty vial pack."),
        ItemID.WATER_FILLED_VIAL_PACK
                to (listOf(Item(ItemID.VIAL_OF_WATER_2, 100))
                to "You open the water vial pack."),
        ItemID.FEATHER_PACK
                to (listOf(Item(ItemID.FEATHER, 100))
                to "You open the feather pack."),
        ItemID.BAIT_PACK
                to (listOf(Item(ItemID.FISHING_BAIT, 100))
                to "You open the bait pack."),
        ItemID.SOFT_CLAY_PACK
                to (listOf(Item(ItemID.SOFT_CLAY_2, 100))
                to "You open the soft clay pack."),
        ItemID.AIR_RUNE_PACK
                to (listOf(Item(ItemID.AIR_RUNE, 100))
                to "You open the air rune pack."),
        ItemID.WATER_RUNE_PACK
                to (listOf(Item(ItemID.WATER_RUNE, 100))
                to "You open the water rune pack."),
        ItemID.EARTH_RUNE_PACK
                to (listOf(Item(ItemID.EARTH_RUNE, 100))
                to "You open the earth rune pack."),
        ItemID.FIRE_RUNE_PACK
                to (listOf(Item(ItemID.FIRE_RUNE, 100))
                to "You open the fire rune pack."),
        ItemID.MIND_RUNE_PACK
                to (listOf(Item(ItemID.MIND_RUNE, 100))
                to "You open the mind rune pack."),
        ItemID.CHAOS_RUNE_PACK
                to (listOf(Item(ItemID.CHAOS_RUNE, 100))
                to "You open the chaos rune pack."),
        ItemID.OLIVE_OIL_PACK
                to (listOf(Item(ItemID.OLIVE_OIL_4_2, 100))
                to "You open the olive oil pack."),
        ItemID.EYE_OF_NEWT_PACK
                to (listOf(Item(ItemID.EYE_OF_NEWT_2, 100))
                to "You open the eye of newt pack."),
        ItemID.AMYLASE_PACK
                to (listOf(Item(ItemID.AMYLASE_CRYSTAL, 100))
                to "You open the amylase pack."),
        ItemID.PLANT_POT_PACK
                to (listOf(Item(ItemID.EMPTY_PLANT_POT_2, 100))
                to "You open the plant pot pack."),
        ItemID.SACK_PACK
                to (listOf(Item(ItemID.EMPTY_SACK_2, 100))
                to "You open the sack pack."),
        ItemID.BASKET_PACK
                to (listOf(Item(ItemID.BASKET_2, 100))
                to "You open the basket pack."),
        ItemID.COMPOST_PACK
                to (listOf(Item(ItemID.COMPOST_2, 100))
                to "You open the compost pack."),
        ItemID.ADAMANT_ARROW_PACK
                to (listOf(Item(ItemID.ADAMANT_ARROW, 100))
                to "You open the adamant arrow pack."),
        ItemID.RUNE_ARROW_PACK
                to (listOf(Item(ItemID.RUNE_ARROW, 100))
                to "You open the rune arrow pack."),
        ItemID.BIRD_SNARE_PACK
                to (listOf(Item(ItemID.BIRD_SNARE_2, 100))
                to "You open the bird snare pack."),
        ItemID.BOX_TRAP_PACK
                to (listOf(Item(ItemID.BOX_TRAP_2, 100))
                to "You open the box trap pack."),
        ItemID.MAGIC_IMP_BOX_PACK
                to (listOf(Item(ItemID.MAGIC_BOX_2, 100))
                to "You open the magic imp box pack."),
        ItemID.SANDWORMS_PACK
                to (listOf(Item(ItemID.SANDWORMS, 100))
                to "You open the sandworms pack."),
        ItemID.BROAD_ARROWHEAD_PACK
                to (listOf(Item(ItemID.BROAD_ARROWHEADS, 100))
                to "You open the broad arrowhead pack."),
        ItemID.UNFINISHED_BROAD_BOLT_PACK
                to (listOf(Item(ItemID.UNFINISHED_BROAD_BOLTS, 100))
                to "You open the unfinished broad bolt pack."),
        ItemID.AMYLASE_PACK
            to (listOf(Item(ItemID.AMYLASE_CRYSTAL, 100))
            to "You open the amylase pack."),
        ItemID.ESSENCE_PACK
                to (listOf(Item(ItemID.PURE_ESSENCE_2, 100))
                to "You open the essence pack."),
        ItemID.BONE_BOLT_PACK
                to (listOf(Item(ItemID.BONE_BOLTS, 100))
                to "You open the bone bolt pack."))


    init {
        onFirstInventoryAction(*packs.keys.toIntArray()){
            val packItem = getItem()?:return@onFirstInventoryAction
            val entry = packs[packItem.id]?:return@onFirstInventoryAction
            if (player.removeInventoryItem(packItem, -1)) {
                player.addInventoryItems(entry.first)
                player.message(entry.second)
            }
        }
    }
}