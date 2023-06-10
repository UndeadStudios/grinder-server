package com.grinder.util.time

import java.util.concurrent.TimeUnit

import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.NANOSECONDS

/**
 * @author Stan van der Bend
 */
class Timer(var time : Long = 0) {

    fun headStart(nanosStart: Long): Timer {
        time = System.nanoTime() - nanosStart
        return this
    }

    fun reset(timeUnit: TimeUnit, i: Long): Timer {
        time = timeUnit.toNanos(i)
        return this
    }

    fun reset(i: Long): Timer {
        time = TimeUnit.MILLISECONDS.toNanos(i)
        return this
    }

    fun reset(): Timer {
        time = System.nanoTime()
        return this
    }

    fun elapsed(): Long {
        return System.nanoTime() - time
    }

    fun elapsed(timeUnit: TimeUnit): Long {
        return timeUnit.convert(elapsed(), NANOSECONDS)
    }

    fun millisElapsed(): Long {
        return elapsed(MILLISECONDS)
    }

    fun elapsed(time: Long, timeUnit: TimeUnit): Boolean {
        return elapsed(timeUnit) >= time
    }

    fun elapsed(time: Long, timeUtil: TimeUtil): Boolean {
        return elapsed(timeUtil.toMillis(time), TimeUnit.MILLISECONDS)
    }

    fun millisElapsed(time: Long): Boolean {
        return elapsed(time, TimeUnit.MILLISECONDS)
    }

    @JvmOverloads
    fun print(timeUnit: TimeUnit = TimeUnit.MILLISECONDS) {
        println("Elapsed = " + elapsed(timeUnit) + " in " + timeUnit.name)
    }


}