package com.grinder.game.entity.agent.movement.pathfinding.target

import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.grounditem.ItemOnGround
import com.grinder.game.model.Position

/**
 * Checks if within reachable range of a tile
 * e.g floor item on a tile or table
 * Note: Doesn't check if blocked
 * @author Greg Hibberd <greg@greghibberd.com>
 * @since May 18, 2020
 */
data class PointTargetStrategy(
    override val tile: Position,
    override val width: Int,
    override val height: Int
) : TargetStrategy {

    constructor(floorItem: ItemOnGround) : this(floorItem.position, floorItem.width, floorItem.height)

    constructor(gameObject: GameObject) : this(gameObject.position, gameObject.width, gameObject.height)

    override fun reached(x: Int, y: Int, z: Int, width: Int, height: Int): Boolean {
        if (tile.x + width <= x || tile.x >= x + width) {
            return false
        }
        return y < tile.y + height && height + y > tile.y
    }
}