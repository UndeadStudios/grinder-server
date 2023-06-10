package com.grinder.util.benchmark

import com.grinder.util.benchmark.Monitors.monitors
import org.apache.logging.log4j.LogManager

/**
 * TODO: add documentation
 *
 * TODO: clear [monitors] at some point, might cause memory leak.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   24/09/2020
 */
object Monitors {

    private const val ENABLED = false
    private const val DISPLAY_THRESHOLD = 30

    private val logger = LogManager.getLogger(Monitors::class.java.simpleName)
    val monitors = HashMap<Monitored, ObjectMonitor>()

    fun onStartCycle(any: Any) {

        if (!ENABLED)
            return

        if (any is Monitored) {
            val monitor = monitors.getOrPut(any) { ObjectMonitor(any, DISPLAY_THRESHOLD) }
            monitor.markStartCycle()
        }
    }

    fun onEndCycle(any: Any) {

        if (!ENABLED)
            return

        if (any is Monitored) {
            val monitor = monitors.getOrPut(any) { ObjectMonitor(any, DISPLAY_THRESHOLD) }
            monitor.markEndCycle()
        }
    }

    fun onStateSwitch(any: Any, newState: String) {

        if (!ENABLED)
            return

        if (any !is Monitored){
            logger.error("Can only switch states of classes that implement Monitored.", Exception("$any does not implement Monitored"))
            return
        }

        val monitor = monitors.getOrPut(any) { ObjectMonitor(any, DISPLAY_THRESHOLD) }
        monitor.markState(newState)
    }
}