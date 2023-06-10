package com.grinder.util.tools

import com.grinder.game.GameConstants
import com.grinder.game.definition.loader.impl.ItemDefinitionLoader
import com.grinder.game.definition.loader.impl.ItemValueDefinitionLoader
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerLoading
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.game.model.item.container.bank.BankUtil
import com.grinder.util.Logging
import com.grinder.util.Misc
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private val calendar = GregorianCalendar()
val players = HashMap<String, Player>()
val playerLogins = HashMap<String, ArrayList<LoginEntry>>()

class LoginEntry(val time: String, val name: String, val ip: String, val mac: String, val date: Date)
fun main() {
    ItemDefinitionLoader().load()
    ItemValueDefinitionLoader().load()

    scanInput()
}

private fun scan() {
    val mcGroup = players.values.groupBy { it.macAddress }
    val snGroup = players.values.groupBy { it.snAddress }
    val ipGroup = HashMap<String, ArrayList<Player>>()
    for (entry in players.entries) {
        entry.value.recentIPS.forEach {
            ipGroup.getOrPut(it) {ArrayList()}.add(entry.value)
        }
    }
    val serialGroup = players.values.groupBy { it.hdSerialNumber }
    val passGroup = players.values.groupBy { it.password }
    val pinGroup = players.values.groupBy { it.pin }

    println("Enter suspicious name:")
    val suspiciousName = Misc.formatName(readLine())

    val player = players[suspiciousName]

    if (player == null) {
        println("Player for name '$suspiciousName' was not found!")
        scan()
        return
    }

    println("-------------------------PLAYER DETAILS---------------------------")
    println("play time\t = ${PlayerUtil.sendPlayTime(player)}")
    println("value in bank\t = ${Misc.formatWithAbbreviation(BankUtil.determineValueInBankOf(player))}")
    println("value in inventory\t = ${Misc.formatWithAbbreviation(ItemContainerUtil.determineValueOfContents(player.inventory))}")
    println("value in equipment\t = ${Misc.formatWithAbbreviation(ItemContainerUtil.determineValueOfContents(player.equipment))}")
    println("--------------------------SCAN REPORT-----------------------------")
    val mac = player.macAddress
    val sn = player.snAddress
    val lastIps = player.recentIPS
    val serial = player.hdSerialNumber
    val pass = player.password
    val pin = player.pin

    if (mac != null) {
        println("-> mac: $mac")
        println("\t${mcGroup[mac]?.joinToString { it.username }}")
    }
    if (sn != null) {
        println("-> sn: $sn")
        println("\t${snGroup[sn]?.joinToString { it.username }}")
    }
    if (serial != null) {
        println("-> serial: $serial")
        println("\t${serialGroup[serial]?.joinToString { it.username }}")
    }
    if (pass != null) {
        println("-> pass: $pass")
        println("\t${passGroup[pass]?.joinToString { it.username }}")
    }

    println("-> pin: $pin")
    println("\t${pinGroup[pin]?.joinToString { it.username }}")

    println("-> scanning IPs")
    if (!lastIps.isNullOrEmpty()) {
        lastIps.forEach {ip ->
            println("\tip: $ip")
            println("\t\t${ipGroup[ip]?.joinToString { it.username }}")
        }
    }
    val hourMap = HashMap<Int, AtomicInteger>()

    for(login in playerLogins[player.username]?:return){
        val split = login.time.split(":")
        val hours = split[0].toInt()
        val minutes = split[1].toInt()
        val seconds = split[2].toInt()
        hourMap.getOrPut(hours) { AtomicInteger(0) }
                .addAndGet(1)
    }

    val bestTree = ArrayList<Int>()

    for (entry in hourMap.entries.sortedByDescending { it.value.get() }) {
        val hour = entry.key
        val count = entry.value
        if(bestTree.size < 3){
            bestTree.add(hour)
            println("\t #${bestTree.size} first login of the day happened ${count.get()} times in period [${formatHour(hour)}:00:00 to ${formatHour(hour+1)}:00:00]")
        }
    }

    val similiarLoginTimes = ArrayList<String>()

    for (entry in playerLogins) {
        val otherHourMap = HashMap<Int, AtomicInteger>()
        entry.value.forEach {
            val split = it.time.split(":")
            val hours = split[0].toInt()
            otherHourMap.getOrPut(hours) { AtomicInteger(0) }
                    .addAndGet(1)
        }

        var similarHourCount = 0
        for (other in otherHourMap.entries.sortedByDescending { it.value.get() }) {
            val hour = other.key
            if(bestTree.contains(hour)){
                similarHourCount++
            }
        }

        if(similarHourCount > 2){
            similiarLoginTimes.add(entry.key)
        }
    }
    println("Top 3 hours of login has matches with ${similiarLoginTimes.sorted().joinToString()}")
    println("------------------------------------------------------------------")
    scan()
}

private fun formatHour(hour: Int) = (hour).let { if (it < 10) "0$it" else it.toString() }

private fun scanInput() {

    println("Please enter how many days back you want to search:")

    val daysBack = readLine()?.toIntOrNull()
    if (daysBack == null) {
        println("Please enter an integer value (e.g. 3)")
        scanInput()
        return
    }
    calendar.add(Calendar.DAY_OF_MONTH, -daysBack)
    val startDate = calendar.time

    println("You set the date from which to scan logs to ${Logging.DATE_FORMAT.format(startDate)}")

    println("Parsing daily login logs...")
    val dateFormat = SimpleDateFormat("yyyy-mm-dd")
    val today = Date()
    for (file in Paths.get("data/logs/DailyLoginRewards").toFile().listFiles()) {

        val d = Logging.DATE_FORMAT.parse(file.nameWithoutExtension)

        if(d.after(startDate)) {
            println("\t -> including $d")
            file.readLines().map {
                val time = it.substringAfter("[").substringBefore("]")
                val name = it.substringAfter("]").substringBeforeLast("received").trim()
                val ip = it.substringAfter("IP:").substringBeforeLast("and").trim()
                val mac = it.substringAfter("MAC:").trim()
                playerLogins.getOrPut(name) {
                    ArrayList()
                }.add(LoginEntry(time, name, ip, mac, d))
            }
        }
    }
    println("\t -> parsed ${playerLogins.size} unique player log entries list")

    println("Parsing player files...")
    val time = System.currentTimeMillis()

    var count = 0
    for (file in Paths.get(GameConstants.PLAYER_DIRECTORY).toFile().listFiles()) {

        if (file.extension != "json")
            continue

        val diff = TimeUnit.MILLISECONDS.toDays(time - file.lastModified())
        if(diff > daysBack) {
            continue
        }

        val player = Player()
        player.username = Misc.formatName(file.nameWithoutExtension)
        PlayerLoading.getResult(player, true, true)
        //println("scanning ${player.username}")
        players[player.username] = player
    }
    scan()
}
