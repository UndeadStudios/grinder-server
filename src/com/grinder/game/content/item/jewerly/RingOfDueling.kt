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
import java.util.function.Consumer
import kotlin.random.Random

/**
 * https://oldschool.runescape.wiki/w/Ring_of_dueling#(8)
 *
 * "The ring of dueling is a teleportation ring that
 * may be made by casting Lvl-2 Enchant on an emerald ring,
 * granting 37 Magic experience."
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/04/2020
 * @version 1.0
 */
object RingOfDueling {

    private val RING_OF_DUELING_IDS = intArrayOf(
            ItemID.RING_OF_DUELING_1_,
            ItemID.RING_OF_DUELING_2_,
            ItemID.RING_OF_DUELING_3_,
            ItemID.RING_OF_DUELING_4_,
            ItemID.RING_OF_DUELING_5_,
            ItemID.RING_OF_DUELING_6_,
            ItemID.RING_OF_DUELING_7_,
            ItemID.RING_OF_DUELING_8_)

    init {
        onSecondInventoryAction(*RING_OF_DUELING_IDS) {
            handleRubbing(player, getItemId(), inventory = true)
        }
        onSecondContainerEquipmentAction(*RING_OF_DUELING_IDS) {
            handleRubbing(player, getItemId(), inventory = false)
        }
    }

    private fun handleRubbing(player: Player, itemId: Int, inventory: Boolean) {
        player.message("You rub the ring...")
        player.playSound(Sounds.RUB_JEWELRY)
        player.removeInterfaces()
        player.sendRubbingOptions(
                "Al Kharid Duel Arena." to consumer(itemId, inventory, getDuelArenaPosition()),
                "Castle Wars Arena." to consumer(itemId, inventory, getCastleWarsArenaPosition()),
                "Clan Wars Arena." to consumer(itemId, inventory, getClanWarsArenaPosition())
        )
    }

    private fun consumer(itemId: Int, inventory: Boolean, position: Position) = Consumer<Player> {
        val item = Item(itemId)
        val charges = getCharges(item).toIntOrNull()?:1
        if(charges == 1){
            it.message("Your ring of dueling crumbles to dust.", Color.JEWERLY,3)
            if(inventory)
                it.removeInventoryItem(item, 3)
            else
                it.removeEquipmentItem(item, 3)
        } else {
            val newItemDefinition = ItemDefinition.forName(item.name().replaceAfterLast("(", "${charges-1})"))
            if(newItemDefinition != null) {
                val newItem = Item(newItemDefinition.id, 1)
                it.message( "Your ring of dueling has ${getCharges(newItem)} charges left.", Color.JEWERLY,3)
                if (inventory)
                    it.replaceInventoryItem(item, newItem, 3)
                else
                    it.replaceEquipmentItem(item, newItem, 3)
            }
        }
    }.andThen(teleportConsumer(position, TeleportType.JEWELRY_RUB))

    private fun getCharges(item: Item) = item.name().substringAfterLast("(").substringBeforeLast(")")
    private fun getDuelArenaPosition() = Position(Random.nextInt(3367, 3370), Random.nextInt(3275, 3277), 0)
    private fun getCastleWarsArenaPosition() = Position(Random.nextInt(2441, 2444), Random.nextInt(3085, 3086), 0)
    private fun getClanWarsArenaPosition() = Position(Random.nextInt(3373, 3378), Random.nextInt(3161, 3164), 0)

}

