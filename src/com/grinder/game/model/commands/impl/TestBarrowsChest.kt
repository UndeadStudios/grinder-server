package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.model.commands.Command
import com.grinder.game.model.commands.DeveloperCommand

class TestBarrowsChest : DeveloperCommand() {

    override fun getDescription(): String {
        return "Opens the barrows chest."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        player.barrowsManager.handleReward()
    }
}