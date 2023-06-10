package com.grinder.game.content.trading

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.item.container.ItemContainer
import com.grinder.game.model.item.container.StackType

/**
 * Represents an [ItemContainer] used by the [TradeController].
 *
 * @author Swiffy
 */
class TradeContainer(player: Player) : ItemContainer(player) {

    override fun full(): ItemContainer {
        player.message("You can't trade more items.")
        return this
    }

    override fun capacity() = 28

    override fun stackType() = StackType.DEFAULT

    override fun refreshItems(): ItemContainer {
        player.packetSender.sendInterfaceSet(TradeConstants.INTERFACE, TradeConstants.CONTAINER_INVENTORY_INTERFACE)
        player.packetSender.sendItemContainer(this, TradeConstants.CONTAINER_INTERFACE_ID)
        player.packetSender.sendItemContainer(player.inventory, TradeConstants.INVENTORY_CONTAINER_INTERFACE)
        val interact = player.trading.interact
        if (interact != null) {
            player.packetSender.sendItemContainer(interact.trading.container, TradeConstants.CONTAINER_INTERFACE_ID_2)
            interact.packetSender.sendItemContainer(player.trading.container, TradeConstants.CONTAINER_INTERFACE_ID_2)
        }
        return this
    }

}