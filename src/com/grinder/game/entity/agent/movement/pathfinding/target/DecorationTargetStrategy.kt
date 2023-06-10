package com.grinder.game.entity.agent.movement.pathfinding.target

import com.grinder.game.collision.Collisions
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.model.Direction
import com.grinder.game.model.Position

/**
 * Checks if within interact range of a targeted decoration
 * - Refactored from client
 * @author Greg Hibberd <greg@greghibberd.com>
 * @since May 18, 2020
 */
data class DecorationTargetStrategy(
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

    override fun reached(x: Int, y: Int, z: Int, width: Int, height: Int): Boolean {
        val sizeXY = width
        var rotation = rotation
        if (sizeXY == 1) {
            if (tile.x == x && y == tile.y) {
                return true
            }
        } else if (x <= tile.x && sizeXY + x - 1 >= tile.x && tile.y <= sizeXY + tile.y - 1) {
            return true
        }
        if (sizeXY == 1) {
            if (type == 6 || type == 7) {
                if (type == 7) {
                    rotation = rotation + 2 and 0x3
                }
                if (rotation == 0) {
                    if (x == tile.x + 1 && y == tile.y && !Collisions.check(x, y, z, Direction.WEST.flag())) {
                        return true
                    }
                    if (tile.x == x && y == tile.y - 1 && !Collisions.check(x, y, z, Direction.NORTH.flag())) {
                        return true
                    }
                } else if (rotation == 1) {
                    if (x == tile.x - 1 && y == tile.y && !Collisions.check(x, y, z, Direction.EAST.flag())) {
                        return true
                    }
                    if (tile.x == x && y == tile.y - 1 && !Collisions.check(x, y, z, Direction.NORTH.flag())) {
                        return true
                    }
                } else if (rotation == 2) {
                    if (x == tile.x - 1 && tile.y == y && !Collisions.check(x, y, z, Direction.EAST.flag())) {
                        return true
                    }
                    if (tile.x == x && y == tile.y + 1 && !Collisions.check(x, y, z, Direction.SOUTH.flag())) {
                        return true
                    }
                } else if (rotation == 3) {
                    if (tile.x + 1 == x && y == tile.y && !Collisions.check(x, y, z, Direction.WEST.flag())) {
                        return true
                    }
                    if (tile.x == x && y == tile.y + 1 && !Collisions.check(x, y, z, Direction.SOUTH.flag())) {
                        return true
                    }
                }
            }
            if (type == 8) {
                if (tile.x == x && y == tile.y + 1 && !Collisions.check(x, y, z, Direction.SOUTH.flag())) {
                    return true
                }
                if (x == tile.x && tile.y - 1 == y && !Collisions.check(x, y, z, Direction.NORTH.flag())) {
                    return true
                }
                return if (x == tile.x - 1 && tile.y == y && !Collisions.check(x, y, z, Direction.EAST.flag())) {
                    true
                } else tile.x + 1 == x && tile.y == y && !Collisions.check(x, y, z, Direction.WEST.flag())
            }
        } else {
            val sizeX = sizeXY + x - 1
            val sizeY = y + sizeXY - 1
            if (type == 6 || type == 7) {
                if (type == 7) {
                    rotation = rotation + 2 and 0x3
                }
                if (rotation == 0) {
                    if (x == tile.x + 1 && y <= tile.y && tile.y <= sizeY && !Collisions.check(x, tile.y, z, Direction.WEST.flag())) {
                        return true
                    }
                    if (tile.x in x..sizeX && y == tile.y - sizeXY && !Collisions.check(tile.x, sizeY, z, Direction.NORTH.flag())) {
                        return true
                    }
                } else if (rotation == 1) {
                    if (x == tile.x - sizeXY && tile.y >= y && tile.y <= sizeY && !Collisions.check(sizeX, tile.y, z, Direction.EAST.flag())) {
                        return true
                    }
                    if (tile.x in x..sizeX && y == tile.y - sizeXY && !Collisions.check(tile.x, sizeY, z, Direction.NORTH.flag())) {
                        return true
                    }
                } else if (rotation == 2) {
                    if (tile.x - sizeXY == x && tile.y >= y && tile.y <= sizeY && !Collisions.check(sizeX, tile.y, z, Direction.EAST.flag())) {
                        return true
                    }
                    if (tile.x in x..sizeX && tile.y + 1 == y && !Collisions.check(tile.x, y, z, Direction.SOUTH.flag())) {
                        return true
                    }
                } else if (rotation == 3) {
                    if (x == tile.x + 1 && y <= tile.y && tile.y <= sizeY && !Collisions.check(x, tile.y, z, Direction.WEST.flag())) {
                        return true
                    }
                    if (tile.x in x..sizeX && y == tile.y + 1 && !Collisions.check(tile.x, y, z, Direction.SOUTH.flag())) {
                        return true
                    }
                }
            }
            if (type == 8) {
                if (tile.x in x..sizeX && y == tile.y + 1 && !Collisions.check(tile.x, y, z, Direction.SOUTH.flag())) {
                    return true
                }
                if (tile.x in x..sizeX && y == tile.y - sizeXY && !Collisions.check(tile.x, sizeY, z, Direction.NORTH.flag())) {
                    return true
                }
                return if (x == tile.x - sizeXY && y <= tile.y && tile.y <= sizeY && !Collisions.check(sizeX, tile.y, z, Direction.EAST.flag())) {
                    true
                } else x == tile.x + 1 && y <= tile.y && tile.y <= sizeY && !Collisions.check(x, tile.y, z, Direction.WEST.flag())
            }
        }
        return false
    }
}