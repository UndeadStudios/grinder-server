package com.grinder.game.content.item

import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask
import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.Skill
import com.grinder.game.model.consumable.ConsumableUtil
import com.grinder.game.model.item.Item
import com.grinder.game.model.onFirstInventoryAction
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID

/**
 * Handles poison chalice, kills the player 4 ticks after consumption.
 */
object PoisonChalice {

    init {
        onFirstInventoryAction(ItemID.POISON_CHALICE) {
            val potion = getItem()?:return@onFirstInventoryAction
            if (player.canConsumeDrink(potion)){
                if (player.replaceInventoryItem(potion, Item(ItemID.COCKTAIL_GLASS))) {
                    ConsumableUtil.onDrink(player)
                    player.say("I feel I am being poisoned! HELP!")
                    player.message("You drink the ${getItemId()}")
                    player.progressAchievement(AchievementType.ENLIGHTMENT)
                    TaskManager.submit(4) {
                        if (player.isAlive) {
                            player.combat.queue(Damage(player.getLevel(Skill.HITPOINTS), DamageMask.POISON))
                            player.message("It feels like you're being tortured from the inside!")
                        }
                    }
                }
            }
        }
    }
}