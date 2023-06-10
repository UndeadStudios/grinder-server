package com.grinder.game.model.commands.impl

import com.grinder.game.definition.loader.impl.NpcDropDefinitionLoader
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.commands.Command

class ReloadDropsCommand : Command {

    override fun getSyntax(): String {
        return ""
    }

    override fun getDescription(): String {
        return "Reloads the npc drop definitions."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        NpcDropDefinitionLoader().load()
        player.packetSender.sendConsoleMessage("Drops has been reloaded successfully!")
    }

    override fun canUse(player: Player): Boolean {
        return player.rights.isAdvancedStaff
    }
}