package com.grinder.game.model.commands.impl

import com.grinder.game.entity.`object`.ObjectManager
import com.grinder.game.entity.agent.npc.monster.boss.impl.vorkath.task.QuickfireTask
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Direction
import com.grinder.game.model.commands.DeveloperCommand
import com.grinder.game.task.TaskManager

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   01/04/2020
 * @version 1.0
 */
class TestObjectRemovalCommand : DeveloperCommand() {

    override fun execute(player: Player, command: String, parts: Array<out String>) {
//        val obj = QuickfireTask.Companion.PoisonSplashObject(player.position.clone().move(Direction.NORTH), player)
//        obj.spawn()
    }
}