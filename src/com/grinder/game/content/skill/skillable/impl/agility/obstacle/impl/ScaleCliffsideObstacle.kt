package com.grinder.game.content.skill.skillable.impl.agility.obstacle.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.Direction
import com.grinder.game.model.ForceMovement
import com.grinder.game.model.Position
import com.grinder.game.task.Task

/**
 * ScaleCliffsideObstacle, which is really a shortcut most the times. Is an agility obstacle that requires the player
 * to climb up, or down a side of a hill/mountain.
 *
 * @param obj The object being climbed.
 * @param climb Perform a upper movement.
 */
class ScaleCliffsideObstacle(
        val player: Player,
        val climb:Boolean,
        val dist:Int=1,
        val faceDir:Direction=Direction.NORTH): Task() {

    init {
        bind(player)
    }

    var cycle = if (climb) 1 else 0
    val start: Position = player.position.clone()
    val moveDir: Direction = if (climb) faceDir else faceDir.opposite
    val relativeDes = Position(moveDir.x*dist, moveDir.y*dist)

    override fun execute() {
        when (cycle++) {
            0 -> {
                player.BLOCK_ALL_BUT_TALKING = true;
                player.positionToFace = start.transform(faceDir.x, faceDir.y, 0)
            }
            1 -> {
                player.performAnimation(Animation(1148))
                player.packetSender.sendSound(2454, 10);
            }
            2 -> {
                val forceWalk = ForceMovement(start, relativeDes, 3, 40, faceDir.id, 737)
                //Reset combat
                player.lastPosition = player.position.clone()
                //Reset movement queue
                player.combat.reset(false)
                player.motion.clearSteps()
                //Playerupdating
                player.forceMovement = forceWalk
                player.packetSender.sendSound(2454, 10);
            }
            6 -> {
                player.moveTo(start.transform(relativeDes.x, relativeDes.y, 0))
                player.performAnimation(Animation(-1))
                player.forceMovement = null
                player.BLOCK_ALL_BUT_TALKING = false;
                stop()
            }
        }
    }
}