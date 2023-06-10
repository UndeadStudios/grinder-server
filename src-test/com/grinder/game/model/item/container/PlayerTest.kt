package com.grinder.game.model.item.container

import com.grinder.GrinderPlayerTest
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.player.Inventory
import com.grinder.net.packet.impl.EquipPacketListener
import com.grinder.util.ItemID
import com.grinder.util.oldgrinder.EquipSlot
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PlayerTest : GrinderPlayerTest("player_test") {

    @BeforeEach
    fun setUp() {
        player.randomizeLevels()
        player.isPrintMessages = true
    }

    @Test
    fun equipItems() {
        val inventory = player.inventory
        inventory.add(ItemID.RUNE_ARROW, 1000)
        inventory.add(ItemID.DARK_BOW, 1)
        val equipment = player.equipment
        equipment[EquipSlot.ARROWS] = Item(ItemID.RUNITE_BOLTS, 1000)
        equipment[EquipSlot.WEAPON] = Item(ItemID.ARMADYL_CROSSBOW, 1)
        EquipPacketListener.equip(player, ItemID.DARK_BOW, inventory.getSlot(ItemID.DARK_BOW), Inventory.INTERFACE_ID)
        Assertions.assertEquals(ItemID.DARK_BOW, equipment[EquipSlot.WEAPON].id)
        Assertions.assertTrue(inventory.contains(ItemID.ARMADYL_CROSSBOW))
        EquipPacketListener.equip(player, ItemID.RUNE_ARROW, inventory.getSlot(ItemID.RUNE_ARROW), Inventory.INTERFACE_ID)
        Assertions.assertEquals(ItemID.RUNE_ARROW, equipment[EquipSlot.ARROWS].id)
        Assertions.assertTrue(inventory.contains(ItemID.RUNITE_BOLTS))
    }
}