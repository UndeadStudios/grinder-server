package com.grinder.util.benchmark

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   24/09/2020
 */
class ObjectMonitor(private val any: Any, private val displayThreshold: Int) {

    private val benchMarker = SimpleBenchMarker()
    private val logger = LogManager.getLogger("$any-monitor")!!
    private var state = "none"

    fun markStartCycle() {
        benchMarker.reset()
    }

    fun markEndCycle(){
        print(displayThreshold, logger)
        state = "none"
    }

    fun markState(newState: String){
        val hasState = state != "none"
        if(hasState && state != newState){
            benchMarker.mark("$state -> $newState")
            state = newState
        } else if(!hasState)
            state = newState
    }

    fun print(){
        benchMarker.println(toString())
    }

    fun print(displayThreshold: Int, logger: Logger){
        benchMarker.println(any.toString(), displayThreshold, logger)
    }
}