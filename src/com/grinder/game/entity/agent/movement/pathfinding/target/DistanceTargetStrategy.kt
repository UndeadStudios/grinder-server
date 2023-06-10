package com.grinder.game.entity.agent.movement.pathfinding.target

import com.grinder.game.entity.Entity
import com.grinder.game.model.Position

/**
 * Motherlode mine hoppers are also weird
 * @author Greg Hibberd <greg@gregs.world>
 * @since Feb 15, 2020
 */
data class DistanceTargetStrategy(
    val entity: Entity,
    val distance: Int
) : TargetStrategy {

    override val tile: Position
        get() = entity.position

    override val width: Int
        get() = entity.width

    override val height: Int
        get() = entity.height

    override fun reached(x: Int, y: Int, z: Int, width: Int, height: Int): Boolean {
        return tile.isWithinDistance(x, y, z, distance)
    }
}