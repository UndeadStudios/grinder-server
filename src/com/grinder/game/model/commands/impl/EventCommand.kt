package com.grinder.game.model.commands.impl

import com.grinder.game.content.pvm.WildernessBossSpirit
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.sendOptionsKt
import com.grinder.game.model.commands.Command

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   06/01/2020
 * @version 1.0
 */
class EventCommand : Command {

    override fun getDescription() = "Start a global event!"
    override fun canUse(player: Player) = player.rights.isAdvancedStaff
    override fun execute(player: Player, command: String?, parts: Array<out String>) {
        player.sendOptionsKt(
                "wilderness boss spirit" to {WildernessBossSpirit.spawn()}
        )
    }

    override fun getSyntax() = ""
}