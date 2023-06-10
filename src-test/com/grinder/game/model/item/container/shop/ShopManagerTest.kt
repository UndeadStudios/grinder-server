package com.grinder.game.model.item.container.shop

import com.grinder.GrinderPlayerTest
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ShopManagerTest : GrinderPlayerTest("shop_manager_test"){

    @Test
    fun miniBMTrader() {
        val shop = ShopManager.shops[49]
        ShopManager.open(player, 49)
        player.inventory.add(Item(ItemID.BLOOD_MONEY, 1000000), false)
        val slot = shop!!.getSlot(14162, true)
        ShopManager.buyItem(player, slot, 14162, 1)
        Assertions.assertEquals(1000, player.inventory.getAmount(386))
    }
}