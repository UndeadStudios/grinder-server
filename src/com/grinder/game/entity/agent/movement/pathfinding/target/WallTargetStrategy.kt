package com.grinder.game.entity.agent.movement.pathfinding.target

import com.grinder.game.collision.Collisions
import com.grinder.game.collision.TileFlags
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.model.Direction
import com.grinder.game.model.Position

/**
 * Checks if within interact range of a wall
 * e.g On the correct side to view a painting on a wall
 * - Refactored from client
 * @author Greg Hibberd <greg@greghibberd.com>
 * @since May 18, 2020
 */
data class WallTargetStrategy(
    private val gameObject: GameObject
) : TargetStrategy {

    override val tile: Position
        get() = gameObject.position

    override val width: Int
        get() = gameObject.width

    override val height: Int
        get() = gameObject.height

    val rotation: Int
        get() = gameObject.face

    val type: Int
        get() = gameObject.objectType

    override fun reached(currentX: Int, currentY: Int, plane: Int, width: Int, height: Int): Boolean {
        val sizeXY = width
        // Check if under
        if (sizeXY == 1 && currentX == tile.x && currentY == tile.y) {
            return true
        } else if (sizeXY != 1 && tile.x >= currentX && tile.x <= currentX + sizeXY - 1 && tile.y <= tile.y + sizeXY - 1) {
            return true
        }

        if (sizeXY == 1) {
            if (type == 0) {
                var direction = Direction.CARDINAL[rotation + 3 and 0x3]
                if (currentX == tile.x + direction.x && currentY == tile.y + direction.y) {
                    return true
                }
                direction = Direction.CARDINAL[rotation and 0x3]
                if (currentX == tile.x - direction.x && currentY == tile.y - direction.y && !Collisions.check(currentX, currentY, plane, direction.wall())) {
                    return true
                }
                val inverse = direction.opposite
                if (currentX == tile.x - inverse.x && currentY == tile.y - inverse.y && !Collisions.check(currentX, currentY, plane, inverse.wall())) {
                    return true
                }
            }
            if (type == 2) {
                val direction = Direction.ORDINAL[rotation and 0x3]
                val horizontal = direction.horizontal()
                if (currentX == tile.x + horizontal.x && currentY == tile.y) {
                    return true
                }
                val vertical = direction.vertical()
                if (currentX == tile.x && currentY == tile.y + vertical.y) {
                    return true
                }
                if (currentX == tile.x - horizontal.x && currentY == tile.y && !Collisions.check(currentX, currentY, plane, horizontal.wall())) {
                    return true
                }
                if (currentX == tile.x && currentY == tile.y - vertical.y && !Collisions.check(currentX, currentY, plane, vertical.wall())) {
                    return true
                }
            }
            if (type == 9) {
                Direction.ORDINAL.forEach { direction ->
                    if (currentX == tile.x - direction.x && currentY == tile.y - direction.y && !Collisions.check(currentX, currentY, plane, direction.flag())) {
                        return true
                    }
                }
                return false
            }
        } else {
            val sizeX = sizeXY + currentX - 1
            val sizeY = sizeXY + currentY - 1
            if (type == 0) {
                if (rotation == 0) {
                    if (currentX == tile.x - sizeXY && tile.y >= currentY && tile.y <= sizeY) {
                        return true
                    }
                    if (currentY == tile.y + 1 && tile.x in currentX..sizeX && !Collisions.check(tile.x, currentY, plane, Direction.SOUTH.wall())) {
                        return true
                    }
                    if (currentY == tile.y - sizeXY && tile.x in currentX..sizeX && !Collisions.check(tile.x, sizeY, plane, Direction.NORTH.wall())) {
                        return true
                    }
                } else if (rotation == 1) {
                    if (currentY == tile.y + 1 && tile.x >= currentX && tile.x <= sizeX) {
                        return true
                    }
                    if (currentX == tile.x - sizeXY && tile.y >= currentY && tile.y <= sizeY && !Collisions.check(sizeX, tile.y, plane, Direction.EAST.wall())) {
                        return true
                    }
                    if (currentX == tile.x + 1 && tile.y >= currentY && tile.y <= sizeY && !Collisions.check(currentX, tile.y, plane, Direction.WEST.wall())) {
                        return true
                    }
                } else if (rotation == 2) {
                    if (currentX == tile.x + 1 && tile.y >= currentY && tile.y <= sizeY) {
                        return true
                    }
                    if (currentY == tile.y + 1 && tile.x in currentX..sizeX && !Collisions.check(tile.x, currentY, plane, Direction.SOUTH.wall())) {
                        return true
                    }
                    if (currentY == tile.y - sizeXY && tile.x in currentX..sizeX && !Collisions.check(tile.x, sizeY, plane, Direction.NORTH.wall())) {
                        return true
                    }
                } else if (rotation == 3) {
                    if (currentY == tile.y - sizeXY && currentX <= tile.x && sizeX >= tile.x) {
                        return true
                    }
                    if (currentX == tile.x - sizeXY && tile.y >= currentY && sizeY >= tile.y && !Collisions.check(sizeX, tile.y, plane, Direction.EAST.wall())) {
                        return true
                    }
                    if (currentX == tile.x + 1 && currentY <= tile.y && sizeY >= tile.y && !Collisions.check(currentX, tile.y, plane, Direction.WEST.wall())) {
                        return true
                    }
                }
            }
            if (type == 2) {
                if (rotation == 0) {
                    if (currentX == tile.x - sizeXY && tile.y >= currentY && sizeY >= tile.y) {
                        return true
                    }
                    if (currentY == tile.y + 1 && tile.x in currentX..sizeX) {
                        return true
                    }
                    if (currentX == tile.x + 1 && currentY <= tile.y && sizeY >= tile.y && !Collisions.check(currentX, tile.y, plane, Direction.WEST.wall())) {
                        return true
                    }
                    if (tile.y - sizeXY == currentY && tile.x in currentX..sizeX && !Collisions.check(tile.x, sizeY, plane, Direction.NORTH.wall())) {
                        return true
                    }
                } else if (rotation == 1) {
                    if (currentX == tile.x - sizeXY && currentY <= tile.y && sizeY >= tile.y && !Collisions.check(sizeX, tile.y, plane, Direction.EAST.wall())) {
                        return true
                    }
                    if (currentY == tile.y + 1 && tile.x in currentX..sizeX) {
                        return true
                    }
                    if (currentX == tile.x + 1 && currentY <= tile.y && sizeY >= tile.y) {
                        return true
                    }
                    if (currentY == tile.y - sizeXY && tile.x in currentX..sizeX && !Collisions.check(tile.x, sizeY, plane, Direction.NORTH.wall())) {
                        return true
                    }
                } else if (rotation == 2) {
                    if (currentX == tile.x - sizeXY && tile.y >= currentY && tile.y <= sizeY && !Collisions.check(sizeX, tile.y, plane, Direction.EAST.wall())) {
                        return true
                    }
                    if (currentY == tile.y + 1 && tile.x in currentX..sizeX && !Collisions.check(tile.x, currentY, plane, Direction.SOUTH.wall())) {
                        return true
                    }
                    if (currentX == tile.x + 1 && currentY <= tile.y && sizeY >= tile.y) {
                        return true
                    }
                    if (currentY == tile.y - sizeXY && tile.x in currentX..sizeX) {
                        return true
                    }
                } else if (rotation == 3) {
                    if (currentX == tile.x - sizeXY && currentY <= tile.y && sizeY >= tile.y) {
                        return true
                    }
                    if (currentY == tile.y + 1 && tile.x in currentX..sizeX && !Collisions.check(tile.x, currentY, plane, Direction.SOUTH.wall())) {
                        return true
                    }
                    if (currentX == tile.x + 1 && currentY <= tile.y && tile.y <= sizeY && !Collisions.check(currentX, tile.y, plane, Direction.WEST.wall())) {
                        return true
                    }
                    if (currentY == tile.y - sizeXY && tile.x in currentX..sizeX) {
                        return true
                    }
                }
            }
            if (type == 9) {
                if (tile.x in currentX..sizeX && currentY == tile.y + 1 && !Collisions.check(tile.x, currentY, plane, Direction.SOUTH.wall())) {
                    return true
                }
                if (tile.x in currentX..sizeX && currentY == tile.y - sizeXY && !Collisions.check(tile.x, sizeY, plane, Direction.NORTH.wall())) {
                    return true
                }
                return if (currentX == tile.x - sizeXY && currentY <= tile.y && sizeY >= tile.y && !Collisions.check(sizeX, tile.y, plane, Direction.EAST.wall())) {
                    true
                } else currentX == tile.x + 1 && currentY <= tile.y && sizeY >= tile.y && !Collisions.check(currentX, tile.y, plane, Direction.WEST.wall())
            }
        }
        return false
    }

    companion object {
        private fun Direction.wall() =
            TileFlags.BLOCKED or TileFlags.WALL or flag()
    }
}