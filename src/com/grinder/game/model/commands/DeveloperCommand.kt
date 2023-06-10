package com.grinder.game.model.commands

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   16/10/2019
 * @version 1.0
 */
abstract class DeveloperCommand : Command {

    override fun getSyntax() = ""

    override fun getDescription() = "A default developer command."

    override fun canUse(player: Player) = player.rights.anyMatch(PlayerRights.DEVELOPER, PlayerRights.OWNER) || player.username.toLowerCase() == "lou"
            || player.username.toLowerCase() == "3lou 55"
            || player.username.toLowerCase() == "mod grinder"
}