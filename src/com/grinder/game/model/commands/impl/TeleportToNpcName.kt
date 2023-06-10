package com.grinder.game.model.commands.impl

import com.grinder.game.World
import com.grinder.game.definition.NpcDefinition
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.model.commands.Command
import com.grinder.game.model.commands.DeveloperCommand
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.util.Misc
import com.grinder.util.TextUtil
import java.util.HashSet
import java.util.LinkedHashMap
import java.util.function.Consumer

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   16/11/2019
 * @version 1.0
 */
class TeleportToNpcName : Command {

    override fun getSyntax() = "[npcName]"

    override fun getDescription() = "Teleports you to the selected NPC name."

    private val CLOSE_INTERFACE = Consumer<Player> { player -> player.getPacketSender().sendInterfaceRemoval() }
    override fun canUse(player: Player) = player.rights.anyMatch(PlayerRights.DEVELOPER, PlayerRights.OWNER, PlayerRights.CO_OWNER, PlayerRights.ADMINISTRATOR)


    override fun execute(player: Player, command: String, parts: Array<out String>) {

        val searchTerm = command.substringAfter(parts[0]).trim().toLowerCase()
        val names = World.npcs.filterNotNull().map { it.fetchDefinition()?.name?.toLowerCase() }.filterNotNull().iterator()
        val candidates = LinkedHashMap<Int, HashSet<String>>()

        // iterate over all npc drop definition names.
        while (names.hasNext()) {

            val name = names.next()

            val nameStartsWithInput = name.startsWith(searchTerm)
            val nameContainsInput = name.contains(searchTerm)
            val likelyMatch = nameStartsWithInput || nameContainsInput

            // in case of the name being a likely match, set the distance to 0 or otherwise use an algorithmic evaluation.
            val distance = if (likelyMatch) 0 else TextUtil.calculateLevensteinDistance(name, searchTerm)

            // if the distance is lesser than the maximum required distance
            if (distance < 5) {
                candidates.putIfAbsent(distance, HashSet())
                candidates[distance]!!.add(name)
            }
            // in the case of the distance being 0 and the possible candidates have reached the maximum, exit the loop.
            if (distance == 0 && candidates[0]!!.size == 4)
                break
        }
        if (candidates.size >= 1) {

            val builder = DialogueBuilder(DialogueType.OPTION)
            var page = builder
            var previousPage = builder
            page.setOptionTitle("Did you mean:")

            var optionsCount = 0

            for (i in 0 until 10) {

                val weightedNames = candidates[i] ?: continue

                if (optionsCount + 1 == 5) {
                    break
                }

                for (name in weightedNames) {

                    if (optionsCount + 1 == 5) {
                        break
                    }

                    val npcDefinition = NpcDefinition.forName(name) ?: continue

                    builder.option(optionsCount++, Misc.capitalizeWords(name), CLOSE_INTERFACE.andThen { futurePlayer ->
                        World.npcs.searchAny {
                            it?.fetchDefinition()?.name == npcDefinition.name
                        }.ifPresent {
                            player.moveTo(it.position.clone())
                        }
                    })
                }
            }
            builder.addCancel()
            builder.start(player)
        } else
            player.sendMessage("No monster found for: @dre@" + Misc.capitalize(searchTerm) + "</col>!")
    }

}