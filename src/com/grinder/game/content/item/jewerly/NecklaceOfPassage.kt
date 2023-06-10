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
 * https://oldschool.runescape.wiki/w/Necklace_of_passage#(5)
 *
 * "The necklace of passage is a jade amulet
 * enchanted by the Magic spell Lvl-2 Enchant."
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/04/2020
 * @version 1.0
 */
object NecklaceOfPassage {

    private val NECKLACE_OF_PASSAGE_IDS = intArrayOf(
            ItemID.NECKLACE_OF_PASSAGE_1_,
            ItemID.NECKLACE_OF_PASSAGE_2_,
            ItemID.NECKLACE_OF_PASSAGE_3_,
            ItemID.NECKLACE_OF_PASSAGE_4_,
            ItemID.NECKLACE_OF_PASSAGE_5_)

    init {
        onSecondInventoryAction(*NECKLACE_OF_PASSAGE_IDS) {
            handleRubbing(player, getItemId(), inventory = true)
        }
        onSecondContainerEquipmentAction(*NECKLACE_OF_PASSAGE_IDS) {
            handleRubbing(player, getItemId(), inventory = false)
        }
    }

    private fun handleRubbing(player: Player, itemId: Int, inventory: Boolean) {
        player.message("You rub the necklace...")
        player.playSound(Sounds.RUB_JEWELRY)
        player.removeInterfaces()
        player.sendRubbingOptions(
                "Wizards' Tower." to consumer(itemId, inventory, getWizardsTowerPosition()),
                "The Outpost." to consumer(itemId, inventory, getTheOutPostPosition()),
                "Eagle's Eyrie." to consumer(itemId, inventory, getEaglesEyriePosition())
        )
    }

    private fun consumer(itemId: Int, inventory: Boolean, position: Position) = Consumer<Player> {

        val item = Item(itemId)
        val charges = getCharges(item).toIntOrNull()?:1

        if(charges == 1){
            it.message("Your necklace disintegrate after running out of charges.", Color.JEWERLY,3)
            if(inventory)
                it.removeInventoryItem(item,3)
            else
                it.removeEquipmentItem(item,3)
        } else {
            val newItemDefinition = ItemDefinition.forName(item.name().replaceAfterLast("(", "${charges-1})"))
            if(newItemDefinition != null) {
                val newItem = Item(newItemDefinition.id, 1)
                it.message("Your necklace has ${getCharges(newItem)} charges left.", Color.JEWERLY, 3)
                if (inventory)
                    it.replaceInventoryItem(item, newItem, 3)
                else
                    it.replaceEquipmentItem(item, newItem, 3)
            }
        }
    }.andThen(teleportConsumer(position, TeleportType.JEWELRY_RUB))

    private fun getCharges(item: Item) = item.name().substringAfterLast("(").substringBeforeLast(")")
    private fun getWizardsTowerPosition() = Position(Random.nextInt(3111, 3113), Random.nextInt(3168, 3170), 0)
    private fun getTheOutPostPosition() = Position(Random.nextInt(2440, 2442), Random.nextInt(3343, 3345), 0)
    private fun getEaglesEyriePosition() = Position(Random.nextInt(2729, 2730), Random.nextInt(10218, 10220), 0)
}

