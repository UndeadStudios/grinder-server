package com.grinder.game.content.item

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.getInt
import com.grinder.game.entity.hasAttribute
import com.grinder.game.entity.setInt
import com.grinder.game.model.*
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.item.container.bank.BankConstants
import com.grinder.util.ItemID

/**
 * @author Zach (zach@findzach.com)
 * @since 12/16/2020
 *
 * Will handle interactions with Players Coal Bag
 */
object CoalBag {

    val OPENED_COAL_BAG = 24480

    init {

        /**
         * Opening & Closing Coal Bag
         */
        onEquipAction(ItemID.COAL_BAG_2, OPENED_COAL_BAG) {
            val newId = if (getItemId() == ItemID.COAL_BAG_2) OPENED_COAL_BAG else ItemID.COAL_BAG_2

            val actionPhase = if (newId == ItemID.COAL_BAG_2) "closed" else "opened"

            player.inventory.replaceAll(getItemId(), newId, true)
            player.sendMessage("You have $actionPhase your coal bag!")
        }

        /**
         * Filling Coal Bag
         */
        onFirstInventoryAction(ItemID.COAL_BAG_2, OPENED_COAL_BAG) {
            addCoalToBag(player, false)
            return@onFirstInventoryAction
        }

        ItemActions.onClick(ItemID.COAL_BAG_2, OPENED_COAL_BAG) {
            if (itemActionMessage.opcode == 75 && player.interfaceId == BankConstants.INTERFACE_ID) {
                player.sendDevelopersMessage("Clicked Coal Bag whilst Bank is open")
            }
            return@onClick true
        }

        /**
         * Emptying Coal Bag
         */
        onSecondInventoryAction(ItemID.COAL_BAG_2, OPENED_COAL_BAG) {
            emptyCoalBag(player, false)
            return@onSecondInventoryAction
        }

        /**
         * Checking Coal Bag
         * TODO: add dialogue with coal item perhaps
         */
        onThirdInventoryAction(ItemID.COAL_BAG_2, OPENED_COAL_BAG) {
            if (player.hasAttribute(Attribute.COAL_BAG_TOTAL)) {
                var amt = player.attributes.numInt(Attribute.COAL_BAG_TOTAL)
                player.sendMessage("You have $amt coal in your bag!")
            } else {
                player.sendMessage("Your coal bag is empty!");
            }
            return@onThirdInventoryAction
        }
    }


    /**
     * Finds the max capacity for our Players Coal Bag
     */
    fun getPlayerMaxCapacity(player: Player): Int {
        return if (player.equipment.containsAny(ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPE_T_)) 36 else 27
    }

    /**
     *
     * @param silent - If we should message the user or not.
     * @return The amount of Coal Leftover from transaction
     */
    fun addCoalToBagFromBank(player: Player, silent: Boolean): Int {
        var total = getPlayerMaxCapacity(player)

        when {
            !player.inventory.contains(ItemID.COAL) -> if (!silent) player.sendMessage("You don't have any coal!")
            player.getInt(Attribute.COAL_BAG_TOTAL) >= total -> if (!silent) player.sendMessage("Empty your coal bag in order to add more; your coal bag is already full!")
            else -> {
                //todo
            }
        }
        return player.inventory.getAmount(ItemID.COAL)
    }

    /**
     *
     * @param silent - If we should message the user or not.
     * @return The amount of Coal Leftover from transaction
     */
    fun addCoalToBag(player: Player, silent: Boolean): Int {
        var total = getPlayerMaxCapacity(player)

        when {
            !player.inventory.contains(ItemID.COAL) -> if (!silent) player.sendMessage("You don't have any coal!")
            player.getInt(Attribute.COAL_BAG_TOTAL) >= total -> if (!silent) player.sendMessage("Empty your coal bag in order to add more; your coal bag is already full!")
            else -> {
                var totalAdded = 0;
                while (player.inventory.contains(ItemID.COAL) && (player.getInt(Attribute.COAL_BAG_TOTAL) + totalAdded) < total) {
                    player.inventory.delete(ItemID.COAL, 1);
                    totalAdded++;
                }
                player.sendDevelopersMessage("@red@[Dev] TotalAdded: $totalAdded")
                //Add to our Total if we need too.
                if (totalAdded > 0)
                    player.setInt(Attribute.COAL_BAG_TOTAL, player.getInt(Attribute.COAL_BAG_TOTAL) + totalAdded)

                if (player.getInt(Attribute.COAL_BAG_TOTAL) == total) {
                    player.sendMessage("Your coal bag is now full!")
                }
            }
        }
        return player.inventory.getAmount(ItemID.COAL)
    }

    /**
     * Will be used in our Mining for Coal bag
     */
    @JvmStatic
    fun addOneCoal(): Boolean {


        return false
    }

    /**
     * @param silent - if we message the user or not
     */
    fun emptyCoalBag(player: Player, silent: Boolean) {

        val currentCoal = player.getInt(Attribute.COAL_BAG_TOTAL)
        var emptySpace = player.inventory.countFreeSlots()

        player.sendDevelopersMessage("Empty Slots in Inventory: $emptySpace")
        when {
            currentCoal == 0 -> player.sendMessage("Your Coal bag is already empty!")
            player.inventory.isFull -> player.sendMessage("Your inventory is full!")
            else -> {
                var amtGiven: Int;
                if (emptySpace >= currentCoal) {
                    amtGiven = currentCoal
                    player.setInt(Attribute.COAL_BAG_TOTAL,0)
                    player.inventory.add(ItemID.COAL, amtGiven)
                } else {
                    amtGiven = emptySpace;
                    player.setInt(Attribute.COAL_BAG_TOTAL, currentCoal - amtGiven)
                    player.inventory.add(ItemID.COAL, amtGiven)
                }
                player.sendMessage("You withdraw $amtGiven coal from your bag")
            }
        }
    }

}