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
 * https://oldschool.runescape.wiki/w/Digsite_pendant#(5)
 *
 * "The digsite pedant is a ruby necklace
 * enchanted by the Magic spell Lvl-3 Enchant."
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/04/2020
 * @version 1.0
 */
object DigsitePedant {

    private val DIGSITE_PEDANT_IDS = intArrayOf(
            ItemID.DIGSITE_PENDANT_1_,
            ItemID.DIGSITE_PENDANT_2_,
            ItemID.DIGSITE_PENDANT_3_,
            ItemID.DIGSITE_PENDANT_4_,
            ItemID.DIGSITE_PENDANT_5_)

    init {
        onSecondInventoryAction(*DIGSITE_PEDANT_IDS) {
            handleRubbing(player, getItemId(), inventory = true)
        }
        onSecondContainerEquipmentAction(*DIGSITE_PEDANT_IDS) {
            handleRubbing(player, getItemId(), inventory = false)
        }
    }

    private fun handleRubbing(player: Player, itemId: Int, inventory: Boolean) {
        player.message("You rub the necklace...")
        player.playSound(Sounds.RUB_JEWELRY)
        player.removeInterfaces()
        player.sendRubbingOptions(
                "The Digsite." to consumer(itemId, inventory, getTheDigSitePosition())
        )
    }

    private fun consumer(itemId: Int, inventory: Boolean, position: Position) = Consumer<Player> {
        val item = Item(itemId)
        val charges = getCharges(item).toIntOrNull()?:1
        if(charges == 1){
            it.message("Your digsite necklace crumbles to dust.", Color.JEWERLY,3)
            if(inventory)
                it.removeInventoryItem(item, 3)
            else
                it.removeEquipmentItem(item, 3)
        } else {
            val newItemDefinition = ItemDefinition.forName(item.name().replaceAfterLast("(", "${charges-1})"))
            if(newItemDefinition != null) {
                val newItem = Item(newItemDefinition.id, 1)
                it.message( "Your digsite necklace has ${getCharges(newItem)} charges left.", Color.JEWERLY,3)
                if (inventory)
                    it.replaceInventoryItem(item, newItem, 3)
                else
                    it.replaceEquipmentItem(item, newItem, 3)
            }
        }
    }.andThen(teleportConsumer(position, TeleportType.JEWELRY_RUB))

    private fun getCharges(item: Item) = item.name().substringAfterLast("(").substringBeforeLast(")")
    private fun getTheDigSitePosition() = Position(Random.nextInt(3360, 3362), Random.nextInt(3416, 3417), 0)
}

