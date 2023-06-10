package com.grinder.util.benchmark

import org.apache.logging.log4j.Logger
import java.util.concurrent.TimeUnit

/**
 * This simple utility is for bench-marking operations.
 *
 * Created by Stan van der Bend on 17/07/2017.
 */
class SimpleBenchMarker {

    var disable = false

    private val markings = linkedMapOf<String, Long>()
    private var start = 0L
    private var end = 0L

    fun mark(identifier : String){
        if(disable)
            return
        markings[identifier] = duration()
    }

    fun start() {
        start = System.nanoTime()
    }
    fun end() {
        end = System.nanoTime()
    }
    fun reset() {
        markings.clear()
        start()
        end = start
    }
    fun duration(): Long { return System.nanoTime() - start }
    fun startTillEndDuration(): Long { return end - start }

    fun println(identifier: String) {

        if(disable)
            return

        end()
        kotlin.io.println("[" + identifier + "] -> measures " + TimeUnit.NANOSECONDS.toMillis(startTillEndDuration()) + " ms. ")
        reset()
    }

    fun println(identifier: String, displayThreshold: Int, logger: Logger) {

        if(disable)
            return

        end()

        val total = TimeUnit.NANOSECONDS.toMillis(startTillEndDuration())

        if (total >= displayThreshold){

            val builder = StringBuilder()

            builder.appendln("[$identifier][$total ms]: ->")
            markings.forEach { (mark, timestamp) -> builder.appendln("   [$mark]: finished after ${TimeUnit.NANOSECONDS.toMillis(timestamp)} ms.") }
            logger.warn(builder.toString())
        }

        reset()
    }
}
