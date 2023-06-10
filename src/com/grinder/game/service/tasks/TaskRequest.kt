package com.grinder.game.service.tasks

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   01/04/2020
 * @version 1.0
 *
 * @param mayExecuteSingleThreaded in case of [TaskService.taskRequests] having reached its capacity,
 *                                 may the [Runnable] be executed in-place (same thread as invoker).
 */
open class TaskRequest(val runnable: Runnable, val mayExecuteSingleThreaded: Boolean)