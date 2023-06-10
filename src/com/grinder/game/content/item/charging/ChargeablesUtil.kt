package com.grinder.game.content.item.charging

import com.grinder.game.definition.ItemValueDefinition.Companion.getValue
import com.grinder.game.definition.ItemValueType
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.grounditem.ItemOnGroundManager
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.ItemUtil
import com.grinder.game.model.item.name
import com.grinder.game.model.sound.Sounds
import com.grinder.net.packet.impl.DropItemPacketListener

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   11/09/2020
 */
object ChargeablesUtil {

    fun createDropDialogue(chargeable: Chargeable, dropPolicy: ChargeableDropPolicy, unchargedId: Int, slot: Int, item: Item): DialogueBuilder {
        return DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                .setItem(itemId = item.id, zoom = 200)
                .setText(*chargeable.dropDialogueText())
                .add(DialogueType.OPTION)
                .setOptionTitle("${item.name()}: ${dropPolicy.confirmText}")
                .firstOption("Proceed.") {
                    if(it.hasItemInInventory(item, message = false)){

                        val unchargedItem =  Item(unchargedId, item.amount)

                        it.inventory[it.inventory.getSlot(item)] = if(dropPolicy == ChargeableDropPolicy.DROP_ON_FLOOR)
                            ItemUtil.createInvalidItem()
                        else
                            unchargedItem

                        val charges = chargeable.getCharges(item)
                        if(charges > 0) {
                            chargeable.chargeItemReturnedOnDrop(charges).ifPresent { item ->
                                it.addInventoryItem(item, -1)
                            }
                        }

                        it.inventory.refreshItems()

                        chargeable.dropMessageText().ifPresent { message ->
                            it.message(message)
                        }

                        it.removeInterfaces()

                        if(dropPolicy == ChargeableDropPolicy.DROP_ON_FLOOR) {

                            it.playSound(Sounds.DROP_ITEM)

                            if (AreaManager.inWilderness(it))
                                ItemOnGroundManager.registerGlobal(it, unchargedItem)
                            else
                                ItemOnGroundManager.register(it, unchargedItem)
                            val itemAmount = item.amount
                            val itemValue =  getValue(unchargedId, ItemValueType.PRICE_CHECKER)
                            DropItemPacketListener.handleDropLogging(it, item, item.name(), itemAmount, itemValue)
                        }
                    }
                }
                .addCancel("Cancel.")
    }

}