package com.grinder.game.model.item.container.player

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.ButtonActions
import com.grinder.game.model.NPCActions
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueExpression
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.interfaces.dialogue.secondOption
import com.grinder.game.model.interfaces.dialogue.thirdOption
import com.grinder.game.model.interfaces.dialogue.fourthOption
import com.grinder.game.model.interfaces.dialogue.fifthOption
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainer
import com.grinder.game.model.item.container.StackType
import com.grinder.game.model.item.container.bank.BankConstants
import com.grinder.game.model.sound.Sounds

/**
 * TODO: add documentation
 *
 * @author  Blake (took most of his safe deposit code)
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   22/11/2019
 * @version 1.0
 */
class InstanceDeadItemCollectionBox(player: Player, itemList: List<Item>) : ItemContainer(player) {

    init {
        items = Array(capacity()) {
            if(it >= itemList.size)
                Item(-1)
            else
                itemList[it]
        }
    }

    override fun capacity() = Inventory.CAPACITY + Equipment.CAPACITY + RunePouch.CAPACITY

    override fun stackType(): StackType {
        return StackType.DEFAULT
    }

    override fun refreshItems(): InstanceDeadItemCollectionBox {
        player.packetSender.sendItemContainer(this, ITEM_CONTAINER_ID)
        player.packetSender.sendItemContainer(player.inventory, BankConstants.INVENTORY_INTERFACE_ID)
        player.packetSender.sendString(SLOTS_STRING_ID, validItems.size.toString() + " / " + capacity())
        return this
    }

    override fun full(): InstanceDeadItemCollectionBox {
        player.packetSender.sendMessage("Not enough space in the deposit box.", 1000)
        player.packetSender.sendSound(Sounds.INVENTORY_FULL_SOUND)
        return this
    }
    override fun validateSpace(target: ItemContainer)= target is Inventory

    fun open() {
        player.packetSender.sendSound(Sounds.OPEN_BANK_BOOTH_2)
        refreshItems()
        player.packetSender.sendInterfaceSet(INTERFACE_ID, 5063)
        player.inventory.refreshItems()
    }

    fun collect(){


    }

    companion object {
        private const val NPC_ID = 3779
        const val INTERFACE_ID = 23400
        const val SLOTS_STRING_ID = INTERFACE_ID + 12
        const val ITEM_CONTAINER_ID = INTERFACE_ID + 13

        init {
//            NPCFactory.create(NPC_ID, Position(3092, 3481, 0)).let {
//                it.movementCoordinator.radius = 4
//                World.getNpcAddQueue().add(it)
//            }
            NPCActions.onClick(NPC_ID) {
                val box = it.player.instanceDeadItemCollectionBox
                if(box == null || box.isEmpty){
                    DialogueBuilder(DialogueType.NPC_STATEMENT)
                            .setNpcChatHead(NPC_ID)
                            .setExpression(DialogueExpression.DISTRESSED)
                            .setText(
                                    "I have currently no items collected of you!"
                            ).start(it.player)
                    return@onClick true
                }
                DialogueBuilder(DialogueType.NPC_STATEMENT)
                        .setNpcChatHead(3779)
                        .setText(
                                "Hey there, I collected some of your items!",
                                "You can buy them back from me for a small fee of:",
                                "1gp"
                        ).add(DialogueType.OPTION)
                        .firstOption("Show me") { box.open() }
                        .secondOption("How do my items end up here?") {}
                        .start(it.player)
                return@onClick true
            }
            ButtonActions.onClick(23410) {
                player.instanceDeadItemCollectionBox.collect()
            }
        }
    }
}