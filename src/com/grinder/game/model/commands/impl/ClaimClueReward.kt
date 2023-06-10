package com.grinder.game.model.commands.impl

import com.grinder.game.content.cluescroll.scroll.ScrollDifficulty
import com.grinder.game.content.cluescroll.task.ClueTaskFactory
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.model.commands.Command
import com.grinder.game.model.commands.DeveloperCommand

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-04
 */
class ClaimClueReward : DeveloperCommand() {

    override fun getDescription(): String {
        return "Claim an easy clue reward."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        ClueTaskFactory.getInstance().rollReward(player, ScrollDifficulty.EASY)
    }
}