package com.grinder.game.content.`object`

import com.grinder.game.content.miscellaneous.TravelSystem
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.playAreaSound
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.game.model.onFirstObjectAction
import com.grinder.util.ObjectID

object ChasmOfFire {

    private val CLIMB_DOWN_ROPE_ANIMATION = Animation(827)

    init {

        // Chasm of Fire entrance
        onFirstObjectAction(ObjectID.CHASM) {
            TravelSystem.scheduleMoveTo(it, CLIMB_DOWN_ROPE_ANIMATION, destination = Position(1434, 10078, 3))
        }

        onFirstObjectAction(ObjectID.LIFT_5) {
            val player = it.player
            val destination = when(player.z) {
                3 -> player.position.copy().setZ(2)
                2 -> player.position.copy().setZ(1)
                else -> return@onFirstObjectAction
            }
            handleLift(player, destination)
        }

        onFirstObjectAction(ObjectID.LIFT_6) {
            val player = it.player
            val destination = when(player.z) {
                2 -> player.position.copy().setZ(3)
                1 -> player.position.copy().setZ(2)
                else -> return@onFirstObjectAction
            }
            handleLift(player, destination)
        }
    }

    private fun handleLift(player: Player, destination: Position?) {
        if (TravelSystem.fadeTravelAction(player, duration = 3, tickDelay = 2) {
                    player.moveTo(destination)
                    player.playAreaSound(1539, radius = 3)
                }) {
            player.playAreaSound(1541, radius = 5)
        }
    }

}