package com.grinder.game.content.`object`

import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.skill.skillable.impl.Thieving
import com.grinder.game.content.task_new.DailyTask
import com.grinder.game.content.task_new.PlayerTaskManager
import com.grinder.game.content.task_new.WeeklyTask
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.passedTimeGenericAction
import com.grinder.game.model.Animation
import com.grinder.game.model.ObjectActions
import com.grinder.game.model.attribute.AttributeManager
import com.grinder.game.model.item.Item
import com.grinder.game.model.onFirstObjectAction
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import com.grinder.util.Logging
import com.grinder.util.Misc
import com.grinder.util.ObjectID
import kotlin.random.Random

/**
 * Handles various chests in the game.
 *
 * TODO: port remaining chests in ObjectActionPacketListener
 *
 * @author Stan van der Bend
 */
object Chests {

    init {

        onFirstObjectAction(
                ObjectID.LOCKED_OGRE_CHEST,
                ObjectID.LOCKED_CHEST,
                ObjectID.CLOSED_CHEST,
                ObjectID.CLOSED_CHEST_2,
                ObjectID.CLOSED_CHEST_3,
                ObjectID.CLOSED_CHEST_4,
                ObjectID.CLOSED_CHEST_5,
                ObjectID.CLOSED_CHEST_6,
                ObjectID.CLOSED_CHEST_7,
                ObjectID.CLOSED_CHEST_8,
                ObjectID.CLOSED_CHEST_9,
                ObjectID.CLOSED_CHEST_10,
                ObjectID.CLOSED_CHEST_11,
                ObjectID.CLOSED_CHEST_12,
                ObjectID.CLOSED_CHEST_13,
                ObjectID.CLOSED_CHEST_14,
                ObjectID.CLOSED_CHEST_15,
                ObjectID.CLOSED_CHEST_16,
                ObjectID.CLOSED_CHEST_17,
                ObjectID.CLOSED_CHEST_18,
                ObjectID.CLOSED_CHEST_19,
                ObjectID.CLOSED_CHEST_20,
                ObjectID.CLOSED_CHEST_21,
                ObjectID.CLOSED_CHEST_22,
                ObjectID.CLOSED_CHEST_23,
                ObjectID.CLOSED_CHEST_24,
                ObjectID.CLOSED_CHEST_25,
                ObjectID.CLOSED_CHEST_26,
                ObjectID.CLOSED_CHEST_27,
                ObjectID.CLOSED_CHEST_28,
                ObjectID.CLOSED_CHEST_29,
                ObjectID.CLOSED_CHEST_30,
                ObjectID.CLOSED_CHEST_31,
                ObjectID.CLOSED_CHEST_32,
                ObjectID.CHEST_14,
                ObjectID.CHEST_55
        ) {
            handleClosedChest(it)
        }

        // muddy chest
        onFirstObjectAction(ObjectID.CLOSED_CHEST_2) {
                val player = it.player
            if (player.passedTimeGenericAction()) {

                if (failedToOpenChest(player, 3, "muddy", Item(991, 1)))
                    return@onFirstObjectAction

                handleMuddyChest(player)
            }
        }

        // crystal key chest
        onFirstObjectAction(ObjectID.CLOSED_CHEST_3) {

            val player = it.player

            if (player.passedTimeGenericAction()) {

                if (failedToOpenChest(player, 4, "crystal", Item(989, 1)))
                    return@onFirstObjectAction

                handleCrystalChest(player)
            }
        }
    }

    fun handleClosedChest(it: ObjectActions.ClickAction) {
        val player = it.player
        if (player.passedTimeGenericAction()) {
            player.message("The chest is locked.")
            player.playSound(Sounds.USE_KEY_ON_LOCKED_DOOR)
        }
    }

    private fun handleCrystalChest(player: Player) {
        if (!player.gameMode.isSpawn)
        Logging.log("crystalChest", "[CrystalChest]: " + player.username + " opened a crystal chest.")

        openChest(player)
        player.block()

        val item1 = Item(Thieving.CRYSTAL_CHEST_LOOT.random(), 1)
        val item2 = Item(if (Misc.randomChance(25F))
            Thieving.CRYSTAL_CHEST_LOOT.random()
        else
            Thieving.NOOBISH_ITEMS.random(), 1)
        val coins = Item(ItemID.COINS, Random.nextInt(150_000, 650_000))

        player.addInventoryItem(item1, -1)
        player.addInventoryItem(item2, -1)
        player.addInventoryItem(coins, 2)

        TaskManager.submit(2) {
            player.unblock()

            AchievementManager.processFor(AchievementType.CRYSTAL_MASTER, player)
            AchievementManager.processFor(AchievementType.CRYSTAL_EXPERT, player)
            AchievementManager.processFor(AchievementType.INITIAL_CRYSTAL, player)

            player.message("You unlock the chest with your key.")
            openRewardsInterface(player, item1, item2, coins)

            player.message("You find some treasure in the chest.")

            // Send Total Completed
            player.points.increase(AttributeManager.Points.CRYSTAL_CHESTS_OPENED, 1) // Increase points
//            TaskList.ProgressTask(player, 9, 15)
            PlayerTaskManager.progressTask(player, DailyTask.CRYSTAL_CHEST)
            PlayerTaskManager.progressTask(player, WeeklyTask.CRYSTAL_CHEST)
            player.sendMessage("You have opened the crystal chest " + player.points.get(AttributeManager.Points.CRYSTAL_CHESTS_OPENED) +" times.")

        }
    }

    private fun handleMuddyChest(player: Player) {
        if (!player.gameMode.isSpawn)
        Logging.log("MuddyChest", "[MuddyChest]: " + player.username + " opened a muddy chest.")
        openChest(player)
        player.block()

        val resources = Item(Thieving.PVP_RESOURCES_ITEMS.random(), Random.nextInt(25, 60))
        val bloodMoney = Item(ItemID.BLOOD_MONEY, Random.nextInt(2500, 5000))
        val luckyItem = if (Random.nextBoolean())
            Item(Thieving.MUDDY_CHEST_ITEMS.random(), 1)
        else
            null

        player.addInventoryItem(bloodMoney, -1)
        player.addInventoryItem(resources, if (luckyItem == null) 2 else -1)
        if (luckyItem != null)
            player.addInventoryItem(luckyItem, 2)

        TaskManager.submit(2) {
            player.unblock()
            AchievementManager.processFor(AchievementType.ALL_MUD, player)
            AchievementManager.processFor(AchievementType.MUDDY_WORK, player)

            player.message("You unlock the chest with your key.")
            openRewardsInterface(player, bloodMoney, resources, luckyItem)

            player.message("You find some treasure in the chest.")

            // Send Total Completed
            player.points.increase(AttributeManager.Points.MUDDY_CHESTS_OPENED, 1) // Increase points
            PlayerTaskManager.progressTask(player, DailyTask.MUDDY_CHEST);
            PlayerTaskManager.progressTask(player, WeeklyTask.MUDDY_CHEST);
            player.sendMessage("You have opened the muddy chest " + player.points.get(AttributeManager.Points.MUDDY_CHESTS_OPENED) +" times.")


        }
    }

    private fun openRewardsInterface(player: Player, item1: Item, item2: Item, item3: Item?) {
        player.packetSender.sendItemOnInterface(6963, item1.id, 0, item1.amount)
        player.packetSender.sendItemOnInterface(6963, item2.id, 1, item2.amount)
        player.packetSender.sendItemOnInterface(6963, item3?.id ?: -1, 2, item3?.amount ?: 0)
        player.packetSender.sendItemOnInterface(6963, -1, 3, -1)
        player.packetSender.sendItemOnInterface(6963, -1, 4, -1)
        player.packetSender.sendItemOnInterface(6963, -1, 5, -1)
        player.packetSender.sendItemOnInterface(6963, -1, 6, -1)
        player.openInterface(6960)

    }

    private fun openChest(player: Player) {
        player.performAnimation(Animation(536))
        player.playSound(Sounds.OPEN_BANK_BOOTH)
    }

    private fun failedToOpenChest(player: Player, requiredSlots: Int, chestType: String, key: Item): Boolean {
        if (player.inventory.countFreeSlots() < requiredSlots) {
            player.message("You need at least $requiredSlots free inventory slots to open the $chestType chest.")
            return true
        }

        if (!player.removeInventoryItem(key)) {
            player.message("The $chestType chest is locked.")
            player.playSound(Sounds.USE_KEY_ON_LOCKED_DOOR)
            return true
        }
        return false
    }

}