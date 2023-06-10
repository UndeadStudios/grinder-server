package com.grinder.game.collision

object TileFlags {
    // Old
    const val BLOCKED_NORTH = 0x400
    const val BLOCKED_EAST = 0x1000
    const val BLOCKED_SOUTH = 0x4000
    const val BLOCKED_WEST = 0x10000
    const val BLOCKED_NORTHEAST = 0x800
    const val BLOCKED_SOUTHEAST = 0x2000
    const val BLOCKED_NORTHWEST = 0x200
    const val BLOCKED_SOUTHWEST = 0x8000
    const val BLOCKED_EAST_NORTH = BLOCKED_EAST or BLOCKED_NORTH
    const val BLOCKED_EAST_SOUTH = BLOCKED_EAST or BLOCKED_SOUTH
    const val BLOCKED_WEST_SOUTH = BLOCKED_WEST or BLOCKED_SOUTH
    const val BLOCKED_WEST_NORTH = BLOCKED_WEST or BLOCKED_NORTH

    // New
    const val FLOOR = 0x200000
    const val FLOOR_DECO = 0x40000
    const val WALL = 0x80000
    const val ENTITY = 0x100000

    const val UNWALKABLE = 3145728// Allows shooting over

    const val LAND = 0x100
    const val SKY = LAND shl 9// or FLOOR
    const val IGNORED = LAND shl 22

    const val BLOCKED = LAND or FLOOR or FLOOR_DECO

    const val NORTH_WEST = 0x1
    const val NORTH = 0x2
    const val NORTH_EAST = 0x4
    const val EAST = 0x8
    const val SOUTH_EAST = 0x10
    const val SOUTH = 0x20
    const val SOUTH_WEST = 0x40
    const val WEST = 0x80

    const val NORTH_OR_WEST = NORTH or WEST
    const val NORTH_OR_EAST = NORTH or EAST
    const val SOUTH_OR_EAST = SOUTH or EAST
    const val SOUTH_OR_WEST = SOUTH or WEST

    const val NORTH_AND_WEST = NORTH_OR_WEST or NORTH_WEST
    const val NORTH_AND_EAST = NORTH_OR_EAST or NORTH_EAST
    const val SOUTH_AND_EAST = SOUTH_OR_EAST or SOUTH_EAST
    const val SOUTH_AND_WEST = SOUTH_OR_WEST or SOUTH_WEST

    const val NOT_NORTH = SOUTH or EAST or WEST or SOUTH_EAST or SOUTH_WEST
    const val NOT_EAST = NORTH or SOUTH or WEST or NORTH_WEST or SOUTH_WEST
    const val NOT_SOUTH = NORTH or EAST or WEST or NORTH_EAST or NORTH_WEST
    const val NOT_WEST = NORTH or SOUTH or EAST or NORTH_EAST or SOUTH_EAST

    /*
        These aren't supposed to be used, just for understanding and testing.
     */
    const val LAND_BLOCK_NORTH_WEST = BLOCKED or NORTH_AND_WEST
    const val LAND_BLOCK_NORTH = BLOCKED or NORTH
    const val LAND_BLOCK_NORTH_EAST = BLOCKED or NORTH_AND_EAST
    const val LAND_BLOCK_EAST = BLOCKED or EAST
    const val LAND_BLOCK_SOUTH_EAST = BLOCKED or SOUTH_AND_EAST
    const val LAND_BLOCK_SOUTH = BLOCKED or SOUTH
    const val LAND_BLOCK_SOUTH_WEST = BLOCKED or SOUTH_AND_WEST
    const val LAND_BLOCK_WEST = BLOCKED or WEST

    const val LAND_WALL_NORTH_WEST = BLOCKED or NORTH_AND_WEST or WALL
    const val LAND_WALL_NORTH = BLOCKED or NORTH or WALL
    const val LAND_WALL_NORTH_EAST = BLOCKED or NORTH_AND_EAST or WALL
    const val LAND_WALL_EAST = BLOCKED or EAST or WALL
    const val LAND_WALL_SOUTH_EAST = BLOCKED or SOUTH_AND_EAST or WALL
    const val LAND_WALL_SOUTH = BLOCKED or SOUTH or WALL
    const val LAND_WALL_SOUTH_WEST = BLOCKED or SOUTH_AND_WEST or WALL
    const val LAND_WALL_WEST = BLOCKED or WEST or WALL

    const val LAND_CLEAR_NORTH_WEST = BLOCKED or SOUTH_AND_EAST
    const val LAND_CLEAR_NORTH = BLOCKED or NOT_NORTH
    const val LAND_CLEAR_NORTH_EAST = BLOCKED or SOUTH_AND_WEST
    const val LAND_CLEAR_EAST = BLOCKED or NOT_EAST
    const val LAND_CLEAR_SOUTH_EAST = BLOCKED or NORTH_AND_WEST
    const val LAND_CLEAR_SOUTH = BLOCKED or NOT_SOUTH
    const val LAND_CLEAR_SOUTH_WEST = BLOCKED or NORTH_AND_EAST
    const val LAND_CLEAR_WEST = BLOCKED or NOT_WEST

    const val SKY_BLOCK_NORTH_WEST = NORTH_AND_WEST shl 9 or SKY
    const val SKY_BLOCK_NORTH = NORTH shl 9 or SKY
    const val SKY_BLOCK_NORTH_EAST = NORTH_AND_EAST shl 9 or SKY
    const val SKY_BLOCK_EAST = EAST shl 9 or SKY
    const val SKY_BLOCK_SOUTH_EAST = SOUTH_AND_EAST shl 9 or SKY
    const val SKY_BLOCK_SOUTH = SOUTH shl 9 or SKY
    const val SKY_BLOCK_SOUTH_WEST = SOUTH_AND_WEST shl 9 or SKY
    const val SKY_BLOCK_WEST = WEST shl 9 or SKY

    const val SKY_WALL_NORTH_WEST = NORTH_AND_WEST or WALL shl 9 or SKY
    const val SKY_WALL_NORTH = NORTH or WALL shl 9 or SKY
    const val SKY_WALL_NORTH_EAST = NORTH_AND_EAST or WALL shl 9 or SKY
    const val SKY_WALL_EAST = EAST or WALL shl 9 or SKY
    const val SKY_WALL_SOUTH_EAST = SOUTH_AND_EAST or WALL shl 9 or SKY
    const val SKY_WALL_SOUTH = SOUTH or WALL shl 9 or SKY
    const val SKY_WALL_SOUTH_WEST = SOUTH_AND_WEST or WALL shl 9 or SKY
    const val SKY_WALL_WEST = WEST or WALL shl 9 or SKY

    const val SKY_CLEAR_NORTH_WEST = SOUTH_AND_EAST shl 9 or SKY
    const val SKY_CLEAR_NORTH = NOT_NORTH shl 9 or SKY
    const val SKY_CLEAR_NORTH_EAST = SOUTH_AND_WEST shl 9 or SKY
    const val SKY_CLEAR_EAST = NOT_EAST shl 9 or SKY
    const val SKY_CLEAR_SOUTH_EAST = NORTH_AND_WEST shl 9 or SKY
    const val SKY_CLEAR_SOUTH = NOT_SOUTH shl 9 or SKY
    const val SKY_CLEAR_SOUTH_WEST = NORTH_AND_EAST shl 9 or SKY
    const val SKY_CLEAR_WEST = NOT_WEST shl 9 or SKY

    const val IGNORED_BLOCK_NORTH_WEST = NORTH_AND_WEST shl 22 or IGNORED
    const val IGNORED_BLOCK_NORTH = NORTH shl 22 or IGNORED
    const val IGNORED_BLOCK_NORTH_EAST = NORTH_AND_EAST shl 22 or IGNORED
    const val IGNORED_BLOCK_EAST = EAST shl 22 or IGNORED
    const val IGNORED_BLOCK_SOUTH_EAST = SOUTH_AND_EAST shl 22 or IGNORED
    const val IGNORED_BLOCK_SOUTH = SOUTH shl 22 or IGNORED
    const val IGNORED_BLOCK_SOUTH_WEST = SOUTH_AND_WEST shl 22 or IGNORED
    const val IGNORED_BLOCK_WEST = WEST shl 22 or IGNORED

    const val IGNORED_WALL_NORTH_WEST = NORTH_AND_WEST or WALL shl 22 or IGNORED
    const val IGNORED_WALL_NORTH = NORTH or WALL shl 22 or IGNORED
    const val IGNORED_WALL_NORTH_EAST = NORTH_AND_EAST or WALL shl 22 or IGNORED
    const val IGNORED_WALL_EAST = EAST or WALL shl 22 or IGNORED
    const val IGNORED_WALL_SOUTH_EAST = SOUTH_AND_EAST or WALL shl 22 or IGNORED
    const val IGNORED_WALL_SOUTH = SOUTH or WALL shl 22 or IGNORED
    const val IGNORED_WALL_SOUTH_WEST = SOUTH_AND_WEST or WALL shl 22 or IGNORED
    const val IGNORED_WALL_WEST = WEST or WALL shl 22 or IGNORED

    const val IGNORED_CLEAR_NORTH_WEST = SOUTH_AND_EAST shl 22 or IGNORED
    const val IGNORED_CLEAR_NORTH = NOT_NORTH shl 22 or IGNORED
    const val IGNORED_CLEAR_NORTH_EAST = SOUTH_AND_WEST shl 22 or IGNORED
    const val IGNORED_CLEAR_EAST = NOT_EAST shl 22 or IGNORED
    const val IGNORED_CLEAR_SOUTH_EAST = NORTH_AND_WEST shl 22 or IGNORED
    const val IGNORED_CLEAR_SOUTH = NOT_SOUTH shl 22 or IGNORED
    const val IGNORED_CLEAR_SOUTH_WEST = NORTH_AND_EAST shl 22 or IGNORED
    const val IGNORED_CLEAR_WEST = NOT_WEST shl 22 or IGNORED
}