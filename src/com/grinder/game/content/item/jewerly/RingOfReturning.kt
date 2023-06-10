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
 * https://oldschoolrunescape.fandom.com/wiki/Ring_of_returning
 *
* A ring of returning is a jade ring enchanted via the Lvl-2 Enchant spell.
 * When rubbed, the player will be teleported to their current respawn point.
 * Available teleports include: Lumbridge, Camelot, Falador, Edgeville
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/04/2020
 * @version 1.0
 */
object RingOfReturning {

    private val RING_OF_RETURNING_IDS = intArrayOf(
            ItemID.RING_OF_RETURNING_1_,
            ItemID.RING_OF_RETURNING_2_,
            ItemID.RING_OF_RETURNING_3_,
            ItemID.RING_OF_RETURNING_4_,
            ItemID.RING_OF_RETURNING_5_)

    init {
        onSecondInventoryAction(*RING_OF_RETURNING_IDS) {
            handleRubbing(player, getItemId(), inventory = true)
        }
        onSecondContainerEquipmentAction(*RING_OF_RETURNING_IDS) {
            handleRubbing(player, getItemId(), inventory = false)
        }
    }

    private fun handleRubbing(player: Player, itemId: Int, inventory: Boolean) {
        player.message("You rub the ring...")
        player.playSound(Sounds.RUB_JEWELRY)
        player.removeInterfaces()
        consumer(itemId, inventory, Position(Random.nextInt(3087, 3092), Random.nextInt(3502, 3505), 0))
    }

    private fun consumer(itemId: Int, inventory: Boolean, position: Position) = Consumer<Player> {
        val item = Item(itemId)
        val charges = getCharges(item).toIntOrNull()?:1
        if(charges == 1){
            it.message("Your ring of returning crumbles to dust.", Color.JEWERLY,3)
            if(inventory)
                it.removeInventoryItem(item, 3)
            else
                it.removeEquipmentItem(item, 3)
        } else {
            val newItemDefinition = ItemDefinition.forName(item.name().replaceAfterLast("(", "${charges-1})"))
            if(newItemDefinition != null) {
                val newItem = Item(newItemDefinition.id, 1)
                it.message( "Your ring of returning has ${getCharges(newItem)} charges left.", Color.JEWERLY,3)
                if (inventory)
                    it.replaceInventoryItem(item, newItem, 3)
                else
                    it.replaceEquipmentItem(item, newItem, 3)
            }
        }
    }.andThen(teleportConsumer(position, TeleportType.JEWELRY_RUB))

    private fun getCharges(item: Item) = item.name().substringAfterLast("(").substringBeforeLast(")")
}

