package com.grinder.game.content.dueling

import com.grinder.game.content.trading.TradeConstants
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.item.container.ItemContainer
import com.grinder.game.model.item.container.StackType

/**
 * Represents an [ItemContainer] used to store items for staking.
 *
 * @see DuelController for the duel mechanics
 */
class DuelStakeContainer(player: Player) : ItemContainer(player) {

    override fun capacity() = 28

    override fun stackType() = StackType.DEFAULT

    override fun full(): ItemContainer? {
        player.message("You can't stake more items.")
        return this
    }

    override fun refreshItems(): ItemContainer? {
        val interact = player.dueling.interact
        player.packetSender.sendInterfaceSet(DuelConstants.INTERFACE_ID, TradeConstants.CONTAINER_INVENTORY_INTERFACE)
        player.packetSender.sendItemContainer(player.inventory, TradeConstants.INVENTORY_CONTAINER_INTERFACE)
        player.packetSender.sendInterfaceItems(DuelConstants.MAIN_INTERFACE_CONTAINER, validItems)
        player.packetSender.sendInterfaceItems(DuelConstants.SECOND_INTERFACE_CONTAINER, interact.dueling.container.validItems)
        interact.packetSender.sendInterfaceItems(DuelConstants.MAIN_INTERFACE_CONTAINER, interact.dueling.container.validItems)
        interact.packetSender.sendInterfaceItems(DuelConstants.SECOND_INTERFACE_CONTAINER, validItems)
        return this
    }
}