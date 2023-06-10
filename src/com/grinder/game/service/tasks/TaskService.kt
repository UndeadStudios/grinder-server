package com.grinder.game.service.tasks

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.grinder.game.service.Service
import org.apache.logging.log4j.LogManager
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

/**
 * Represents a [Service] for offloading [tasks][TaskRequest]
 * to different [worker][TaskWorker] threads.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   13/12/2019
 * @version 1.0
 */
class TaskService : Service{

    internal val logger = LogManager.getLogger(TaskService::javaClass.name)

    val taskRequests = LinkedBlockingQueue<TaskRequest>(400)

    private var threadCount = 3

    override fun init() {}

    override fun postLoad() {
        val executorService = Executors
                .newFixedThreadPool(threadCount, ThreadFactoryBuilder()
                    .setNameFormat("task-worker")
                    .setUncaughtExceptionHandler { t, e ->
                        logger.error("Error with task service worker {$t}", e)
                    }
                    .build())
        for(i in 0 until threadCount)
            executorService.execute(TaskWorker(this))
    }

    override fun bindNet() {}

    override fun terminate() {}

    fun addTaskRequest(taskRequest: TaskRequest){
        if(!taskRequests.offer(taskRequest)){
            logger.warn("Could not add $taskRequest due to capacity reached!",
                Exception("Task service capacity reached!"))
            if (taskRequest.mayExecuteSingleThreaded) {
                logger.info("Executing $taskRequest on current thread.")
                try {
                    taskRequest.runnable.run()
                } catch (e: Exception){
                    logger.error("Failed to execute task {$taskRequest} on current thread.", e)
                }
            }
        }
    }

    fun waitTillCompleted(sleepTime: Long = 5L) {
        while(taskRequests.isNotEmpty()){
            Thread.sleep(sleepTime)
        }
    }
}