package com.grinder.game.content.skill.skillable.impl.runecrafting

import com.grinder.game.content.skill.task.SkillMasterType
import com.grinder.game.content.skill.task.SkillTaskManager
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.statement
import com.grinder.game.model.Position
import com.grinder.game.model.Skill
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import com.grinder.util.NpcID
import com.grinder.util.TaskFunctions
import com.grinder.util.TaskFunctions.delayBy
import kotlin.math.floor

object OuraniaAltar {

    // Position that players teleport into the Ourania altar.
    val ALTAR_POSITION = Position(3017, 5624)

    /**
     *  Calculate the runes received from the Ourania altar.
     */
    fun getRandomRunes(player: Player, ess : Int): Map<CraftableRune, Double> {
        val level = player.skills.getLevel(Skill.RUNECRAFTING)

        // Random runes from the Ourania altar
        val runes = CraftableRune.values()
                .sortedBy { -it.forLevel.values.first() }
                .associateBy(
                        {it},
                        { floor(it.getOuraniaProbability(player) * ess) }
                ).toMutableMap()

        // Make sure that every essence receives a rune
        val sum = runes.values.sum()
        if(sum < ess) {
            runes[CraftableRune.AIR] = ess-sum
        }

        return runes.mapValues { it.value * it.key.amountForLevel(level).coerceAtMost(1) }
    }

    /**
     * Remove pure essence from the player's inventory and add runes.
     */
    fun craft(player: Player) {

        // Find the amount of essence in the player's inventory
        val essence = player.inventory.getAmount(ItemID.PURE_ESSENCE)

        if(essence == 0) {
            player.statement("You do not have any pure essence to bind.")
            return
        }


        player.BLOCK_ALL_BUT_TALKING = true

        delayBy(1) {
            player.performAnimation(AltarRunecrafting.CRAFT_ANIM)
            player.performGraphic(AltarRunecrafting.CRAFT_GRAPHIC)
            player.playSound(AltarRunecrafting.CRAFT_SOUND)
        }

        TaskFunctions.delayBy(3) {
            // Delete the essence from the player's inventory
            player.inventory.deleteAll(ItemID.PURE_ESSENCE)

            // Add the correct number of runes to the player's inventory
            val runes = getRandomRunes(player, essence)
            runes.forEach { (k, v) ->
                player.inventory.add(Item(k.itemId, v.toInt()))
                SkillTaskManager.perform(player, k.itemId, v.toInt(), SkillMasterType.RUNECRAFTING)
            }

            player.BLOCK_ALL_BUT_TALKING = false

            val exp = runes.map { it.key.exp * it.value }.sum()

            // Add experience
            player.skillManager.addExperience(Skill.RUNECRAFTING, exp)
            player.message("You bind the temple's power into runes.")
        }
    }

}
