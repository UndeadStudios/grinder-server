package com.grinder.util.time

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class TimeUtilTest {
    @Test
    fun testConversions() {
        Assertions.assertEquals(1, TimeUtil.MILLIS.toGameCycles(600))
        Assertions.assertEquals(1000, TimeUtil.MILLIS.toMillis(1000))
        Assertions.assertEquals(1, TimeUtil.MILLIS.toSeconds(1000))
        Assertions.assertEquals(600, TimeUtil.GAME_CYCLES.toMillis(1))
        Assertions.assertEquals(0, TimeUtil.GAME_CYCLES.toSeconds(1))
        Assertions.assertEquals(1, TimeUtil.GAME_CYCLES.toGameCycles(1))
    }
}