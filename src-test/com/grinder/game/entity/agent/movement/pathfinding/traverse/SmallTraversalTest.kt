package com.grinder.game.entity.agent.movement.pathfinding.traverse

import com.grinder.game.collision.Collisions
import com.grinder.game.collision.TileFlags
import com.grinder.game.model.Direction
import com.grinder.game.model.Position
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * @author Greg Hibberd <greg@greghibberd.com>
 * @since May 18, 2020
 */
internal class SmallTraversalTest {

    lateinit var traversal: SmallTraversal

    @BeforeEach
    fun setup() {
        mockkStatic(Collisions::class)
        mockkObject(Collisions)
        traversal = spyk(SmallTraversal(TraversalType.Land, true))
    }

    @Test
    fun `Blocked cardinal`() {
        // Given
        val start = Position(1, 1)
        val direction = Direction.NORTH
        val tile = start.copy().add(direction.x, direction.y)
        every { Collisions.check(tile.x, tile.y, tile.z, any()) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, direction)
        // Then
        assertTrue(result)
    }

    @Test
    fun `Clear cardinal`() {
        // Given
        val start = Position(1, 1)
        val direction = Direction.NORTH
        every { Collisions.check(1, 2, 0, any()) } returns false
        // When
        val result = traversal.blocked(start.x, start.y, start.z, direction)
        // Then
        assertFalse(result)
    }

    @Test
    fun `Diagonal blocked diagonally`() {
        // Given
        val start = Position(1, 1)
        val direction = Direction.NORTH_EAST
        every { Collisions.check(2, 2, 0, any()) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, direction)
        // Then
        assertTrue(result)
    }

    @Test
    fun `Diagonal blocked horizontally`() {
        // Given
        val start = Position(1, 1)
        val direction = Direction.NORTH_EAST
        every { Collisions.check(2, 1, 0, any()) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, direction)
        // Then
        assertTrue(result)
    }

    @Test
    fun `Diagonal blocked vertically`() {
        // Given
        val start = Position(1, 1)
        val direction = Direction.SOUTH_WEST
        every { Collisions.check(1, 0, 0, any()) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, direction)
        // Then
        assertTrue(result)
    }

    @Test
    fun `Diagonal clear`() {
        // Given
        val start = Position(1, 1)
        val direction = Direction.SOUTH_WEST
        every { Collisions.check(any(), any(), any(), any()) } returns false
        // When
        val result = traversal.blocked(start.x, start.y, start.z, direction)
        // Then
        assertFalse(result)
    }

    @Test
    fun `Blocked by entities`() {
        // Given
        val start = Position(1, 1)
        val direction = Direction.SOUTH_WEST
        traversal = spyk(SmallTraversal(TraversalType.Land, true))
        every { Collisions.check(any(), any(), any(), TileFlags.LAND_BLOCK_NORTH_EAST or TileFlags.ENTITY) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, direction)
        // Then
        assertTrue(result)
    }

    @Test
    fun `Not blocked by entities`() {
        // Given
        val start = Position(1, 1)
        val direction = Direction.SOUTH_WEST
        traversal = spyk(SmallTraversal(TraversalType.Land, false))
        every { Collisions.check(any(), any(), any(), TileFlags.LAND_BLOCK_NORTH_EAST or TileFlags.ENTITY) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, direction)
        // Then
        assertFalse(result)
    }

    @Test
    fun `Blocked sky`() {
        // Given
        val start = Position(1, 1)
        val direction = Direction.SOUTH_WEST
        traversal = spyk(SmallTraversal(TraversalType.Sky, false))
        every { Collisions.check(any(), any(), any(), TileFlags.SKY_BLOCK_NORTH_EAST) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, direction)
        // Then
        assertTrue(result)
    }

    @Test
    fun `Blocked ignored`() {
        // Given
        val start = Position(1, 1)
        val direction = Direction.SOUTH_WEST
        traversal = spyk(SmallTraversal(TraversalType.Ignored, false))
        every { Collisions.check(any(), any(), any(), TileFlags.IGNORED_BLOCK_NORTH_EAST) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, direction)
        // Then
        assertTrue(result)
    }
}