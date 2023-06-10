package com.grinder.game.content.item.charging.impl

import com.grinder.GrinderPlayerTest
import com.grinder.game.model.item.AttributableItem
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Represents a test for [AncientWyvernShield].
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/09/2020
 */
internal class AncientWyvernShieldTest : GrinderPlayerTest("ancient_wyvern"){

    @Test
    fun testGetCharges(){
        val shield = AttributableItem(AncientWyvernShield.CHARGED, 1)
        Assertions.assertEquals(0, AncientWyvernShield.getCharges(shield))
        shield.setAttribute(AncientWyvernShield.CHARGES, 10)
        Assertions.assertEquals(10, AncientWyvernShield.getCharges(shield))
    }

    @Test
    fun testChargingChargedShield(){

        val shield = AttributableItem(AncientWyvernShield.CHARGED, 1)

        player.inventory[0] = shield
        player.inventory[1] = Item(ItemID.NUMULITES, 500)

        AncientWyvernShield.charge(player, ItemID.NUMULITES, shield.id, 0)

        Assertions.assertEquals(1, AncientWyvernShield.getCharges(shield))
        Assertions.assertFalse(player.inventory.contains(ItemID.NUMULITES))
    }

    @Test
    fun testChargingUnchargedShield(){

        var shield = Item(AncientWyvernShield.UNCHARGED, 1)

        player.inventory[0] = shield
        player.inventory[1] = Item(ItemID.NUMULITES, 500)

        AncientWyvernShield.charge(player, ItemID.NUMULITES, shield.id, 0)

        shield = player.inventory[0]

        Assertions.assertTrue(shield is AttributableItem)
        Assertions.assertTrue(shield.id == AncientWyvernShield.CHARGED)
        Assertions.assertEquals(1, AncientWyvernShield.getCharges(shield))
        Assertions.assertFalse(player.inventory.contains(ItemID.NUMULITES))
    }
}