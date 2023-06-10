package com.grinder.game.entity.agent.movement.pathfinding.traverse

import com.grinder.game.collision.TileFlags

enum class TraversalType(val flag: Int, val shift: Int) {
    Land(TileFlags.LAND or TileFlags.FLOOR or TileFlags.FLOOR_DECO, 0),
    Water(TileFlags.FLOOR.inv(), 0),
    Sky(TileFlags.SKY/* or TileFlags.FLOOR*/, 9),
    Ignored(TileFlags.IGNORED, 22);
}