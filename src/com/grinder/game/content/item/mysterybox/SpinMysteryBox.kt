package com.grinder.game.content.item.mysterybox

import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.passedTime
import com.grinder.game.model.ButtonActions
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.attribute.AttributeManager
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.game.model.item.container.bank.BankUtil
import com.grinder.game.model.item.isHighValued
import com.grinder.game.model.item.isLargeAmount
import com.grinder.game.model.item.nameAndQuantity
import com.grinder.game.task.TaskManager
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.max

object SpinMysteryBox {

    init {
        for (boxType in SpinMysteryBoxType.values()){
            ButtonActions.onClick(boxType.rewardsButtonId) {
                viewRewards(player, boxType)
            }
            ButtonActions.onClick(boxType.spinButtonId) {
                spin(player, boxType)
            }
        }
    }
    @JvmStatic
    fun passedSpinWaitTime(player: Player): Boolean {
        return player.passedTime(Attribute.MYSTERY_BOX_LAST_SPIN, 7, TimeUnit.SECONDS)
    }

    @JvmStatic
    fun open(player: Player, box: SpinMysteryBoxType) {

        if (!player.passedTime(Attribute.MYSTERY_BOX_LAST_SPIN, 7, TimeUnit.SECONDS, updateIfPassed = false))
            return


        val packetSender = player.packetSender

        packetSender.sendInterfaceItems(MysteryBoxConstants.SPINNER_CONTAINER_ID, box.getItems(player))

            packetSender.sendInterfaceItems(MysteryBoxConstants.SPIN_BUTTON_CONTAINER_ID, listOf(Item(box.itemId)))
            // Hide/show buttons
            for (other in SpinMysteryBoxType.values()) {
                packetSender.sendInterfaceDisplayState(other.spinButtonId, other != box)
                packetSender.sendInterfaceDisplayState(other.rewardsButtonId, other != box)
            }
            packetSender.sendString(MysteryBoxConstants.TITLE_ID, box.formattedName)
            packetSender.sendInterface(MysteryBoxConstants.INTERFACE_ID)
    }

    fun spin(player: Player, boxType: SpinMysteryBoxType) {

        if (!passedSpinWaitTime(player))
            return

        val boxItem = Item(boxType.itemId)

        if (!player.hasItemInInventory(boxItem))
            return

        if (player.interfaceId != MysteryBoxConstants.INTERFACE_ID)
            return

        if (player.gameMode.isSpawn) {
            player.sendMessage("You cannot open the mystery box in the spawn game mode.")
            return
        }

        val rewardItem = boxType.getItems(player)[51]

        player.block(blockDisconnect = true)
        player.message(":startspin:")

        TaskManager.submit(9) {
            player.unblock(unblockDisconnect = true)

            val packetSender = player.packetSender

            if (player.removeInventoryItem(boxItem, 0)) {

                var boxName = boxType.formattedName
                if (boxName.equals("Fifty Dollar Mystery Box")) {
                    boxName = "$50 Mystery Box"
                }
                val rewardText = rewardItem.nameAndQuantity()

                packetSender.sendString(MysteryBoxConstants.REWARD_TEXT_ID, rewardText)
                packetSender.sendInterfaceItems(MysteryBoxConstants.REWARD_CONTAINER_ID, listOf(rewardItem))
                packetSender.sendOverlayInterface(MysteryBoxConstants.REWARD_INTERFACE_ID)

                if (rewardItem.isLargeAmount() || rewardItem.isHighValued()) {
                    PlayerUtil.broadcastMessage("<img=767> " + PlayerUtil.getImages(player) + "" + player.username +" has just won $rewardText from @dre@$boxName</col>")
                    player.packetSender.sendJinglebitMusic(234, 0)
                }

                if (!player.gameMode.isUltimate) {
                    if (player.inventory.countFreeSlots() < rewardItem.amount) {
                        packetSender.sendMessage("<img=767> You have won $rewardText from @dre@$boxName</col>! The reward has been sent to your bank.")
                        BankUtil.addToBank(player, rewardItem)
                    } else {
                        player.inventory.add(rewardItem)
                        packetSender.sendMessage("<img=767> You have won $rewardText from @dre@$boxName</col>!")
                    }
                } else
                    ItemContainerUtil.addOrDrop(player.inventory, player, rewardItem)

                // Regular reward jinglebit
                if (player.passedTime(Attribute.LAST_MYSTERY_BOX_OPENING, 30_000, TimeUnit.MILLISECONDS, message = false, updateIfPassed = true)) {
                    player.packetSender.sendJinglebitMusic(101, 0)
                }

                // Increase points and send messages
                processPointsAndMessages(player, boxType)

                // Achievements
                AchievementManager.processFor(AchievementType.CURIOSITY, player)
                AchievementManager.processFor(AchievementType.SUPER_CURIOUS, player)

                // Collection Log
                player.getCollectionLog().createOrUpdateEntry(player,  boxName, rewardItem)


                boxType.refreshItems(player)
                packetSender.sendInterfaceItems(MysteryBoxConstants.SPINNER_CONTAINER_ID, boxType.getItems(player))
            }
        }

    }

    /**
     * Processes the points and messages sent to players upon opening different types
     * of mystery boxes.
     */

    private fun processPointsAndMessages(player: Player, boxType: SpinMysteryBoxType) {
        val boxName = "@dre@${boxType.formattedName}</col>"

        // Increase points
        player.points.increase(AttributeManager.Points.TOTAL_MYSTERY_BOXES_OPENED) // Increase points

        val points = boxType.points

        if (points != null) {
            player.points.increase(points) // Increase points

            if (points != AttributeManager.Points.BARROWS_BOXES_COUNT
                && points != AttributeManager.Points.VOTING_BOXES_COUNT) {
                if (player.points[points] % 5 == 0)
                    player.sendMessage("<img=767> You have opened $boxName ${player.points[points]} times.")
            }
        }
    }

    fun viewRewards(player: Player, boxType: SpinMysteryBoxType) {
        val packetSender = player.packetSender
        val rewards = boxType.rewards.itemRewards
        val items = rewards.map { it.reward }.toList()

        for (i in 0..99) {
            var color = 0
            var text = ""
            if (i < rewards.size) {
                val chance = rewards[i].chance
                when {
                    chance <= 1 -> {
                        color = 0xFFFFFF
                        text = "VIP"
                    }
                    chance < 10 -> {
                        color = 0xff3000
                        text = "Very rare"
                    }
                    chance < 30 -> {
                        color = 0xff7000
                        text = "Rare"
                    }
                    chance < 60 -> {
                        color = 0xffed4c
                        text = "Uncommon"
                    }
                    else -> {
                        color = 0x56e156
                        text = "Common"
                    }
                }
            }
            val id = 77507 + i * 2
            packetSender.sendStringColour(id, color)
            packetSender.sendString(id, text)
        }
        packetSender.sendString(MysteryBoxConstants.REWARDS_TITLE_ID, boxType.formattedName + " Rewards")
        packetSender.sendInterfaceItems(MysteryBoxConstants.REWARDS_CONTAINER_ID, items)
        packetSender.sendScrollbarHeight(MysteryBoxConstants.REWARDS_SCROLL_BAR_ID, max(277, ceil(items.size / 5.0).toInt() * 69 + 1))
        packetSender.sendOverlayInterface(MysteryBoxConstants.REWARDS_INTERFACE_ID)
    }
}