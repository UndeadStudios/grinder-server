package com.grinder.game.service.logging

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.grinder.game.service.Service
import com.grinder.util.SmartLogger
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque

/**
 * Represents a [Service] used to log game activities.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   29/02/2020
 * @version 1.0
 */
class LoggingService : Service {

    private val logger: Logger = LogManager.getLogger(LoggingService::class.java)
    private var threadCount = 1

    val loggingEntries = LinkedBlockingDeque<LoggingEntry>(100)
    val concurrentMap = ConcurrentHashMap<String, SmartLogger>()

    override fun init() {}

    override fun postLoad() {
        val executorService = Executors
                .newFixedThreadPool(threadCount, ThreadFactoryBuilder()
                        .setNameFormat("logging-worker")
                        .setUncaughtExceptionHandler { t, e ->
                            logger.error("Error with thread $t", e) }.build())
        executorService.execute(LoggingWorker(this))
    }

    override fun bindNet() {}

    override fun terminate() {
        concurrentMap.values.forEach {
            it.flush()
            it.close()
        }
    }

    fun addLogEntry(loggingEntry: LoggingEntry) {
        if(!loggingEntries.offer(loggingEntry)){
            logger.info("Could not add $loggingEntry due to capacity reached!")
        }
    }
}