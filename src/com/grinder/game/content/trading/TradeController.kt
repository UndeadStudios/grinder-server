package com.grinder.game.content.trading

import com.grinder.game.content.item.VotingTicket.checkVotingTickets
import com.grinder.game.content.minigame.MinigameManager
import com.grinder.game.content.miscellaneous.rugmerchant.RugMerchant
import com.grinder.game.content.trading.TradeState.*
import com.grinder.game.content.trading.TradeUtil.cannotAcceptTrade
import com.grinder.game.content.trading.TradeUtil.cannotInitiateTrade
import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.passedTime
import com.grinder.game.model.ButtonActions
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.attribute.AttributeManager
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainer
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.game.model.item.container.player.Inventory
import com.grinder.util.Misc
import com.grinder.util.time.SecondsTimer
import com.grinder.util.tools.DupeDetector.check
import org.apache.logging.log4j.LogManager
import java.util.concurrent.TimeUnit

/**
 * Represents a controller for the argued [player] that handles trading.
 *
 * TODO: add documentation
 *
 * @author Swiffy
 * @author Stan van der Bend
 */
class TradeController(private val player: Player) {

    companion object {
        private val LOGGER = LogManager.getLogger(TradeController::class.java.simpleName)
        init {
            ButtonActions.onClick(3420, 3546) {
                player.trading.acceptTrade()
            }
        }
    }

    val container = TradeContainer(player)
    var interact: Player? = null
    var state = NONE

    private val requestDelay = SecondsTimer()

    /**
     * Requests a trade with [other].
     */
    fun requestTrade(other: Player) {

        if (cannotInitiateTrade(other, player))
            return

        if (other.appearance.bas != null) {
            if (other.appearance.bas.idle == RugMerchant.BAS_ID) {
                return;
            }
        }

        if (state === NONE || state === REQUESTED_TRADE) {

            // Make sure to not allow flooding!
            if (!requestDelay.finished()) {
                val seconds = requestDelay.secondsRemaining()
                player.message("You must wait another " + (if (seconds == 1) "second" else "$seconds seconds") + " before sending more trade requests.")
                return
            }

            val otherTradeController = other.trading
            val otherTradeState = otherTradeController.state
            val otherInteract = otherTradeController.interact

            interact = other
            state = REQUESTED_TRADE

            if (cannotAcceptTrade(other, player))
                return

            var initiateTrade = false

            // Check if target requested a trade with us...
            if (otherTradeState === REQUESTED_TRADE) {
                if (otherInteract != null && otherInteract === player)
                    initiateTrade = true
            }

            if (initiateTrade) {
                initiateTrade()
                otherTradeController.initiateTrade()
            } else {
                player.message("You've sent a trade request to " + other.username + ".")
                other.message(player.username + ":tradereq:")
            }
            requestDelay.start(1)
        } else
            player.message("You can't do that right now.")
    }

    fun initiateTrade() {

        player.status = PlayerStatus.TRADING
        state = TRADE_SCREEN

        player.message("Sending trade offer...")

        // Update strings on interface
        player.packetSender.sendString(TradeConstants.STATUS_FRAME_1, "")
        player.packetSender.sendString(TradeConstants.TRADING_WITH_FRAME, "Trading with: " + interact!!.username)
        player.packetSender.sendString(TradeConstants.TRADING_WITH_FRAME_2, interact!!.username).sendString(TradeConstants.TRADING_WITH_FRAME_3, interact!!.username)
        player.packetSender.sendString(TradeConstants.FREE_SLOTS_FRAME, "has " + interact!!.inventory.countFreeSlots() + " free")

        container.resetItems()
        container.refreshItems()
    }

    fun closeTrade() {

        if (state !== NONE) {

            val other = interact

            for (item in container.validItems) {
                ItemContainerUtil.switchItem(container, player.inventory, item.clone(), false, false)
            }

            player.inventory.refreshItems()
            resetAttributes()
            player.packetSender.sendInterfaceRemoval()

            if (other != null) {
                val otherTradeController = other.trading
                val otherInteract = otherTradeController.interact
                if (other.status === PlayerStatus.TRADING) {
                    if (otherInteract != null && otherInteract === player) {
                        player.message("You have declined the trade.")
                        other.message("Other player declined trade.")
                        other.removeInterfaces()
                    }
                }
            }
        }
    }

    fun acceptTrade() {

        if (TradeUtil.cannotContinueState(player, interact,
                        TRADE_SCREEN,
                        ACCEPTED_TRADE_SCREEN,
                        CONFIRM_SCREEN,
                        ACCEPTED_CONFIRM_SCREEN))
            return

        val other = interact!!

        if (player.gameMode.isAnyIronman && (/*!interact?.username.equals("Mod Hellmage") && */!PlayerUtil.isDeveloper(interact) && !player.username.equals("Lord Hunterr")
                    && !other.username.equals("Lord Hunterr")))
            return

        if (player.gameMode.isSpawn && !interact?.gameMode?.isSpawn!!) {
            return;
        }

        if (player.minigame != null || player.area == MinigameManager.BATTLE_ROYALE.boundaries() || player.area == MinigameManager.WEAPON_GAME.boundaries())
            return

        val otherInventory = other.inventory
        val otherTradeController = other.trading
        val otherTradeState = otherTradeController.state

        // Check which action to take..
        if (state === TRADE_SCREEN) {

            if (!player.passedTime(Attribute.GENERIC_ACTION, 2, TimeUnit.SECONDS, message = false, updateIfPassed = true))
                return

            // Verify that the interact can receive all items first..
            var slotsNeeded = 0
            for (item in container.validItems) {
                slotsNeeded += if (item.definition.isStackable && otherInventory.contains(item.id)) 0 else 1
            }

            val otherFreeSlots = otherInventory.countFreeSlots()
            if (slotsNeeded > otherFreeSlots) {
                player.message("@or3@" + other.username + " will not be able to hold that much items. They have " + otherFreeSlots + " free inventory slot" + (if (otherFreeSlots == 1) "." else "s") + ".")
                other.message("Trade can't be accepted, you don't have enough free inventory space.")
                return
            }

            state = ACCEPTED_TRADE_SCREEN

            // Update status...
            player.packetSender.sendString(TradeConstants.STATUS_FRAME_1, "Waiting for other player..")
            other.packetSender.sendString(TradeConstants.STATUS_FRAME_1, "" + player.username + " has accepted.")

            // Check if both have accepted..
            if (state === ACCEPTED_TRADE_SCREEN && otherTradeState === ACCEPTED_TRADE_SCREEN) {
                confirmScreen()
                otherTradeController.confirmScreen()
            }
        } else if (state === CONFIRM_SCREEN) {

            if (!player.passedTime(Attribute.GENERIC_ACTION, 1, TimeUnit.SECONDS, message = false, updateIfPassed = true))
                return

            // Both are in the same state. Do the second-stage accept.
            state = ACCEPTED_CONFIRM_SCREEN



            // Update status...
            player.packetSender.sendString(TradeConstants.STATUS_FRAME_2, "Waiting for other player...")
            other.packetSender.sendString(TradeConstants.STATUS_FRAME_2, "Other player has accepted!")

            // Check if both have accepted..
            if (state === ACCEPTED_CONFIRM_SCREEN && otherTradeState === ACCEPTED_CONFIRM_SCREEN) {

                TradeUtil.logTradedItems(player, other)

                for (item in other.trading.container.validItems)
                    player.inventory.add(item)

                for (item in container.validItems)
                    other.inventory.add(item)

                resetAttributes()
                otherTradeController.resetAttributes()

                player.removeInterfaces()
                other.removeInterfaces()
                player.message("Accepted trade!")
                other.message("Accepted trade!")

                player.points.increase(AttributeManager.Points.TRADES_COMPLETED, 1) // Increase points
                other.points.increase(AttributeManager.Points.TRADES_COMPLETED, 1) // Increase points


                checkVotingTickets(player)
                check(player)
                check(other)
                PlayerSaving.save(player)
                PlayerSaving.save(other)
            }
        }
    }

    private fun confirmScreen() {

        state = CONFIRM_SCREEN
        player.packetSender.sendString(TradeConstants.STATUS_FRAME_2, "Are you sure you want to make this trade?")

        player.packetSender.sendInterfaceSet(TradeConstants.CONFIRM_SCREEN_INTERFACE, TradeConstants.CONTAINER_INVENTORY_INTERFACE)
        player.packetSender.sendItemContainer(player.inventory, TradeConstants.INVENTORY_CONTAINER_INTERFACE)

        val playerItems = ItemContainerUtil.listItems(container)
        val otherItems = ItemContainerUtil.listItems(interact!!.trading.container)
        player.packetSender.sendString(TradeConstants.ITEM_LIST_1_FRAME, playerItems)
        player.packetSender.sendString(TradeConstants.ITEM_LIST_2_FRAME, otherItems)
    }

    fun moveItem(id: Int, amount: Int, slot: Int, from: ItemContainer, to: ItemContainer?) {
        var amountToMove = amount
        if (player.interfaceId == TradeConstants.INTERFACE) {

            val other = interact!!
            var otherTradeState = other.trading.state

            if (other.gameMode.isAnyIronman && (!player.username.equals("Lord Hunterr") && /*!player.username.equals("Mod Hellmage") && */!PlayerUtil.isDeveloper(player))) {
                player.message("You can't trade items when trading with an Iron Man.")
                return
            }

            if (player.gameMode.isAnyIronman && (!other.username.equals("Lord Hunterr") &&/*!other.username.equals("Mod Hellmage") && */!PlayerUtil.isDeveloper(other))) {
                player.message("You can't trade items as an Iron Man.")
                return
            }
            if (player.gameMode.isSpawn && !other.gameMode.isSpawn) {
                player.sendMessage("You can only trade with spawn game mode players.")
                return;
            }
            if (other.gameMode.isSpawn && !player.gameMode.isSpawn) {
                player.sendMessage("You can only trade with spawn game mode players.")
                return;
            }

            if (TradeUtil.cannotContinueState(player, interact, TRADE_SCREEN, ACCEPTED_TRADE_SCREEN)) {
                LOGGER.warn("Could not continue item deposit/withdrawal")
                return
            }

            // Check if the trade was previously accepted (and now modified)...
            var modified = false
            if (state === ACCEPTED_TRADE_SCREEN) {
                state = TRADE_SCREEN
                modified = true
            }
            if (otherTradeState === ACCEPTED_TRADE_SCREEN) {
                other.trading.state = TRADE_SCREEN
                otherTradeState = other.trading.state
                modified = true
            }
            if (modified) {
                player.packetSender.sendString(TradeConstants.STATUS_FRAME_1, "@red@TRADE MODIFIED!")
                other.packetSender.sendString(TradeConstants.STATUS_FRAME_1, "@red@TRADE MODIFIED!")
            }

            // Handle the item switch..
            if (state === TRADE_SCREEN && otherTradeState === TRADE_SCREEN) {

                // Check if the item is in the right place
                if (from.items[slot].id == id) {

                    val itemDefinition = ItemDefinition.forId(id)

                    if (!PlayerUtil.isDeveloper(player)) {
                        if (TradeUtil.itemNotTradeable(player, itemDefinition))
                            return
                    }

                    // Make sure we can fit that amount in the trade
                    if (from is Inventory) {
                        if (!itemDefinition.isStackable) {
                            if (amountToMove > container.countFreeSlots()) {
                                amountToMove = container.countFreeSlots()
                            }
                        }
                    }

                    if (amountToMove <= 0)
                        return

                    val item = Item(id, amountToMove)

                    // Do the switch!
                    if (item.amount == 1) from.moveItemFromSlot(to, item, slot, false, true) else ItemContainerUtil.switchItem(from, to, item, false, true)

                    // Update value frames for both players
                    var containerValue = ItemContainerUtil.readValueOfContents(container)
                    if (containerValue != "Too high!") containerValue = Misc.insertCommasToNumber(containerValue)
                    var otherContainerValue = ItemContainerUtil.readValueOfContents(other.trading.container)
                    if (otherContainerValue != "Too high!") otherContainerValue = Misc.insertCommasToNumber(otherContainerValue)
                    player.packetSender.sendString(TradeConstants.ITEM_VALUE_1_FRAME, "You're about to give:\\n(Value: @whi@$containerValue@yel@ Gp)")
                    player.packetSender.sendString(TradeConstants.ITEM_VALUE_2_FRAME, "In return you will receive:\\n(Value: @whi@$otherContainerValue@yel@ Gp)")
                    other.packetSender.sendString(TradeConstants.ITEM_VALUE_1_FRAME, "In return you will receive:\\n(Value: @whi@$otherContainerValue@yel@ Gp)")
                    other.packetSender.sendString(TradeConstants.ITEM_VALUE_2_FRAME, "You're about to give:\\n(Value: @whi@$containerValue@yel@ Gp)")

                    // Update free slots frame for other player
                    other.packetSender.sendString(TradeConstants.FREE_SLOTS_FRAME, "has " + player.inventory.countFreeSlots() + " free")
                }
            } else
                player.removeInterfaces()
        }
    }

    fun resetAttributes() {

        interact = null
        state = NONE

        if (player.status === PlayerStatus.TRADING)
            player.status = PlayerStatus.NONE

        container.resetItems()
        player.packetSender.sendString(TradeConstants.ITEM_VALUE_1_FRAME, "You're about to give:\\n(Value: @whi@0@yel@ Gp)")
        player.packetSender.sendString(TradeConstants.ITEM_VALUE_2_FRAME, "In return you will receive:\\n(Value: @whi@0@yel@ Gp)")
        player.packetSender.sendItemContainer(container, TradeConstants.CONTAINER_INTERFACE_ID)
    }
}