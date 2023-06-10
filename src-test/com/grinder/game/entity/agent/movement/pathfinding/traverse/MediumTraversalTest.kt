package com.grinder.game.entity.agent.movement.pathfinding.traverse

import com.grinder.game.collision.Collisions
import com.grinder.game.collision.TileFlags
import com.grinder.game.collision.TileFlags.ENTITY
import com.grinder.game.collision.TileFlags.LAND_BLOCK_SOUTH_EAST
import com.grinder.game.collision.TileFlags.LAND_BLOCK_SOUTH_WEST
import com.grinder.game.collision.TileFlags.LAND_CLEAR_NORTH
import com.grinder.game.collision.TileFlags.LAND_CLEAR_WEST
import com.grinder.game.collision.TileFlags.LAND_WALL_NORTH_WEST
import com.grinder.game.collision.TileFlags.SKY_BLOCK_SOUTH_EAST
import com.grinder.game.model.Direction
import com.grinder.game.model.Position
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MediumTraversalTest {

    lateinit var traversal: MediumTraversal

    @BeforeEach
    fun setup() {
        mockkStatic(Collisions::class)
        mockkObject(Collisions)
        traversal = spyk(MediumTraversal(TraversalType.Land, false))
    }

    /**
     * |X| | |
     * |E|E| |
     * |E|E| |
     */
    @Test
    fun `North blocked at the start`() {
        // Given
        val start = Position(1, 1)
        every { Collisions.check(start.x, start.y + 2, start.z, LAND_BLOCK_SOUTH_EAST) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.NORTH)
        // Then
        assertTrue(result)
    }

    /**
     * | |X| |
     * |E|E| |
     * |E|E| |
     */
    @Test
    fun `North blocked at the end`() {
        // Given
        val start = Position(1, 1)
        every { Collisions.check(start.x + 1, start.y + 2, start.z, LAND_BLOCK_SOUTH_WEST) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.NORTH)
        // Then
        assertTrue(result)
    }

    /**
     * |E|E| |
     * |E|E| |
     * | | |X|
     */
    @Test
    fun `South-east blocked diagonally`() {
        // Given
        val start = Position(1, 1)
        every { Collisions.check(any(), any(), any(), any()) } returns true
        every { Collisions.check(start.x + 2, start.y - 1, start.z, LAND_WALL_NORTH_WEST) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.SOUTH_EAST)
        // Then
        assertTrue(result)
    }

    /**
     * |E|E| |
     * |E|E| |
     * | |X| |
     */
    @Test
    fun `South-east blocked vertically`() {
        // Given
        val start = Position(1, 1)
        every { Collisions.check(any(), any(), any(), any()) } returns true
        every { Collisions.check(start.x + 1, start.y - 1, start.z, LAND_CLEAR_NORTH) } returns false
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.SOUTH_EAST)
        // Then
        assertTrue(result)
    }

    /**
     * |E|E| |
     * |E|E|X|
     * | | | |
     */
    @Test
    fun `South-east blocked horizontally`() {
        // Given
        val start = Position(1, 1)
        every { Collisions.check(any(), any(), any(), any()) } returns true
        every { Collisions.check(start.x + 2, start.y, start.z, LAND_CLEAR_WEST) } returns false
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.SOUTH_EAST)
        // Then
        assertTrue(result)
    }

    @Test
    fun `North blocked by entity`() {
        // Given
        val start = Position(1, 1)
        traversal = spyk(MediumTraversal(TraversalType.Land, true))
        every { Collisions.check(start.x, start.y + 2, start.z, LAND_BLOCK_SOUTH_EAST or ENTITY) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.NORTH)
        // Then
        assertTrue(result)
    }

    @Test
    fun `Blocked by sky`() {
        // Given
        val start = Position(1, 1)
        traversal = spyk(MediumTraversal(TraversalType.Sky, false))
        every { Collisions.check(start.x, start.y + 2, start.z, SKY_BLOCK_SOUTH_EAST) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.NORTH)
        // Then
        assertTrue(result)
    }

    @Test
    fun `Blocked by ignored`() {
        // Given
        val start = Position(1, 1)
        traversal = spyk(MediumTraversal(TraversalType.Ignored, false))
        every { Collisions.check(start.x, start.y + 2, start.z, TileFlags.IGNORED_BLOCK_SOUTH_EAST) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.NORTH)
        // Then
        assertTrue(result)
    }

}