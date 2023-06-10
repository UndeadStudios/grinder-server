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
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class AxisAlignmentTest {

    lateinit var aa: AxisAlignment
    val width = 1
    val height = 1

    @BeforeEach
    fun setup() {
        aa = spyk(AxisAlignment())
    }

    @Test
    fun `Already reached target is complete`() {
        // Given
        val steps: StepQueue = mockk(relaxed = true)
        val tile = Position(0, 0)
        val target = Position(0, 0)
        val strategy: TargetStrategy = mockk(relaxed = true)
        val traversal: TraversalStrategy = mockk(relaxed = true)
        every { strategy.tile } returns target
        every { strategy.reached(target, width, height) } returns true
        // When
        val result = aa.find(tile, width, height, steps, strategy, traversal, false)
        // Then
        assertEquals(target, result)
    }

    @Test
    fun `Unreached no steps towards target is failure`() {
        // Given
        val steps: StepQueue = mockk(relaxed = true)
        val tile = Position(10, 10)
        val target = Position(10, 10)
        val strategy: TargetStrategy = mockk(relaxed = true)
        val traversal: TraversalStrategy = mockk(relaxed = true)
        every { strategy.tile } returns target
        every { strategy.reached(any(), width, height) } returns true
        every { strategy.reached(target, width, height) } returns false
        every { aa.toDirection(any()) } returns Direction.NONE
        // When
        val result = aa.find(tile, width, height, steps, strategy, traversal, false)
        // Then
        assertNotNull(result)
    }

    @Test
    fun `Diagonal blocked tries horizontal`() {
        // Given
        val steps: StepQueue = mockk(relaxed = true)
        val tile = Position(10, 10)
        val target = Position(11, 10)
        val strategy: TargetStrategy = mockk(relaxed = true)
        val traversal: TraversalStrategy = mockk(relaxed = true)
        every { strategy.tile } returns target
        every { strategy.reached(target, width, height) } returns true
        every { aa.toDirection(any()) } returns Direction.SOUTH_EAST
        every { traversal.blocked(tile, Direction.SOUTH_EAST) } returns true
        // When
        val result = aa.find(tile, width, height, steps, strategy, traversal, false)
        // Then
        assertEquals(target, result)
        verify {
            steps.queue(Direction.EAST, false)
        }
    }

    @Test
    fun `Diagonal and horizontal blocked tries vertical`() {
        // Given
        val steps: StepQueue = mockk(relaxed = true)
        val tile = Position(10, 10)
        val target = Position(10, 9)
        val strategy: TargetStrategy = mockk(relaxed = true)
        val traversal: TraversalStrategy = mockk(relaxed = true)
        every { strategy.tile } returns target
        every { strategy.reached(target, width, height) } returns true
        every { aa.toDirection(any()) } returns Direction.SOUTH_EAST
        every { traversal.blocked(tile, Direction.SOUTH_EAST) } returns true
        every { traversal.blocked(tile, Direction.EAST) } returns true
        // When
        val result = aa.find(tile, width, height, steps, strategy, traversal, false)
        // Then
        assertEquals(target, result)
        verify {
            steps.queue(Direction.SOUTH, false)
        }
    }

    @Test
    fun `Blocked route is failure`() {
        // Given
        val steps: StepQueue = mockk(relaxed = true)
        val tile = Position(10, 10)
        val target = Position(11, 9)
        val strategy: TargetStrategy = mockk(relaxed = true)
        val traversal: TraversalStrategy = mockk(relaxed = true)
        every { strategy.tile } returns target
        every { strategy.reached(target, width, height) } returns true
        every { aa.toDirection(any()) } returns Direction.SOUTH_EAST
        every { traversal.blocked(tile, Direction.SOUTH_EAST) } returns true
        every { traversal.blocked(tile, Direction.EAST) } returns true
        every { traversal.blocked(tile, Direction.SOUTH) } returns true
        // When
        val result = aa.find(tile, width, height, steps, strategy, traversal, false)
        // Then
        assertNull(result)
        verify(exactly = 0) {
            steps.queue(any(), false)
        }
    }

    @Test
    fun `Blocked route is partial`() {
        // Given
        val steps: StepQueue = mockk(relaxed = true)
        val tile = Position(9, 11)
        val target = Position(11, 9)
        val strategy: TargetStrategy = mockk(relaxed = true)
        val traversal: TraversalStrategy = mockk(relaxed = true)
        every { strategy.tile } returns target
        every { strategy.reached(target, width, height) } returns true
        every { aa.toDirection(any()) } returns Direction.SOUTH_EAST
        every { traversal.blocked(tile, Direction.SOUTH_EAST) } returns false
        val blocked = tile.clone().add(Direction.SOUTH_EAST.x, Direction.SOUTH_EAST.y)
        every { traversal.blocked(blocked, Direction.SOUTH_EAST) } returns true
        every { traversal.blocked(blocked, Direction.EAST) } returns true
        every { traversal.blocked(blocked, Direction.SOUTH) } returns true
        // When
        val result = aa.find(tile, width, height, steps, strategy, traversal, false)
        // Then
        assertEquals(blocked, result)
    }

    @Test
    fun `Direction from delta`() {
        Direction.values().forEach {
            val delta = Position(it.x * 10, it.y * 10)
            val direction = aa.toDirection(delta)
            assertEquals(it, direction)
        }
    }
}