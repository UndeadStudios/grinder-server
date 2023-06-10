package com.grinder.game.entity.agent.movement.pathfinding.target

import com.grinder.game.entity.Entity
import com.grinder.game.model.Position

/**
 * @author Greg Hibberd <greg@greghibberd.com>
 * @since May 18, 2020
 */
interface TargetStrategy {
    val tile: Position
    val width: Int
    val height: Int

    fun reached(x: Int, y: Int, z: Int, width: Int, height: Int): Boolean

    fun reached(tile: Position, width: Int, height: Int) = reached(tile.x, tile.y, tile.z, width, height)

    fun reached(entity: Entity) = reached(entity.position, entity.width, entity.height)
}