package com.grinder.game.entity.agent.movement.pathfinding.target

import com.grinder.game.entity.Entity
import com.grinder.game.model.Position

/**
 * Checks if on an exact tile
 * @author Greg Hibberd <greg@greghibberd.com>
 * @since May 18, 2020
 */
data class EntityTileTargetStrategy(
    val entity: Entity
) : TargetStrategy {

    override val tile: Position
        get() = entity.position

    override val width: Int
        get() = entity.width

    override val height: Int
        get() = entity.height

    override fun reached(x: Int, y: Int, z: Int, width: Int, height: Int): Boolean {
        return tile.sameAs(x, y, z)
    }
}