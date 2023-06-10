package com.grinder.game.model.item.container.player

import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainer
import com.grinder.game.model.item.container.StackType
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID

/**
 * Represents a player's Rune Pouch item container.
 *
 * @author Blake
 */
class RunePouch
/**
 * Constructs a new [RunePouch].
 *
 * @param player The [Player] who this container is constructed for.
 */
(player: Player) : ItemContainer(player) {

    override fun capacity() = CAPACITY

    override fun stackType() = StackType.STACKS

    override fun refreshItems(): RunePouch {
        player.packetSender.sendItemContainer(this, RUNE_CONTAINER_ID)
        player.packetSender.sendItemContainer(player.inventory, INVENTORY_CONTAINER_ID)
        return this
    }

    override fun full(): RunePouch {
        player.message("Not enough space in your rune pouch.")
        player.playSound(Sounds.INVENTORY_FULL_SOUND)
        return this
    }

    fun open() {
        refreshItems()
        player.openInterface(INTERFACE_ID)
    }

    /**
     * Deposits the specified item into the [RunePouch].
     *
     * @param itemId    the id of the item to deposit
     * @param slot      the slot of the item in the inventory
     * @param amount    the amount of the item to deposit
     */
    fun deposit(itemId: Int, slot: Int, amount: Int) {

        var amountToDeposit = amount

        val inventory = player.inventory

        if (!inventory.containsAtSlot(slot, itemId))
            return

        if (itemId == ItemID.RUNE_POUCH) {
            player.message("Don't be silly.")
            return
        }

        if (!isRune(itemId)) {
            player.message("You can only deposit runes into your rune pouch.")
            return
        }

        val amountAtSlot = inventory.getAmountForSlot(slot)
        if (amountToDeposit > amountAtSlot)
            amountToDeposit = amountAtSlot

        if (countFreeSlots() <= 0 && !(contains(itemId) && ItemDefinition.forId(itemId).isStackable)) {
            full()
            return
        }

        if (amountToDeposit + getAmount(itemId) > MAX_DEPOSIT_AMOUNT) {
            amountToDeposit = MAX_DEPOSIT_AMOUNT - getAmount(itemId)
            full()
        }

        add(Item(itemId, amountToDeposit), false)
        player.inventory.delete(itemId, amountToDeposit)
        refreshItems()
    }

    /**
     * Withdraws an item from this [RunePouch].
     *
     * @param itemId    the id of the item to withdraw
     * @param slot      the slot of the item in this pouch
     * @param amount    the amount to withdraw
     */
    fun withdraw(itemId: Int, slot: Int, amount: Int) {
        var amountToWithdraw = amount
        if (!containsAtSlot(slot, itemId))
            return

        val amountInSlot = getAmountForSlot(slot)
        if (amountToWithdraw > amountInSlot)
            amountToWithdraw = amountInSlot

        if (countFreeSlots() <= 0 && !(contains(itemId) && ItemDefinition.forId(itemId).isStackable)) {
            player.inventory.full()
            return
        }

        val itemToWithdraw = Item(itemId, amountToWithdraw)
        delete(itemToWithdraw, false)
        player.addInventoryItem(itemToWithdraw)
        refreshItems()
    }

    companion object {

        /**
         * The interface id.
         */
        const val INTERFACE_ID = 26699

        /**
         * The rune container id.
         */
        const val RUNE_CONTAINER_ID = 41710

        /**
         * The inventory container id.
         */
        const val INVENTORY_CONTAINER_ID = 41711
        const val CAPACITY = 3

        const val MAX_DEPOSIT_AMOUNT = 16000

        /**
         * Checks if the specified id is a rune.
         *
         * @param itemId the id of the item to check
         * @return `true` if the id is a rune
         */
        fun isRune(itemId: Int): Boolean {
            // Elemental runes
            if (itemId in 554..566)
                return true
            // Mixed runes
            if (itemId in 4694..4699)
                return true
            // Astral rune
            if (itemId == ItemID.ASTRAL_RUNE)
                return true
            return itemId == 21880
        }
    }
}