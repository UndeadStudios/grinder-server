package com.grinder.game.entity.agent.movement.pathfinding.traverse

import com.grinder.game.collision.TileFlags
import com.grinder.game.model.Direction

/**
 * Checks for collision in the direction of movement for entities of size 2x2
 * @author Greg Hibberd <greg@greghibberd.com>
 * @since May 18, 2020
 */
class MediumTraversal(override val type: TraversalType, collidesWithEntities: Boolean) : TraversalStrategy {

    override val extra = if(collidesWithEntities) TileFlags.ENTITY else 0

    override fun blocked(x: Int, y: Int, z: Int, direction: Direction): Boolean {
        val opposite = direction.opposite
        var offsetX = if (direction.x == 1) SIZE else direction.x
        var offsetY = if (direction.y == 1) SIZE else direction.y
        if (!opposite.isDiagonal) {
            // Start
            if (check(x + offsetX, y + offsetY, z, getNorthCorner(opposite))) {
                return true
            }
            // End
            offsetX = if (direction.x == 0) 1 else if (direction.x == 1) SIZE else -1
            offsetY = if (direction.y == 0) 1 else if (direction.y == 1) SIZE else -1
            if (check(x + offsetX, y + offsetY, z, getSouthCorner(opposite))) {
                return true
            }
        } else {
            // Diagonal
            if (check(x + offsetX, y + offsetY, z, opposite)) {
                return true
            }
            // Vertical
            val dx = if (direction.x == -1) 0 else direction.x
            if (not(x + dx, y + offsetY, z, direction.vertical())) {
                return true
            }
            // Horizontal
            val dy = if (direction.y == -1) 0 else direction.y
            if (not(x + offsetX, y + dy, z, direction.horizontal())) {
                return true
            }
        }
        return false
    }

    companion object {
        const val SIZE = 2
        fun getNorthCorner(direction: Direction): Direction {
            return when (direction) {
                Direction.EAST -> Direction.NORTH_EAST
                Direction.WEST -> Direction.NORTH_WEST
                Direction.NORTH -> Direction.NORTH_EAST
                Direction.SOUTH -> Direction.SOUTH_EAST
                else -> Direction.NONE
            }
        }

        fun getSouthCorner(direction: Direction): Direction {
            return when (direction) {
                Direction.EAST -> Direction.SOUTH_EAST
                Direction.WEST -> Direction.SOUTH_WEST
                Direction.NORTH -> Direction.NORTH_WEST
                Direction.SOUTH -> Direction.SOUTH_WEST
                else -> Direction.NONE
            }
        }
    }
}