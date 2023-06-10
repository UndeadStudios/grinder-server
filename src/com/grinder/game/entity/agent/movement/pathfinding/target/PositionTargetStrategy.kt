package com.grinder.game.entity.agent.movement.pathfinding.target

import com.grinder.game.model.Position

/**
 * Checks if on an exact tile
 * @author Greg Hibberd <greg@greghibberd.com>
 * @since May 18, 2020
 */
data class PositionTargetStrategy(
    override val tile: Position,
    override val width: Int = 1,
    override val height: Int = 1
) : TargetStrategy {

    override fun reached(x: Int, y: Int, z: Int, width: Int, height: Int): Boolean {
        return tile.sameAs(x, y, z)
    }
}