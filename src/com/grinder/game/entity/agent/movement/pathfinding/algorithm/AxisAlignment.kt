package com.grinder.game.entity.agent.movement.pathfinding.algorithm

import com.grinder.game.entity.agent.movement.StepQueue
import com.grinder.game.entity.agent.movement.pathfinding.target.TargetStrategy
import com.grinder.game.entity.agent.movement.pathfinding.traverse.TraversalStrategy
import com.grinder.game.model.Direction
import com.grinder.game.model.Position

/**
 * Moves diagonally until aligned with target or blocked by obstacle then moves cardinally
 * Used by NPCs
 * @author Greg Hibberd <greg@greghibberd.com>
 * @since May 31, 2020
 */
class AxisAlignment : PathAlgorithm {
    override fun find(position: Position, width: Int, height: Int, movement: StepQueue, strategy: TargetStrategy, traversal: TraversalStrategy, shouldNoClip: Boolean): Position? {
        val delta = strategy.tile.clone().add(-position.x, -position.y, position.z)
        val current = position.clone()

        var reached = strategy.reached(current, width, height)
        while (!reached) {
            var direction = toDirection(delta)
            if (traversal.blocked(current, direction)) {
                direction = if (direction.isDiagonal) {
                    if (!traversal.blocked(current, direction.horizontal())) {
                        direction.horizontal()
                    } else if (!traversal.blocked(current, direction.vertical())) {
                        direction.vertical()
                    } else {
                        break
                    }
                } else {
                    break
                }
            }
            if (direction == Direction.NONE) {
                break
            }
            delta.add(-direction.x, -direction.y)
            current.add(direction.x, direction.y)
            movement.queue(direction, shouldNoClip)
            reached = strategy.reached(current, width, height)
        }

        if (!reached && delta.x in 0 until width && delta.y in 0 until height) {
            reached = stepOut(traversal, current, delta, movement, strategy, width, height)
        }

        return when {
            reached -> current// Complete
            current != position -> current// Partial
            else -> null// Failure
        }
    }

    private fun stepOut(traversal: TraversalStrategy, current: Position, delta: Position, movement: StepQueue, strategy: TargetStrategy, width: Int, height: Int): Boolean {
        val direction = Direction.VALUES.filterNot { it == Direction.NONE }.random()
        while (!traversal.blocked(current, direction)) {
            delta.add(-direction.x, -direction.y)
            current.add(direction.x, direction.y)
            movement.queue(direction, false)
            if (strategy.reached(current, width, height)) {
                return true
            }
        }
        return false
    }

    fun toDirection(delta: Position) = when {
        delta.x > 0 -> when {
            delta.y > 0 -> Direction.NORTH_EAST
            delta.y < 0 -> Direction.SOUTH_EAST
            else -> Direction.EAST
        }
        delta.x < 0 -> when {
            delta.y > 0 -> Direction.NORTH_WEST
            delta.y < 0 -> Direction.SOUTH_WEST
            else -> Direction.WEST
        }
        else -> when {
            delta.y > 0 -> Direction.NORTH
            delta.y < 0 -> Direction.SOUTH
            else -> Direction.NONE
        }
    }

}