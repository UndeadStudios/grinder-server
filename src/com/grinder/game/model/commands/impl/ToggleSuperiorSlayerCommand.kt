package com.grinder.game.model.commands.impl

import com.grinder.game.content.skill.skillable.impl.slayer.SlayerManager
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.statement
import com.grinder.game.model.commands.DeveloperCommand

class ToggleSuperiorSlayerCommand : DeveloperCommand() {

    override fun execute(player: Player, command: String?, parts: Array<out String>?) {
        SlayerManager.DISABLED_SUPERIOR_SLAYER = !SlayerManager.DISABLED_SUPERIOR_SLAYER;
        val state = if(SlayerManager.DISABLED_SUPERIOR_SLAYER) "off" else "on"
        player.statement("You turned $state superior slayer")
    }
}