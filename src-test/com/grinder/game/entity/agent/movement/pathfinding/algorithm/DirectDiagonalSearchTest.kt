package com.grinder.game.entity.agent.movement.pathfinding.algorithm

import com.grinder.game.entity.agent.movement.StepQueue
import com.grinder.game.entity.agent.movement.pathfinding.target.TargetStrategy
import com.grinder.game.entity.agent.movement.pathfinding.traverse.TraversalStrategy
import com.grinder.game.model.Direction
import com.grinder.game.model.Position
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

internal class DirectDiagonalSearchTest {

    lateinit var dd: DirectDiagonalSearch

    @BeforeEach
    fun setup() {
        dd = spyk(DirectDiagonalSearch())
    }

    val width = 1
    val height = 1

    @TestFactory
    fun `Move towards target`() = arrayOf(
        Position(11, 9) to Direction.NORTH_WEST,
        Position(10, 9) to Direction.NORTH,
        Position(9, 9) to Direction.NORTH_EAST,
        Position(9, 10) to Direction.EAST,
        Position(9, 11) to Direction.SOUTH_EAST,
        Position(10, 11) to Direction.SOUTH,
        Position(11, 11) to Direction.SOUTH_WEST,
        Position(11, 10) to Direction.WEST
    ).map { (tile, dir) ->
        DynamicTest.dynamicTest("Move $dir to $tile") {
            // Given
            val steps: StepQueue = mockk(relaxed = true)
            val target = Position(10, 10)
            val strategy: TargetStrategy = mockk(relaxed = true)
            val traversal: TraversalStrategy = mockk(relaxed = true)
            every { strategy.tile } returns target
            // When
            val result = dd.find(tile, width, height, steps, strategy, traversal, false)
            // Then
            assertNotNull(result)
            verify {
                steps.queue(dir, false)
            }
        }
    }

    @TestFactory
    fun `Blocked diagonal moves horizontal`() = arrayOf(
        Position(11, 9) to Direction.WEST,
        Position(9, 9) to Direction.EAST,
        Position(9, 11) to Direction.EAST,
        Position(11, 11) to Direction.WEST
    ).map { (tile, dir) ->
        DynamicTest.dynamicTest("Move $dir to $tile") {
            // Given
            val steps: StepQueue = mockk(relaxed = true)
            val target = Position(10, 10)
            val strategy: TargetStrategy = mockk(relaxed = true)
            val traversal: TraversalStrategy = mockk(relaxed = true)
            every { strategy.tile } returns target
            every { traversal.blocked(11, 11, tile.z, Direction.SOUTH_WEST) } returns true
            every { traversal.blocked(9, 9, tile.z, Direction.NORTH_EAST) } returns true
            every { traversal.blocked(9, 11, tile.z, Direction.SOUTH_EAST) } returns true
            every { traversal.blocked(11, 9, tile.z, Direction.NORTH_WEST) } returns true
            // When
            val result = dd.find(tile, width, height, steps, strategy, traversal, false)
            // Then
            assertNotNull(result)
            verify {
                steps.queue(dir, false)
            }
        }
    }

    @TestFactory
    fun `Blocked diagonal and horizontal moves vertical`() = arrayOf(
        Position(11, 9) to Direction.NORTH,
        Position(9, 9) to Direction.NORTH,
        Position(9, 11) to Direction.SOUTH,
        Position(11, 11) to Direction.SOUTH
    ).map { (tile, dir) ->
        DynamicTest.dynamicTest("Move $dir to $tile") {
            // Given
            val steps: StepQueue = mockk(relaxed = true)
            val target = Position(10, 10)
            val strategy: TargetStrategy = mockk(relaxed = true)
            val traversal: TraversalStrategy = mockk(relaxed = true)
            every { strategy.tile } returns target
            every { traversal.blocked(11, 11, tile.z, Direction.SOUTH_WEST) } returns true
            every { traversal.blocked(11, 11, tile.z, Direction.WEST) } returns true
            every { traversal.blocked(9, 9, tile.z, Direction.NORTH_EAST) } returns true
            every { traversal.blocked(9, 9, tile.z, Direction.EAST) } returns true
            every { traversal.blocked(9, 11, tile.z, Direction.SOUTH_EAST) } returns true
            every { traversal.blocked(9, 11, tile.z, Direction.EAST) } returns true
            every { traversal.blocked(11, 9, tile.z, Direction.NORTH_WEST) } returns true
            every { traversal.blocked(11, 9, tile.z, Direction.WEST) } returns true
            // When
            val result = dd.find(tile, width, height, steps, strategy, traversal, false)
            // Then
            assertNotNull(result)
            verify {
                steps.queue(dir, false)
            }
        }
    }
}