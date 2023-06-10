package com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.task

import com.grinder.game.model.FacingDirection
import com.grinder.util.Misc


/**
 * The directions that the tsunami wall can move in.
 * We could add more if desired.
 *
 * @author Pea2nuts
 */
enum class TsunamiDirection(val facingDirection: FacingDirection, val offsetX: Int, val offsetY: Int) {
    NORTHERN(FacingDirection.SOUTH, -3, 5),
    SOURTHERN(FacingDirection.NORTH, -3, -5);

    companion object {
        fun random(): TsunamiDirection {
            return Misc.randomEnum(TsunamiDirection::class.java)
        }
    }
}