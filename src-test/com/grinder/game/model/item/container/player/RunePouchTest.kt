package com.grinder.game.model.item.container.player

import com.grinder.GrinderPlayerTest
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RunePouchTest : GrinderPlayerTest("pouch_test") {

    lateinit var pouch: RunePouch

    @BeforeEach
    fun setup(){
        pouch = player.runePouch
    }

    @Test
    fun deposit() {
        val toAdd = Item(ItemID.LAW_RUNE, 10_000)
        player.inventory[0] = toAdd
        pouch.deposit(toAdd.id,0, toAdd.amount)
        Assertions.assertFalse(player.inventory[0]?.id == toAdd.id)
        Assertions.assertTrue(pouch[0]?.id == toAdd.id)
    }

    @Test
    fun withdraw() {
        val toAdd = Item(ItemID.LAW_RUNE, 10_000)
        player.inventory[0] = toAdd
        pouch.withdraw(toAdd.id,0, toAdd.amount)

        val inInventory = player.inventory[0]!!
        Assertions.assertEquals(inInventory.id, toAdd.id)
        Assertions.assertEquals(inInventory.amount, toAdd.amount)
        Assertions.assertFalse(pouch[0]!!.id == toAdd.id)
    }
}