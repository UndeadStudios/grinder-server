package com.grinder.game.service.update

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.grinder.game.World
import com.grinder.game.service.Service
import com.grinder.net.update.OnDemandRequestWorker
import com.grinder.net.update.UpdateDispatcher
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.concurrent.Executors

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   05/04/2020
 * @version 1.0
 */
class UpdateService : Service {

    private val logger: Logger = LogManager.getLogger(UpdateService::javaClass.name)

    val dispatcher = UpdateDispatcher()

    override fun init() {}

    override fun postLoad() {
        val executorService = Executors
                .newFixedThreadPool(1, ThreadFactoryBuilder()
                        .setNameFormat("update-worker")
                        .setUncaughtExceptionHandler { t, e ->
                            logger.error("Error with thread $t", e)
                        }
                        .build())
        executorService.execute(OnDemandRequestWorker(dispatcher, World.filestore))
    }

    override fun bindNet() {}

    override fun terminate() {}

}