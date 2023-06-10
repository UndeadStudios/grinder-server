@file:JvmName("Dough")

package com.grinder.game.content.skill.skillable.impl.cooking

import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.content.skill.skillable.DefaultSkillable
import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.getLevel
import com.grinder.game.model.Skill
import com.grinder.game.model.interfaces.menu.CreationMenu
import com.grinder.game.model.interfaces.menu.impl.QuardrupleItemCreationMenu
import com.grinder.util.ItemID
import java.util.*
import kotlin.math.min

// Pairs of Water->Empty containers
val waterItem = arrayOf(
        Pair(ItemID.BUCKET_OF_WATER, ItemID.BUCKET),
        Pair(ItemID.BOWL_OF_WATER, ItemID.BOWL),
        Pair(ItemID.JUG_OF_WATER, ItemID.JUG))

// Pair of flour->Empty container
val flourPot = Pair(ItemID.POT_OF_FLOUR, ItemID.POT)

/**
 * Contains an array of intArrays. Each array is Pair<Int,Int> representing a dough, and respective level.
 */
val doughList = arrayOf(
        Pair(ItemID.BREAD_DOUGH, 1),
        Pair(ItemID.PASTRY_DOUGH, 1),
        Pair(ItemID.PIZZA_BASE, 35),
        Pair(ItemID.PITTA_DOUGH, 58)
)

/**
 * Confirm the user is mixing flour and a water container. Show them the options they can make, then attempt to create
 * the corresponding type of dough.
 *
 * @param player The player performing the action.
 * @param slot1 The slot of the item used.
 * @param slot2 The slot of the item being used on.
 */
fun mixDough(player: Player, slot1: Int, slot2: Int): Boolean {
    val item1 = player.inventory[slot1]
    val item2 = player.inventory[slot2]
    if (item1?.id == flourPot.first || item2?.id == flourPot.first) {
        for (water in waterItem) {
            if (item1?.id == water.first || item2?.id == water.first) {
                val menu = QuardrupleItemCreationMenu(player, doughList[0].first, doughList[1].first, doughList[2].first,
                        doughList[3].first, "What would you like to make?", DoughAction(player, water))
                player.creationMenu = Optional.of(menu.open())
                return true
            }
        }
        return false
    }
    return false
}

/**
 * Handles mixing water and flour together.
 *
 * @param player The player performing the cooking action.
 * @param water The Pair containing the empty and full water id.
 */
class DoughAction(val player: Player, val water:Pair<Int, Int>): CreationMenu.CreationMenuAction, DefaultSkillable() {
    // we do not need this value until we execute the menu
    lateinit var dough: Pair<Int, Int>
    var amount = 0

    override fun execute(index: Int, item: Int, amount: Int) {
        for (dough in doughList) {
            if (dough.first == item) {
                this.dough = dough
                // This is how OSRS works now, so why not include it.
                val maxAmount = min(player.inventory.getAmount(water.first), player.inventory.getAmount(flourPot.first))
                this.amount = min(amount, maxAmount)
                SkillUtil.startSkillable(player, this)
                return
            }
        }
    }

    override fun startAnimationLoop(player: Player?) {}
    override fun startGraphicsLoop(player: Player?) {}

    override fun startSoundLoop(player: Player?) {}

    override fun cyclesRequired(player: Player?): Int { return 1 }

    override fun finishedCycle(player: Player?) {
        checkNotNull(player)
        player.inventory.replaceFirst(water.first, water.second)
        player.inventory.replaceFirst(flourPot.first, flourPot.second)
        player.inventory.add(dough.first, 1)
        player.sendMessage("You mix the water and flour to make some ${ItemDefinition.forId(dough.first).name}.")
        if (--this.amount <= 0)
            this.cancel(player)
    }

    override fun loopRequirements(): Boolean { return true }
    override fun hasRequirements(player: Player?): Boolean {
        checkNotNull(player)
        if (dough.second > player.getLevel(Skill.COOKING)) {
            player.sendMessage("You need a cooking level of at least ${dough.second} to make " +
                    "${ItemDefinition.forId(dough.first).name}.")
            return false
        }
        if (!player.inventory.contains(flourPot.first) || !player.inventory.contains(water.first))
            return false
        return true
    }

    override fun allowFullInventory(): Boolean { return false }
}