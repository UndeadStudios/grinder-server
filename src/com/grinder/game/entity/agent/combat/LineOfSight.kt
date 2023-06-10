package com.grinder.game.entity.agent.combat

import com.grinder.game.collision.Collisions
import com.grinder.game.collision.TileFlags
import com.grinder.game.entity.Entity
import com.grinder.game.entity.Entity.getNearest
import com.grinder.game.model.Position
import kotlin.math.abs

object LineOfSight {

    private fun blocked(x: Int, y: Int, plane: Int, flip: Boolean, flag: Int): Boolean {
        return if (flip) {
            Collisions.check(y, x, plane, flag)
        } else {
            Collisions.check(x, y, plane, flag)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun withinSight(entity: Entity, other: Position, distance: Int = 0, walls: Boolean = false): Boolean {
        if (distance > 0 && !entity.isWithinDistance(other, distance)) {
            return false
        }
        return withinSight(entity.getNearest(other), other, walls)
    }

    @JvmStatic
    @JvmOverloads
    fun withinSight(entity: Entity, other: Position, width: Int, height: Int, distance: Int = 0, walls: Boolean = false): Boolean {
        val target = getNearest(other, width, height, entity.position)
        if (distance > 0 && !entity.isWithinDistance(target, distance)) {
            return false
        }
        return withinSight(entity.getNearest(other), target, walls)
    }

    @JvmStatic
    @JvmOverloads
    fun withinSight(entity: Entity, other: Entity, distance: Int = 0, walls: Boolean = false): Boolean {
        if (distance > 0 && !entity.isWithinDistance(other, distance)) {
            return false
        }
        return withinSight(entity.getNearest(other.position), other.getNearest(entity.position), walls)
    }

    @JvmStatic
    @JvmOverloads
    fun withinSight(position: Position, other: Entity, distance: Int = 0, walls: Boolean = false): Boolean {
        if (distance > 0 && !other.isWithinDistance(position, distance)) {
            return false
        }
        return withinSight(position, other.getNearest(position), walls)
    }

    /**
     * Checks line of sight in both directions
     */
    @JvmStatic
    @JvmOverloads
    fun withinSight(position: Position, other: Position, walls: Boolean = false): Boolean {
        return canSee(other, position, walls)
    }

    /**
     * A variation of Bresenham's line algorithm which marches from starting point [tile]
     * alternating axis until reaching a blockage or target [other]
     * @return whether there is nothing blocking between the two points
     */
    private fun canSee(tile: Position, other: Position, walls: Boolean = false): Boolean {
        if (tile.z != other.z) {
            return false
        }
        if (tile.x == other.x && tile.y == other.y) {
            return true
        }

        var dx = other.x - tile.x
        var dy = other.y - tile.y
        var dxAbs = abs(dx)
        val dyAbs = abs(dy)

        val flip = dxAbs <= dyAbs

        var horizontalFlag = if(walls) {
            if (dx < 0) TileFlags.LAND_WALL_EAST else TileFlags.LAND_WALL_WEST
        } else {
            if (dx < 0) TileFlags.SKY_BLOCK_EAST else TileFlags.SKY_BLOCK_WEST
        }
        var verticalFlag = if(walls) {
            if (dy < 0) TileFlags.LAND_WALL_NORTH else TileFlags.LAND_WALL_SOUTH
        } else {
            if (dy < 0) TileFlags.SKY_BLOCK_NORTH else TileFlags.SKY_BLOCK_SOUTH
        }

        if (flip) {
            var temp = dx
            dx = dy
            dy = temp
            dxAbs = dyAbs
            temp = horizontalFlag
            horizontalFlag = verticalFlag
            verticalFlag = temp
        }

        var shifted: Int = shift(if (flip) tile.x else tile.y)
        shifted += shiftedHalfTile
        if (needsRounding(dy)) {
            shifted--
        }

        var position: Int = if (flip) tile.y else tile.x
        val target = if (flip) other.y else other.x
        val z = tile.z

        val direction = if (dx < 0) -1 else 1
        val slope = shift(dy) / dxAbs
        while (position != target) {

            position += direction
            val value = revert(shifted)
            if (blocked(position, value, z, flip, horizontalFlag)) {
                return false
            }

            shifted += slope
            val next = revert(shifted)
            if (next != value && blocked(position, next, z, flip, verticalFlag)) {
                return false
            }
        }

        return true
    }

    /**
     * Shift values to avoid rounding errors
     */
    private fun shift(value: Int) = value shl 16

    private fun revert(value: Int) = value ushr 16

    private fun needsRounding(value: Int) = value < 0

    private const val shiftedHalfTile = 0x8000
}