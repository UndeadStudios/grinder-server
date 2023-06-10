package com.grinder.game.model.commands.impl

import com.grinder.game.content.cluescroll.task.ClueTask
import com.grinder.game.content.cluescroll.task.ClueTaskFactory
import com.grinder.game.content.cluescroll.task.impl.PerformEmoteClueTask
import com.grinder.game.content.miscellaneous.Emotes
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.commands.DeveloperCommand
import com.grinder.game.model.interfaces.dialogue.*
import com.grinder.game.model.interfaces.syntax.EnterSyntax
import com.grinder.game.model.item.Item
import com.grinder.util.Misc
import com.grinder.util.TextUtil
import java.util.*
import java.util.function.Consumer
import kotlin.collections.HashMap
import kotlin.collections.set

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   10/01/2020
 * @version 1.0
 */
class TestClueCommand : DeveloperCommand() {

    override fun execute(player: Player, command: String?, parts: Array<out String>?) {

        DialogueBuilder(DialogueType.OPTION)
                .firstOption("Search clues") {
                    it.enterSyntax = object : EnterSyntax {
                        override fun handleSyntax(player: Player, input: String) {
                            val searchTerm = input.trim().toLowerCase()
                            val nameCluePairs = ClueTaskFactory.getInstance().taskMap.values.filter { task ->
                                task?.clueScroll?.clueGuide?.description?.get(0) != null
                            }.map {task ->
                                Pair(task.clueScroll.clueGuide.description!![0].toLowerCase(), task)
                            }.iterator()
                            val candidates = LinkedHashMap<Int, HashSet<Pair<String, ClueTask>>>()

                            // iterate over all npc drop definition names.
                            while (nameCluePairs.hasNext()) {

                                val pair = nameCluePairs.next()
                                val name = pair.first
                                val task = pair.second

                                val nameStartsWithInput = name.startsWith(searchTerm)
                                val nameContainsInput = name.contains(searchTerm)
                                val likelyMatch = nameStartsWithInput || nameContainsInput

                                // in case of the name being a likely match, set the distance to 0 or otherwise use an algorithmic evaluation.
                                val distance = if (likelyMatch) 0 else TextUtil.calculateLevensteinDistance(name, searchTerm)

                                // if the distance is lesser than the maximum required distance
                                if (distance < 5) {
                                    candidates.putIfAbsent(distance, HashSet())
                                    candidates[distance]!!.add(pair)
                                }
                                // in the case of the distance being 0 and the possible candidates have reached the maximum, exit the loop.
                                if (distance == 0 && candidates[0]!!.size == 4)
                                    break
                            }

                            if (candidates.isNotEmpty()) {
                                val builder = DialogueBuilder(DialogueType.OPTION)
                                val map = HashMap<String, Consumer<Player>>()
                                candidates.forEach { (_, weightedNames) ->
                                    weightedNames.forEach {
                                        val name = it.first.capitalize()
                                        val task = it.second
                                        map[name] = Consumer {
                                            if (task.difficulty != null) {
                                                if (!player.inventory.contains(task.difficulty.scrollID))
                                                    player.inventory.add(task.difficulty.scrollID, 1)
                                                ClueTaskFactory.getInstance().setTask(player, task.difficulty, task)
                                            }
                                        }
                                    }
                                }
                                builder.addOptions(*map.toList().toTypedArray())
                                builder.addCancel()
                                builder.start(player)
                            } else
                                player.sendMessage("No clues found for: @dre@" + Misc.capitalize(searchTerm) + "</col>!")
                        }

                        override fun handleSyntax(player: Player, input: Int) {}
                    }
                    it.packetSender.sendEnterInputPrompt("Write a part of the description..")
                }
                .secondOption("Get equipment") { p ->
                    p.clueScrollManager.findTask().ifPresent {
                        it.requiredEquipments.forEach { item ->
                            p.inventory.add(Item(item.id, item.amount), false)
                        }
                        p.inventory.refreshItems()
                    }
                }.thirdOption("View emote") { p ->
                    p.clueScrollManager.findTask().ifPresent {
                        if(it is PerformEmoteClueTask)
                            p.performAnimation(Emotes.EmoteData.values()[it.emoteOrdinal].animation)
                    }
                }.start(player)

    }

}