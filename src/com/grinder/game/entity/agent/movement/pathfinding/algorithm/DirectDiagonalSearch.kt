package com.grinder.game.entity.agent.movement.pathfinding.algorithm

import com.grinder.game.entity.agent.movement.StepQueue
import com.grinder.game.entity.agent.movement.pathfinding.target.TargetStrategy
import com.grinder.game.entity.agent.movement.pathfinding.traverse.TraversalStrategy
import com.grinder.game.model.Direction
import com.grinder.game.model.Position

/**
 * Moves in any direction towards the target until blocked by obstacle or reaches
 * Used for following and combat.
 * @author Greg Hibberd <greg@greghibberd.com>
 * @since May 20, 2020
 */
class DirectDiagonalSearch : PathAlgorithm {
    override fun find(position: Position, width: Int, height: Int, movement: StepQueue, strategy: TargetStrategy, traversal: TraversalStrategy, shouldNoClip: Boolean): Position? {
        val delta = position.clone().add(-strategy.tile.x, -strategy.tile.y, strategy.tile.z)
        var dx = delta.x
        var dy = delta.y
        var x = position.x
        var y = position.y

        while (dx != 0 || dy != 0) {
            val deltaX = -dx.coerceIn(-1, 1)
            val deltaY = -dy.coerceIn(-1, 1)
            val direction = Direction.getDirection(deltaX, deltaY)

            if (!shouldNoClip) {
                if (direction.isDiagonal && !traversal.blocked(x, y, position.z, direction)) {
                    movement.queue(direction, shouldNoClip)
                    dx += deltaX
                    dy += deltaY
                    x += deltaX
                    y += deltaY
                } else if (deltaX != 0 && !traversal.blocked(x, y, position.z, direction.horizontal())) {
                    movement.queue(direction.horizontal(), shouldNoClip)
                    dx += deltaX
                    x += deltaX
                } else if (deltaY != 0 && !traversal.blocked(x, y, position.z, direction.vertical())) {
                    movement.queue(direction.vertical(), shouldNoClip)
                    dy += deltaY
                    y += deltaY
                } else {
                    break
                }
            } else {
                if (direction.isDiagonal) {
                    movement.queue(direction, shouldNoClip)
                    dx += deltaX
                    dy += deltaY
                    x += deltaX
                    y += deltaY
                } else if (deltaX != 0) {
                    movement.queue(direction.horizontal(), shouldNoClip)
                    dx += deltaX
                    x += deltaX
                } else if (deltaY != 0) {
                    movement.queue(direction.vertical(), shouldNoClip)
                    dy += deltaY
                    y += deltaY
                } else {
                    break
                }
            }
        }
        return Position(x, y, position.z)// Complete or partial
    }
}