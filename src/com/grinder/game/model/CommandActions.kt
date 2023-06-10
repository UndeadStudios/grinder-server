package com.grinder.game.model

import com.grinder.game.World
import com.grinder.game.entity.agent.player.Color
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.message
import com.grinder.game.message.impl.CommandMessage
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * TODO: add documentation
 * TODO: add regex for argument extraction
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   11/09/2020
 */
object CommandActions {

    val commandActions = HashMap<Pair<String, Collection<PlayerRights>>, Triple<String, Boolean, ArrayList<(CommandAction) -> Boolean>>>()

    fun onCommand(commandName: String,
                  permittedRights: Collection<PlayerRights> = PlayerRights.HIGH_STAFF,
                  separator: String = " ",
                  ignoreCase: Boolean = true,
                  function: CommandAction.() -> Boolean)
    {
        val pair = commandName to permittedRights
        commandActions.putIfAbsent(pair, Triple(separator, ignoreCase, ArrayList()))
        commandActions[pair]!!.third.add(function)
    }

    fun handleCommand(player: Player, commandMessage: CommandMessage) : Boolean {

        val fullCommandString = commandMessage.command

        val entry = commandActions.entries.find {
            if(!it.key.second.contains(player.rights))
                return@find false
            return@find fullCommandString.startsWith(it.key.first, ignoreCase = it.value.second)
        }?:return false

        val action = CommandAction(player,
                commandName = entry.key.first,
                commandArguments = fullCommandString.split(entry.value.first).drop(1))

        for(function in entry.value.third)
            if(function.invoke(action))
                return true

        return false
    }

    class CommandAction(val player: Player, val commandName: String, val commandArguments: List<String>) {

        fun findPlayerTarget(argumentIndices: IntRange = 0..1): Optional<Player> {

            if(commandArguments.size > argumentIndices.last){
                player.message("You did not provide enough arguments to extract a player name.", Color.MAGENTA)
                return Optional.empty()
            }

            val targetName = commandArguments.subList(argumentIndices.first, argumentIndices.last).joinToString().trim()

            if(targetName.isBlank()){
                player.message("You provided a blank string as argument, could not extract a player name.", Color.MAGENTA)
                return Optional.empty()
            }

            return World.players.searchAny {
                it?.username?.equals(targetName, true)?:false
            }
        }
    }

}
