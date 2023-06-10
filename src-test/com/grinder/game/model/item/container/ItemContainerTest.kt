package com.grinder.game.model.item.container

import com.grinder.GrinderPlayerTest
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.player.Inventory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ItemContainerTest : GrinderPlayerTest("item_container_test") {
    
    var slot = 0
    lateinit var item: Item
    lateinit var container: ItemContainer
    
    @BeforeEach
    fun setUp() {
        slot = 0
        item = Item(995, 1000)
        container = Inventory(player)
        container.items[slot] = item
    }

    @Test
    fun getSlot() {
        val slot = container.getSlot(item)
        Assertions.assertEquals(slot, 0)
    }

    @Test
    fun containsAtSlot() {
        Assertions.assertTrue(container.containsAtSlot(slot, item.id))
    }

    @Test
    fun moveItemFromSlot() {
        val otherContainer: ItemContainer = Inventory(null)
        val preAmountSource = container.getAmount(item)
        container.moveItemFromSlot(otherContainer, item.clone(), slot, false, false)
        val postAmountSource = container.getAmount(item)
        val postAmountTarget = otherContainer.getAmount(item)
        val deltaSource = Math.abs(postAmountSource - preAmountSource)
        Assertions.assertEquals(deltaSource, postAmountTarget)
    }

    @Test
    fun add() {
        container.add(Item(995, 1000))
        Assertions.assertEquals(container.getAmount(995), 2000)
        Assertions.assertEquals(container.getAmount(item), 2000)
        container.add(995, 1000)
        Assertions.assertEquals(container.getAmount(995), 3000)
        Assertions.assertEquals(container.getAmount(item), 3000)
    }

    @Test
    fun delete() {
        container.delete(item)
        Assertions.assertEquals(container.getAmount(995), 0)
        Assertions.assertEquals(container.getAmount(item), 0)
        container.delete(995, 1000)
        Assertions.assertEquals(container.getAmount(995), 0)
        Assertions.assertEquals(container.getAmount(item), 0)
    }

    @Test
    fun testDelete() {
    }
}