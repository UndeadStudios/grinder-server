package com.grinder.game.entity.agent.movement.pathfinding.algorithm

import com.grinder.game.entity.agent.movement.StepQueue
import com.grinder.game.entity.agent.movement.pathfinding.target.TargetStrategy
import com.grinder.game.entity.agent.movement.pathfinding.traverse.TraversalStrategy
import com.grinder.game.model.Direction.*
import com.grinder.game.model.Position
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

internal class AxisAlignmentIntegrationTest {

    lateinit var aa: AxisAlignment
    val width = 1
    val height = 1

    @BeforeEach
    fun setup() {
        aa = spyk(AxisAlignment())
    }

    @TestFactory
    fun `Move diagonal towards target`() =
        arrayOf(
            Position(-4, 0) to arrayOf(EAST, EAST, EAST, EAST),
            Position(-3, 0) to arrayOf(EAST, EAST, EAST),
            Position(-2, 0) to arrayOf(EAST, EAST),
            Position(-1, 0) to arrayOf(EAST),
            Position(-4, 1) to arrayOf(SOUTH_EAST, EAST, EAST, EAST),
            Position(-3, 1) to arrayOf(SOUTH_EAST, EAST, EAST),
            Position(-2, 1) to arrayOf(SOUTH_EAST, EAST),
            Position(-1, 1) to arrayOf(SOUTH_EAST),
            Position(-4, 2) to arrayOf(SOUTH_EAST, SOUTH_EAST, EAST, EAST),
            Position(-3, 2) to arrayOf(SOUTH_EAST, SOUTH_EAST, EAST),
            Position(-2, 2) to arrayOf(SOUTH_EAST, SOUTH_EAST),
            Position(-1, 2) to arrayOf(SOUTH_EAST, SOUTH),
            Position(-4, 3) to arrayOf(SOUTH_EAST, SOUTH_EAST, SOUTH_EAST, EAST),
            Position(-3, 3) to arrayOf(SOUTH_EAST, SOUTH_EAST, SOUTH_EAST),
            Position(-2, 3) to arrayOf(SOUTH_EAST, SOUTH_EAST, SOUTH),
            Position(-1, 3) to arrayOf(SOUTH_EAST, SOUTH, SOUTH),
            Position(-4, 4) to arrayOf(SOUTH_EAST, SOUTH_EAST, SOUTH_EAST, SOUTH_EAST),
            Position(-3, 4) to arrayOf(SOUTH_EAST, SOUTH_EAST, SOUTH_EAST, SOUTH),
            Position(-2, 4) to arrayOf(SOUTH_EAST, SOUTH_EAST, SOUTH, SOUTH),
            Position(-1, 4) to arrayOf(SOUTH_EAST, SOUTH, SOUTH, SOUTH)
        ).map { (offset, expected) ->
            DynamicTest.dynamicTest("Move $offset") {
                // Given
                val steps: StepQueue = mockk(relaxed = true)
                val target = Position(10, 10)
                val strategy: TargetStrategy = mockk(relaxed = true)
                val traversal: TraversalStrategy = mockk(relaxed = true)
                every { strategy.tile } returns target
                every { strategy.reached(target, width, height) } returns true
                val tile = target.clone().add(offset.x, offset.y)
                // When
                aa.find(tile, width, height, steps, strategy, traversal, false)
                // Then
                verify {
                    expected.forEach {
                        steps.queue(it, false)
                    }
                }
            }
        }

    @TestFactory
    fun `Move diagonal around block target`() =
        arrayOf(
            Triple(Position(-1, 2), Position(-1, 1), arrayOf(EAST, SOUTH, SOUTH)),
            Triple(Position(-2, 1), Position(-1, 1), arrayOf(SOUTH, EAST, EAST)),
            Triple(Position(-2, 3), Position(-2, 2), arrayOf(EAST, SOUTH_EAST, SOUTH, SOUTH)),
            Triple(Position(-3, 2), Position(-2, 2), arrayOf(SOUTH, SOUTH_EAST, EAST, EAST))
        ).map { (offset, block, expected) ->
            DynamicTest.dynamicTest("Move $offset") {
                // Given
                val steps: StepQueue = mockk(relaxed = true)
                val target = Position(10, 10)
                val strategy: TargetStrategy = mockk(relaxed = true)
                val traversal: TraversalStrategy = mockk(relaxed = true)
                every { strategy.tile } returns target
                val block = target.clone().add(block.x, block.y)
                every { traversal.blocked(block.clone().add(-1, 0), SOUTH_EAST) } returns true
                every { traversal.blocked(block.clone().add(-1, 0), EAST) } returns true
                every { traversal.blocked(block.clone().add(0, 1), SOUTH_EAST) } returns true
                every { traversal.blocked(block.clone().add(0, 1), SOUTH) } returns true
                every { strategy.reached(target, width, height) } returns true
                val tile = target.clone().add(offset.x, offset.y)
                // When
                aa.find(tile, width, height, steps, strategy, traversal, false)
                // Then
                verify {
                    expected.forEach {
                        steps.queue(it, false)
                    }
                }
            }
        }
}