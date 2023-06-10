package com.grinder.game.service.host

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.grinder.game.service.Service
import com.grinder.game.service.login.LoginService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   05/01/2020
 * @version 1.0
 */
class HostLookupService : Service {

    private val logger: Logger = LogManager.getLogger(LoginService::class.java)
    val requests = LinkedBlockingQueue<HostLookupServiceRequest>()
    val macCountryMap = ConcurrentHashMap<String, HashSet<String>>()

    override fun init() {
        val executorService = Executors
                .newFixedThreadPool(1, ThreadFactoryBuilder()
                        .setNameFormat("host-lookup-worker")
                        .setUncaughtExceptionHandler { t, e ->
                            logger.error("Error with thread $t, ${e.message}", e)
                        }
                        .build())
        executorService.execute(HostLookupWorker(this))
    }

    override fun postLoad() {

    }

    override fun bindNet() {}

    override fun terminate() {}

    fun addHostLookupRequest(msg: HostLookupServiceRequest) {
        requests.offer(msg)
    }

}