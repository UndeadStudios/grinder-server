package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Appearance
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.model.commands.Command
import com.grinder.game.model.commands.DeveloperCommand
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.Misc

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-04
 */
class RandomizeAppearance : DeveloperCommand() {

    override fun getDescription(): String {
        return "Randomizes your current appearance."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        TaskManager.submit(object : Task(1) {
            override fun execute() {
                stop()
                player.appearance[Appearance.HEAD] = Misc.getRandomInclusive(3)
                player.appearance[Appearance.CHEST] = 18 + Misc.getRandomInclusive(3)
                player.appearance[Appearance.ARMS] = 26
                player.appearance[Appearance.HANDS] = 34
                player.appearance[Appearance.LEGS] = 38 + Misc.getRandomInclusive(3)
                player.appearance[Appearance.FEET] = 42
                player.appearance[Appearance.BEARD] = 10 + Misc.getRandomInclusive(5)

                //Colors
                player.appearance[Appearance.HAIR_COLOUR] = Misc.getRandomInclusive(5)
                player.appearance[Appearance.TORSO_COLOUR] = 10 + Misc.getRandomInclusive(5)
                player.appearance[Appearance.LEG_COLOUR] = Misc.getRandomInclusive(5)
                player.appearance[Appearance.FEET_COLOUR] = Misc.getRandomInclusive(4)
                player.appearance[Appearance.SKIN_COLOUR] = Misc.getRandomInclusive(5)
                player.updateAppearance()
                //if (player.isAllowRegionChangePacket) stop()
                player.sendMessage("You have randomized your current appearance.")
            }
        })
    }
}