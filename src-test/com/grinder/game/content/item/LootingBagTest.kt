package com.grinder.game.content.item

import com.grinder.GrinderPlayerTest
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class LootingBagTest : GrinderPlayerTest("") {

    @Test
    fun `deposit stackable items to bank`(){
        val item = Item(ItemID.COINS, 100)
        player.moveTo(AreaManager.WILD.boundaries().first().centerPosition)
        player.inventory[0] = Item(ItemID.LOOTING_BAG)
        player.lootingBag.container.add(item)
        LootingBag.deposit(player)
        Assertions.assertTrue(player.getBank(0).contains(item))
    }

}