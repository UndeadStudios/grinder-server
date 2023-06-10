package com.grinder.game.content.cluescroll.test

import com.grinder.game.content.cluescroll.task.ClueTaskFactory
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.commands.DeveloperCommand

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-03-08
 */
class ResetScrollsHandlerCommand : DeveloperCommand() {

    override fun getSyntax() = ""

    override fun getDescription() = "Resets all the clue scroll system."

    override fun execute(player: Player, command: String, parts: Array<String>) {
        ClueTaskFactory.getInstance().initialize()
        player.sendMessage("Resetting Clue Scrolls.")
    }
}