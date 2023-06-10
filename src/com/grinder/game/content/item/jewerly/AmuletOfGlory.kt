package com.grinder.game.content.item.jewerly

import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.movement.teleportation.TeleportType
import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.Position
import com.grinder.game.model.attribute.AttributeManager
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
object AmuletOfGlory {

    private val AMULET_OF_GLORY_IDS = intArrayOf(
            ItemID.AMULET_OF_GLORY_1_,
            ItemID.AMULET_OF_GLORY_2_,
            ItemID.AMULET_OF_GLORY_3_,
            ItemID.AMULET_OF_GLORY_4_,
            ItemID.AMULET_OF_GLORY_5_,
            ItemID.AMULET_OF_GLORY_6_,
            ItemID.AMULET_OF_ETERNAL_GLORY)

    init {
        onSecondInventoryAction(*AMULET_OF_GLORY_IDS) {
            handleRubbing(player, getItemId(), inventory = true)
        }
        onSecondContainerEquipmentAction(*AMULET_OF_GLORY_IDS) {
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

        if(itemId == ItemID.AMULET_OF_ETERNAL_GLORY)
            return@Consumer

        if (it.wildernessLevel > 30) {
            it.message("You can't teleport or use this above level 30 Wilderness.");
            return@Consumer
        }

        val item = Item(itemId)
        val charges = getCharges(item).toIntOrNull()?:1

        if(charges == 1){
            val regularGlory = Item(ItemID.AMULET_OF_GLORY, 1)
            it.message("Your amulet has ran out of charges.", Color.JEWERLY,3)
            if(inventory)
                it.replaceInventoryItem(item, regularGlory,3)
            else
                it.replaceEquipmentItem(item, regularGlory,3)
        } else {
            val newItemDefinition = ItemDefinition.forName(item.name().replaceAfterLast("(", "${charges-1})"))
            if(newItemDefinition != null) {
                val newItem = Item(newItemDefinition.id, 1)
                it.message("Your amulet has ${getCharges(newItem)} charges left.", Color.JEWERLY, 3)
                it.points.increase(AttributeManager.Points.GLORY_TELEPORTS, 1) // Increase points
                if (inventory)
                    it.replaceInventoryItem(item, newItem, 3)
                else
                    it.replaceEquipmentItem(item, newItem, 3)
            }
        }
    }.andThen(dragonstoneTeleport(position, TeleportType.JEWELRY_RUB))

    private fun getCharges(item: Item) = item.name().substringAfterLast("(").substringBeforeLast(")")
    private fun getEdgevillePosition() = Position(Random.nextInt(3089, 3092), Random.nextInt(3487, 3491), 0)
    private fun getAlKharidPosition() = Position(Random.nextInt(3275, 3278), Random.nextInt(3165, 3167), 0)
    private fun getKaramjaPosition() = Position(Random.nextInt(2923, 2925), Random.nextInt(3173, 3174), 0)
    private fun getDraynorVillagePosition() = Position(Random.nextInt(3078, 3080), Random.nextInt(3250, 3251), 0)
}

