package com.grinder.game.content.item.charging.impl

import com.grinder.GrinderPlayerTest
import com.grinder.game.World
import com.grinder.game.entity.agent.player.addExperience
import com.grinder.game.message.impl.DropItemMessage
import com.grinder.game.model.Skill
import com.grinder.game.model.item.AttributableItem
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.player.Inventory
import com.grinder.net.packet.impl.DropItemPacketListener
import com.grinder.net.packet.impl.EquipPacketListener
import com.grinder.util.ItemID
import com.grinder.util.ServerClassPreLoader
import com.grinder.util.oldgrinder.EquipSlot
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   25/09/2020
 */
internal class BlowpipeTest : GrinderPlayerTest("blopwipe"){


    @BeforeEach
    fun setUp() {
        ServerClassPreLoader.forceInit(Blowpipe::class.java)
        player.addExperience(Skill.RANGED, 13_000_000)
    }


    @Test
    fun testCharging(){
        val unchargedBlowpipe = Item(Blowpipe.UNCHARGED, 1)
        player.inventory[0] = unchargedBlowpipe
        player.inventory[1] = Item(ItemID.ZULRAHS_SCALES, 100)
        Blowpipe.charge(player, ItemID.ZULRAHS_SCALES, unchargedBlowpipe.id, 0)

        Assertions.assertTrue(player.inventory[0] is AttributableItem)
        Assertions.assertEquals(0, player.inventory[1].amount)
        Assertions.assertEquals(100, player.inventory[0].asAttributable.getAttribute(Blowpipe.SCALES))

        player.inventory[2] = Item(ItemID.DRAGON_DART, 100)
        Blowpipe.charge(player, ItemID.DRAGON_DART, Blowpipe.CHARGED, 0)

        Assertions.assertTrue(player.inventory[0] is AttributableItem)
        Assertions.assertEquals(0, player.inventory[2].amount)
        Assertions.assertEquals(ItemID.DRAGON_DART, player.inventory[0].asAttributable.getAttribute(Blowpipe.DARTS_TYPE))
        Assertions.assertEquals(100, player.inventory[0].asAttributable.getAttribute(Blowpipe.DARTS))
    }

    @Test
    fun testEquipping(){
        val blowpipe1 = AttributableItem(Blowpipe.CHARGED, 1)
        blowpipe1.setAttribute(Blowpipe.SCALES, 100)
        blowpipe1.setAttribute(Blowpipe.DARTS, 100)
        blowpipe1.setAttribute(Blowpipe.DARTS_TYPE, ItemID.DRAGON_DART)
        val blowpipe2 = AttributableItem(Blowpipe.CHARGED, 1)
        blowpipe2.setAttribute(Blowpipe.SCALES, 1)
        blowpipe2.setAttribute(Blowpipe.DARTS, 1)
        blowpipe2.setAttribute(Blowpipe.DARTS_TYPE, ItemID.DRAGON_DART)

        player.inventory[0] = blowpipe1
        player.inventory[1] = blowpipe2

        EquipPacketListener.equip(player, blowpipe2.id, 1, Inventory.INTERFACE_ID)

        var equippedBlowpipe = player.equipment[EquipSlot.WEAPON]?.asAttributable

        Assertions.assertNotNull(equippedBlowpipe)
        Assertions.assertTrue(equippedBlowpipe!!.getAttribute(Blowpipe.SCALES) == 1)
        Assertions.assertTrue(equippedBlowpipe.getAttribute(Blowpipe.DARTS) == 1)
        Assertions.assertTrue(equippedBlowpipe.getAttribute(Blowpipe.DARTS_TYPE) == ItemID.DRAGON_DART)

        Assertions.assertTrue(!player.inventory[1].isValid)

        EquipPacketListener.equip(player, blowpipe1.id, 0, Inventory.INTERFACE_ID)

        equippedBlowpipe = player.equipment[EquipSlot.WEAPON]?.asAttributable

        Assertions.assertNotNull(equippedBlowpipe)
        Assertions.assertTrue(equippedBlowpipe!!.getAttribute(Blowpipe.SCALES) == 100)
        Assertions.assertTrue(equippedBlowpipe.getAttribute(Blowpipe.DARTS) == 100)
        Assertions.assertTrue(equippedBlowpipe.getAttribute(Blowpipe.DARTS_TYPE) == ItemID.DRAGON_DART)

        val inventoryBlowpipe = player.inventory[0]?.asAttributable
        Assertions.assertNotNull(inventoryBlowpipe)
        Assertions.assertTrue(inventoryBlowpipe!!.getAttribute(Blowpipe.SCALES) == 1)
        Assertions.assertTrue(inventoryBlowpipe.getAttribute(Blowpipe.DARTS) == 1)
        Assertions.assertTrue(inventoryBlowpipe.getAttribute(Blowpipe.DARTS_TYPE) == ItemID.DRAGON_DART)
    }

    @Test
    fun testDrop(){
        val blowpipe1 = AttributableItem(Blowpipe.CHARGED, 1)
        blowpipe1.setAttribute(Blowpipe.SCALES, 100)
        blowpipe1.setAttribute(Blowpipe.DARTS, 100)
        blowpipe1.setAttribute(Blowpipe.DARTS_TYPE, ItemID.DRAGON_DART)
        player.inventory[0] = blowpipe1
        DropItemPacketListener.handleDropItemMessage(player, DropItemMessage(Blowpipe.CHARGED, Inventory.INTERFACE_ID, 0))
        player.dialogue?.nextDialogue()
        player.dialogueOptions.handleOption(player, 1)

        Assertions.assertFalse(player.inventory[0] == blowpipe1)
        Assertions.assertTrue(World.groundItems.any { it.item.id == Blowpipe.UNCHARGED })
    }
}