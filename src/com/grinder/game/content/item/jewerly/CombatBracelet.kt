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
 * https://oldschool.runescape.wiki/w/Combat_bracelet
 *
 * "The combat bracelet is a dragonstone bracelet
 * enchanted by the Magic spell Lvl-5 Enchant."
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/04/2020
 * @version 1.0
 */
object CombatBracelet {

    private val COMBAT_BRACELET_IDS = intArrayOf(
            ItemID.COMBAT_BRACELET_1_,
            ItemID.COMBAT_BRACELET_2_,
            ItemID.COMBAT_BRACELET_3_,
            ItemID.COMBAT_BRACELET_4_,
            ItemID.COMBAT_BRACELET_5_,
            ItemID.COMBAT_BRACELET_6_)

    init {
        onSecondInventoryAction(*COMBAT_BRACELET_IDS) {
            handleRubbing(player, getItemId(), inventory = true)
        }
        onSecondContainerEquipmentAction(*COMBAT_BRACELET_IDS) {
            handleRubbing(player, getItemId(), inventory = false)
        }
    }

    private fun handleRubbing(player: Player, itemId: Int, inventory: Boolean) {
        player.message("You rub the bracelet...")
        player.playSound(Sounds.RUB_JEWELRY)
        player.removeInterfaces()
        player.sendRubbingOptions(
                "Warrior's Guild." to consumer(itemId, inventory, getWarriorsGuildPosition()),
                "Champion's Guild." to consumer(itemId, inventory, getChampionsGuildPosition()),
                "Edgeville Monastery" to consumer(itemId, inventory, getEdgevilleMonasteryPosition()),
                "Ranging Guild." to consumer(itemId, inventory, getRangingGuildPosition())
        )
    }

    private fun consumer(itemId: Int, inventory: Boolean, position: Position) = Consumer<Player> {

        val item = Item(itemId)
        val charges = getCharges(item).toIntOrNull()?:1

        if(charges == 1){
            val combatBracelet = Item(ItemID.COMBAT_BRACELET, 1)
            it.message("Your bracelet has ran out of charges.", Color.JEWERLY,3)
            if(inventory)
                it.replaceInventoryItem(item, combatBracelet,3)
            else
                it.replaceEquipmentItem(item, combatBracelet,3)
        } else {
            val newItemDefinition = ItemDefinition.forName(item.name().replaceAfterLast("(", "${charges-1})"))
            if(newItemDefinition != null) {
                val newItem = Item(newItemDefinition.id, 1)
                it.message("Your bracelet has ${getCharges(newItem)} charges left.", Color.JEWERLY, 3)
                if (inventory)
                    it.replaceInventoryItem(item, newItem, 3)
                else
                    it.replaceEquipmentItem(item, newItem, 3)
            }
        }
    }.andThen(dragonstoneTeleport(position, TeleportType.JEWELRY_RUB))

    private fun getCharges(item: Item) = item.name().substringAfterLast("(").substringBeforeLast(")")
    private fun getWarriorsGuildPosition() = Position(Random.nextInt(2879, 2881), Random.nextInt(3544, 3547), 0)
    private fun getChampionsGuildPosition() = Position(Random.nextInt(3190, 3193), Random.nextInt(3365, 3368), 0)
    private fun getEdgevilleMonasteryPosition() = Position(Random.nextInt(3051, 3053), Random.nextInt(3495, 3496), 0)
    private fun getRangingGuildPosition() = Position(Random.nextInt(2662, 2665), Random.nextInt(3433, 3434), 0)
}

