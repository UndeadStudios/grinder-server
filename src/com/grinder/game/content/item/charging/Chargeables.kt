package com.grinder.game.content.item.charging

import com.grinder.game.content.item.charging.Chargeables.map
import com.grinder.game.content.item.charging.impl.*
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.addInventoryItem
import com.grinder.game.model.CommandActions
import com.grinder.game.model.item.AttributableItem
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.stackable
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.ServerClassPreLoader
import org.apache.logging.log4j.LogManager
import org.reflections.Reflections
import java.util.function.Consumer

/**
 * Represents a singleton object handling [Chargeable] objects.
 *
 * [Chargeable] objects annotated with [Charge] is loaded through reflection
 * in server startup and stored into [map].
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   09/09/2020
 */
object Chargeables {

    private val map = HashMap<Pair<Int, Int>, Chargeable>()

    private val logger = LogManager.getLogger(Chargeables::javaClass.name)!!

    fun isChargeable(itemId: Int) : Boolean {
        return map.keys.any {  it.first == itemId }
    }

    fun isSameChargeable(attributableItem1: AttributableItem, attributableItem2: AttributableItem) : Boolean {
        val key1 = map.keys.find { it.first == attributableItem1.id }?:return false
        val key2 = map.keys.find { it.first == attributableItem2.id }?:return false
        val class1 = map[key1]?.javaClass?.superclass
        val class2 = map[key2]?.javaClass?.superclass
        return class1 == class2
    }

    fun getChargeable(attributableItem1: AttributableItem): Chargeable? {
        val key = map.keys.find { it.first == attributableItem1.id } ?: return null
        return map[key]
    }

    init {
        val reflections = Reflections(javaClass.packageName)
        val children = reflections.getSubTypesOf(Chargeable::class.java)
        children.forEach {
            val instance = ServerClassPreLoader.forceInit(it)
            val charge = it.getAnnotation(Charge::class.java)?:return@forEach
            val objectInstance = instance.kotlin.objectInstance
            if(objectInstance == null)
                logger.error("objectInstance of $instance must not be null due to Charge annotation")
            else
                map[Pair(charge.chargedId, charge.unchargedId)] = objectInstance
        }
        CommandActions.onCommand("chargeables", PlayerRights.HIGH_STAFF) {
            player.inventory.resetItems()
            map.keys.forEach {
                player.addInventoryItem(AttributableItem(it.first, 1), -1)
            }
            player.inventory.refreshItems()
            return@onCommand true
        }
        CommandActions.onCommand("unchargeables", PlayerRights.HIGH_STAFF) {
            player.inventory.resetItems()
            map.keys.forEach {
                player.addInventoryItem(Item(it.second, 1), -1)
            }
            player.inventory.refreshItems()
            return@onCommand true
        }
        // note: does not work unless item has been manually charged before, due to key not being present otherwise
        CommandActions.onCommand("rcharges", PlayerRights.HIGH_STAFF) {
            player.inventory.validItems.forEach {
                if(it is AttributableItem){
                    for(key in it.attributes.keys){
                        it.increase(key, Misc.random(10, 1000))
                    }
                }
            }
            return@onCommand true
        }
    }

    fun postDropEvaluation(keep: ArrayList<Item>, dropped: ArrayList<Item>) : Consumer<Player> {

        var totalRevenantCharges = 0
        var totalScaleDrops = 0

        var consumer = Consumer<Player> {  }

        val droppedCharges = ArrayList<Item>()

        for((idPair, chargeable) in map){
            val chargedId = idPair.first
            val unchargedId = idPair.second
            val droppedChargeables = dropped.filter { it.id == chargedId }

            if(droppedChargeables.isNotEmpty()){


                if(chargeable is RevenantEtherChargeable) // Revenant chargeable items drop their charges on death
                    totalRevenantCharges += droppedChargeables.sumBy { chargeable.getCharges(it) }
                if(chargeable is TridentOfSwamp || chargeable is TridentOfSwampEnchanced) // Trident of swamp drops their scales upon death
                    totalScaleDrops += droppedChargeables.sumBy { chargeable.getCharges(it) }
                if(chargeable is SerpentineHelmet || chargeable is MagmaHelmet || chargeable == TanzaniteHelmet) // Serpentine helmets drops their scales upon death
                    totalScaleDrops += droppedChargeables.sumBy { chargeable.getCharges(it) }
                if(chargeable is ToxicStaffOfTheDead) // Toxic staff of the dead drops scales upon death
                    totalScaleDrops += droppedChargeables.sumBy { chargeable.getCharges(it) }

                when(chargeable.deathPolicy){
                    ChargeableDeathPolicy.KEEP -> {
                        dropped.removeAll(droppedChargeables.toSet())
                        keep.addAll(droppedChargeables)
                    }
                    ChargeableDeathPolicy.DROP -> {
                        // do nothing, items already in dropped
                    }
                    ChargeableDeathPolicy.DROP_UNCHARGED -> {
                        for (item in droppedChargeables) {
                            if (item is AttributableItem) {
                                chargeAddLoop@ for (chargeItem in chargeable.toChargeItems(item)) {
                                    for (droppedChargeItem in droppedCharges) {
                                        if (!droppedChargeItem.isValid) {
                                            return consumer
                                        }
                                        if (droppedChargeItem.id == chargeItem.id && chargeItem.stackable()) {
                                            droppedChargeItem.amount += chargeItem.amount
                                            continue@chargeAddLoop
                                        }
                                    }
                                    droppedCharges.add(chargeItem)
                                }
                            }
                        }
                        dropped.removeAll(droppedChargeables.toSet())
                        val unchargedItems = droppedChargeables.map {
                            Item(unchargedId, it.amount)
                        }
                        dropped.addAll(unchargedItems)

                        consumer = consumer.andThen {
                            it.inventory.replaceAll(chargedId, unchargedId, true)
                            it.equipment.replaceAll(chargedId, unchargedId, true)
                            it.lootingBag.container.replaceAll(chargedId, unchargedId, true)
                        }
                    }
                    ChargeableDeathPolicy.DESTROY -> {}
                }
            }
        }

        if(totalRevenantCharges > 0)
            dropped.add(Item(ItemID.REVENANT_ETHER, totalRevenantCharges))
        if(totalScaleDrops > 0)
            dropped.add(Item(ItemID.ZULRAHS_SCALES, totalScaleDrops))

        return consumer
    }

    fun handleDrop(player: Player, item: Item, itemSlot: Int): Boolean{
        for((idPair, chargeable) in map) {
            val chargedId = idPair.first
            val unchargedId = idPair.second
            if (item.id == chargedId) {
                ChargeablesUtil
                        .createDropDialogue(chargeable, chargeable.dropPolicy(), unchargedId, itemSlot, item)
                        .start(player)
                return true
            }
        }
        return false
    }

    fun handleItemOnItem(player: Player, used: Int, with: Int, withSlot: Int): Boolean {
        for((_, chargeable) in map) {
            if(chargeable is ItemChargeable)
                if(chargeable.charge(player, used, with, withSlot))
                    return true
        }
        return false
    }
}