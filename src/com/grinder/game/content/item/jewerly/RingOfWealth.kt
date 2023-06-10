package com.grinder.game.content.item.jewerly

import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.movement.teleportation.TeleportType
import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.Position
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.name
import com.grinder.game.model.onSecondContainerEquipmentAction
import com.grinder.game.model.onSecondInventoryAction
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID
import com.grinder.util.ItemID.RING_OF_WEALTH
import java.util.function.Consumer
import kotlin.random.Random

/**
 * Features (see https://oldschool.runescape.wiki/w/Ring_of_wealth#(5)):
 * - Rare drop enhancement
 * - Currency collection
 * - Teleportation
 * - Boss log
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/04/2020
 * @version 1.0
 */
object RingOfWealth {

    private val RING_OF_WEALTH_IDS = intArrayOf(
            ItemID.RING_OF_WEALTH_1_,
            ItemID.RING_OF_WEALTH_2_,
            ItemID.RING_OF_WEALTH_3_,
            ItemID.RING_OF_WEALTH_4_,
            ItemID.RING_OF_WEALTH_5_)

    init {
        onSecondInventoryAction(*RING_OF_WEALTH_IDS) {
            handleRubbing(player, getItemId(), inventory = true)
        }
        onSecondContainerEquipmentAction(*RING_OF_WEALTH_IDS) {
            handleRubbing(player, getItemId(), inventory = false)
        }
    }

    private fun handleRubbing(player: Player, itemId: Int, inventory: Boolean) {
        player.message("You rub the ring...")
        player.playSound(Sounds.RUB_JEWELRY)
        player.removeInterfaces()
        player.sendRubbingOptions(
                "Miscellania." to consumer(itemId, inventory, getMiscellaniaPosition()),
                "Grand Exchange." to consumer(itemId, inventory, getGrandExchangePosition()),
                "Falador Park." to consumer(itemId, inventory, getFaladorParkPosition()),
                "Dondakan." to consumer(itemId, inventory, getDonDakanPosition())
        )
    }

    private fun consumer(itemId: Int, inventory: Boolean, position: Position) = Consumer<Player> {

        val item = Item(itemId)
        val charges = getCharges(item).toIntOrNull()?:1

        if(charges == 1){
            val imbuedROW = Item(RING_OF_WEALTH, 1)
            it.message("Your ring has ran out of charges.", Color.JEWERLY,3)
            if(inventory)
                it.replaceInventoryItem(item, imbuedROW,3)
            else
                it.replaceEquipmentItem(item, imbuedROW,3)
        } else {
            val newItemDefinition = ItemDefinition.forName(item.name().replaceAfterLast("(", "${charges-1})"))
            if(newItemDefinition != null) {
                val newItem = Item(newItemDefinition.id, 1)
                it.message("Your ring has ${getCharges(newItem)} charges left.", Color.JEWERLY, 3)
                if (inventory)
                    it.replaceInventoryItem(item, newItem, 3)
                else
                    it.replaceEquipmentItem(item, newItem, 3)
            }
        }
    }.andThen(dragonstoneTeleport(position, TeleportType.JEWELRY_RUB))

    private fun getCharges(item: Item) = item.name().substringAfterLast("(").substringBeforeLast(")")
    private fun getMiscellaniaPosition() = Position(Random.nextInt(2564, 2567), Random.nextInt(3846, 3849), 0)
    private fun getGrandExchangePosition() = Position(Random.nextInt(3163, 3166), Random.nextInt(3464, 3467), 0)
    private fun getFaladorParkPosition() = Position(Random.nextInt(2994, 2997), Random.nextInt(3376, 3379), 0)
    private fun getDonDakanPosition() = Position(Random.nextInt(2829, 2832), Random.nextInt(10165, 10168), 0)
}

