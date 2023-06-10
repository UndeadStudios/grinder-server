package com.grinder.game.model.item.container.bank

import com.grinder.game.GameConstants
import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainer
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.game.model.item.container.StackType
import com.grinder.game.model.item.container.player.Inventory
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Logging
import com.grinder.util.Misc

/**
 * Shitty bank system with flaws.
 *
 * @author Gabriel Hannason
 */
class Bank(player: Player?) : ItemContainer(player) {

    override fun capacity() = 756

    override fun stackType() = StackType.STACKS

    fun open(): Bank {

        if (player.gameMode.isUltimate) {
            player.message("You can't bank as an Ultimate Iron Man.")
            return this
        }

        if (player.minigame != null) {
            PlayerUtil.broadcastPlayerDeveloperMessage(player.username + " tried to deposit item while inside a minigame " + player.minigame + " " + player.position + "")
            return this
        }

        player.status = PlayerStatus.BANKING
        player.enterSyntax = null
        player.removeInputHandler()

        // Sort items in every container
        for (i in 0 until BankConstants.TOTAL_BANK_TABS)
            ItemContainerUtil.shiftValidItemsToLeft(player.getBank(i))

        // Refresh
        refreshItems()

        // Send current bank tab being viewed
        player.packetSender.sendCurrentBankTab(player.currentBankTab)

        // Prevent hiding tab bar if we have more than one tab (failsafe)
        if (player.tabDisplayConfig == 3 && Banking.getLastTab(player) > 0) {
            player.tabDisplayConfig = 0
        }

        // Reset note withdraw (OSRS does this)
        player.setNoteWithdrawal(false)

        // Send configs
        player.packetSender
                .sendConfig(115, if (player.withdrawAsNote()) 1 else 0) // Withdraw as
                .sendConfig(304, if (player.insertMode()) 1 else 0) // Rearrange mode
                .sendConfig(1112, 0) // Show menu (bank settings)
                .sendConfig(1114, player.bankQuantityConfig) // Quantity
                .sendConfig(1115, if (player.hasPlaceHoldersEnabled()) 1 else 0) // Placeholders
                .sendConfig(1116, 0) // Search
                .sendConfig(1119, player.tabDisplayConfig) // Tab display
                .sendConfig(1120, if (player.isFixedBankWidth) 1 else 0) // Fixed width
                .sendConfig(1121, if (player.showDepositWornItems()) 1 else 0) // Deposit worn items button
                .sendModifiableXValue()
                .sendInterfaceSet(BankConstants.INTERFACE_ID, 5063)

        // Sound
        player.playSound(Sounds.OPEN_BANK_BOOTH_2)
        return this
    }

    override fun refreshItems(): Bank {

        val packetSender = player.packetSender

        if (player.interfaceId == BankConstants.DEPOSIT_BOX_INTERFACE_ID) {
            packetSender.sendItemContainer(player.inventory, BankConstants.DEPOSIT_BOX_ITEM_CONTAINER_ID)
            return this
        }

        // Reconfigure bank tabs.
        if (Banking.reconfigureTabs(player))
            return this

        // Send capacity information
        var size = 0
        for (i in 0 until BankConstants.TOTAL_BANK_TABS) {
            size += player.getBank(i).validItems.size
        }

        packetSender.sendString(50002, size.toString())
        packetSender.sendString(50003, capacity().toString())

        // Send amount of placeholders
        val placeholdersAmt = Banking.getPlaceholdersAmount(player)
        packetSender.sendString(50111, "Release all placeholders" + if (placeholdersAmt > 0) " ($placeholdersAmt)" else "")

        // Send all bank tabs and their contents
        for (i in 0 until BankConstants.TOTAL_BANK_TABS) {
            if (!player.getBank(i).isEmpty)
                player.getBank(i).shiftItemsToFirstSlot()
            packetSender.sendItemContainer(player.getBank(i), BankConstants.CONTAINER_START + i)
        }

        packetSender.sendItemContainer(player.inventory, BankConstants.INVENTORY_INTERFACE_ID)
        packetSender.sendString(5383, bankTitle)
        return this
    }

    override fun full(): Bank {
        player.message("Not enough space in bank.")
        return this
    }

    override fun add(item: Item, refresh: Boolean, dropIfFull: Boolean): ItemContainer {

        if (!isValidAndExistingItem(item))
            return this

        val definition = item.definition

        // null check is required for offline bank lookups
        if (player != null) {
            if (player.gameMode.isUltimate) {
                addAsUltimateIronman(item, definition)
                return this
            }
        }

        if (definition.isNoted) {
            val notedId = definition.noteId
            val notedDefinition = ItemDefinition.forId(notedId)
            if (!notedDefinition.isNoted)
                item.id = definition.noteId
        }
        return super.add(item, refresh, dropIfFull)
    }

    private fun addAsUltimateIronman(item: Item, definition: ItemDefinition) {
        if (!isEmpty) {
            Banking.wipe(player)
            Logging.log("UIMWIPE", player.username + " bank has been wiped from ItemContainer due to its not empty.")
        }
        Logging.log("UIM", player.username + " got " + item.amount + " x:  " + ItemDefinition.forId(item.id).name + " as dropped.")
        val inventory = player.inventory
        if (definition.isStackable && inventory.contains(item.id))
            inventory.add(item.clone())
        else
            ItemContainerUtil.dropUnder(player, item.id, item.amount)
    }

    override fun hasPlaceHolders() = player.hasPlaceHoldersEnabled()

    override fun moveItemFromSlot(target: ItemContainer, item: Item, slot: Int, sort: Boolean, refresh: Boolean) {

        if (player.gameMode.isUltimate)
            return

        // Make sure we're actually banking!
        if (player.status !== PlayerStatus.BANKING || player.interfaceId != BankConstants.INTERFACE_ID)
            return

        val itemAtSlot = atSlot(slot) ?: return

        // Make sure we have the item!
        if (itemAtSlot.id != item.id)
            return

        // Get the item definition for the item which is being withdrawn
        val definition = item.definition
        val notedDefinition = ItemDefinition.forId(definition.noteId) ?: return

        val withdrawingAsNote = player.withdrawAsNote()
        val noteable = notedDefinition.isNoted
        val stackable = definition.isStackable
        val targetContainsItem = target.contains(item.id)
        val targetContainsNotedItem = target.contains(notedDefinition.id)
        val targetFreeSlots = target.countFreeSlots()

        // Make sure we have enough space in the other container
        if (targetFreeSlots <= 0 && !(targetContainsItem && stackable) && !(withdrawingAsNote && noteable && targetContainsNotedItem)) {
            target.full()
            return
        }

        if (item.amount > targetFreeSlots && !stackable) {
            if (target is Inventory) {
                if (withdrawingAsNote) {
                    if (!noteable)
                        item.amount = targetFreeSlots
                } else {
                    item.amount = targetFreeSlots
                    player.message("You don't have enough inventory space to withdraw that many.")
                }
            }
        }

        val amountOfItemInBank = getAmount(item)
        // Make sure we aren't taking more than we have.
        if (item.amount > amountOfItemInBank)
            item.amount = amountOfItemInBank

        if (target is Inventory) {
            val withdrawAsNote = player.withdrawAsNote()
                    && definition != null
                    && notedDefinition.isNoted
                    && notedDefinition.name.equals(item.definition.name, ignoreCase = true)
            val amount = if (withdrawAsNote)
                target.getAmount(item.id + 1).toLong()
            else
                target.getAmount(item).toLong()

            val totalAmount = amount + item.amount.toLong()
            if (totalAmount > Int.MAX_VALUE || totalAmount <= 0) {
                player.message("You can't withdraw that entire amount into your inventory.")
                item.amount = Math.toIntExact(Int.MAX_VALUE - amount)
            }
        }

        // Delete item from this container
        delete(item, slot, refresh, target)

        // Check if we can actually withdraw the item as a note.
        if (withdrawingAsNote) {
            if (noteable && notedDefinition.name.equals(item.definition.name, ignoreCase = true))
                item.id = notedDefinition.id
            else
                player.message("This item can't be withdrawn as a note.")
        }

        // Add the item to the other container
        target.add(item, refresh)

        // Sort this container
        if (sort && getAmount(item) <= 0)
            ItemContainerUtil.shiftValidItemsToLeft(this)

        // Refresh containers
        if (refresh) {
            refreshItems()
            target.refreshItems()
        }
    }

    fun containsAnyNonePlaceHolders(vararg ids: Int) = ids.any { getById(it)?.isValid == true }

    private val bankTitle: String
        get() = "The Bank of " + GameConstants.NAME + " (est. value = " + Misc.formatWithAbbreviation(BankUtil.determineValueInBankOf(player)) + ")"

    /**
     * Prevents first slot in a tab from being empty
     */
    fun shiftItemsToFirstSlot() {
        var slotsToShift = 0
        for (k in 0 until capacity()) {
            if (items[k].id == -1)
                slotsToShift++
            else
                break
        }
        for (k in 0 until slotsToShift) {
            for (i in 0 until capacity() - 1) {
                if (items[i] == null || items[i].id == -1) {
                    swap(i + 1, i)
                }
            }
        }
    }
}