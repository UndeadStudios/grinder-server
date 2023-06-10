package com.grinder.game.content.cluescroll.task

/**
 * Represents the state of a [ClueTask].
 *
 * @param completed                 is the task completed now?
 * @param preventDefaultOperation   block clue related operations in [ClueTask.performOperation]
 */
class ClueTaskState(
        val completed: Boolean,
        val preventDefaultOperation: Boolean
) {
    override fun toString() = "TaskActionProgress [completed=$completed, preventDefaultOperation=$preventDefaultOperation]"
}