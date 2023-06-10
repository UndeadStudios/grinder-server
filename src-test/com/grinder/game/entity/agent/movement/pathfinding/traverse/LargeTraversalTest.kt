package com.grinder.game.entity.agent.movement.pathfinding.traverse

import com.grinder.game.collision.Collisions
import com.grinder.game.collision.TileFlags.ENTITY
import com.grinder.game.collision.TileFlags.IGNORED_WALL_SOUTH_EAST
import com.grinder.game.collision.TileFlags.LAND_CLEAR_EAST
import com.grinder.game.collision.TileFlags.LAND_CLEAR_NORTH
import com.grinder.game.collision.TileFlags.LAND_CLEAR_SOUTH
import com.grinder.game.collision.TileFlags.LAND_CLEAR_WEST
import com.grinder.game.collision.TileFlags.LAND_WALL_NORTH_EAST
import com.grinder.game.collision.TileFlags.LAND_WALL_NORTH_WEST
import com.grinder.game.collision.TileFlags.LAND_WALL_SOUTH_EAST
import com.grinder.game.collision.TileFlags.LAND_WALL_SOUTH_WEST
import com.grinder.game.collision.TileFlags.SKY_WALL_SOUTH_EAST
import com.grinder.game.model.Direction
import com.grinder.game.model.Position
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class LargeTraversalTest {

    lateinit var traversal: LargeTraversal

    @BeforeEach
    fun setup() {
        mockkStatic(Collisions::class)
        mockkObject(Collisions)
        every { Collisions.check(any(), any(), any(), any()) } returns true
    }

    /**
     * |X| | |
     * |E|E|E|
     * | | | |
     */
    @Test
    fun `North blocked at the start`() {
        // Given
        val start = Position(1, 1)
        traversal = spyk(LargeTraversal(TraversalType.Land, false, 3, 1))
        every { Collisions.check(start.x, start.y + 1, start.z, LAND_WALL_SOUTH_EAST) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.NORTH)
        // Then
        assertTrue(result)
    }

    /**
     * | |X| |
     * |E|E|E|
     * | | | |
     */
    @Test
    fun `North blocked in the middle`() {
        // Given
        val start = Position(1, 1)
        traversal = spyk(LargeTraversal(TraversalType.Land, false, 3, 1))
        every { Collisions.check(start.x + 1, start.y + 1, start.z, LAND_CLEAR_SOUTH) } returns false
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.NORTH)
        // Then
        assertTrue(result)
    }

    /**
     * | | |X|
     * |E|E|E|
     * | | | |
     */
    @Test
    fun `North blocked at the end`() {
        // Given
        val start = Position(1, 1)
        traversal = spyk(LargeTraversal(TraversalType.Land, false, 3, 1))
        every { Collisions.check(start.x + 2, start.y + 1, start.z, LAND_WALL_SOUTH_WEST) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.NORTH)
        // Then
        assertTrue(result)
    }

    /**
     * | | | |X|
     * |E|E|E| |
     * |E|E|E| |
     * |E|E|E| |
     */
    @Test
    fun `North-east blocked diagonally`() {
        // Given
        val start = Position(1, 1)
        traversal = spyk(LargeTraversal(TraversalType.Land, false, 3, 3))
        every { Collisions.check(start.x + 3, start.y + 3, start.z, LAND_WALL_SOUTH_WEST) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.NORTH_EAST)
        // Then
        assertTrue(result)
    }

    /**
     * | |X|X| |
     * |E|E|E| |
     * |E|E|E| |
     * |E|E|E| |
     */
    @ParameterizedTest
    @ValueSource(ints = [1, 2])
    fun `North-east blocked vertically`(offset: Int) {
        // Given
        val start = Position(1, 1)
        traversal = spyk(LargeTraversal(TraversalType.Land, false, 3, 3))
        every { Collisions.check(start.x + offset, start.y + 3, start.z, LAND_CLEAR_SOUTH) } returns false
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.NORTH_EAST)
        // Then
        assertTrue(result)
    }

    /**
     * | | | | |
     * |E|E|E|X|
     * |E|E|E|X|
     * |E|E|E| |
     */
    @ParameterizedTest
    @ValueSource(ints = [1, 2])
    fun `North-east blocked horizontally`(offset: Int) {
        // Given
        val start = Position(1, 1)
        traversal = spyk(LargeTraversal(TraversalType.Land, false, 3, 3))
        every { Collisions.check(start.x + 3, start.y + offset, start.z, LAND_CLEAR_WEST) } returns false
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.NORTH_EAST)
        // Then
        assertTrue(result)
    }

    /**
     * |E| | |
     * |E| | |
     * |E|X| |
     */
    @Test
    fun `East blocked at the start`() {
        // Given
        val start = Position(1, 1)
        traversal = spyk(LargeTraversal(TraversalType.Land, false, 1, 3))
        every { Collisions.check(start.x + 1, start.y, start.z, LAND_WALL_NORTH_WEST) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.EAST)
        // Then
        assertTrue(result)
    }

    /**
     * |E| | |
     * |E|X| |
     * |E| | |
     */
    @Test
    fun `East blocked in the middle`() {
        // Given
        val start = Position(1, 1)
        traversal = spyk(LargeTraversal(TraversalType.Land, false, 1, 3))
        every { Collisions.check(start.x + 1, start.y + 1, start.z, LAND_CLEAR_WEST) } returns false
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.EAST)
        // Then
        assertTrue(result)
    }

    /**
     * |E|X| |
     * |E| | |
     * |E| | |
     */
    @Test
    fun `East blocked at the end`() {
        // Given
        val start = Position(1, 1)
        traversal = spyk(LargeTraversal(TraversalType.Land, false, 1, 3))
        every { Collisions.check(start.x + 1, start.y + 2, start.z, LAND_WALL_SOUTH_WEST) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.EAST)
        // Then
        assertTrue(result)
    }

    /**
     * | |E|E|
     * | |E|E|
     * | |E|E|
     * | |E|E|
     * |X| | |
     */
    @Test
    fun `South-west blocked diagonally`() {
        // Given
        val start = Position(1, 1)
        traversal = spyk(LargeTraversal(TraversalType.Land, false, 2, 4))
        every { Collisions.check(start.x - 1, start.y - 1, start.z, LAND_WALL_NORTH_EAST) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.SOUTH_WEST)
        // Then
        assertTrue(result)
    }

    /**
     * | |E|E|
     * | |E|E|
     * | |E|E|
     * | |E|E|
     * | |X| |
     */
    @Test
    fun `South-west blocked vertically`() {
        // Given
        val start = Position(1, 1)
        traversal = spyk(LargeTraversal(TraversalType.Land, false, 2, 4))
        every { Collisions.check(start.x, start.y - 1, start.z, LAND_CLEAR_NORTH) } returns false
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.SOUTH_WEST)
        // Then
        assertTrue(result)
    }

    /**
     * | |E|E|
     * |X|E|E|
     * |X|E|E|
     * |X|E|E|
     * | | | |
     */
    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2])
    fun `South-west blocked horizontally`(offset: Int) {
        // Given
        val start = Position(1, 1)
        traversal = spyk(LargeTraversal(TraversalType.Land, false, 2, 4))
        every { Collisions.check(start.x - 1, start.y + offset, start.z, LAND_CLEAR_EAST) } returns false
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.SOUTH_WEST)
        // Then
        assertTrue(result)
    }

    @Test
    fun `Blocked by entity`() {
        // Given
        val start = Position(1, 1)
        traversal = spyk(LargeTraversal(TraversalType.Land, true, 3, 1))
        every { Collisions.check(start.x, start.y + 1, start.z, LAND_WALL_SOUTH_EAST or ENTITY) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.NORTH)
        // Then
        assertTrue(result)
    }

    @Test
    fun `Blocked by sky`() {
        // Given
        val start = Position(1, 1)
        traversal = spyk(LargeTraversal(TraversalType.Sky, false, 3, 1))
        every { Collisions.check(start.x, start.y + 1, start.z, SKY_WALL_SOUTH_EAST) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.NORTH)
        // Then
        assertTrue(result)
    }

    @Test
    fun `Blocked by ignored`() {
        // Given
        val start = Position(1, 1)
        traversal = spyk(LargeTraversal(TraversalType.Ignored, false, 3, 1))
        every { Collisions.check(start.x, start.y + 1, start.z, IGNORED_WALL_SOUTH_EAST) } returns true
        // When
        val result = traversal.blocked(start.x, start.y, start.z, Direction.NORTH)
        // Then
        assertTrue(result)
    }
}