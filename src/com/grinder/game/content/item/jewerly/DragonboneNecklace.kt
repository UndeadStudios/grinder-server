package com.grinder.game.content.item.jewerly

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.markTime
import com.grinder.game.entity.passedTime
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.onEquipAction
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.TaskManager
import com.grinder.net.packet.impl.EquipPacketListener
import com.grinder.util.ItemID
import com.grinder.util.oldgrinder.EquipSlot
import java.util.concurrent.TimeUnit

/**
 * Handles the Dragonbone necklace mechanics.
 *
 * TODO: find sound after timer is over
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 */
object DragonboneNecklace {

    private const val name = "Dragonbone necklace"

    init {

        onEquipAction(ItemID.DRAGONBONE_NECKLACE) {
            if (player.getBoolean(Attribute.BONECRUSHER_NECKLACE_ACTIVE)) {
                player.markTime(Attribute.DRAGONBONE_NECKLACE_WEAR_TIMER)
                val timer = player.attributes[Attribute.DRAGONBONE_NECKLACE_WEAR_TIMER] ?: return@onEquipAction
                TaskManager.cancelTasks(timer)
                TaskManager.submit(timer, 9) {
                    if (player.equipment.containsAtSlot(EquipSlot.AMULET, ItemID.DRAGONBONE_NECKLACE)) {
                        player.packetSender.sendSound(Sounds.DRAGONBONE_NECKLACE_ACTIVE_TIMER)
                        player.message("Your $name restoration effect is now active!")
                    }
                }
            }
            EquipPacketListener.equip(this)
        }
    }

    fun Player.hasDragonbonerNecklaceEffect() = equipment.containsAtSlot(EquipSlot.AMULET, ItemID.DRAGONBONE_NECKLACE)
            && passedTime(Attribute.DRAGONBONE_NECKLACE_WEAR_TIMER, 9, TimeUnit.SECONDS, false)
}