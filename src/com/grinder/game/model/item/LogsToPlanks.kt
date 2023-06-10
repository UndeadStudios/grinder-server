package com.grinder.game.model.item


import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.util.ItemID
import com.grinder.util.NpcID
import java.util.*

/**
 * Represents items that can be imbued.
 *
 * @param plankId    the id of the plank version of the item.
 * @param logId  the id of the logId of the item.
 * @param convertCost     the cost to imbue the item.
 */
enum class LogsToPlanks(val plankId: Int, val logId: Int, val convertCost: Int) {

    LOGS(ItemID.PLANK, ItemID.LOGS, 10_000),
    OAK_LOGS(ItemID.OAK_PLANK, ItemID.OAK_LOGS, 50_000),
    TEAK_LOGS(ItemID.TEAK_PLANK, ItemID.TEAK_LOGS, 150_000),
    MAHOGANY_LOGS(ItemID.MAHOGANY_PLANK, ItemID.MAHOGANY_LOGS, 250_000);

    companion object {

        private val convertableItems: MutableMap<Int, LogsToPlanks> = HashMap()

        /**
         * Gets the total cost of converting a player's logs.
         *
         * @param player
         * @return
         */
        @JvmStatic
        fun getTotalConvertCost(player: Player): Int {
            return values().sumBy {
                player.inventory.getAmount(it.logId) * it.convertCost
            }
        }

        /**
         * converts all logs to planks for a player.
         *
         * @param player the [Player] converting items.
         */
        @JvmStatic
        fun convertLogsToPlanks(player: Player) {
            var converted = false
            for (u in values()) {
                val amt = player.inventory.getAmount(u.logId)
                if (amt > 0) {
                    val cost = u.convertCost * amt
                    converted = if (player.inventory.getAmount(ItemID.COINS) >= cost) {
                        player.removeInventoryItem(Item(ItemID.COINS, cost), -1)
                        player.removeInventoryItem(Item(u.logId, amt), -1)
                        player.addInventoryItem(Item(u.plankId, amt), -1)
                        player.inventory.refreshItems()
                        true
                    } else {
                        DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.SAWMILL_OPERATOR)
                            .setText("You could not afford converting all your logs!").start(player);
                        break
                    }
                }
            }

            if (converted) {
                DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.SAWMILL_OPERATOR)
                    .setText("Pleasure doing business with you!").start(player);
            } else {
                player.removeInterfaces()
            }
        }

        operator fun get(originalId: Int) = convertableItems[originalId]

        init {
            for (imbuedableItems in values()) {
                convertableItems[imbuedableItems.plankId] = imbuedableItems
            }
        }
    }
}