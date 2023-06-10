package com.grinder.game.entity.agent.npc.monster.boss

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.task.Task
import java.util.stream.Collectors

abstract class BossTask<B : Boss>(val boss: B, playerRange: Int = 20, private val cyclesDuration: Int, delay: Int, immediate: Boolean = false)
    : Task(delay, boss, immediate)
{

    var cycle = 0
    val playerList = boss.playerStream(playerRange).collect(Collectors.toList())

    abstract fun onCycle(cycle: Int)

    override fun execute() {

        if (!boss.isAlive) {
            stop()
            return
        }

        playerList.removeIf { !it.isActive }

        onCycle(cycle++)

        if (cycle >= cyclesDuration)
            stop()
    }
}