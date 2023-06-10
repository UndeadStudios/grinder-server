package com.grinder.game.content.miscellaneous

import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.entity.agent.movement.teleportation.TeleportType
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Animation
import com.grinder.game.model.Direction
import com.grinder.game.model.ObjectActions
import com.grinder.game.model.Position
import com.grinder.game.model.attribute.Attribute.Companion.TRAVEL_ACTION
import com.grinder.game.task.TaskManager
import java.util.concurrent.TimeUnit

/**
 * Utility class for displacement of [players][Player].
 */
object TravelSystem {

    /**
     * Directly moves the [player] to the [destination]
     * and make the [player] face the [faceDirection].
     */
    fun instantMoveTo(player: Player, destination: Position, faceDirection: Direction = Direction.NONE) {
        player.resetInteractions(combat = false)
        player.moveTo(destination)
        if(faceDirection != Direction.NONE){
            player.setPositionToFace(destination.clone().move(faceDirection), true)
        }
    }

    /**
     * Moves the [Player] contained in the [objectAction]
     * to the [destination] with a 1 tick delay.
     */
    fun scheduleMoveTo(objectAction: ObjectActions.ClickAction, animation: Animation? = null, tickDelay: Int = 1, destination: Position) {
        val player = objectAction.player
        val obj = objectAction.getObject()
        player.block()
        player.resetInteractions(combat = false)
        ObjectActions.faceObj(player, obj, obj.definition)
        TaskManager.submit(player, tickDelay) {
            player.moveTo(destination)
            player.unblock()
        }
        player.performAnimation(animation ?: return)
    }


    /**
     * Executes the [function] after [tickDelay]
     * and sends a fading screen effect to the [player].
     */
    fun fadeTravelAction(player: Player,
                         checkBusy: Boolean = true,
                         checkSpam: Boolean = true,
                         screenMessage: String = "",
                         state: Int = 2,
                         duration: Int = 4,
                         tickDelay: Int = 3,
                         function: () -> Unit
    ) : Boolean {

        if (checkBusy && player.busy())
            return false

        if(checkSpam && !player.passedTime(TRAVEL_ACTION, 3, TimeUnit.SECONDS))
            return false

        player.block()
        player.resetInteractions(combat = false)
        player.packetSender.sendMinimapFlagRemoval()
        SkillUtil.stopSkillable(player)
        player.isUntargetable = true
        player.setTeleporting(TeleportType.LEVER)
        player.packetSender.sendFadeScreen(screenMessage, state, duration)

        TaskManager.submit(player, tickDelay) {
            player.removeInterfaces()
            player.unblock()
            player.setTeleporting(null)
            player.isUntargetable = false
            function.invoke()
        }

        return true
    }

    /**
     * Moves the [player] to the [destination] with a [tickDelay]
     * and sends a fading screen effect to the [player].
     */
    fun fadeTravel(player: Player,
                   checkBusy: Boolean = true,
                   checkSpam: Boolean = true,
                   screenMessage: String = "",
                   state: Int = 2,
                   duration: Int = 4,
                   tickDelay: Int = 3,
                   destination: Position,
                   function: () -> Unit = {}
    ): Boolean {
        return fadeTravelAction(player, checkBusy, checkSpam, screenMessage, state, duration, tickDelay) {
            player.moveTo(destination)
            function.invoke()
        }
    }
}