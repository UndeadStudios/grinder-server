package com.grinder.game.model

import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.game.message.Message
import com.grinder.game.message.impl.ItemActionMessage
import com.grinder.game.message.impl.ItemContainerActionMessage
import com.grinder.game.message.impl.ItemOnItemMessage
import com.grinder.game.message.impl.ItemOnObjectMessage
import com.grinder.game.model.ItemActions.onClick
import com.grinder.game.model.ItemActions.onContainerClick
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.player.Inventory
import com.grinder.net.packet.PacketConstants

/**
 * This class can be used to configure actions to item ids.
 *
 * Each item id can have multiple mappings
 * (to account for different interfaces, menu options, etc.)
 *
 * @see onContainerClick to configure actions for [ItemContainerActionMessage]
 * @see onClick to configure actions for [ItemActionMessage]
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   30/11/2019
 * @version 1.0
 */
object ItemActions {

    private val itemContainerActions = HashMap<Int, ArrayList<(ItemContainerClickAction) -> Boolean>>()
    private val itemActions = HashMap<Int, ArrayList<(ItemClickAction) -> Boolean>>()
    private val itemOnItemPairedActions = HashMap<Pair<Int, Int>, ArrayList<(ItemOnItemAction) -> Boolean>>()
    private val itemOnItemActions = HashMap<Int, ArrayList<(ItemOnItemAction) -> Boolean>>()
    private val itemOnObjectPairedActions = HashMap<Pair<Int, Int>, ArrayList<(ItemOnObjectAction) -> Boolean>>()
    private val itemOnObjectActionsByItemId = HashMap<Int, ArrayList<(ItemOnObjectAction) -> Boolean>>()
    private val itemOnObjectActionsByObjectId = HashMap<Int, ArrayList<(ItemOnObjectAction) -> Boolean>>()

    /**
     * Configure the specified function to the specified ids,
     * and store the mapping in [itemContainerActions].
     *
     * @see ItemContainerActionMessage for the packets relevant to the function
     *
     * @param itemIds an array of item ids (map keys)
     * @param function an unit that takes a [ItemContainerClickAction] and returns a boolean
     */
    fun onContainerClick(vararg itemIds: Int, function: ItemContainerClickAction.() -> Boolean) {
        itemIds.forEach {
            itemContainerActions.putIfAbsent(it, ArrayList())
            itemContainerActions[it]!!.add(function)
        }
    }

    /**
     * Configure the specified function to the specified ids,
     * and store the mapping in [itemActions].
     *
     * @see ItemActionMessage for the packets relevant to the function
     *
     * @param itemIds an array of item ids (map keys)
     * @param function an unit that takes a [ItemClickAction] and returns a boolean
     */
    fun onClick(vararg itemIds: Int, function: ItemClickAction.() -> Boolean) {
        itemIds.forEach {
            itemActions.putIfAbsent(it, ArrayList())
            itemActions[it]!!.add(function)
        }
    }

    /**
     * Configure the specified function to the specified ids,
     * and store the mapping in [itemActions].
     *
     * @see ItemActionMessage for the packets relevant to the function
     *
     * @param itemIds an array of item ids (map keys)
     * @param function an unit that takes a [ItemClickAction] and returns a boolean
     */
    fun onClick(itemIds: List<Int>, function: ItemClickAction.() -> Boolean) {
        itemIds.forEach {
            itemActions.putIfAbsent(it, ArrayList())
            itemActions[it]!!.add(function)
        }
    }

    fun onItemOnItem(item: Int, function: ItemOnItemAction.() -> Boolean) {
        itemOnItemActions.putIfAbsent(item, ArrayList())
        itemOnItemActions[item]!!.add(function)
    }

    fun onItemOnItem(itemPair: Pair<Int, Int>, function: ItemOnItemAction.() -> Boolean) {
        itemOnItemPairedActions.putIfAbsent(itemPair, ArrayList())
        itemOnItemPairedActions[itemPair]!!.add(function)
    }

    fun onItemOnObjectByItemId(itemId: Int, function: ItemOnObjectAction.() -> Boolean) {
        itemOnObjectActionsByItemId.putIfAbsent(itemId, ArrayList())
        itemOnObjectActionsByItemId[itemId]!!.add(function)
    }

    fun onItemOnObjectByObjectId(objectId: Int, function: ItemOnObjectAction.() -> Boolean) {
        itemOnObjectActionsByObjectId.putIfAbsent(objectId, ArrayList())
        itemOnObjectActionsByObjectId[objectId]!!.add(function)
    }

    fun onItemOnObjectByItemId(vararg itemIds: Int, function: ItemOnObjectAction.() -> Boolean) {
        for (id in itemIds) {
            onItemOnObjectByItemId(id, function)
        }
    }
    fun onItemOnObjectByItemId(itemObjectIdPair: Pair<Int, Int>, function: ItemOnObjectAction.() -> Boolean) {
        itemOnObjectPairedActions.putIfAbsent(itemObjectIdPair, ArrayList())
        itemOnObjectPairedActions[itemObjectIdPair]!!.add(function)
    }
    fun onItemOnObjectByItemId(itemId: Int, objectId:Int, function: ItemOnObjectAction.() -> Boolean) {
        itemOnObjectPairedActions.putIfAbsent(Pair(itemId,objectId), ArrayList())
        itemOnObjectPairedActions[Pair(itemId,objectId)]!!.add(function)
    }


    /**
     * Check if there is any mapping for the [ItemContainerActionMessage.itemId]
     * in [itemContainerActions] of which the function returns true.
     *
     * @param player the [Player] who send the packet
     * @param itemContainerActionMessage the [Message] representing the packet
     *
     * @return 'true' if there exists a mapping that returns 'true' onClick,
     *          'false' otherwise.
     */
    fun handleClick(player: Player, itemContainerActionMessage: ItemContainerActionMessage) : Boolean {
        itemContainerActions[itemContainerActionMessage.itemId]?.let { listeners ->
            for (listener in listeners) {
                if(listener.invoke(ItemContainerClickAction(player, itemContainerActionMessage)))
                    return true
            }
        }
        return false
    }

    /**
     * Check if there is any mapping for the [ItemActionMessage.itemId]
     * in [itemActions] of which the function returns true.
     *
     * @param player the [Player] who send the packet
     * @param itemActionMessage the [Message] representing the packet
     *
     * @return 'true' if there exists a mapping that returns 'true' onClick,
     *          'false' otherwise.
     */
    fun handleClick(player: Player, itemActionMessage: ItemActionMessage) : Boolean {
        itemActions[itemActionMessage.itemId]?.let { listeners ->
            for (listener in listeners) {
                if(listener.invoke(ItemClickAction(player, itemActionMessage)))
                    return true
            }
        }
        return false
    }

    fun handleItemOnObject(player: Player, gameObject: GameObject, itemOnObjectMessage: ItemOnObjectMessage) : Boolean {

        val pairedAction = itemOnObjectPairedActions[itemOnObjectMessage.id to itemOnObjectMessage.objectId]

        if (pairedAction != null) {
            for (listener in pairedAction) {
                if (listener.invoke(ItemOnObjectAction(player, gameObject, itemOnObjectMessage)))
                    return true
            }
        }

        itemOnObjectActionsByItemId[itemOnObjectMessage.id]
                ?.apply {
                        for (listener in this) {
                            if(listener.invoke(ItemOnObjectAction(player, gameObject, itemOnObjectMessage)))
                                return true
                        }
                    }

        itemOnObjectActionsByObjectId[itemOnObjectMessage.objectId]
                ?.apply {
                    for (listener in this) {
                        if(listener.invoke(ItemOnObjectAction(player, gameObject, itemOnObjectMessage)))
                            return true
                    }
                }
        return false
    }

    fun handleItemOnItem(player: Player, itemOnItemMessage: ItemOnItemMessage) : Boolean {

        val pairedOptions = arrayOf(
                itemOnItemMessage.id to itemOnItemMessage.targetId,
                itemOnItemMessage.targetId to itemOnItemMessage.id
        )

        for(option in pairedOptions) {
            val pairedAction = itemOnItemPairedActions[option]
            if (pairedAction != null) {
                for (listener in pairedAction) {
                    if (listener.invoke(ItemOnItemAction(player, itemOnItemMessage)))
                        return true
                }
            }
        }

        itemOnItemActions[itemOnItemMessage.targetId]
                ?: itemOnItemActions[itemOnItemMessage.id]
                        ?.let { listeners ->
                            for (listener in listeners) {
                                if(listener.invoke(ItemOnItemAction(player, itemOnItemMessage)))
                                    return true
                            }
                        }
        return false
    }

    /**
     * Represents an [itemContainerActionMessage] received by the [player].
     */
    class ItemContainerClickAction(val player: Player, val itemContainerActionMessage: ItemContainerActionMessage) {
        fun isFirstAction() = itemContainerActionMessage.opcode == PacketConstants.FIRST_ITEM_CONTAINER_ACTION_OPCODE
        fun isSecondAction() = itemContainerActionMessage.opcode == PacketConstants.SECOND_ITEM_CONTAINER_ACTION_OPCODE
        fun isThirdAction() = itemContainerActionMessage.opcode == PacketConstants.THIRD_ITEM_CONTAINER_ACTION_OPCODE
        fun isInInventory() = itemContainerActionMessage.interfaceId == Inventory.INTERFACE_ID
        fun isInEquipment() = itemContainerActionMessage.interfaceId == EquipmentConstants.INVENTORY_INTERFACE_ID
        fun getItemId() = itemContainerActionMessage.itemId
        fun getSlot() = itemContainerActionMessage.slot
        fun getInterfaceId() = itemContainerActionMessage.interfaceId
    }

    /**
     * Represents an [itemActionMessage] received by the [player].
     */
    class ItemClickAction(val player: Player, val itemActionMessage: ItemActionMessage){
        //TODO: figure out proper name
        fun isEquipAction() = itemActionMessage.opcode == PacketConstants.EQUIP_ITEM_OPCODE
        fun isDropAction() = itemActionMessage.opcode == PacketConstants.DROP_ITEM_OPCODE
        fun isFirstAction() = itemActionMessage.opcode == PacketConstants.FIRST_ITEM_ACTION_OPCODE
        fun isSecondAction() = itemActionMessage.opcode == PacketConstants.SECOND_ITEM_ACTION_OPCODE
        fun isThirdAction() = itemActionMessage.opcode == PacketConstants.THIRD_ITEM_ACTION_OPCODE
        fun isInInventory() = itemActionMessage.interfaceId == Inventory.INTERFACE_ID
        fun isInEquipment() = itemActionMessage.interfaceId == EquipmentConstants.INVENTORY_INTERFACE_ID
        fun getItemId() = itemActionMessage.itemId
        fun getSlot() = itemActionMessage.slot
        fun getOpcode() = itemActionMessage.opcode
        fun getItem() : Item? {
            if (isInInventory())
                return player.inventory.atSlot(getSlot())
            if (isInEquipment())
                return player.equipment.atSlot(getSlot())
            return null
        }
    }

    /**
     * Represents an [itemOnItemMessage] received by the [player].
     */
    class ItemOnItemAction(val player: Player, val itemOnItemMessage: ItemOnItemMessage){

        fun getOtherItemId(itemId: Int): Int {
            return if(getSourceItemId() == itemId)
                getTargetItemId()
            else
                getSourceItemId()
        }

        fun getOtherSlot(itemId: Int): Int {
            return if(getSourceItemId() == itemId)
                getTargetSlot()
            else
                getSourceSlot()
        }

        fun getSlot(itemId: Int): Int {
            return if(getSourceItemId() == itemId)
                getSourceSlot()
            else
                getTargetSlot()
        }

        fun getSourceSlot() = itemOnItemMessage.slot
        fun getTargetSlot() = itemOnItemMessage.targetSlot

        fun getSourceItemId() = itemOnItemMessage.id
        fun getTargetItemId() = itemOnItemMessage.targetId

        fun getSourceInterfaceId() = itemOnItemMessage.interfaceId
        fun getTargetInterfaceId() = itemOnItemMessage.targetInterfaceId

        fun isInInventory() = getSourceInterfaceId() == Inventory.INTERFACE_ID
        fun isInEquipment() = getSourceInterfaceId() == EquipmentConstants.INVENTORY_INTERFACE_ID
    }

    /**
     * Represents an [ItemOnObjectMessage] received by the [player].
     */
    class ItemOnObjectAction(val player: Player, val gameObject: GameObject, val itemOnItemMessage: ItemOnObjectMessage){

        fun getSlot() = itemOnItemMessage.slot
        fun getItemId() = itemOnItemMessage.id
        fun getObjectId() = itemOnItemMessage.objectId
        fun getInterfaceId() = itemOnItemMessage.interfaceId

        fun isInInventory() = getInterfaceId() == Inventory.INTERFACE_ID
        fun isInEquipment() = getInterfaceId() == EquipmentConstants.INVENTORY_INTERFACE_ID

        fun getItem() : Item? {
            if (isInInventory())
                return player.inventory.atSlot(getSlot())
            if (isInEquipment())
                return player.equipment.atSlot(getSlot())
            return null
        }
    }
}