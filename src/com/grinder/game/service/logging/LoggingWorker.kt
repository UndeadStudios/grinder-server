package com.grinder.game.service.logging

import com.grinder.util.SmartLogger

/**
 * Represents a worker for the [service] that parses incoming [log entries][LoggingEntry].
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   29/02/2020
 * @version 1.0
 */
class LoggingWorker(private val service: LoggingService) : Runnable {

    override fun run() {

        while(true){

            val request = service.loggingEntries.take()
            val folder = request.folder
            val string = request.data

            service.concurrentMap.getOrPut(folder) {SmartLogger(folder)}.write(string)
       }
    }
}