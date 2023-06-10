package com.grinder.net.packet.impl

import com.grinder.GrinderPlayerTest
import com.grinder.game.entity.grounditem.ItemOnGroundManager
import com.grinder.game.message.impl.DropItemMessage
import com.grinder.game.message.impl.PickupItemMessage
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.player.Inventory
import com.grinder.util.ItemID
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

internal class PickupItemPacketListenerTest : GrinderPlayerTest("pickup") {

    @Test
    fun test() {

        val items = Array(8) { Item(ItemID.DRAGON_WHIP, 1) }

        player.inventory.addItems(items, false)

        val dropMessages = items.mapIndexed { index, item ->
            DropItemMessage(item.id, Inventory.INTERFACE_ID, index)
        }

        for (dropMessage in dropMessages) {
            DropItemPacketListener.handleDropItemMessage(player, dropMessage)
            player.dialogue.nextDialogue()
            player.dialogueOptions.handleOption(player, 1)
        }

        val dropPosition = player.position.clone()
        for (i in 0 until 8) {
            val itemOnGround = ItemOnGroundManager.getItemOnGround(Optional.of(player.username), ItemID.DRAGON_WHIP, dropPosition)
            Assertions.assertTrue(itemOnGround.isPresent)
            val pickupItemMessage = PickupItemMessage(player.position.y, ItemID.DRAGON_WHIP, player.position.x)
            PickupItemPacketListener.handleMessage(player, pickupItemMessage)
            player.lastItemPickup.reset(System.currentTimeMillis()-5000)
        }

        val itemOnGround = ItemOnGroundManager.getItemOnGround(Optional.of(player.username), ItemID.DRAGON_WHIP, dropPosition)
        Assertions.assertTrue(itemOnGround.isEmpty)
        Assertions.assertEquals(8, player.inventory.getAmount(ItemID.DRAGON_WHIP))
    }
}