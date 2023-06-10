package com.grinder.game.content.item.charging.impl

import com.grinder.GrinderPlayerTest
import com.grinder.game.model.item.AttributableItem
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Represents a test for [ScytheOfVitur].
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/09/2020
 */
internal class ScytheOfViturTest : GrinderPlayerTest("scytheofvitur"){

    @Test
    fun testGetCharges(){
        val scythe = AttributableItem(ScytheOfVitur.CHARGED, 1)
        Assertions.assertEquals(0, ScytheOfVitur.getCharges(scythe))
        scythe.setAttribute(ScytheOfVitur.CHARGES, 10)
        Assertions.assertEquals(10, ScytheOfVitur.getCharges(scythe))
    }

    @Test
    fun testCharge(){

        val scythe = AttributableItem(ScytheOfVitur.CHARGED, 1)

        player.inventory[0] = scythe
        player.inventory[1] = Item(ItemID.BLOOD_RUNE, 300)
        player.inventory[2] = Item(ScytheOfVitur.VIAL_OF_BLOOD, 1)

        ScytheOfVitur.charge(player, ItemID.BLOOD_RUNE, ScytheOfVitur.CHARGED, 0)

        Assertions.assertEquals(100, ScytheOfVitur.getCharges(scythe))
        Assertions.assertFalse(player.inventory.contains(ItemID.BLOOD_RUNE))
        Assertions.assertFalse(player.inventory.contains(ScytheOfVitur.VIAL_OF_BLOOD))
    }
}