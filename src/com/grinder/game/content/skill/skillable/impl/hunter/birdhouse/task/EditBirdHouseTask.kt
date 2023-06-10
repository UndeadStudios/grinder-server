package com.grinder.game.content.skill.skillable.impl.hunter.birdhouse.task

import com.grinder.game.content.skill.skillable.impl.hunter.birdhouse.BirdHouse
import com.grinder.game.content.skill.skillable.impl.hunter.birdhouse.BirdHouseState
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.unblock
import com.grinder.game.model.Animation
import com.grinder.game.task.Task

/**
 * @author Zach (zach@findzach.com)
 * @since 12/22/2020
 *
 * A task for building our Bird Houses
 */
class EditBirdHouseTask(var player: Player, var birdHouse: BirdHouse) : Task(1, true) {

    var timer = -1;

    override fun execute() {
        // player is never null, use !player.isActive if u want to check if player is still registered
        if (player == null)
            stop()

        if (timer == -1) {
            player.setPositionToFace(birdHouse.spot.hotSpotPos)
        }
        if (timer < 0) {
            if (birdHouse.state == BirdHouseState.BUILT_COLLECTING) {
                player.performAnimation(Animation(543, 1))
                haltTask()
            }

        }
        if (timer == 1) {
            if (birdHouse.state == BirdHouseState.BUILT_EMPTY) {
                player.performAnimation(Animation(3971))
            }
        }
        if (timer > 4) {
            haltTask()
        }

        timer++; // semicolon not required in Kotlin
    }

    fun haltTask() {
        birdHouse.updateBirdHouse(birdHouse.state)
        player.unblock()
        stop()
    }
}