package com.grinder.game.entity.agent.movement.pathfinding.algorithm

import com.grinder.game.entity.agent.movement.StepQueue
import com.grinder.game.entity.agent.movement.pathfinding.algorithm.BreadthFirstSearch.Companion.GRAPH_SIZE
import com.grinder.game.entity.agent.movement.pathfinding.target.TargetStrategy
import com.grinder.game.entity.agent.movement.pathfinding.traverse.TraversalStrategy
import com.grinder.game.model.Direction
import com.grinder.game.model.Position
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class BreadthFirstSearchTest {

    lateinit var calc: BreadthFirstSearch.Calculation
    lateinit var bfs: BreadthFirstSearch
    val width = 1
    val height = 1

    @BeforeEach
    fun setup() {
        mockkStatic(BreadthFirstSearch::class)
        mockkObject(BreadthFirstSearch)
        calc = mockk(relaxed = true)
        bfs = spyk(BreadthFirstSearch(calc))
    }

    @Test
    fun `Movement reset`() {
        // Given
        val tile = Position(0, 0)
        val steps: StepQueue = mockk(relaxed = true)
        val strategy: TargetStrategy = mockk(relaxed = true)
        val traversal: TraversalStrategy = mockk(relaxed = true)
        val directions = Array(GRAPH_SIZE) { Array<Direction?>(GRAPH_SIZE) { Direction.NONE } }
        val distances = Array(GRAPH_SIZE) { IntArray(GRAPH_SIZE) }
        every { calc.directions } returns directions
        every { calc.distances } returns distances
        every { bfs.calculate(any(), any(), any(), any(), any(), any(), any()) } returns tile
        every { bfs.backtrace(any(), any(), 0, 0) } returns tile
        // When
        bfs.find(tile, width, height, steps, strategy, traversal, false)
        // Then
        assertEquals(null, directions[1][2])
        assertEquals(99999999, distances[3][4])
    }

    @Test
    fun `Partial path calculated if no complete path found`() {
        // Given
        val tile = Position(0, 0)
        val steps: StepQueue = mockk(relaxed = true)
        val strategy: TargetStrategy = mockk(relaxed = true)
        val traversal: TraversalStrategy = mockk(relaxed = true)
        val response = Position(0, 0)
        every { calc.directions } returns Array(GRAPH_SIZE) { arrayOfNulls<Direction?>(GRAPH_SIZE) }
        every { calc.distances } returns Array(GRAPH_SIZE) { IntArray(GRAPH_SIZE) }
        every {
            bfs.calculate(any(), any(), tile.z, width, height, strategy, traversal)
        } returns null
        every { bfs.calculatePartialPath(strategy, any(), any(), any()) } returns response
        // When
        bfs.find(tile, width, height, steps, strategy, traversal, false)
        // Then
        verify { bfs.calculatePartialPath(strategy, any(), any(), any()) }
    }

    @Test
    fun `Finds route to target`() {
        // Given
        val strategy: TargetStrategy = mockk(relaxed = true)
        val traversal: TraversalStrategy = mockk(relaxed = true)
        val distances = Array(GRAPH_SIZE) { IntArray(GRAPH_SIZE) }
        val directions = Array(GRAPH_SIZE) { arrayOfNulls<Direction?>(GRAPH_SIZE) }
        val queue = spyk(LinkedList<Position>())
        every { calc.queue } returns queue
        every { calc.directions } returns directions
        every { calc.distances } returns distances
        every { strategy.reached(72, 74, 1, width, height) } returns true
        // When
        val result = bfs.calculate(10, 10, 1, width, height, strategy, traversal)
        // Then
        verifyOrder {
            queue.add(Position(64, 64))
            strategy.reached(74, 74, 1, width, height)
            strategy.reached(73, 74, 1, width, height)// West
            strategy.reached(75, 74, 1, width, height)// East
            strategy.reached(74, 73, 1, width, height)// South
            strategy.reached(74, 75, 1, width, height)// North
            strategy.reached(73, 73, 1, width, height)// South west
            strategy.reached(75, 73, 1, width, height)// South east
            strategy.reached(73, 75, 1, width, height)// North west
            strategy.reached(75, 75, 1, width, height)// North east
        }
        assertEquals(0, distances[64][64])
        assertEquals(Direction.NONE, directions[64][64])
    }

    @Test
    fun `Obstructions are ignored`() {
        // Given
        val strategy: TargetStrategy = mockk(relaxed = true)
        val traversal: TraversalStrategy = mockk(relaxed = true)
        val distances = Array(GRAPH_SIZE) { IntArray(GRAPH_SIZE) }
        val directions = Array(GRAPH_SIZE) { arrayOfNulls<Direction?>(GRAPH_SIZE) }
        val queue = spyk(LinkedList<Position>())
        every { calc.queue } returns queue
        every { calc.directions } returns directions
        every { calc.distances } returns distances
        every { strategy.reached(73, 74, 1, width, height) } returns true
        every { traversal.blocked(73, 74, 1, Direction.WEST) } returns true
        // When
        val result = bfs.calculate(10, 10, 1, width, height, strategy, traversal)
        // Then
        verify(exactly = 0) {
            queue.add(Position(73, 74, 1))
        }
        verifyOrder {
            queue.add(Position(64, 64))
            queue.add(Position(65, 64, 0))
        }
    }

    @Test
    fun `Partial calculation takes lowest cost`() {
        // Given
        val strategy: TargetStrategy = mockk(relaxed = true)
        val distances = Array(GRAPH_SIZE) { IntArray(GRAPH_SIZE) { 99999999 } }
        distances[5][5] = 2
        distances[4][5] = 3
        distances[3][5] = 4
        every { calc.distances } returns distances
        every { strategy.tile } returns Position(10, 10)
        // When
        val result = bfs.calculatePartialPath(strategy, 10, 10, 0)
        // Then
        assertEquals(Position(3, 5), result)
    }

    @Test
    fun `Partial calculation takes lowest distance`() {
        // Given
        val strategy: TargetStrategy = mockk(relaxed = true)
        val distances = Array(GRAPH_SIZE) { IntArray(GRAPH_SIZE) { 99999999 } }
        distances[5][5] = 2
        distances[6][6] = 2
        distances[7][7] = 2
        every { calc.distances } returns distances
        every { strategy.tile } returns Position(10, 10)
        // When
        val result = bfs.calculatePartialPath(strategy, 10, 10, 0)
        // Then
        assertEquals(Position(5, 5), result)
    }

    @Test
    fun `Partial calculation returns failure if no values`() {
        // Given
        val strategy: TargetStrategy = mockk(relaxed = true)
        val distances = Array(GRAPH_SIZE) { IntArray(GRAPH_SIZE) { 99999999 } }
        every { calc.distances } returns distances
        every { strategy.tile } returns Position(10, 10)
        // When
        val result = bfs.calculatePartialPath(strategy, 10, 10, 0)
        // Then
        assertNull(result)
    }

    @Test
    fun `Backtrace steps`() {
        // Given
        val tile = Position(10, 10)
        val directions = Array(GRAPH_SIZE) { arrayOfNulls<Direction?>(GRAPH_SIZE) }
        directions[10][10] = Direction.NORTH
        directions[10][9] = Direction.EAST
        directions[9][9] = Direction.SOUTH
        directions[9][10] = Direction.SOUTH
        directions[9][11] = Direction.WEST
        directions[10][11] = Direction.NONE
        val steps: StepQueue = mockk(relaxed = true)
        every { steps.stepCount() } returns 1
        every { calc.directions } returns directions
        // When
        bfs.backtrace(steps, tile, 0, 0)
        // Then
        verifyOrder {
            steps.queueHead(Direction.NORTH)
            steps.queueHead(Direction.EAST)
            steps.queueHead(Direction.SOUTH)
            steps.queueHead(Direction.SOUTH)
            steps.queueHead(Direction.WEST)
        }
    }

    @Test
    fun `Backtrace returns target if no movement`() {
        // Given
        val tile = Position(10, 10)
        val directions = Array(GRAPH_SIZE) { arrayOfNulls<Direction?>(GRAPH_SIZE) }
        val steps: StepQueue = mockk(relaxed = true)
        every { steps.stepCount() } returns 1
        every { calc.directions } returns directions
        // When
        val result = bfs.backtrace(steps, tile, 0, 0)
        // Then
        assertEquals(tile, result)
    }
}