package com.grinder.game.entity.agent.movement.pathfinding.target

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Position

/**
 * Checks if on the tile behind a player
 * @author Greg Hibberd <greg@greghibberd.com>
 * @since August 22, 2020
 */
data class FollowTargetStrategy(
    private val player: Player
) : TargetStrategy {

    override val tile: Position
        get() = player.actualLastPosition

    override val width: Int
        get() = player.width

    override val height: Int
        get() = player.height

    override fun reached(x: Int, y: Int, z: Int, width: Int, height: Int): Boolean {
        return tile.sameAs(x, y, z)
    }
}