package com.grinder.game.model.item


import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.interfaces.dialogue.DialogueManager
import com.grinder.util.ItemID
import java.util.*

/**
 * Represents items that can be imbued.
 *
 * @param imbuedItem    the id of the imbued version of the item.
 * @param unImbuedItem  the id of the un-imbued version of the item.
 * @param imbueCost     the cost to imbue the item.
 */
enum class ImbuedableItems(val imbuedItem: Int, val unImbuedItem: Int, val imbueCost: Int) {

    SALVE_AMULET(ItemID.SALVE_AMULET_I_, ItemID.SALVE_AMULET, 55_000),
    SALVE_AMULET_ENCHANTED(ItemID.SALVE_AMULET_EI_, ItemID.SALVE_AMULET_E_, 75_000),

    SEERS_RING_I(11770, 6731, 50000),
    ARCHER_RING_I(11771, 6733, 60000),
    WARRIOR_RING_I(11772, 6735, 50000),
    BERSERKER_RING_I(11773, 6737, 75000),
    TYRANNICAL_RING_I(12691, 12603, 90000),
    TREASONOUS_RING_I(12692, 12605, 95000),
    RING_OF_SUFFERING_I(19710, 19550, 80000),
    GRANITE_RING_I(21752, 21739, 120000),
    RING_OF_THE_GODS_I(13202, 12601, 95000),
    BLACK_MASK_I(11784, 8921, 35000),
    BLACK_MASK_I_1(11783, 8919, 35000),
    BLACK_MASK_I_2(11782, 8917, 35000),
    BLACK_MASK_I_3(11781, 8915, 35000),
    BLACK_MASK_I_4(11780, 8913, 35000),
    BLACK_MASK_I_5(11779, 8911, 35000),
    BLACK_MASK_I_6(11778, 8909, 35000),
    BLACK_MASK_I_7(11777, 8907, 35000),
    BLACK_MASK_I_8(11776, 8905, 35000),
    BLACK_MASK_I_9(11775, 8903, 35000),
    BLACK_MASK_I_10(11774, 8901, 35000),
    CRYSTAL_BOW_I_NEW(11748, 4212, 75000),
    CRYSTAL_BOW_I_FULL(11749, 4214, 75000),
    CRYSTAL_SHIELD_I_NEW(11759, 4224, 75000),
    CRYSTAL_SHIELD_I_FULL(11760, 4225, 75000),
    MAGIC_SHORTBOW_I(12788, 861, 10000),
    SLAYER_HELMET_I(11865, 11864, 75000),
    BLACK_SLAYER_HELMET_I(19641, 19639, 75000),
    GREEN_SLAYER_HELMET_I(19645, 19643, 75000),
    RED_SLAYER_HELMET_I(19649, 19647, 75000),
    PURPLE_SLAYER_HELMET_I(21266, 21264, 75000),
    TURQUOISE_SLAYER_HELMET_I(21890, 21888, 75000),
    HYDRA_SLAYER_HELMET_I(23075, 23073, 75000),
    TWISTED_SLAYER_HELMET_I(24444, 24370, 75000),
    TZTOK_SLAYER_HELMET_I(25900, 25898, 250_000),
    VAMPYRIC_SLAYER_HELMET_I(25906, 25904, 350_000),
    TZKAL_SLAYER_HELMET_I(25910, 25912, 500_000),
    //SALVE_AMULET_I(12017, 4081, 80000),
    CRYSTAL_HALBERD_I_NEW(13080, 13091, 120000),
    CRYSTAL_HALBERD_I_FULL(13081, 13092, 120000);

    companion object {

        private val imbueableItems: MutableMap<Int, ImbuedableItems> = HashMap()

        /**
         * Gets the total cost of imbuing a player's stuff.
         *
         * @param player
         * @return
         */
        @JvmStatic
        fun getTotalImbueCost(player: Player): Int {
            return values().sumBy {
                player.inventory.getAmount(it.unImbuedItem) * it.imbueCost
            }
        }

        /**
         * Imbues all imbued stuff for a player.
         *
         * @param player the [Player] imbueing items.
         */
        @JvmStatic
        fun imbueItems(player: Player) {
            var imbued = false
            for (u in values()) {
                val amt = player.inventory.getAmount(u.unImbuedItem)
                if (amt > 0) {
                    val cost = u.imbueCost * amt
                    imbued = if (player.inventory.getAmount(ItemID.BLOOD_MONEY) >= cost) {
                        player.removeInventoryItem(Item(ItemID.BLOOD_MONEY, cost), -1)
                        player.removeInventoryItem(Item(u.unImbuedItem, amt), -1)
                        player.addInventoryItem(Item(u.imbuedItem, amt), -1)
                        player.inventory.refreshItems()
                        true
                    } else {
                        player.message("You could not afford imbuing all of your items.")
                        break
                    }
                }
            }

            if (imbued)
                DialogueManager.start(player, 21)
            else
                player.removeInterfaces()
        }

        operator fun get(originalId: Int) = imbueableItems[originalId]

        init {
            for (imbuedableItems in values()) {
                imbueableItems[imbuedableItems.imbuedItem] = imbuedableItems
            }
        }
    }
}