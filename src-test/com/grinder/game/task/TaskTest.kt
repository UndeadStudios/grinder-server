package com.grinder.game.task

import com.google.gson.Gson
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class TaskTest {

    @Test
    fun `create anonymous task`() {
        val task = object : Task(true) {
            override fun execute() {

            }
        }
        TaskManager.submit(task)
        repeat(100) {
            TaskManager.sequence()
        }
        Assertions.assertTrue(TaskManager.getActiveTasks()
            .filter { it.javaClass.isAnonymousClass }
            .contains(task))
        Assertions.assertEquals(100, task.executionCount)
        Assertions.assertNotNull(Gson().toJson(task, Task::class.java))
        Assertions.assertEquals("Task" +
                "{" +
                "name=Anonymous, " +
                "immediate=true, " +
                "delay=1, " +
                "countdown=1, " +
                "iterations=100, " +
                "running=true, " +
                "key=${Task.DEFAULT_KEY}, " +
                "origin=com.grinder.game.task.TaskTest\$create anonymous task\$task\$1.<init>(TaskTest.kt:11)" +
                "}", task.toString())
    }
}