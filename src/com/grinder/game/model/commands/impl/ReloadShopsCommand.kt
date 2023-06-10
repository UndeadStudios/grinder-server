package com.grinder.game.model.commands.impl

import com.grinder.game.definition.loader.impl.ShopDefinitionLoader
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.commands.Command

class ReloadShopsCommand : Command {

    override fun getSyntax(): String {
        return ""
    }

    override fun getDescription(): String {
        return "Reloads the system shop definitions."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        ShopDefinitionLoader().load()
        player.packetSender.sendConsoleMessage("Shops has been reloaded successfully!")
    }

    override fun canUse(player: Player): Boolean {
        return player.rights.isAdvancedStaff
    }
}