package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.commands.Command
import com.grinder.game.model.punishment.PunishmentManager

class ReloadPunishmentsCommand : Command {

    override fun getSyntax(): String {
        return ""
    }

    override fun getDescription(): String {
        return "Reloads the server punishment's system."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        PunishmentManager.load()
        player.message("You loaded " + PunishmentManager.count() + " punishments.")
    }

    override fun canUse(player: Player): Boolean {
        return player.rights.isStaff && !(player.rights.anyMatch(PlayerRights.SERVER_SUPPORTER))
    }
}