package com.grinder.game.model.commands.impl

import com.grinder.game.World
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Direction
import com.grinder.game.model.FacingDirection
import com.grinder.game.model.commands.DeveloperCommand

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   10/11/2019
 * @version 1.0
 */
class FaceNPCCommand : DeveloperCommand() {

    override fun getSyntax() = "[id] [face]"

    override fun getDescription() = "Sets all NPC ids to face a certain direction."

    override fun execute(player: Player, command: String?, parts: Array<out String>) {
        val npcIndex = parts[1].toInt()
        World.npcs[npcIndex].face = FacingDirection.valueOf(parts[2].trim().toUpperCase())

    }
}