package com.grinder.util.tools

import com.grinder.game.definition.ItemDefinition
import com.grinder.game.definition.ItemValueDefinition
import com.grinder.game.definition.ItemValueType
import com.grinder.game.definition.loader.impl.ItemDefinitionLoader
import com.grinder.game.entity.agent.player.PlayerLoading
import com.grinder.game.model.item.Item
import com.grinder.util.Logging
import com.grinder.util.Misc
import java.nio.file.Files
import java.nio.file.Paths
import java.text.NumberFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap
import java.util.Calendar
import java.util.GregorianCalendar



/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   2019-04-19
 * @version 1.0
 */
object EconomyInfluxScanner {

    private val usePlayerFilesForComparisonOfWealth = true

    private val calendar = GregorianCalendar()

    private val loadPath = Paths.get("data", "logs", "trades")!!
    private val entriesForPlayer = HashMap<String, EconomicStatusSnapshot>()

    private val timePattern = Pattern.compile("\\[(.*?)\\]")!!
    private val dataPattern = Pattern.compile("'(.*?)' (.*?) (.*?) x (.*?) to '(.*?)'")!!// <user_name> <action> <item_amount> <item_name> <target_name>

    @JvmStatic
    fun main(args: Array<String>){

        ItemDefinitionLoader().run()

        println("Welcome to the Economy Influx Scanner (by Stan van der Bend)")
        println()


        scanInput()
    }

    private fun scanInput() {

        println("Please enter how many days back you want to search:")

        val daysBack = readLine()?.toIntOrNull()
        if(daysBack == null){
            println("Please enter an integer value (e.g. 3)")
            scanInput()
            return
        }
        calendar.add(Calendar.DAY_OF_MONTH, -daysBack)
        val startDate = calendar.time

        println("You set the date from which to scan logs to ${Logging.DATE_FORMAT.format(startDate)}")

        println("Please enter the value threshold for transaction searches:")

        val threshold = readLine()?.toIntOrNull()

        if(threshold == null){
            println("Please enter an integer value (e.g. 3000)")
            scanInput()
            return
        }

        println("You set the threshold to ${Misc.insertCommasToNumber(threshold.toString())}")

        val transactionsByDate = HashMap<Date, HashMap<Date, LinkedList<EconomicTransaction>>>()


        for (file in Files.list(loadPath)) {

            val date = Logging.DATE_FORMAT.parse(file.fileName.toString().removeSuffix(Logging.SUFFIX))

            if(date.before(startDate))
                continue

            if (date == null) {
                System.err.println("Could not parse file " + file.fileName)
                continue
            }

            val timedTransactions = HashMap<Date, LinkedList<EconomicTransaction>>()

            var failureLineParseCount = 0

            transactionsByDate.putIfAbsent(date, timedTransactions)

            for (line in Files.readAllLines(file)) {

                val timeMatcher = timePattern.matcher(line)

                if (timeMatcher.find()) {

                    val time = Logging.TIME_FORMAT.parse(timeMatcher.group(1))

                    val dataMatcher = dataPattern.matcher(line)

                    if (dataMatcher.find()) {

                        val userNameOne = dataMatcher.group(1)
                        val action = dataMatcher.group(2)
                        val itemAmount = dataMatcher.group(3)
                        val itemName = dataMatcher.group(4)
                        val userNameTwo = dataMatcher.group(5)

                        if(usePlayerFilesForComparisonOfWealth) {

                            val entryOne = EconomicStatusSnapshot(userNameOne, PlayerLoading.getInventoryItems(userNameOne), PlayerLoading.getBankItems(userNameOne))
                            val entryTwo = EconomicStatusSnapshot(userNameTwo, PlayerLoading.getInventoryItems(userNameTwo), PlayerLoading.getBankItems(userNameTwo))

                            entriesForPlayer.putIfAbsent(userNameOne, entryOne)
                            entriesForPlayer.putIfAbsent(userNameTwo, entryTwo)

                        }

                        val transactionType = if (action == "give") TransactionType.GIVE else TransactionType.RECEIVE
                        val transaction = EconomicTransaction(transactionType, userNameOne, userNameTwo, itemName, NumberFormat.getIntegerInstance(Locale.US).parse(itemAmount).toInt())

                        timedTransactions.putIfAbsent(time, LinkedList())
                        timedTransactions[time]?.add(transaction)?:continue

                    } else
                        failureLineParseCount++

                } else
                    failureLineParseCount++

            }

            if (failureLineParseCount > 0)
                println("Failed to parse $failureLineParseCount lines in file ${file.fileName}")
        }


        val results = mapToPlayer(transactionsByDate)


        results.forEach { player, datedTransactions ->
            run {

                println("Printing transactions for player $player")

                val wealth = entriesForPlayer[player]?.calculateValue()?:0L

                var totalWealthTraded = 0L

                for((date, timedTransactions) in datedTransactions){

                    for ((time, transactions) in timedTransactions) {
                        var totalValue = 0L

                        for((index, transaction) in transactions.withIndex()){
                            totalValue += transaction.calculateValue()
                            if(index > 0){
                                val previous = transactions[index-1]
                                if(previous.itemName == transaction.itemName){
                                    transaction.itemAmount += previous.itemAmount
                                    previous.itemAmount = 0
                                }
                            }
                        }

                        totalWealthTraded+= totalValue

                        transactions.removeIf { it.itemAmount == 0 }

                        if(totalValue > threshold){

                            println("\t[${Logging.DATE_FORMAT.format(date)}]: total value exceeds threshold: ${Misc.insertCommasToNumber(totalValue.toString())} > ${Misc.insertCommasToNumber(threshold.toString())}")

                            for(transaction in transactions){

                                if(transaction.itemAmount > 0) {
                                    val percentage =  (100.0f *transaction.calculateValue() / wealth.toDouble()).toFloat()
                                    println("\t\t[${Logging.TIME_FORMAT.format(time)}]: $transaction ")
                                    if(usePlayerFilesForComparisonOfWealth)
                                        print("\t(% wealth = $percentage)")
                                }
                            }
                        }
                    }
                }

                if(totalWealthTraded > 0)
                    println("Total wealth traded by player $player is ${Misc.insertCommasToNumber(totalWealthTraded.toString())}")
            }
        }

    }

    private fun mapToPlayer(snapshotsByDate : HashMap<Date, HashMap<Date, LinkedList<EconomicTransaction>>>) : HashMap<String, HashMap<Date, HashMap<Date, LinkedList<EconomicTransaction>>>> {
        val playerEntries = HashMap<String, HashMap<Date, HashMap<Date, LinkedList<EconomicTransaction>>>>()
        for (entry in snapshotsByDate) {
            for (entry1 in entry.value) {
                for (snapshot in entry1.value) {
                    val main = snapshot.wealthAffected()

                    playerEntries.putIfAbsent(main, HashMap())
                    playerEntries[main]?.putIfAbsent(entry.key, HashMap()) ?: continue
                    playerEntries[main]?.get(entry.key)?.putIfAbsent(entry1.key, LinkedList()) ?: continue
                    playerEntries[main]?.get(entry.key)?.get(entry1.key)?.add(snapshot) ?: continue
                }
            }
        }
        return playerEntries
    }

    class EconomicStatusSnapshot(val username: String, val inventoryItems : List<Item>, val bankItems : List<Item>){

        fun calculateValue() : Long {
            var value = 0L

            for(item in inventoryItems)
                value += if(item.id == 995) item.amount.toLong() else (item.amount * item.getValue(ItemValueType.PRICE_CHECKER))

            for(item in bankItems)
                value += if(item.id == 995) item.amount.toLong() else (item.amount * item.getValue(ItemValueType.PRICE_CHECKER))

            return value
        }

        override fun toString(): String {
            return "Snapshot(in_inventory=${inventoryItems.count()}, in_bank=${bankItems.count()}, total_value=${Misc.insertCommasToNumber(calculateValue().toString())})"
        }
    }

    class EconomicTransaction(val type: TransactionType, val userNameOne : String, private val userNameTwo: String, val itemName: String, var itemAmount: Int) {

        fun calculateValue() : Long {

            if(itemName == "Coins")
                return itemAmount.toLong()

            val definition = ItemDefinition.forName(itemName)?:return 0

            return ItemValueDefinition.getValue(definition.id, ItemValueType.PRICE_CHECKER) * itemAmount
        }

        fun wealthAffected(): String{
            return if(type == TransactionType.RECEIVE)
                userNameTwo
            else
                userNameOne
        }

        override fun toString(): String {
            return if(type == TransactionType.RECEIVE)
                "$userNameTwo \treceived from $userNameOne \t$itemAmount x $itemName \t = ${Misc.insertCommasToNumber(calculateValue().toString())}"
            else
                "$userNameOne \tgave to $userNameTwo \t$itemAmount x $itemName \t = ${Misc.insertCommasToNumber(calculateValue().toString())}"
        }

    }

    enum class TransactionType {
        GIVE,
        RECEIVE
    }
}