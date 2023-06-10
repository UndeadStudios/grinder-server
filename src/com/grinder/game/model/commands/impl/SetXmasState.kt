package com.grinder.game.model.commands.impl

import com.grinder.game.World
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022.setState
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022State
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.commands.DeveloperCommand
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import java.util.function.Consumer

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   20/12/2019
 * @version 1.0
 */
class SetXmasState : DeveloperCommand() {
    override fun execute(player: Player, command: String?, parts: Array<out String>) {

        val target = if(parts.size > 2) {
            val name = command!!.substring(parts[0].length + 1)
            World.findPlayerByName(name).orElseGet(null)
        } else player

        if(target == null){
            player.sendMessage("Could not find that player online!")
            return
        }

        DialogueBuilder(DialogueType.OPTION)
                .addOptions(*Christmas2022State.values().map {
                    Pair(it.name.toLowerCase().capitalize().replace("_", " "), Consumer<Player> { player ->
                        target.setState(it)
                        target.packetSender.sendInterfaceRemoval()
                    })
                }.toTypedArray())
                .start(player)
    }

}