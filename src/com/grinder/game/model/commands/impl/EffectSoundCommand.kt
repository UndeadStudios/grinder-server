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
class EffectSoundCommand : Command {

    override fun getDescription() = "Plays sound"
    override fun canUse(player: Player) = player.rights.isAdvancedStaff
    override fun execute(player: Player, command: String?, parts: Array<out String>) {
        val id = parts[1].toIntOrNull() ?:-1
        val loopCount = parts.getOrNull(2)?.toIntOrNull() ?:0
        val delay = parts.getOrNull(3)?.toIntOrNull() ?:0
        player.packetSender.sendSound(id, delay, 1)
    }

    override fun getSyntax() = "asound -id -radius -volume -delay"
}