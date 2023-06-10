package com.grinder.game.model.commands.impl

import com.grinder.game.World
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.commands.DeveloperCommand

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   13/12/2019
 * @version 1.0
 */
class TeleportToNpcIndex : DeveloperCommand() {

    override fun getSyntax() = "[index]"
    override fun getDescription() = "Teleport to the npc with the provided world index (not the npc id!)"

    override fun execute(player: Player, command: String, parts: Array<out String>) {

        val npcIndex = parts[1].trim().toIntOrNull()

        if(npcIndex == null){
            player.sendMessage("You entered an invalid syntax, please provide an integer number!")
            return
        }

        val npc = World.npcs.get(npcIndex)

        if(npc == null){
            player.sendMessage("The npc with world index $npcIndex was not found")
            return
        }

        player.sendMessage("Teleporting to npc {$npc} with index $npcIndex")
        player.moveTo(npc.position.clone())
    }
}