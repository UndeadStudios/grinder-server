package com.grinder.game.content.skill.skillable.impl.agility.obstacle.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.Direction
import com.grinder.game.model.ForceMovement
import com.grinder.game.model.Position
import com.grinder.game.task.Task

/**
 * RockObstacle, which is really a shortcut most the times. Is an agility obstacle that requires the player
 * to climb up, or down a pile of rocks.
 *
 * @param obj The object being climbed.
 * @param climb Perform a upper movement.
 */
class RockObstacle(val player: Player, val climb: Boolean): Task() {

    var cycle = if (climb) 2 else 0
    val start: Position = player.position.clone()

    init {
        bind(player)
    }

    override fun execute() {
        when (cycle++) {
            0 -> {
                player.positionToFace = player.position.transform(0, 1, 0)
                player.packetSender.sendSound(2454, 10);
            }
            1 -> {
                player.performAnimation(Animation(1148))
                player.packetSender.sendSound(2454, 10);
            }
            2 -> {
                // This is the walkdown portion.
                val forceWalk = ForceMovement(start, Position(0, if (climb) 3 else -3), 0, 15,
                        Direction.SOUTH.id, 737)
                //Reset combat
                player.combat.reset(false)
                player.lastPosition = player.position.clone()
                //Reset movement queue
                player.motion.clearSteps()
                //Playerupdating
                player.forceMovement = forceWalk
                player.packetSender.sendSound(2454, 10);
            }
            3 -> {
                player.packetSender.sendSound(2454, 10);
            }
            5 -> {
                player.moveTo(start.transform(0, if (climb) 3 else -3, 0))
                player.forceMovement = null
                if (climb)
                    player.sendMessage("You climb into the pit.")
                else
                    player.sendMessage("You climb out of the pit.")

                stop()
            }
        }
    }
}