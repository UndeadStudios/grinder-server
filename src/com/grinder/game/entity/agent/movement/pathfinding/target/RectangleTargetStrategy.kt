package com.grinder.game.entity.agent.movement.pathfinding.target

import com.grinder.game.collision.Collisions
import com.grinder.game.entity.Entity
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Direction
import com.grinder.game.model.Position
import kotlin.math.max
import kotlin.math.min

/**
 * Checks if within interact range of a rectangle
 * Used for NPCs of differing sizes
 * @author Greg Hibberd <greg@greghibberd.com>
 * @since May 18, 2020
 */
data class RectangleTargetStrategy @JvmOverloads constructor(
    private val entity: Entity,
    val blockFlag: Int = 0
) : TargetStrategy {

    override val tile: Position
        get() = when (entity) {
            is NPC -> entity.motion.nextPosition
            is Player -> entity.motion.nextPosition
            else -> entity.position
        }

    override val width: Int
        get() = entity.width

    override val height: Int
        get() = entity.height

    private fun free(x: Int, y: Int, z: Int, direction: Direction): Boolean {
        return !Collisions.check(x, y, z, direction.flag())
    }

    override fun reached(x: Int, y: Int, z: Int, width: Int, height: Int): Boolean {
        if (entity.isUnder(x, y, width, height)) {
            return false
        }
        val srcEndX = x + width
        val srcEndY = y + height
        val destEndX = tile.x + this.width
        val destEndY = tile.y + this.height
        if (x == destEndX && blockFlag and EAST == 0) {
            for (dy in max(y, tile.y) until min(destEndY, srcEndY)) {
                if (free(destEndX - 1, dy, z, Direction.EAST)) {
                    return true
                }
            }
        } else if (tile.x == srcEndX && blockFlag and WEST == 0) {
            for (dy in max(y, tile.y) until min(destEndY, srcEndY)) {
                if (free(tile.x, dy, z, Direction.WEST)) {
                    return true
                }
            }
        } else if (y == destEndY && blockFlag and NORTH == 0) {
            for (dx in max(x, tile.x) until min(destEndX, srcEndX)) {
                if (free(dx, destEndY - 1, z, Direction.NORTH)) {
                    return true
                }
            }
        } else if (tile.y == srcEndY && blockFlag and SOUTH == 0) {
            for (dx in max(x, tile.x) until min(destEndX, srcEndX)) {
                if (free(dx, tile.y, z, Direction.SOUTH)) {
                    return true
                }
            }
        }
        return false
    }

    companion object {
        private const val NORTH = 0x1
        private const val EAST = 0x2
        private const val SOUTH = 0x4
        private const val WEST = 0x8
    }
}