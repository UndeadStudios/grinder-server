package com.grinder.util

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager

object TaskFunctions {

    val TICKS_PER_SECOND = 1.0 / 0.6

    val TICKS_PER_MINUTE = TICKS_PER_SECOND * 60

    val TICKS_PER_HOUR = TICKS_PER_MINUTE * 60

    /**
     * Delay an action by a number of ticks
     */
    fun delayBy(delay: Int, task: () -> Unit) {
        TaskManager.submit(object : Task(delay, false) {
            override fun execute() {
                task.invoke()
                stop()
            }
        })
    }

    /**
     * Delay an action by a number of ticks
     */
    fun delayBy(delay: Int, player: Player, task: () -> Unit) {
        TaskManager.submit(object : Task(delay, player, false) {
            override fun execute() {
                task.invoke()
                stop()
            }
        })
    }

    /**
     * Delay an action by a number of ticks
     * @property delay numbers of ticks
     * @property player player object
     */
    fun repeatDelayed(delay : Int, player : Player, task : Task.(Player) -> Unit ) {
        TaskManager.submit(object: Task(delay, false) {
            override fun execute() {
                task.invoke(this, player)
            }
        })
    }

    /**
     * Delay an action by a number of ticks
     * @property delay numbers of ticks
     * @property player player object
     */
    fun repeatDelayed(delay : Int, immediate : Boolean = false, task : Task.() -> Unit ) {
        TaskManager.submit(object: Task(delay, immediate) {
            override fun execute() {
                task.invoke(this)
            }
        })
    }

    /**
     * Delay an action by a number of ticks
     * @property delay numbers of ticks
     * @property player player object
     */
    fun repeatDelayedInterruptable(delay : Int, player : Player, task : Task.() -> Unit ) {
        val startPos = player.position.copy()

        TaskManager.submit(object: Task(delay) {
            override fun execute() {
                if(player.position == startPos) {
                    task.invoke(this)
                } else {
                    stop()
                }
            }
        })
    }


    /**
     * Delay an action by a number of ticks
     * @property delay numbers of ticks
     * @property player player object
     */
    fun repeatDelayedInterruptable(delay : Int, player : Player, stopTask : Task.() -> Unit, task : Task.() -> Unit ) {
        val startPos = player.position.copy()

        TaskManager.submit(object: Task(delay) {
            override fun execute() {
                if(player.position == startPos) {
                    task.invoke(this)
                } else {
                    stopTask()
                    stop()
                }
            }
        })
    }
}