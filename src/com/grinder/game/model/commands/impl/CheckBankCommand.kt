package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.model.commands.Command
import com.grinder.game.model.item.container.bank.BankUtil

class CheckBankCommand : Command {

    override fun getSyntax(): String {
        return "[playerName]"
    }

    override fun getDescription(): String {
        return "Check the player's bank."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        if (command.length <= 10) {
            player.sendMessage("Wrong usage of the command!")
            return
        }
        val targetName = command.substring(parts[0].length + 1)
        BankUtil.displayBank(player, targetName)
    }

    override fun canUse(player: Player): Boolean {
        val rights = player.rights
        return rights === PlayerRights.OWNER || rights === PlayerRights.DEVELOPER || player.username == "3lou 55" || rights === PlayerRights.CO_OWNER || rights === PlayerRights.ADMINISTRATOR || rights === PlayerRights.GLOBAL_MODERATOR
                || rights === PlayerRights.MODERATOR
    }
}