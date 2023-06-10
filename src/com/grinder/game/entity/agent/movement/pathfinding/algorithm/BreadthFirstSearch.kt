package com.grinder.game.entity.agent.movement.pathfinding.algorithm

import com.grinder.game.entity.agent.movement.StepQueue
import com.grinder.game.entity.agent.movement.pathfinding.target.TargetStrategy
import com.grinder.game.entity.agent.movement.pathfinding.traverse.TraversalStrategy
import com.grinder.game.model.Direction
import com.grinder.game.model.Position
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * Searches every tile breadth-first to find the target
 * Closest reachable tile to target is returned if target is unreachable
 * Used by players
 * @author Greg Hibberd <greg@greghibberd.com>
 * @since May 20, 2020
 */
class BreadthFirstSearch(private val calc: Calculation = Calculation()) : PathAlgorithm {

    /**
     * To simplify mocking due to mockk#481
     */
    class Calculation {
        val queue: Queue<Position> = LinkedList()
        val directions: Array<Array<Direction?>> = Array(GRAPH_SIZE) { Array(GRAPH_SIZE) { null } }
        val distances: Array<IntArray> = Array(GRAPH_SIZE) { IntArray(GRAPH_SIZE) }
    }

    override fun find(position: Position, width: Int, height: Int, movement: StepQueue, strategy: TargetStrategy, traversal: TraversalStrategy, shouldNoClip: Boolean): Position? {
        for (x in 0 until GRAPH_SIZE) {
            for (y in 0 until GRAPH_SIZE) {
                calc.directions[x][y] = null
                calc.distances[x][y] = 99999999
            }
        }

        val graph = GRAPH_SIZE / 2
        val graphBaseX = position.x - graph
        val graphBaseY = position.y - graph

        var result = calculate(graphBaseX, graphBaseY, position.z, width, height, strategy, traversal)

        if (debugRadius > 0) {
            for (y in graphBaseY + debugRadius downTo graphBaseY - debugRadius) {
                for (x in graphBaseX + debugRadius until graphBaseX - debugRadius) {
                    val distance = when (calc.directions[x][y]) {
                        Direction.NORTH -> "N  "
                        Direction.EAST -> "E  "
                        Direction.SOUTH -> "S  "
                        Direction.WEST -> "W  "
                        Direction.NORTH_EAST -> "NE "
                        Direction.NORTH_WEST -> "NW "
                        Direction.SOUTH_EAST -> "SE "
                        Direction.SOUTH_WEST -> "SW "
                        else -> "-  "
                    }
                    print(distance)
                }
                println()
            }
            println()
        }

        if (result == null) {
            result = calculatePartialPath(strategy, graphBaseX, graphBaseY, position.z)
        }

        return when (result) {
            null -> result
            else -> backtrace(movement, result, graphBaseX, graphBaseY)
        }
    }

    fun calculate(
        graphBaseX: Int,
        graphBaseY: Int,
        plane: Int,
        width: Int,
        height: Int,
        target: TargetStrategy,
        traversal: TraversalStrategy
    ): Position? {
        // Cache fields for jit compiler performance boost
        val directions = calc.directions
        val distances = calc.distances
        val all = all

        val queue = calc.queue
        queue.clear()

        // Set starting tile as visited
        queue.add(start.clone())
        distances[start.x][start.y] = 0
        directions[start.x][start.y] = Direction.NONE

        var parent: Position
        while (queue.isNotEmpty()) {
            parent = queue.poll()

            if (target.reached(parent.x + graphBaseX, parent.y + graphBaseY, plane, width, height)) {
                return parent.setZ(plane)// Success
            }

            for (dir in all) {
                val moved = parent.clone().add(dir.x, dir.y)

                if (moved.x !in 0 until GRAPH_SIZE) {
                    continue
                }

                if (moved.y !in 0 until GRAPH_SIZE) {
                    continue
                }

                // Skip already calculated steps
                if (directions[moved.x][moved.y] != null) {
                    continue
                }

                // Skip blocked tiles
                if (traversal.blocked(parent.x + graphBaseX, parent.y + graphBaseY, plane, dir)) {
                    continue
                }

                queue.add(moved)
                directions[moved.x][moved.y] = dir
                distances[moved.x][moved.y] = distances[parent.x][parent.y] + 1
            }
        }
        return null// Failure
    }

    /**
     *  Checks for a tile closest to the target which is reachable
     */
    fun calculatePartialPath(target: TargetStrategy, graphBaseX: Int, graphBaseY: Int, plane: Int): Position? {
        var lowestCost = Integer.MAX_VALUE
        var lowestDistance = Integer.MAX_VALUE
        val distances = calc.distances

        val destX = target.tile.x - graphBaseX
        val destY = target.tile.y - graphBaseY
        var endX = 0
        var endY = 0
        val width = target.width
        val height = target.height

        val minX = max(0, destX - PARTIAL_PATH_RANGE)
        val maxX = min(GRAPH_SIZE, destX + PARTIAL_PATH_RANGE)
        val minY = max(0, destY - PARTIAL_PATH_RANGE)
        val maxY = min(GRAPH_SIZE, destY + PARTIAL_PATH_RANGE)
        for (graphX in minX until maxX) {
            for (graphY in minY until maxY) {
                if (distances[graphX][graphY] >= PARTIAL_MAX_DISTANCE) {
                    continue
                }

                val deltaX = when {
                    destX > graphX -> destX - graphX// West
                    destX + width <= graphX -> -(destX + width) + graphX + 1// East
                    else -> 0
                }
                val deltaY = when {
                    destY > graphY -> destY - graphY// North
                    destY + height <= graphY -> -(destY + height) + graphY + 1// South
                    else -> 0
                }
                val cost = deltaX * deltaX + deltaY * deltaY
                // Accept lower costs or shorter paths
                if (cost < lowestCost || cost == lowestCost && distances[graphX][graphY] < lowestDistance && calc.directions[graphX][graphY] != Direction.NONE) {
                    lowestCost = cost
                    lowestDistance = distances[graphX][graphY]
                    endX = graphX
                    endY = graphY
                }
            }
        }

        if (lowestCost == Integer.MAX_VALUE || lowestDistance == Integer.MAX_VALUE) {
            return null// No partial path found
        }

        return Position(endX, endY, plane)// Partial
    }

    /**
     *  Traces the path back to find individual steps taken to reach the target
     */
    fun backtrace(movement: StepQueue, result: Position, graphBaseX: Int, graphBaseY: Int): Position? {
        val trace = result.clone()
        var direction = calc.directions[trace.x][trace.y]
        while (direction != null && direction != Direction.NONE) {
            movement.queueHead(direction)
            trace.add(-direction.x, -direction.y)
            direction = calc.directions[trace.x][trace.y]
        }
        movement.setLastDirection(direction)
        return result.add(graphBaseX, graphBaseY)
    }

    companion object {
        const val debugRadius = -1
        const val GRAPH_SIZE = 128
        private const val QUEUE_SIZE = 0xfff
        private const val PARTIAL_MAX_DISTANCE = QUEUE_SIZE
        private const val PARTIAL_PATH_RANGE = 10
        private val start = Position(GRAPH_SIZE / 2, GRAPH_SIZE / 2)

        private val all = arrayOf(
            Direction.WEST,
            Direction.EAST,
            Direction.SOUTH,
            Direction.NORTH,
            Direction.SOUTH_WEST,
            Direction.SOUTH_EAST,
            Direction.NORTH_WEST,
            Direction.NORTH_EAST
        )
    }
}