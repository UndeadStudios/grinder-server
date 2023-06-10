package com.grinder.game.content.skill.skillable.impl.runecrafting.abyss.obstacle

import com.grinder.game.content.skill.skillable.impl.runecrafting.abyss.ObstacleType
import com.grinder.game.entity.`object`.DynamicGameObject
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.setBoolean
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.sound.Sound
import com.grinder.game.task.TaskManager
import com.grinder.game.task.impl.TimedObjectReplacementTask
import com.grinder.util.TaskFunctions.delayBy
import com.grinder.util.TaskFunctions.repeatDelayedInterruptable
import java.util.concurrent.ThreadLocalRandom

abstract class PassableObstacle(val exit: Position, val type : ObstacleType) {

    /**
     *  Attempt to pass through an obstacle
     */
    open fun pass(player: Player, gameObject: GameObject) {
        if(player.getBoolean(Attribute.PASSING_OBSTACLE)) return

        if(!canDo(player)) {
            player.message(type.fail)
            return
        }

        player.positionToFace = gameObject.position
        player.performAnimation(getAnim(player))
        player.playSound(getSound())
        player.message(type.start)
        player.setBoolean(Attribute.PASSING_OBSTACLE, true);

        repeatDelayedInterruptable(3, player, {player.setBoolean(Attribute.PASSING_OBSTACLE, false)}) {
            val chance = player.skills.getLevel(type.skill)

            if(ThreadLocalRandom.current().nextInt(0, 100) < chance) {
                player.message(type.success)
                player.performAnimation(Animation(-1))
                onEnd(player, gameObject)
                delayBy(3) {
                    player.moveTo(exit)
                }

                stop()
            } else {
                player.performAnimation(getAnim(player))
                player.playSound(getSound())
            }
        }

    }

    protected abstract fun canDo(player: Player) : Boolean

    protected abstract  fun onEnd(player: Player, gameObject: GameObject)

    protected abstract fun getSound() : Sound

    protected abstract  fun getAnim(player: Player) : Animation

    companion object {


        /**
         * Clears an object for 6 ticks.
         */
        fun replaceObject(player: Player, obj : GameObject, replace : Int) {
            TaskManager.submit(TimedObjectReplacementTask(obj,
                    DynamicGameObject.createPublic(replace, obj.position, obj.objectType, obj.face),6))
        }

    }
}