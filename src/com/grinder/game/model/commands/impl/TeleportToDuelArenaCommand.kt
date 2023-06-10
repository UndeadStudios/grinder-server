package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler
import com.grinder.game.entity.agent.movement.teleportation.TeleportType
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerStatus
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.Position
import com.grinder.game.model.commands.Command
import com.grinder.util.Misc

class TeleportToDuelArenaCommand : Command {

    override fun getSyntax(): String {
        return ""
    }

    override fun getDescription(): String {
        return "Teleports you to the Duel arena."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        if (TeleportHandler.checkReqs(player, Position(3367, 3274), true, false, player.spellbook.teleportType)) {
            TeleportHandler.teleport(
                player,
                Position(
                    3367 + Misc.getRandomInclusive(3),
                    3274 + Misc.getRandomInclusive(3),
                    0
                ),
                TeleportType.NORMAL, false, true
            )
        }
    }

    override fun canUse(player: Player): Boolean {
        return true
    }
}