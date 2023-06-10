package com.grinder.game.content.item.charging.impl

import com.grinder.GrinderPlayerTest
import com.grinder.game.GameConstants
import com.grinder.game.entity.agent.combat.event.impl.DragonFireEvent
import com.grinder.game.entity.agent.npc.NPCFactory
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.bot.BotPlayer
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.game.message.impl.ItemContainerActionMessage
import com.grinder.game.model.ItemActions
import com.grinder.game.model.item.AttributableItem
import com.grinder.game.model.item.Item
import com.grinder.net.packet.PacketConstants
import com.grinder.util.ServerClassPreLoader
import com.grinder.util.oldgrinder.EquipSlot
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Represents a test for [DragonFireShield].
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/09/2020
 */
internal class DragonFireShieldTest : GrinderPlayerTest("dragonfire_shield"){

    @BeforeEach
    fun setUp() {
        ServerClassPreLoader.forceInit(DragonFireShield::class.java)
    }

    @Test
    fun testGetCharges(){
        val scythe = AttributableItem(DragonFireShield.CHARGED, 1)
        Assertions.assertEquals(0, DragonFireShield.getCharges(scythe))
        scythe.setAttribute(DragonFireShield.CHARGES, 10)
        Assertions.assertEquals(10, DragonFireShield.getCharges(scythe))
    }

    @Test
    fun testCharging(){

        val shield = AttributableItem(DragonFireShield.CHARGED, 1)

        player.equipment[EquipSlot.SHIELD] = shield
        player.combat.submit(DragonFireEvent())

        Assertions.assertEquals(1, DragonFireShield.getCharges(shield))
    }

    @Test
    fun testFiring(){

        var shield : Item = AttributableItem(DragonFireShield.CHARGED, 1).also {
            it.setAttribute(DragonFireShield.CHARGES, 1)
        }

        player.equipment[EquipSlot.SHIELD] = shield
        player.combat.target = NPCFactory.create(1, player.position.clone().add(1, 0))

        ItemActions.handleClick(player, ItemContainerActionMessage(shield.id, EquipSlot.SHIELD, EquipmentConstants.INVENTORY_INTERFACE_ID, PacketConstants.SECOND_ITEM_CONTAINER_ACTION_OPCODE))

        shield = player.equipment[EquipSlot.SHIELD]
        Assertions.assertTrue(shield.id == DragonFireShield.UNCHARGED)
        Assertions.assertEquals(0, DragonFireShield.getCharges(shield))
    }
}