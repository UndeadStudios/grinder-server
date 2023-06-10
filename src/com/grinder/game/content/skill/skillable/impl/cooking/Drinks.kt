@file:JvmName("Drinks")
package com.grinder.game.content.skill.skillable.impl.cooking

import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.content.skill.skillable.ItemCreationSkillable
import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.addExperience
import com.grinder.game.entity.agent.player.getLevel
import com.grinder.game.model.Animation
import com.grinder.game.model.Skill
import com.grinder.game.model.interfaces.menu.impl.DoubleItemCreationMenu
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.RequiredItem
import com.grinder.util.ItemID
import com.grinder.util.timing.Timer
import com.grinder.util.timing.TimerKey
import java.util.*
import kotlin.math.min

/**
 * Holds information on the cutting options a fruit has.
 *
 * @param fruit The fruitID.
 * @param options Different options of cutting a fruit.
 */
enum class FruitOptions(val fruit:Int, val options:IntArray) {
    LEMON(ItemID.LEMON, intArrayOf(ItemID.LEMON_SLICES, ItemID.LEMON_CHUNKS)),
    LIME(ItemID.LIME, intArrayOf(ItemID.LIME_SLICES, ItemID.LIME_CHUNKS)),
    ORANGE(ItemID.ORANGE, intArrayOf(ItemID.ORANGE_SLICES, ItemID.ORANGE_CHUNKS)),
    PINEAPPLE(ItemID.PINEAPPLE, intArrayOf(ItemID.PINEAPPLE_RING, ItemID.PINEAPPLE_CHUNKS))
}

/**
 * Presents the player with options of how to cut the fruit.
 *
 * @param player The player.
 * @param slot1 The item used.
 * @param slot2 The item being used on.
 */
fun cutFruit(player: Player, slot1: Int, slot2: Int): Boolean {
    val item1 = player.inventory.atSlot(slot1)
    val item2 = player.inventory.atSlot(slot2)
    for (fruit in FruitOptions.values()) {
        if (item1?.id == ItemID.KNIFE && item2?.id == fruit.fruit || item2?.id == ItemID.KNIFE && item1?.id == fruit.fruit) {
            when(fruit.options.size) {
                2-> player.creationMenu = Optional.of(DoubleItemCreationMenu(player, fruit.options[0], fruit.options[1],
                        "What would you like to make?") { i: Int, item: Int, amt: Int ->
                    val amount = min(amt, player.inventory.getAmount(fruit.fruit))
                    SkillUtil.startSkillable(player, ItemCreationSkillable(
                            listOf(RequiredItem(Item(fruit.fruit), true)), Item(item), amount, null, 1, 0, Skill.COOKING,
                    "You cut the ${ItemDefinition.forId(fruit.fruit).name}.", 2))
                }.open())
            }
            return true
        }
    }
    return false
}



val wineAnim = Animation(7529)

/**
 * Holds information on wine.
 *
 * @param grapeID The grap that will be squeezed.
 * @param fermentId The wine fermenting id.
 * @param finishId The final wine product.
 * @param lv Level required to create the wine.
 * @param exp Amount of experienced rewarded.
 */
enum class Wines(val grapeID:Int, val fermentId:Int, val finishId: Int, val lv:Int, val exp:Int) {
    WINE(ItemID.GRAPES, ItemID.UNFERMENTED_WINE, ItemID.JUG_OF_WINE, 35,200),
    ZAMORAK(ItemID.ZAMORAKS_GRAPES,-ItemID.ZAMORAKS_UNFERMENTED_WINE, ItemID.WINE_OF_ZAMORAK, 65,200);
}

/**
 * attempts to make win out of grapes.
 *
 * @param player The player.
 * @param slot1 The item used.
 * @param slot2 The item being used on.
 */
fun squeezeGrapes(player: Player, slot1: Int, slot2: Int): Boolean {
    val item1 = player.inventory[slot1]
    val item2 = player.inventory[slot2]
    if (waterItem[2].first !=item1.id && waterItem[2].first != item2.id)
        return false
    for (wine in Wines.values()) {
        if (wine.grapeID ==item1.id || wine.grapeID == item2.id) {
            if (wine.lv > player.getLevel(Skill.COOKING)) {
                player.sendMessage("You need at least ${wine.lv} cooking to ferment these grapes.")
                continue
            }
            player.inventory.delete(wine.grapeID, 1)
            player.inventory.delete(waterItem[2].first, 1)
            player.inventory.add(wine.fermentId, 1)
            player.timerRepository.register(FermentTimer(player))
            player.performAnimation(wineAnim)
            player.sendMessage("You squeeze the grapes into the jug. The wine begins to ferment.")
            return true
        }
    }
    return false
}

/**
 * A timer that extends the timer class allowing fermented wine to be done after 12 seconds.
 *
 * @param player The player mixing wine.
 */
class FermentTimer(val player: Player) : Timer(TimerKey.WINE_FERMENT, 20) {

    companion object {
        fun ferment(player:Player) {
            for (wine in Wines.values()) {
                val invCount = player.inventory.getAmount(wine.fermentId)
                if (invCount > 0) {
                    val invSuccess = cookSuccess(player, wine.lv)
                    player.inventory.replaceAll(wine.fermentId, if (invSuccess) wine.finishId else ItemID.JUG_OF_BAD_WINE)
                    if (invSuccess) player.addExperience(Skill.COOKING, invCount * wine.exp)
                }
                for (bank in player.banks) {
                    val bankCount = bank.getAmount(wine.fermentId)
                    if (bankCount > 0) {
                        val bankSuccess = cookSuccess(player, wine.lv)
                        bank.replaceAll(wine.fermentId, if (bankSuccess) wine.finishId else ItemID.JUG_OF_BAD_WINE)
                        if (bankSuccess) player.addExperience(Skill.COOKING, bankCount * wine.exp)
                    }
                }
            }
        }
    }

    override fun tick() {
        if (this.ticks() == 1) {
            ferment(player)
        }
        super.tick()
    }
}