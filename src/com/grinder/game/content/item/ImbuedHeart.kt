package com.grinder.game.content.item

import com.grinder.game.content.dueling.DuelRule
import com.grinder.game.entity.agent.player.getMaxLevel
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.passedTime
import com.grinder.game.entity.timeLeftString
import com.grinder.game.model.Graphic
import com.grinder.game.model.Skill
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.onFirstInventoryAction
import com.grinder.util.ItemID
import com.grinder.util.Priority
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


/**
 * Temporarily increases magic level by 1 to 10, cooldown of 7 minutes.
 * Cooldown timer resets upon death.
 */
object ImbuedHeart {

    init {
        onFirstInventoryAction(ItemID.IMBUED_HEART) {
            if (player.dueling.selectedRule(DuelRule.NO_POTIONS)){
                player.message("You're not allowed to drink during this duel.")
                return@onFirstInventoryAction
            }
            if (!player.passedTime(Attribute.IMBUED_HEART_TIMER, 7, TimeUnit.MINUTES, message = false)){
                val timeLeft = player.timeLeftString(Attribute.IMBUED_HEART_TIMER, 7, TimeUnit.MINUTES)
                player.message("The heart is still drained of its power. Judging by how it feels, it will be ready in around $timeLeft.")
                return@onFirstInventoryAction
            }
            val magicLevel = player.getMaxLevel(Skill.MAGIC)
            val tempMagicLevel = ((magicLevel * 0.10) + 1).roundToInt()
            player.skillManager.increaseLevelTemporarily(Skill.MAGIC, tempMagicLevel)
            player.performGraphic(Graphic(1316, Priority.HIGH))
        }
    }
}