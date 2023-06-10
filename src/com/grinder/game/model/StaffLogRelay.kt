package com.grinder.game.model

import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.nameAndQuantity
import com.grinder.game.model.item.price
import com.grinder.util.time.SecondsTimer
import java.util.concurrent.atomic.AtomicInteger

/**
 * A simple system that caches staff logs, merges duplicates ones,
 * and broadcasts them to relevant staff ranks after a specific interval.
 *
 * @author Stan van der Bend
 */
object StaffLogRelay {

    /**
     * Amount of seconds between each broadcast of all cached logs,
     * after each broadcast all cached logs are cleared.
     */
    private const val PRINT_DELAY = 1

    private val timer = SecondsTimer(PRINT_DELAY)
    private val itemLogs = HashMap<StaffLogType, HashMap<String, ArrayList<Item>>>()
    private val rawLogs = HashMap<StaffLogType, HashMap<String, HashMap<String, AtomicInteger>>>()

    /**
     * Saves string [log]s for the argued [username] of [logType].
     */
    fun save(logType: StaffLogType, username: String, log: String){
        rawLogs.putIfAbsent(logType, HashMap())
        val userLogs = rawLogs[logType]!!
        userLogs.putIfAbsent(username, HashMap())
        val rawLogs = userLogs[username]!!
        rawLogs.putIfAbsent(log, AtomicInteger(0))
        rawLogs[log]!!.incrementAndGet()
    }

    /**
     * Saves [item] logs for the argued [username] of [logType].
     */
    fun save(logType: StaffLogType, username: String, item: Item) {
        itemLogs.putIfAbsent(logType, HashMap())
        val userLogs = itemLogs[logType]!!
        userLogs.putIfAbsent(username, ArrayList())
        val loggedItems = userLogs[username]!!
        var merged = false
        for(previousLoggedItem in loggedItems){
            if(previousLoggedItem.id == item.id){
                previousLoggedItem.amount += item.amount
                merged = true
                break
            }
        }
        if(!merged) loggedItems.add(item.clone())
    }

    /**
     * Notifies online staff of new logs.
     */
    fun broadcastLogs(){
        if(timer.finished()) {
            itemLogs.forEach { (logType, userLogs) ->
                val prefix = getPrefix(logType)
                userLogs.forEach { (userName, items) ->
                    for (item in items) {
                        val message =  prefix + " $userName - ${item.nameAndQuantity()} - price: ${item.price()}"
                        if(logType.mediumStaffOnly)
                            PlayerUtil.broadcastPlayerMediumStaffMessage(message)
                        else
                            PlayerUtil.broadcastPlayerStaffMessage(message)
                    }
                }
            }
            itemLogs.clear()
            rawLogs.forEach { (logType, userLogs) ->
                val prefix = getPrefix(logType)
                userLogs.forEach { (userName, rawLogs) ->
                    for (rawLog in rawLogs) {
                        val message = prefix + " $userName - ${rawLog.key} @bla@(${rawLog.value.get()}x)"
                        if(logType.mediumStaffOnly)
                            PlayerUtil.broadcastPlayerMediumStaffMessage(message)
                        else
                            PlayerUtil.broadcastPlayerStaffMessage(message)
                    }
                }
            }
            rawLogs.clear()
            timer.start(PRINT_DELAY)
        }
    }

    private fun getPrefix(logType: StaffLogType) = "<img=750>@bla@[@cya@$logType@bla@]:@yel@ "

    enum class StaffLogType(val mediumStaffOnly: Boolean = true) {
        PICKUP,
        DROP,
        DUELING,
        TRADING(false),
        ITEM_ON_ITEM,
        INVALID_KILL,
        KILL(false),
        LOCKED,
        ALCH,
        SHOP,
        SHOP_PROFIT(false)
    }
}