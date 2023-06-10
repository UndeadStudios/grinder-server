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
object ExpeditiousBracelet {

    init {
        onSecondInventoryAction(ItemID.EXPEDITIOUS_BRACELET) {

            val charges = player.getInt(Attribute.EXPEDITIOUS_BRACELET_CHARGES)

            DialogueBuilder(DialogueType.STATEMENT)
                    .setText("Your bracelet has $charges charges left.", "Are you absolutely sure you want break it?")
                    .add(DialogueType.OPTION)
                    .firstOption("Break the bracelet.") { player ->
                        player.removeInventoryItem(Item(ItemID.EXPEDITIOUS_BRACELET, 1))
                        player.setInt(Attribute.EXPEDITIOUS_BRACELET_CHARGES, 30)
                        player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
                        player.itemStatement(ItemID.EXPEDITIOUS_BRACELET, 200,
                                "The bracelet shatters. Your next bracelet of slaughter", "will start afresh from 30 charges.")
                    }
                    .addCancel("Cancel.")
                    .start(player)
        }
        onThirdInventoryAction(ItemID.EXPEDITIOUS_BRACELET){
            checkCharges(player)
        }
        onSecondContainerEquipmentAction(ItemID.EXPEDITIOUS_BRACELET){
            checkCharges(player)
        }
    }

    private fun checkCharges(player: Player) {
        val charges = player.getInt(Attribute.EXPEDITIOUS_BRACELET_CHARGES, 30)
        player.message("Your expeditious bracelet has $charges charges left.")
    }

    fun handleExpeditiousBracelet(player: Player) {
        var charges = player.getInt(Attribute.EXPEDITIOUS_BRACELET_CHARGES, 30)
        if (player.equipment.items[EquipmentConstants.HANDS_SLOT].id == ItemID.EXPEDITIOUS_BRACELET && charges > 0) {
            if (Misc.randomChance(25F)) {
                charges = player.decInt(Attribute.EXPEDITIOUS_BRACELET_CHARGES, 1)
                player.slayer.task.amountLeft = player.slayer.task.amountLeft - 1
                player.message("Your expeditious bracelet decreases your slayer count" +
                        ": @red@It has " + (if (charges == 1) "one" else "$charges charges") + " left.")
                if (charges <= 0) { // No charges so crumble to dust
                    player.removeEquipmentItem(Item(ItemID.EXPEDITIOUS_BRACELET, 1))
                    player.setInt(Attribute.EXPEDITIOUS_BRACELET_CHARGES, 30)
                    player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
                    player.message("Your bracelet of slaughter crumbles to dust.")
                }
            }
        }
    }
}