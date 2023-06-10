package com.grinder.game.entity.agent.movement.pathfinding.traverse

import com.grinder.game.collision.Collisions
import com.grinder.game.collision.TileFlags
import com.grinder.game.model.Direction
import com.grinder.game.model.Position

interface TraversalStrategy {
    fun blocked(x: Int, y: Int, z: Int, direction: Direction): Boolean

    fun blocked(position: Position, direction: Direction): Boolean = blocked(position.x, position.y, position.z, direction)

    fun check(x: Int, y: Int, z: Int, direction: Direction): Boolean {
        return Collisions.check(x, y, z,  direction.block())
    }

    fun not(x: Int, y: Int, z: Int, direction: Direction): Boolean {
        return Collisions.check(x, y, z,  direction.not())
    }

    val type: TraversalType
    val extra: Int// Collides with entities

    /**
     * Blocked in a given direction, including any diagonals cardinals
     */
    fun Direction.block() = when (this) {
        Direction.NORTH_WEST -> TileFlags.NORTH_AND_WEST
        Direction.NORTH_EAST -> TileFlags.NORTH_AND_EAST
        Direction.SOUTH_EAST -> TileFlags.SOUTH_AND_EAST
        Direction.SOUTH_WEST -> TileFlags.SOUTH_AND_WEST
        else -> flag()
    } shl type.shift or type.flag or extra

    /**
     * Blocked in any direction other than [this] and it's diagonals
     */
    fun Direction.not() = when (this) {
        Direction.NORTH_WEST -> TileFlags.SOUTH_AND_EAST
        Direction.NORTH -> TileFlags.NOT_NORTH
        Direction.NORTH_EAST -> TileFlags.SOUTH_AND_WEST
        Direction.EAST -> TileFlags.NOT_EAST
        Direction.SOUTH_EAST -> TileFlags.NORTH_AND_WEST
        Direction.SOUTH -> TileFlags.NOT_SOUTH
        Direction.SOUTH_WEST -> TileFlags.NORTH_AND_EAST
        Direction.WEST -> TileFlags.NOT_WEST
        Direction.NONE -> 0
    } shl type.shift or type.flag or extra

}