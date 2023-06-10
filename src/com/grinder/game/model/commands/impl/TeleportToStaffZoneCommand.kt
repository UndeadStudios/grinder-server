package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler
import com.grinder.game.entity.agent.movement.teleportation.TeleportType
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.model.Position
import com.grinder.game.model.commands.Command
import com.grinder.util.Misc

class TeleportToStaffZoneCommand : Command {
    override fun getSyntax(): String {
        return ""
    }

    override fun getDescription(): String {
        return "Teleports you to the staff's zone."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        if (PlayerUtil.isDeveloper(player)) {
            TeleportHandler.teleportNoReq(
                player,
                Position(2877 + Misc.getRandomInclusive(5), 10194 + Misc.getRandomInclusive(2), 2),
                TeleportType.FAIRY_RING,
                false,
                false
            )
        } else {
            TeleportHandler.teleport(
                player,
                Position(2877 + Misc.getRandomInclusive(5), 10194 + Misc.getRandomInclusive(2), 2),
                TeleportType.NORMAL,
                false,
                true
            )
        }
    }

    override fun canUse(player: Player): Boolean {
        return player.rights.anyMatch(PlayerRights.CAMPAIGN_DEVELOPER, PlayerRights.SERVER_SUPPORTER, PlayerRights.MODERATOR, PlayerRights.DESIGNER, PlayerRights.GLOBAL_MODERATOR, PlayerRights.ADMINISTRATOR, PlayerRights.DEVELOPER, PlayerRights.CO_OWNER, PlayerRights.OWNER)
    }
}