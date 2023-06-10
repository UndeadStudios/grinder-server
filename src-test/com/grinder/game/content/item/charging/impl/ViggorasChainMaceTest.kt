package com.grinder.game.content.item.charging.impl

import com.grinder.GrinderPlayerTest
import com.grinder.game.model.item.AttributableItem
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Represents a test for [ViggorasChainmace].
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/09/2020
 */
internal class ViggorasChainMaceTest : GrinderPlayerTest("viggoras_chainmace") {

    @Test
    fun testGetCharges(){
        val scythe = AttributableItem(ViggorasChainmace.CHARGED, 1)
        Assertions.assertEquals(0, ViggorasChainmace.getCharges(scythe))
        scythe.setAttribute(ViggorasChainmace.CHARGES, 10)
        Assertions.assertEquals(10, ViggorasChainmace.getCharges(scythe))
    }

    @Test
    fun testCharge(){

        val scythe = AttributableItem(ViggorasChainmace.CHARGED, 1)

        player.inventory[0] = scythe
        player.inventory[1] = Item(ItemID.REVENANT_ETHER, 1000)

        ViggorasChainmace.charge(player, ItemID.REVENANT_ETHER, ViggorasChainmace.CHARGED, 0)

        Assertions.assertEquals(1000, ViggorasChainmace.getCharges(scythe))
        Assertions.assertFalse(player.inventory.contains(ItemID.REVENANT_ETHER))
    }
}