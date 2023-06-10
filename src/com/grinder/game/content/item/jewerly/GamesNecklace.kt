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
 * https://oldschool.runescape.wiki/w/Games_necklace#(8)
 *
 * "The games necklace is a sapphire bracelet
 * enchanted by the Magic spell Lvl-1 Enchant."
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/04/2020
 * @version 1.0
 */
object GamesNecklace {

    private val GAMES_NECKLACE_IDS = intArrayOf(
            ItemID.GAMES_NECKLACE_1_,
            ItemID.GAMES_NECKLACE_2_,
            ItemID.GAMES_NECKLACE_3_,
            ItemID.GAMES_NECKLACE_4_,
            ItemID.GAMES_NECKLACE_5_,
            ItemID.GAMES_NECKLACE_6_,
            ItemID.GAMES_NECKLACE_7_,
            ItemID.GAMES_NECKLACE_8_)

    init {
        onSecondInventoryAction(*GAMES_NECKLACE_IDS) {
            handleRubbing(player, getItemId(), inventory = true)
        }
        onSecondContainerEquipmentAction(*GAMES_NECKLACE_IDS) {
            handleRubbing(player, getItemId(), inventory = false)
        }
    }

    private fun handleRubbing(player: Player, itemId: Int, inventory: Boolean) {
        player.message("You rub the necklace...")
        player.playSound(Sounds.RUB_JEWELRY)
        player.removeInterfaces()
        player.sendRubbingOptions(
                "Barbarian Outpost." to consumer(itemId, inventory, getBarbarianOutPostPosition()),
                "Burthorpe Games Room." to consumer(itemId, inventory, getBurthopeGamesRoomPosition()),
                "Corporeal Beast's Cave." to consumer(itemId, inventory, getCorporealBeastCavePosition()),
                "Tears of Guthix." to consumer(itemId, inventory, getTearsOfGuthixPosition()),
                "Wintertodt Camp." to consumer(itemId, inventory, getWinterTodtCampPosition())
        )
    }

    private fun consumer(itemId: Int, inventory: Boolean, position: Position) = Consumer<Player> {
        val item = Item(itemId)
        val charges = getCharges(item).toIntOrNull()?:1
        if(charges == 1){
            it.message("Your games necklace crumbles to dust.", Color.JEWERLY,3)
            if(inventory)
                it.removeInventoryItem(item, 3)
            else
                it.removeEquipmentItem(item, 3)
        } else {
            val newItemDefinition = ItemDefinition.forName(item.name().replaceAfterLast("(", "${charges-1})"))
            if(newItemDefinition != null) {
                val newItem = Item(newItemDefinition.id, 1)
                it.message( "Your games necklace has ${getCharges(newItem)} charges left.", Color.JEWERLY,3)
                if (inventory)
                    it.replaceInventoryItem(item, newItem, 3)
                else
                    it.replaceEquipmentItem(item, newItem, 3)
            }
        }
    }.andThen(teleportConsumer(position, TeleportType.JEWELRY_RUB))

    private fun getCharges(item: Item) = item.name().substringAfterLast("(").substringBeforeLast(")")
    private fun getBarbarianOutPostPosition() = Position(Random.nextInt(2532, 2534), Random.nextInt(3569, 3571), 0)
    private fun getBurthopeGamesRoomPosition() = Position(Random.nextInt(2196, 2199), Random.nextInt(4959, 4960), 0)
    private fun getCorporealBeastCavePosition() = Position(Random.nextInt(2966, 2969), Random.nextInt(4381, 4384), 2)
    private fun getTearsOfGuthixPosition() = Position(Random.nextInt(3256, 3258), Random.nextInt(9516, 9518), 2)
    private fun getWinterTodtCampPosition() = Position(Random.nextInt(1627, 1633), Random.nextInt(3980, 3984), 0)
}

