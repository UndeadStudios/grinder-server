package com.grinder.game.entity.agent.combat

import com.grinder.game.collision.Collisions
import com.grinder.game.collision.Collisions.get
import com.grinder.game.model.Position
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LineOfSightTest {

    lateinit var data: MutableMap<Int, Int>

    @BeforeEach
    fun setup() {
        data = spyk(mutableMapOf())
        mockkStatic(Collisions::class)
        mockkObject(Collisions)
        every { Collisions[any(), any(), any()] } returns 0
    }

    @Test
    fun `Has line of sight if under`() {
        val tile = Position(0, 0)
        val other = Position(0, 0)
        // Then
        assertTrue(LineOfSight.withinSight(tile, other))
    }

    @Test
    fun `No line of sight on other plane`() {
        val tile = Position(0, 0)
        val other = Position(0, 0, 1)
        // Then
        assertFalse(LineOfSight.withinSight(tile, other))
    }

    @Test
    fun `Has line of sight over fence`() {
        every { Collisions[0, 0, 0] } returns 33554440
        every { Collisions[1, 0, 0] } returns 537919616
        val tile = Position(0, 0)
        val other = Position(1, 0)
        // Then
        assertTrue(LineOfSight.withinSight(tile, other))
    }

    @Test
    fun `No line of sight over wall`() {
        every { Collisions[0, 0, 0] } returns 34607112
        every { Collisions[1, 0, 0] } returns 536936576
        val tile = Position(0, 0)
        val other = Position(1, 0)
        // Then
        assertFalse(LineOfSight.withinSight(tile, other))
    }

    @Test
    fun `Has line of sight over distant bush`() {
        for(x in 2..4) {
            for(y in 2..4) {
                every { Collisions[x, y, 0] } returns 1048576
            }
        }
        every { Collisions[3, 3, 0] } returns 1074790656
        val tile = Position(0, 0)
        val other = Position(5, 5)
        // Then
        assertTrue(LineOfSight.withinSight(tile, other))
    }

    @Test
    fun `Has diagonal line of sight over fence`() {
        for(x in 0..4) {
            every { Collisions[x, 1, 0] } returns 134217760
            every { Collisions[x, 0, 0] } returns 9437186
        }
        val tile = Position(0, 0)
        val other = Position(1, 2)
        // Then
        assertTrue(LineOfSight.withinSight(tile, other))
    }

    @Test
    fun `No diagonal line of sight over wall`() {
        for(x in 0..4) {
            every { Collisions[x, 1, 0] } returns 134234144
            every { Collisions[x, 0, 0] } returns 9438210
        }
        val tile = Position(0, 0)
        val other = Position(1, 2)
        // Then
        assertFalse(LineOfSight.withinSight(tile, other))
    }

    /**
     *  |A|-|-|-|-|
     *  |-|\|-|T|T|
     *  |-|-|x|T|T|
     *  |-|-|-|-|B|
     */
    @Test
    fun `No horizontal line of sight behind tree`() {
        for(x in 3..4) {
            for(y in 1..2) {
                every { Collisions[x, y, 0] } returns 1073873152
            }
        }
        val tile = Position(0, 3)
        val other = Position(4, 0)
        // Then
        assertFalse(LineOfSight.withinSight(tile, other))
    }

    /**
     *  |-|-|B|-|
     *  |-|x|T|T|
     *  |-|-|T|T|
     *  |-|/|-|-|
     *  |A|-|-|-|
     */
    @Test
    fun `No vertical line of sight behind tree`() {
        for(x in 2..3) {
            for(y in 2..3) {
                every { Collisions[x, y, 0] } returns 1073873152
            }
        }
        val tile = Position(0, 0)
        val other = Position(3, 4)
        // Then
        assertFalse(LineOfSight.withinSight(tile, other))
    }

}