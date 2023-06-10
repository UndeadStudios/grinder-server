package com.grinder.game.entity.agent.movement.pathfinding.target

import com.grinder.game.entity.agent.combat.LineOfSight
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.model.Position

/**
 * Bankers are weird
 * @author Greg Hibberd <greg@greghibberd.com>
 * @since Oct 31, 2020
 */
data class BankerTargetStrategy(
    val entity: NPC,
    val distance: Int
) : TargetStrategy {

    override val tile: Position
        get() = entity.position

    override val width: Int
        get() = entity.width

    override val height: Int
        get() = entity.height

    override fun reached(x: Int, y: Int, z: Int, width: Int, height: Int): Boolean {
        val direction = entity.spawnFace.direction
        val tile = tile.copy().add(direction.x, direction.y)
        return tile.isWithinDistance(x, y, z, distance) && LineOfSight.withinSight(tile, Position(x, y, z))
    }
}