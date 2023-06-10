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
 * https://oldschool.runescape.wiki/w/Burning_amulet
 *
 * "The burning amulet is a topaz amulet
 * enchanted by the Magic spell Lvl-3 Enchant."
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/04/2020
 * @version 1.0
 */
object BurningAmulet {

    private val BURNING_AMULET_IDS = intArrayOf(
            ItemID.BURNING_AMULET_1_,
            ItemID.BURNING_AMULET_2_,
            ItemID.BURNING_AMULET_3_,
            ItemID.BURNING_AMULET_4_,
            ItemID.BURNING_AMULET_5_)

    init {
        onSecondInventoryAction(*BURNING_AMULET_IDS){
            handleRubbing(player, getItemId(), inventory = true)
        }
        onSecondContainerEquipmentAction(*BURNING_AMULET_IDS){
            handleRubbing(player, getItemId(), inventory = false)
        }
    }

    private fun handleRubbing(player: Player, itemId: Int, inventory: Boolean) {
        player.message("You rub the amulet...")
        player.playSound(Sounds.RUB_JEWELRY)
        player.removeInterfaces()
        player.sendRubbingOptions(
                "Chaos Temple." to consumer(itemId, inventory, getChaosTemplePosition()),
                "Bandit Camp." to consumer(itemId, inventory, getBanditCampPosition()),
                "Lava Maze." to consumer(itemId, inventory, getLavaMazePosition())
        )
    }

    private fun consumer(itemId: Int, inventory: Boolean, position: Position) = Consumer<Player> {

        val item = Item(itemId)
        val charges = getCharges(item).toIntOrNull()?:1

        if(charges == 1){
            it.message("Your amulet disintegrate after running out of charges.", Color.JEWERLY,3)
            if(inventory)
                it.removeInventoryItem(item,3)
            else
                it.removeEquipmentItem(item,3)
        } else {
            val newItemDefinition = ItemDefinition.forName(item.name().replaceAfterLast("(", "${charges-1})"))
            if(newItemDefinition != null) {
                val newItem = Item(newItemDefinition.id, 1)
                it.message("Your amulet has ${getCharges(newItem)} charges left.", Color.JEWERLY, 3)
                if (inventory)
                    it.replaceInventoryItem(item, newItem, 3)
                else
                    it.replaceEquipmentItem(item, newItem, 3)
            }
        }
    }.andThen(teleportConsumer(position, TeleportType.JEWELRY_RUB))

    private fun getCharges(item: Item) = item.name().substringAfterLast("(").substringBeforeLast(")")
    private fun getChaosTemplePosition() = Position(Random.nextInt(3234, 3237), Random.nextInt(3627, 3630), 0)
    private fun getBanditCampPosition() = Position(Random.nextInt(3037, 3039), Random.nextInt(3652, 3654), 0)
    private fun getLavaMazePosition() = Position(Random.nextInt(3090, 3095), Random.nextInt(3857, 3861), 0)
}

