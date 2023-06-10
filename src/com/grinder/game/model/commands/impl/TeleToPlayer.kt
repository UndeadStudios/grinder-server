package com.grinder.game.model.commands.impl

import com.grinder.game.World
import com.grinder.game.content.minigame.Minigame
import com.grinder.game.content.minigame.MinigameManager
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.getBoolean
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.commands.Command

class TeleToPlayer : Command {

    override fun getSyntax(): String {
        return "[playerName]"
    }

    override fun getDescription(): String {
        return "Teleports you to the target player location."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        if (command.length <= 6) {
            player.message("Wrong usage of the command!")
            return
        }
        val optionalTarget = World.findPlayerByName(command.substring(parts[0].length + 1))
        if (optionalTarget.isPresent) {
            val target = optionalTarget.get()
            if (PlayerUtil.isDeveloper(target) && target.getBoolean(Attribute.INVISIBLE)) {
                player.message("The player doesn't seem to be online to execute this action.")
                return
            }
            if (target.minigame != null || MinigameManager.isInPublicMinigameArea(target)) {
                player.message("The player you're trying to teleport to is currently in a Minigame.")
                return
            }
            player.moveTo(target.position.clone())
            player.message("<img=742> You have teleported to @dre@" + target.username + "</col>!")
        } else {
            player.message("The player doesn't seem to be online to execute this action.")
        }
    }

    override fun canUse(player: Player): Boolean {
        return player.rights.anyMatch(PlayerRights.MODERATOR, PlayerRights.GLOBAL_MODERATOR, PlayerRights.ADMINISTRATOR, PlayerRights.DEVELOPER, PlayerRights.CO_OWNER, PlayerRights.OWNER)
    }
}