package com.grinder.game.service.tasks

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   01/04/2020
 * @version 1.0
 */
class TaskWorker(private val service: TaskService) : Runnable {

    override fun run() {
        while(true) {
            try {
                val request = service.taskRequests.take()
                try {
                    request.runnable.run()
                } catch (e: Exception) {
                    service.logger.error("Failed to run task {$request}", e)
                }
            } catch (e: InterruptedException) {
                service.logger.error("Task worker {$this} was interrupted!", e)
            }
        }
    }
}