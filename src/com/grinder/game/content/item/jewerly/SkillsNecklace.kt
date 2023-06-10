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
 * https://oldschool.runescape.wiki/w/Skills_necklace
 *
 * "A skills necklace is a dragonstone necklace that has been enchanted with the Lvl-5 Enchant spell.
 * When charged, a worn skills necklace provides a slight increase
 * to the chance of finding a casket whilst fishing with a big net."
 *
 * TODO: add farming guild teleport
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/04/2020
 * @version 1.0
 */
object SkillsNecklace {

    private val SKILLS_NECKLACE_IDS = intArrayOf(
            ItemID.SKILLS_NECKLACE_1_,
            ItemID.SKILLS_NECKLACE_2_,
            ItemID.SKILLS_NECKLACE_3_,
            ItemID.SKILLS_NECKLACE_4_,
            ItemID.SKILLS_NECKLACE_5_,
            ItemID.SKILLS_NECKLACE_6_)

    init {
        onSecondInventoryAction(*SKILLS_NECKLACE_IDS) {
            handleRubbing(player, getItemId(), inventory = true)
        }
        onSecondContainerEquipmentAction(*SKILLS_NECKLACE_IDS) {
            handleRubbing(player, getItemId(), inventory = false)
        }
    }

    private fun handleRubbing(player: Player, itemId: Int, inventory: Boolean) {
        player.message("You rub the necklace...")
        player.playSound(Sounds.RUB_JEWELRY)
        player.removeInterfaces()
        player.sendOptions(
                "Fishing Guild." to consumer(itemId, inventory, getFishingGuildPosition()),
                "Mining Guild." to consumer(itemId, inventory, getMiningGuildPosition()),
                "Crafting Guild." to consumer(itemId, inventory, getCraftingGuildPosition()),
                "Cooking Guild." to consumer(itemId, inventory, getCookingGuildPosition()),
                "Woodcutting Guild." to consumer(itemId, inventory, getWoodcuttingGuildPosition()),
                "Farming Guild." to consumer(itemId, inventory, getFarmingGuildPosition())
        )
    }

    private fun consumer(itemId: Int, inventory: Boolean, position: Position) = Consumer<Player> {

        val item = Item(itemId)
        val charges = getCharges(item).toIntOrNull()?:1

        if(charges == 1){
            val regularNecklace = Item(ItemID.SKILLS_NECKLACE, 1)
            it.message("Your skills necklace ran out of charges.", Color.JEWERLY,3)
            if(inventory)
                it.replaceInventoryItem(item, regularNecklace,3)
            else
                it.replaceEquipmentItem(item, regularNecklace,3)
        } else {
            val newItemDefinition = ItemDefinition.forName(item.name().replaceAfterLast("(", "${charges-1})"))
            if(newItemDefinition != null) {
                val newItem = Item(newItemDefinition.id, 1)
                it.message("Your skills necklace has ${getCharges(newItem)} charges left.", Color.JEWERLY,3)
                if (inventory)
                    it.replaceInventoryItem(item, newItem, 3)
                else
                    it.replaceEquipmentItem(item, newItem, 3)
            }
        }
    }.andThen(dragonstoneTeleport(position, TeleportType.JEWELRY_RUB))

    private fun getCharges(item: Item) = item.name().substringAfterLast("(").substringBeforeLast(")")
    private fun getFishingGuildPosition() = Position(Random.nextInt(2610, 2613), Random.nextInt(3389, 3391), 0)
    private fun getMiningGuildPosition() = Position(3046, Random.nextInt(9758, 9759), 0)
    private fun getCookingGuildPosition() = Position(Random.nextInt(3144, 3146), Random.nextInt(3438, 3439), 0)
    private fun getCraftingGuildPosition() = Position(Random.nextInt(2931, 2933), Random.nextInt(3296, 3297), 0)
    private fun getWoodcuttingGuildPosition()  = Position(Random.nextInt(1659, 1662), Random.nextInt(3503, 3506), 0)
    private fun getFarmingGuildPosition()  = Position(Random.nextInt(1248, 1249), Random.nextInt(3718, 3720), 0)
}

