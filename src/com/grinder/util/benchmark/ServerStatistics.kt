package com.grinder.util.benchmark

import com.grinder.util.SmartLogger
import java.nio.file.Paths
import java.util.*
import kotlin.collections.HashMap

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   04/12/2019
 * @version 1.0
 */
fun main(){


    val itemDropFiles = Paths.get("/Users/stanbend/IdeaProjects/GrinderScapeSource/data/logs/itemdrops").toFile().listFiles()!!

    println("Enter how many days back u want to read logs from and press enter.")

    val daysBack = readLine()!!.toInt()
    val calendar = Calendar.getInstance()!!

    calendar.add(Calendar.DAY_OF_MONTH, -daysBack)

    val droppedItemMap = HashMap<String, Int>()

    itemDropFiles.forEach {
        val fileDate = SmartLogger.DATE_FORMAT.parse(it.nameWithoutExtension)

        if(fileDate.after(calendar.time)){
            println("Scanning logs in ${it.name}")
            it.readLines().forEach { line ->
                val droppedItemData = line.split("dropped:")[1].split('x')
                val droppedAmount = droppedItemData[0].trim().replace(",", "").toInt()
                val droppedItemName = droppedItemData[1].trim()
                if(droppedItemMap.containsKey(droppedItemName))
                    droppedItemMap[droppedItemName] = droppedItemMap[droppedItemName]!! + droppedAmount
                else
                    droppedItemMap.putIfAbsent(droppedItemName, droppedAmount)
            }
        }
    }

    droppedItemMap.forEach { (t, u) ->
        println("$t \t\t dropped amount: $u")
    }

}