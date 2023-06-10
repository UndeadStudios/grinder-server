package com.grinder.game.content.item

import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.onFirstInventoryAction
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import java.util.concurrent.TimeUnit

/**
 * Handles the dicer rank item, can be consumed to give dicer rank.
 */
object DicerRank {

    init {
        onFirstInventoryAction(ItemID.REWARD_TOKEN_10) {
            if (player.passedTime(Attribute.GENERIC_ACTION, 1, TimeUnit.SECONDS)) {
                if (player.isDicer) {
                    player.message("You can't use this as you already have the dicer's rank!")
                    return@onFirstInventoryAction
                }
                SkillUtil.stopSkillable(player)
                player.resetInteractions(combat = true)
                if (player.removeInventoryItem(getItem()?:return@onFirstInventoryAction)) {
                    player.performAnimation(Animation(1309))
                    TaskManager.submit(3) {
                        player.performGraphic(Graphic(1389))
                        player.isDicer = true
                        player.playSound(Sounds.USING_LAMP_REWARD)
                        player.message("<img=770> Congratulations, you've now unlocked the @dre@dicer's rank@bla@!")
                        //player.message("<img=779> You can select your primary rank from the rank chooser in your quest tab.")
                        player.progressAchievement(AchievementType.TRUSTED_MEMBER)
                        PlayerUtil.broadcastMessage("<img=770> Congratulations! @whi@" + PlayerUtil.getImages(player) + "" + player.username +"</col> has just redeemed the @dre@dicer's rank</col> token!")
                    }
                }
            }
        }
    }
}