package com.grinder.game.content.item.degrading

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.minigame.impl.WeaponMinigame
import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.game.model.item.container.player.Equipment
import com.grinder.game.model.item.container.player.Inventory
import com.grinder.game.model.item.name
import com.grinder.net.packet.impl.EquipPacketListener
import com.grinder.util.ItemID
import com.grinder.util.Misc
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * This class manages the item degradation of a [Player].
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-03-06
 */
class ItemDegradationManager(private val player: Player) {

    private val degradationLevels = HashMap<Int, AtomicInteger>()

    /**
     * Serialises [degradationLevels] as a [JsonArray].
     */
    fun serialize(): JsonElement {
        val array = JsonArray()
        for ((key, value) in degradationLevels) {
            val `object` = JsonObject()
            `object`.addProperty("itemId", key)
            `object`.addProperty("degradationLevel", value.get())
            array.add(`object`)
        }
        return array
    }

    /**
     * Deserializes [element] as [degradationLevels].
     */
    fun read(element: JsonElement) {
        val array = element.asJsonArray
        for (next in array) {
            val `object` = next.asJsonObject
            val itemId = `object`["itemId"].asInt
            val degradationLevel = `object`["degradationLevel"].asInt
            degradationLevels[itemId] = AtomicInteger(degradationLevel)
        }
    }

    /**
     * Sequences all degradable items of the [player].
     */
    fun sequence() {

        if (skipDegrading())
            return

        val equipment = player.equipment

        for (slot in 0 until equipment.capacity()) {

            val item = equipment[slot] ?: continue
            val itemId = item.id
            val itemName = item.name()

            val degradableType = DegradableType.forItem(itemId) ?: continue
            val transformId = degradableType.transformID

            if (degradableType.isDegradingType(DegradingType.WEAR_TIME, DegradingType.WEAR_COMBAT_TIME)) {

                val degradingType = degradableType.degradingType

                if (degradingType === DegradingType.WEAR_COMBAT_TIME)
                    if (!player.combat.isInCombat && player.combat.cooledDownFromCombat())
                        continue

                val degradationLevel = degradationLevels[itemId]
                val preTime = degradationLevel?.get() ?: -1
                val degraded = degraded(degradationLevel, itemId, degradableType.degradeAmount)
                val postTime = degradationLevel?.get() ?: -1

                handleTimeLeftMessage(itemName, preTime, postTime)
                if (degraded) {
                    transformOrRemove(equipment, slot, itemId, itemName, degradableType, transformId)
                    EquipPacketListener.resetWeapon(player)
                    player.combat.reset(false)
                    EquipmentBonuses.update(player)
                    player.equipment.refreshItems()
                    WeaponInterfaces.assign(player)
                    player.updateAppearance()
                    AchievementManager.processFor(AchievementType.DEPRECIATION, player)
                }
            }
        }
    }

    private fun handleTimeLeftMessage(itemName: String, preTime: Int, postTime: Int) {

        val name = itemName.toLowerCase()

        val degradesIntoMessage =
            when {
            name.contains("karil")
                    || name.contains("ahrim")
                    || name.contains("dharok")
                    || name.contains("verac")
                    || name.contains("torag")
                    || name.contains("guthan") -> ""
            name.contains("black") ->
                when {

                    name.contains("(10)") -> ""
                    name.contains("(9)") -> ""
                    name.contains("(8)") -> ""
                    name.contains("(7)") -> ""
                    name.contains("(6)") -> ""
                    name.contains("(5)") -> ""
                    name.contains("(4)") -> ""
                    name.contains("(3)") -> ""
                    name.contains("(2)") -> ""
                    name.contains("(1)") -> ""
                    else -> ""
                }
//            name.contains("torva") ->
//                when {
//                    else -> ""
//                }
                name.contains("banods whip") ->
                    when {
                        else -> ""
                    }
                name.contains("infernal ") ->
                    when {
                        else -> ""
                    }
            name.contains("crystal") ->
                when {
                    name.contains("(1)") -> " into Crystal seed"
                    name.contains("100") -> ""
                    name.contains("75") -> ""
                    name.contains("50") -> ""
                    name.contains("25") -> " completely."
                    else -> ""
                }
            else -> " into dust"
        }

        if (postTime <= 60_000 && preTime > 60_000)
            player.message("@or3@Your $itemName has 60 seconds before it degrades$degradesIntoMessage.")
        else if (postTime <= 30_000 && preTime > 30_000)
            player.message("@or3@Your $itemName has 30 seconds before it degrades$degradesIntoMessage.")
    }

    /**
     * Degrades all degradable items of the specified [DegradingType] by the specified amount in the
     * [Equipment] of this [.player].
     *
     * @param type the [DegradingType] of the affected degradable items.
     * @param degradeAmount the amount by which the affected degradable items are degraded.
     */
    fun degrade(type: DegradingType, degradeAmount: Int) {

        if (skipDegrading())
            return

        val equipment = player.equipment

        for (slot in 0 until equipment.capacity()) {
            val item = equipment[slot] ?: continue
            val degradableType = DegradableType.forItem(item.id) ?: continue
            val degradingType = degradableType.degradingType
            if (degradingType != type)
                continue
            degrade(degradableType, degradeAmount, slot)
        }
    }

    fun degradeInventoryItems(type: DegradingType, degradeAmount: Int, itemID: Int) {
        if (skipDegrading())
            return
        val inventory = player.inventory
        for (slot in 0 until inventory.capacity()) {
            val item = inventory[slot] ?: continue
            val degradableType = DegradableType.forItem(item.id) ?: continue
            val degradingType = degradableType.degradingType
            if (degradingType !== type)
                continue
            degradeInventoryItem(degradableType, degradeAmount, slot, itemID)
        }
    }

    fun degradeInventoryItemsAnyType(degradeAmount: Int, itemID: Int) {
        if (skipDegrading())
            return
        val inventory = player.inventory
        for (slot in 0 until inventory.capacity()) {
            val item = inventory[slot] ?: continue
            val degradableType = DegradableType.forItem(item.id) ?: continue
            degradeInventoryItem(degradableType, degradeAmount, slot, itemID)
        }
    }

    private fun degradeInventoryItem(degradableType: DegradableType, degradeAmount: Int, itemSlot: Int, itemId: Int) {
        val inventory = player.inventory
        val transformId = degradableType.transformID
        val degradationAmount = if (degradeAmount == -1)
            degradableType.degradeAmount
        else
            degradeAmount
        if (degraded(itemId, degradationAmount)) {
            val itemName = ItemDefinition.forId(itemId)?.name?:"undefined"
            transformOrRemoveInv(inventory, itemSlot, itemId, itemName, degradableType, transformId)
        }
    }

    fun repair(itemId: Int): Boolean {
        val degradableType = DegradableType.forItem(itemId) ?: return false
        val degradationLevel = degradationLevels[degradableType.itemId] ?: return false
        degradationLevel.set(degradableType.initialValue)
        return true
    }

    /**
     * Gets the degradation level from the [.degradationLevels] of the specified item id.
     *
     * @param itemID the id of the item to fetch the degradation level of.
     * @return an integer representing the degradation level of the specified item id or -1 if not present.
     */
    fun getLevel(itemID: Int) = degradationLevels[itemID]?.get() ?: -1

    /**
     * Degrades the degradable item of the specified [DegradingType] by the specified amount in the
     * specified slot of the [Equipment] of this [.player].
     *
     * @param degradableType the [DegradableType] expected at the specified slot.
     * @param degradeAmount the amount by which the affected degradable item is degraded.
     * @param itemSlot the slot at which the item is degraded.
     */
    private fun degrade(degradableType: DegradableType, degradeAmount: Int, itemSlot: Int) {
        val equipment = player.equipment
        val itemId = degradableType.itemId
        val transformId = degradableType.transformID
        val degradationAmount = if (degradeAmount == -1)
            degradableType.degradeAmount
        else
            degradeAmount

        if (degraded(itemId, degradationAmount)) {
            val itemName = ItemDefinition.forId(itemId)?.name?:"undefined"
            transformOrRemove(equipment, itemSlot, itemId, itemName, degradableType, transformId)
        }
    }


    /**
     * Updates the item at the specified slot of the [Equipment] of this [.player] or removes it if it's the last degradation state.
     *
     * @param equipment the [Equipment] of this [.player].
     * @param slot the slot at which the specified item id is expected.
     * @param itemId the item id that should be transformed or removed at the specified slot.
     * @param itemName the name of the item at that specified slot that should be transformed or removed.
     * @param degradableType the [DegradableType] that is expected to hold the same [DegradableType.itemId] as the specified item id.
     * @param transformId the id of the item that the item at the specified slot should be transformed into or -1 if it should be removed.
     */
    private fun transformOrRemove(equipment: Equipment, slot: Int, itemId: Int, itemName: String, degradableType: DegradableType, transformId: Int) {
        val notificationMessage = degradableType.degradingMessage
        if (notificationMessage != null) player.sendMessage(notificationMessage.replace("#item".toRegex(), itemName))
        when (transformId) {
            -1 -> equipment.delete(Item(itemId), slot)
            23808 -> { // Crystal seed
                equipment.delete(Item(itemId), slot)
                player.inventory.add(23808, 1)
            }
            15729 -> { // Torva whip (damaged)
                equipment.delete(Item(itemId), slot)
                ItemContainerUtil.addOrDrop(player.inventory, player,  Item(15729, 1))
            }
            15730 -> { // Infernal whip (uncharged)
                equipment.delete(Item(itemId), slot)
                ItemContainerUtil.addOrDrop(player.inventory, player,  Item(15730, 1))
            }
            15751 -> { // Bandos whip
                equipment.delete(Item(itemId), slot)
                ItemContainerUtil.addOrDrop(player.inventory, player,  Item(15797, 1))
            }
            15846 -> { // Zamorak whip
                equipment.delete(Item(itemId), slot)
                ItemContainerUtil.addOrDrop(player.inventory, player,  Item(15852, 1))
            }
            15848 -> { // Saradomin whip
                equipment.delete(Item(itemId), slot)
                ItemContainerUtil.addOrDrop(player.inventory, player,  Item(15853, 1))
            }
            15850 -> { // Guthix whip
                equipment.delete(Item(itemId), slot)
                ItemContainerUtil.addOrDrop(player.inventory, player,  Item(15854, 1))
            }
//            ItemID.TORVA_FULL_HELM_DAMAGED -> { // Torva full helm (damaged)
//                equipment.delete(Item(itemId), slot)
//                ItemContainerUtil.addOrDrop(player.inventory, player,  Item(ItemID.TORVA_FULL_HELM_DAMAGED, 1))
//            }
//            ItemID.TORVA_PLATEBODY_DAMAGED -> { // Torva platebody (damaged)
//                equipment.delete(Item(itemId), slot)
//                ItemContainerUtil.addOrDrop(player.inventory, player,  Item(ItemID.TORVA_PLATEBODY_DAMAGED, 1))
//            }
//            ItemID.TORVA_PLATELEGS_DAMAGED -> { // Torva platelegs (damaged)
//                equipment.delete(Item(itemId), slot)
//                ItemContainerUtil.addOrDrop(player.inventory, player,  Item(ItemID.TORVA_PLATELEGS_DAMAGED, 1))
//            }
            else -> {
                val preDegradedItem = equipment[slot]
                val postDegradedItem = Item(transformId, preDegradedItem.amount)
                equipment.setItem(slot, postDegradedItem)
            }
        }
        equipment.refreshItems()
    }

    private fun transformOrRemoveInv(inventory: Inventory, slot: Int, itemId: Int, itemName: String, degradableType: DegradableType, transformId: Int) {
        val notificationMessage = degradableType.degradingMessage
        if (notificationMessage != null) player.sendMessage(notificationMessage.replace("#item".toRegex(), itemName))
        if (transformId == -1)
            inventory.delete(Item(itemId), slot)
        else {
            val preDegradedItem = inventory[slot]
            val postDegradedItem = Item(transformId, preDegradedItem.amount)
            inventory.setItem(slot, postDegradedItem)
        }
        player.equipment.refreshItems()
        EquipPacketListener.resetWeapon(player)
        WeaponInterfaces.assign(player)
        player.combat.reset(false)
        EquipmentBonuses.update(player)
        player.updateAppearance()
        inventory.refreshItems()
    }

    /**
     * Removes from the degradation level of the specified id in the [.degradationLevels]
     * and checks whether it's fully degraded.
     *
     * @param itemID the id of the item to be degraded.
     * @param amount the amount by which the item should be degraded.
     * @return whether the item is fully degraded.
     */
    public fun degraded(itemID: Int, amount: Int): Boolean {
        return degraded(degradationLevels[itemID], itemID, amount)
    }

    /**
     * Removes from the degradation level of the specified id in the [.degradationLevels]
     * and checks whether it's fully degraded.
     *
     * @param degradationLevel an [AtomicInteger] holding the degradation level of the specified item id or null if not present.
     * @param itemId the id of the item to be degraded.
     * @param amount the amount by which the item should be degraded.
     * @return whether the item is fully degraded.
     */
    private fun degraded(degradationLevel: AtomicInteger?, itemId: Int, amount: Int): Boolean {
        if (degradationLevel != null) {
            degradationLevel.set(degradationLevel.get() - amount)
            if (degradationLevel.get() <= 0) {
                degradationLevels.remove(itemId)
                return true
            }
            return false
        }
        val degradableType = DegradableType.forItem(itemId)?:return false
        degradationLevels[itemId] = AtomicInteger(degradableType.initialValue)
        return false
    }

    /**
     * Should degradation sequencing be skipped for the [player].
     */
    private fun skipDegrading() = player.minigame is WeaponMinigame
}