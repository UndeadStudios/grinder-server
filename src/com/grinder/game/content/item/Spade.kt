package com.grinder.game.content.item

import com.grinder.game.content.cluescroll.task.ClueTaskFactory
import com.grinder.game.content.cluescroll.task.ClueType
import com.grinder.game.content.quest.impl.PiratesTreasure
import com.grinder.game.entity.agent.movement.teleportation.TeleportType
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.playSound
import com.grinder.game.entity.agent.player.resetInteractions
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Animation
import com.grinder.game.model.areas.impl.GiantMoleCave
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.onFirstInventoryAction
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID

/**
 * Handles the spade dig action.
 */
object Spade {

    init {
        onFirstInventoryAction(ItemID.SPADE) {
            if (player.passedTime(Attribute.GENERIC_ACTION, 1, message = false)) {
                player.resetInteractions()
                player.playSound(Sounds.DIGGING_SPADE)
                player.performAnimation(Animation(830))
                TaskManager.submit(player, 1) {
                    if (player.isTeleporting && player.teleportingType == TeleportType.HOME) {
                        player.stopTeleporting()
                    }
                    if (!player.isTeleporting) {
                        if (player.barrowsManager.dig())
                            return@submit
                        if (GiantMoleCave.dig(player))
                            return@submit
                        if (ClueTaskFactory.executeOperation(player, ClueType.DIG))
                            return@submit
                        if(PiratesTreasure.digForTreasure(player))
                            return@submit
                        player.message("Nothing interesting happens.")
                    }
                }
            }
        }
    }
}