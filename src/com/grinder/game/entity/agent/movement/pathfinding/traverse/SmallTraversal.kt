package com.grinder.game.entity.agent.movement.pathfinding.traverse

import com.grinder.game.collision.TileFlags
import com.grinder.game.model.Direction

/**
 * Checks for collision in the direction of movement for entities of size 1x1
 * If direction of movement is diagonal then both horizontal and vertical directions are checked too.
 *
 * @author Greg Hibberd <greg@greghibberd.com>
 * @since May 18, 2020
 */
class SmallTraversal(override val type: TraversalType, collidesWithEntities: Boolean) : TraversalStrategy {

    override val extra = if(collidesWithEntities) TileFlags.ENTITY else 0

    override fun blocked(x: Int, y: Int, z: Int, direction: Direction): Boolean {
        val opposite = direction.opposite
        if (check(x + direction.x, y + direction.y, z, opposite)) {
            return true
        }
        if (!direction.isDiagonal) {
            return false
        }
        // Horizontal
        if (check(x + direction.x, y, z, opposite.horizontal())) {
            return true
        }
        // Vertical
        if (check(x, y + direction.y, z, opposite.vertical())) {
            return true
        }
        return false
    }
}