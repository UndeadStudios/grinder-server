package com.grinder.game.content.`object`

import com.grinder.game.definition.ObjectDefinition
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.`object`.name
import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.playSound
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Animation
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.TaskManager
import java.util.concurrent.TimeUnit

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   03/02/2021
 */
object SearchObjectActions {

    fun isSearchable(definition: ObjectDefinition): Boolean {
        val name = definition.getName()?.toLowerCase()?:return false
        return name.contains("crate")
                || name.contains("bookcase")
                || name.contains("mine cart")
                || name == "boxes"
                || name == "wardrobe"
                || name == "barrel"
                || name == "drawers"
    }

    private fun isCrate(gameObject: GameObject) = gameObject.name().contains("crate", true) || gameObject.id == 15701

    private fun isWardrobe(gameObject: GameObject) = gameObject.name().contains("wardrobe", true) || gameObject.id == 25040

    private fun isBarrel(gameObject: GameObject) = gameObject.name().contains("barrel", true) || gameObject.id == 2619

    fun handle(player: Player, gameObject: GameObject, postSearchAnimationAction: (() -> Boolean)? = null) {
        if (player.passedTime(Attribute.GENERIC_ACTION, 1, TimeUnit.SECONDS, false)){

            if (isCrate(gameObject)) {
                if (postSearchAnimationAction?.invoke() != true)
                    player.message("The crate is empty.")
                return
            } else if (isBarrel(gameObject)) {
                if (postSearchAnimationAction?.invoke() != true)
                    player.message("The barrel is empty.")
                return
            } else if (isWardrobe(gameObject)) {
                    if (postSearchAnimationAction?.invoke() != true)
                        player.message("The wardrobe is locked.")
                        player.playSound(Sounds.DOOR_IS_LOCKED)
                return
            }

            TaskManager.submit(1) {
               player.performAnimation(Animation(833, 15))
               player.motion.update(MovementStatus.DISABLED)
               player.message("You attempt to search the " + gameObject.name() + "...")
               TaskManager.submit(2) {
                   player.motion.update(MovementStatus.NONE)
                   if (postSearchAnimationAction?.invoke() != true){
                       player.message("You don't find anything valuable.")
                   }
               }
           }
       }
   }

}