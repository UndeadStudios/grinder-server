package com.grinder.game.service.search.droptable

import com.grinder.game.World
import com.grinder.game.content.pvm.ItemDropFinderInterface
import com.grinder.game.definition.ItemDefinition
import com.grinder.game.definition.NpcDefinition
import com.grinder.game.definition.NpcDropDefinition
import com.grinder.game.service.search.SearchService
import com.grinder.util.Misc
import com.grinder.util.TextUtil
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   13/12/2019
 * @version 1.0
 */
class SearchDropTableWorker(private val searchService: SearchService) : Runnable {

    private val searchMap = TreeMap<String, Pair<NpcDefinition, NpcDropDefinition>>()
    private val minimalList = ArrayList<NpcDropTable>()

    init {
        NpcDropDefinition.nameDefinitionAlphabetical.forEach {
            val npcName = it.key?.toLowerCase() ?: return@forEach
            val npcDefinition = it.value ?: return@forEach
            val dropTableDefinition = NpcDropDefinition.getName(npcName)?.get() ?: return@forEach
            searchMap[npcName] = Pair(npcDefinition, dropTableDefinition)
        }
        searchMap.forEach { entry ->
            val dropDefinition = entry.value.second
            val itemNames = dropDefinition.allDrops
                    .mapNotNull { it?.itemId?.let { itemId -> ItemDefinition.forId(itemId) }?.name?.toLowerCase() }
            minimalList.add(NpcDropTable(entry.key, itemNames, dropDefinition))
        }
    }

    override fun run() {
        while(true){

            val request = searchService.dropTableRequests.take()

            val start = System.nanoTime()

            val input = request.input.toLowerCase().trim()
            val type = request.type

            val candidates = LinkedHashMap<Int, HashSet<NpcDropTable>>()

            var totalNamesCount = 0

            val distanceCache = HashMap<String, Int>()

            for(table in minimalList){

                if(totalNamesCount > 100)
                    break

                val npcName = table.npcName

                if(type == SearchDropTableType.NPC_DROP_TABLE_BY_ITEM){

                    var bestDistance = Integer.MAX_VALUE

                    for (itemsName in table.itemsNames) {

                        if (itemsName.length < input.length)
                            continue

                        val distance = distanceCache.getOrPut(itemsName) { calculateBestDistance(itemsName, input) }

                        if (distance < bestDistance)
                            bestDistance = distance

                        if(bestDistance == 0)
                            break
                    }

                    if (bestDistance < 3) {
                        candidates.putIfAbsent(bestDistance, HashSet())
                        candidates[bestDistance]!!.add(table)
                        totalNamesCount++
                    }

                } else {

                    if(input.length > npcName.length)
                        continue

                    val distance = distanceCache.getOrPut(npcName) { calculateBestDistance(npcName, input) }

                    // if the distance is lesser than the maximum required distance
                    if (distance < 4) {
                        candidates.putIfAbsent(distance, HashSet())
                        candidates[distance]!!.add(table)
                        totalNamesCount++
                    }
                }
            }

            val duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)

            val results = ArrayList<NpcDropTable>()

            parseResults@for(entry in candidates.toSortedMap()){

                val sortedEntryNames = entry.value.sortedBy { it.npcName }

                for(name in sortedEntryNames) {
                    if (results.size > 100)
                        break@parseResults
                    results.add(name)
                }
            }

//            println("Search took $duration ms")

            World.submitGameThreadJob {

                val player = request.player

                player.setDropTableResults(results)

                val resultsSize = results.size
                val packetSender = player.packetSender

                for (i in 0..11) {
                    packetSender.sendInterfaceDisplayState(ItemDropFinderInterface.LIST_START_ID + i * 2, i >= resultsSize)
                    packetSender.sendInterfaceDisplayState(ItemDropFinderInterface.LIST_START_ID + i * 2 + 1, i >= resultsSize)
                }

                val hideText = input.isNotEmpty() && results.isNotEmpty()
                if (!hideText)
                    packetSender.sendString(ItemDropFinderInterface.ENTER_INPUT_TEXT_ID, if (input.isEmpty()) "Enter a search input." else "No results found.")

                packetSender.sendInterfaceDisplayState(ItemDropFinderInterface.ENTER_INPUT_TEXT_ID, hideText)
                packetSender.sendInterfaceDisplayState(ItemDropFinderInterface.LIST_SCROLL_ID, !hideText)

                packetSender.clearInterfaceText(ItemDropFinderInterface.LIST_START_ID, ItemDropFinderInterface.LIST_START_ID + 200)

                for ((index, table) in results.withIndex()) {
                    packetSender.sendString(ItemDropFinderInterface.LIST_START_ID + index * 2, Misc.formatText(table.npcName), true)
                    packetSender.sendInterfaceDisplayState(ItemDropFinderInterface.LIST_START_ID + index * 2 + 1, false)
                }

                packetSender.sendScrollbarHeight(ItemDropFinderInterface.LIST_SCROLL_ID, 219.coerceAtLeast(resultsSize * 20))
                packetSender.sendInterfaceScrollReset(ItemDropFinderInterface.LIST_SCROLL_ID)
            }

        }
    }

    private fun calculateBestDistance(name: String, input: String): Int {
        var distance = calculateDistance(name, input)

        if(distance == 0)
            return distance

        if (name.contains(" ")) {
            val splitName = name.split(" ")
            var bestDistance = Integer.MAX_VALUE
            for (part in splitName) {
                calculateDistance(part, input).let { partDistance ->
                    if (partDistance < bestDistance)
                        bestDistance = partDistance
                }
            }
            if (bestDistance < distance)
                distance = bestDistance
        }
        return distance
    }

    private fun calculateDistance(name: String, input: String): Int {
        val nameStartsWithInput = name.startsWith(input)
        val nameContainsInput = name.contains(input)

        // in case of the name being a likely match, set the distance to 0 or otherwise use an algorithmic evaluation.
        return when {
            nameStartsWithInput -> 0
            nameContainsInput -> 1
            else -> TextUtil.calculateLevensteinDistance(name, input)
        }
    }

    class NpcDropTable(val npcName: String, val itemsNames: List<String>, val dropDefinition: NpcDropDefinition)

}