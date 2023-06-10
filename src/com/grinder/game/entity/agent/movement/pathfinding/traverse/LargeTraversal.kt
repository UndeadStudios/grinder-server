package com.grinder.game.entity.agent.movement.pathfinding.traverse

import com.grinder.game.collision.TileFlags
import com.grinder.game.entity.agent.movement.pathfinding.traverse.MediumTraversal.Companion.getNorthCorner
import com.grinder.game.entity.agent.movement.pathfinding.traverse.MediumTraversal.Companion.getSouthCorner
import com.grinder.game.model.Direction

/**
 * Checks for collision in the direction of movement for entities with any size
 * @author Greg Hibberd <greg@greghibberd.com>
 * @since May 18, 2020
 */
class LargeTraversal(override val type: TraversalType, collidesWithEntities: Boolean, val width: Int, val height: Int) : TraversalStrategy {

    override val extra = if(collidesWithEntities) TileFlags.ENTITY else 0

    override fun blocked(x: Int, y: Int, z: Int, direction: Direction): Boolean {
        val opposite = direction.opposite
        var offsetX = if (direction.x == 1) width else direction.x
        var offsetY = if (direction.y == 1) height else direction.y
        if (!opposite.isDiagonal) {
            // Start
            if (check(x + offsetX, y + offsetY, z, getNorthCorner(opposite))) {
                return true
            }
            // End
            offsetX = if (direction.x == -1) -1 else width + (direction.x - 1)
            offsetY = if (direction.y == -1) -1 else height + (direction.y - 1)
            if (check(x + offsetX, y + offsetY, z, getSouthCorner(opposite))) {
                return true
            }
            // In between
            val s = if (direction.y == 0) height else width
            for (offset in 1 until s - 1) {
                offsetX = if (direction.x == 1) width else if (direction.x == -1) -1 else offset
                offsetY = if (direction.y == 1) height else if (direction.y == -1) -1 else offset
                if (not(x + offsetX, y + offsetY, z, direction)) {
                    return true
                }
            }
        } else {
            // Diagonal
            if (check(x + offsetX, y + offsetY, z, opposite)) {
                return true
            }
            // Vertical
            for (offset in 1 until width) {
                val dx = offset - if (direction.x == 1) 0 else 1
                if (not(x + dx, y + offsetY, z, direction.vertical())) {
                    return true
                }
            }
            // Horizontal
            for (offset in 1 until height) {
                val dy = offset - if (direction.y == 1) 0 else 1
                if (not(x + offsetX, y + dy, z, direction.horizontal())) {
                    return true
                }
            }
        }

        return false
    }
}