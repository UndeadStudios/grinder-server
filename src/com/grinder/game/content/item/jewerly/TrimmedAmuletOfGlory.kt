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
 * https://oldschool.runescape.wiki/w/Amulet_of_glory
 *
 * "The amulet of glory is a dragonstone amulet
 * enchanted by the Magic spell Lvl-5 Enchant."
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/04/2020
 * @version 1.0
 */
object TrimmedAmuletOfGlory {

    private val TRIMMED_AMULET_OF_GLORY_IDS = intArrayOf(
            ItemID.AMULET_OF_GLORY_T1_,
            ItemID.AMULET_OF_GLORY_T2_,
            ItemID.AMULET_OF_GLORY_T3_,
            ItemID.AMULET_OF_GLORY_T4_,
            ItemID.AMULET_OF_GLORY_T5_,
            ItemID.AMULET_OF_GLORY_T6_)

    init {
        onSecondInventoryAction(*TRIMMED_AMULET_OF_GLORY_IDS) {
            handleRubbing(player, getItemId(), inventory = true)
        }
        onSecondContainerEquipmentAction(*TRIMMED_AMULET_OF_GLORY_IDS) {
            handleRubbing(player, getItemId(), inventory = false)
        }
    }

    private fun handleRubbing(player: Player, itemId: Int, inventory: Boolean) {
        player.message("You rub the amulet...")
        player.playSound(Sounds.RUB_JEWELRY)
        player.removeInterfaces()
        player.sendRubbingOptions(
                "Edgeville." to consumer(itemId, inventory, getEdgevillePosition()),
                "Karamja." to consumer(itemId, inventory, getKaramjaPosition()),
                "Draynor Village." to consumer(itemId, inventory, getDraynorVillagePosition()),
                "Al Kharid Palace." to consumer(itemId, inventory, getAlKharidPosition())
        )
    }

    private fun consumer(itemId: Int, inventory: Boolean, position: Position) = Consumer<Player> {

        val item = Item(itemId)
        val charges = getCharges(item).toIntOrNull()?:1

        if(charges == 1){
            val regularGlory = Item(ItemID.AMULET_OF_GLORY_T_, 1)
            it.message("Your amulet of glory ran out of charges.", Color.JEWERLY,3)
            if(inventory)
                it.replaceInventoryItem(item, regularGlory,3)
            else
                it.replaceEquipmentItem(item, regularGlory,3)
        } else {
            val newItemDefinition = ItemDefinition.forName(item.name().replaceAfterLast("(t", "${charges-1})"))
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

    private fun getCharges(item: Item) = item.name().substringAfterLast("(t").substringBeforeLast(")")
    private fun getEdgevillePosition() = Position(Random.nextInt(3089, 3092), Random.nextInt(3487, 3491), 0)
    private fun getAlKharidPosition() = Position(Random.nextInt(3275, 3278), Random.nextInt(3165, 3167), 0)
    private fun getKaramjaPosition() = Position(Random.nextInt(2923, 2925), Random.nextInt(3173, 3174), 0)
    private fun getDraynorVillagePosition() = Position(Random.nextInt(3078, 3080), Random.nextInt(3250, 3251), 0)
}

