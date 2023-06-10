package com.grinder.game.content.item.jewerly

import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.game.entity.decInt
import com.grinder.game.entity.getInt
import com.grinder.game.entity.setInt
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.player.Equipment
import com.grinder.game.model.onFirstContainerEquipmentAction
import com.grinder.game.model.onSecondContainerEquipmentAction
import com.grinder.game.model.onSecondInventoryAction
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID
import kotlin.math.ceil

/**
 * Handles the bracelet of clay mechanics.
 *
 */
object BraceletOfClay {

    init {
        onSecondContainerEquipmentAction(ItemID.BRACELET_OF_CLAY) {
            if(player.busy()){
                player.message("You can't do that when you're busy.")
                return@onSecondContainerEquipmentAction
            }
            val chargesLeft = player.getInt(Attribute.BRACELET_OF_CLAY_CHARGES)
            player.sendMessage("Your bracelet of clay still has @dre@$chargesLeft</col> more uses before it disintegrates.")
        }
    }

    fun handleBraceletOfClay(equipment: Equipment, reductionAmount: Int, actor: Player) {

        var charges = actor.getInt(Attribute.BRACELET_OF_CLAY_CHARGES, 28)

        if (actor.equipment.items[EquipmentConstants.HANDS_SLOT].id == ItemID.BRACELET_OF_CLAY && charges > 0) {
            charges = actor.decInt(Attribute.BRACELET_OF_CLAY_CHARGES, reductionAmount)
        }

        if (charges <= 0) {
            actor.removeEquipmentItem(Item(ItemID.BRACELET_OF_CLAY, 1))
            equipment.refreshItems()
            actor.message("Your bracelet of clay disintegrates.")
            actor.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
            actor.setInt(Attribute.BRACELET_OF_CLAY_CHARGES, 28)
        }
    }
}