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
 * Represents a test for [DragonFireWard].
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/09/2020
 */
internal class DragonFireWardTest : GrinderPlayerTest("dragonfire_ward"){

    @BeforeEach
    fun setUp() {
        ServerClassPreLoader.forceInit(DragonFireWard::class.java)
    }

    @Test
    fun testGetCharges(){
        val ward = AttributableItem(DragonFireWard.CHARGED, 1)
        Assertions.assertEquals(0, DragonFireWard.getCharges(ward))
        ward.setAttribute(DragonFireWard.CHARGES, 10)
        Assertions.assertEquals(10, DragonFireWard.getCharges(ward))
    }

    @Test
    fun testCharging(){

        val shield = AttributableItem(DragonFireWard.CHARGED, 1)

        player.equipment[EquipSlot.SHIELD] = shield
        player.combat.submit(DragonFireEvent())

        Assertions.assertEquals(1, DragonFireWard.getCharges(shield))
    }

    @Test
    fun testFiring(){

        var shield : Item = AttributableItem(DragonFireWard.CHARGED, 1).also {
            it.setAttribute(DragonFireWard.CHARGES, 1)
        }

        player.equipment[EquipSlot.SHIELD] = shield
        player.combat.target = NPCFactory.create(1, player.position.clone().add(1, 0))

        ItemActions.handleClick(player, ItemContainerActionMessage(shield.id, EquipSlot.SHIELD, EquipmentConstants.INVENTORY_INTERFACE_ID, PacketConstants.SECOND_ITEM_CONTAINER_ACTION_OPCODE))

        shield = player.equipment[EquipSlot.SHIELD]
        Assertions.assertTrue(shield.id == DragonFireWard.UNCHARGED)
        Assertions.assertEquals(0, DragonFireWard.getCharges(shield))
    }
}