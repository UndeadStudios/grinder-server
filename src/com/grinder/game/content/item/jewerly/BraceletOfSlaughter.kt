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
import com.grinder.game.model.onSecondContainerEquipmentAction
import com.grinder.game.model.onSecondInventoryAction
import com.grinder.game.model.onThirdInventoryAction
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID
import com.grinder.util.Misc

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   09/09/2020
 */
object BraceletOfSlaughter {

    init {
        onSecondInventoryAction(ItemID.BRACELET_OF_SLAUGHTER) {
            val charges = player.getInt(Attribute.BRACELET_OF_SLAUGHTER_CHARGES)
            DialogueBuilder(DialogueType.STATEMENT)
                    .setText("Your bracelet has $charges charges left.", "Are you absolutely sure you want break it?")
                    .add(DialogueType.OPTION)
                    .firstOption("Break the bracelet.") { player ->
                        player.removeInventoryItem(Item(21177, 1))
                        player.setInt(Attribute.BRACELET_OF_SLAUGHTER_CHARGES, 30)
                        player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
                        player.itemStatement(21177, 200,
                                "The bracelet shatters. Your next bracelet of slaughter", "will start afresh from 30 charges.")
                    }
                    .addCancel("Cancel.")
                    .start(player)
        }
        onThirdInventoryAction(ItemID.BRACELET_OF_SLAUGHTER){
            checkCharges(player)
        }
        onSecondContainerEquipmentAction(ItemID.BRACELET_OF_SLAUGHTER){
            checkCharges(player)
        }
    }

    private fun checkCharges(player: Player) {
        val charges = player.getInt(Attribute.BRACELET_OF_SLAUGHTER_CHARGES, 30)
        player.message("Your bracelet of slaughter has $charges charges left.")
    }

    fun handleSlaughterEffect(player: Player) : Boolean {
        var charges = player.getInt(Attribute.BRACELET_OF_SLAUGHTER_CHARGES, 30)
        if(player.equipment.items[EquipmentConstants.HANDS_SLOT].id == ItemID.BRACELET_OF_SLAUGHTER && charges > 0) {
            if (Misc.randomChance(25F)) {
                charges = player.decInt(Attribute.BRACELET_OF_SLAUGHTER_CHARGES, 1)
                player.message("Your bracelet of slaughter prevents your slayer count decreasing" +
                        ": @red@It has " + (if (charges == 1) "one" else "$charges charges") + " left.")
                if (charges <= 0) { // No charges so crumble to dust
                    player.removeEquipmentItem(Item(ItemID.BRACELET_OF_SLAUGHTER, 1))
                    player.setInt(Attribute.BRACELET_OF_SLAUGHTER_CHARGES, 30)
                    player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
                    player.message("Your bracelet of slaughter crumbles to dust.")
                }
                return true
            }
        }
        return false
    }
}