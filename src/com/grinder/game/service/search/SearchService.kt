package com.grinder.game.service.search

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.grinder.game.service.Service
import com.grinder.game.service.search.droptable.SearchDropTableRequest
import com.grinder.game.service.search.droptable.SearchDropTableWorker
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   13/12/2019
 * @version 1.0
 */
class SearchService : Service{

    private val logger: Logger = LogManager.getLogger(SearchService::javaClass.name)

    val dropTableRequests = LinkedBlockingQueue<SearchDropTableRequest>(100)

    private var threadCount = 1

    override fun init() {}

    override fun postLoad() {
        val executorService = Executors
                .newFixedThreadPool(threadCount, ThreadFactoryBuilder()
                        .setNameFormat("search-worker")
                        .setUncaughtExceptionHandler { t, e ->
                            logger.error("Error with thread $t, ${e.message}", e)
                        }
                        .build())
        executorService.execute(SearchDropTableWorker(this))
    }

    override fun bindNet() {}

    override fun terminate() {}

    fun addSearchRequest(searchDropTableRequest: SearchDropTableRequest){

        if(!dropTableRequests.offer(searchDropTableRequest)){
            logger.info("Could not add $searchDropTableRequest due to capacity reached!")
        }
    }

}