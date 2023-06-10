package com.grinder.util.time

import java.util.concurrent.TimeUnit

/**
 * Created by Kyle Fricilone on May 30, 2020.
 */

private const val TICK_SCALE = 600.0
private const val SECOND_SCALE = 1000.0

fun TimeUnit.toCycles(duration: Long): Int {
    val seconds = toSeconds(duration)
    return ((seconds / TICK_SCALE) * SECOND_SCALE).toInt()
}