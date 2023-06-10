package com.grinder.game.model.commands.impl

import com.grinder.game.content.skill.skillable.impl.slayer.SlayerManager
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.commands.Command
import com.grinder.game.model.commands.DeveloperCommand

class FinishSlayerTask : DeveloperCommand() {

    override fun getDescription(): String {
        return "Sets remaining slayer task amount to one."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        if (player.slayer.task == null) {
            player.message("You don't have an active Slayer task.")
            return;
        }
        player.slayer.task.amountLeft = 0
        SlayerManager.completeTaskByCommand(player)
        //player.message("Your Slayer task has been completed.")
    }
}