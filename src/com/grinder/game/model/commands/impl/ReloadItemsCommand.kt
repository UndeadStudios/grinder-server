package com.grinder.game.model.commands.impl

import com.grinder.game.definition.loader.impl.ItemDefinitionLoader
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.model.commands.Command
import com.grinder.game.model.commands.DeveloperCommand

class ReloadItemsCommand : DeveloperCommand() {

    override fun getDescription(): String {
        return "Reloads the item definitions."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        ItemDefinitionLoader().load()
        player.packetSender.sendConsoleMessage("Items has been reloaded successfully!")
    }
}