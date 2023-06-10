package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.commands.Command

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   06/01/2020
 * @version 1.0
 */
class AreaSoundCommand : Command {

    override fun getDescription() = "Plays area sound"
    override fun canUse(player: Player) = player.rights.isAdvancedStaff
    override fun execute(player: Player, command: String?, parts: Array<out String>) {
        val id = parts[1].toIntOrNull() ?:-1
        val radius = parts.getOrNull(2)?.toIntOrNull() ?:1
        val loopCount = parts.getOrNull(3)?.toIntOrNull() ?:1
        val delay = parts.getOrNull(4)?.toIntOrNull() ?:0
        player.packetSender.sendAreaSound(player.position.clone(), id, radius, loopCount, delay)
    }

    override fun getSyntax() = "-id -radius -volume -delay"
}