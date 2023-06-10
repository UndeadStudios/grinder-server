package com.grinder.game.content.item.jewerly

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.removeInterfaces
import com.grinder.game.entity.agent.player.removeInventoryItem
import com.grinder.game.entity.getInt
import com.grinder.game.entity.setInt
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.player.Equipment
import com.grinder.game.model.onSecondInventoryAction
import com.grinder.util.ItemID
import kotlin.math.ceil

/**
 * Handles the ring of recoil mechanics.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   24/11/2020
 * @version 1.0
 */
object RingOfRecoil {

    init {
        onSecondInventoryAction(ItemID.RING_OF_RECOIL) {
            if(player.busy()){
                player.message("You can't do that when you're busy.")
                return@onSecondInventoryAction
            }
            val chargesLeft = 40 - player.getInt(Attribute.RING_OF_RECOIL_CHARGES)
            DialogueBuilder(DialogueType.STATEMENT)
                    .setText("You still have @dre@$chargesLeft</col> damage before it breaks. Continue?")
                    .add(DialogueType.OPTION)
                    .firstOption("Yes.") {
                        it.removeInterfaces()
                        if(it.removeInventoryItem(Item(2550, 1))){
                            it.attributes.reset(Attribute.RING_OF_RECOIL_CHARGES)
                            it.message("Your Ring of recoil has degraded.")
                        }
                    }
                    .addCancel("No.")
        }
    }

    fun handleRecoil(equipment: Equipment, damage: Int, recoilDamagePercentage: Double, recoilDamageExtra: Int, actor: Player) {
        val recoilDamage = ceil(damage * recoilDamagePercentage).toInt() + recoilDamageExtra
        val totalRecoilDamage: Int = actor.getInt(Attribute.RING_OF_RECOIL_CHARGES) + recoilDamage
        /*
         * The ring of recoil is capable of dealing
         * up to 40 hitpoints of damage before shattering.
         */
        if (totalRecoilDamage >= 40) {
            equipment.reset(EquipmentConstants.RING_SLOT)
            equipment.refreshItems()
            actor.message("Your ring of recoil has shattered.")
            actor.attributes.reset(Attribute.RING_OF_RECOIL_CHARGES)
        } else
            actor.setInt(Attribute.RING_OF_RECOIL_CHARGES, totalRecoilDamage)
    }
}