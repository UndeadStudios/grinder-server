package com.grinder.game.content.item.mysterybox

import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.markTime
import com.grinder.game.model.ButtonActions
import com.grinder.game.model.ItemActions
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.item.*
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.game.model.onFirstInventoryAction
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.time.TimeUnits


object MysteryBoxes {

    init {
        ItemActions.onClick(ItemID.CASKET){
            openCasket(player, MysteryBoxType.CASKET)
            return@onClick true
        }
        ItemActions.onClick(ItemID.CASKET_EASY_){
            openCasket(player, MysteryBoxType.EASY_CASKET)
            return@onClick true
        }
        ItemActions.onClick(ItemID.CASKET_MEDIUM_){
            openCasket(player, MysteryBoxType.MEDIUM_CASKET)
            return@onClick true
        }
        ItemActions.onClick(ItemID.CASKET_HARD_){
            openCasket(player, MysteryBoxType.HARD_CASKET)
            return@onClick true
        }
        ItemActions.onClick(ItemID.CASKET_ELITE_){
            openCasket(player, MysteryBoxType.ELITE_CASKET)
            return@onClick true
        }

        ButtonActions.onClick(34310, 42509) {
            player.message("@dre@Opening Grinderscape's store page..")
            player.packetSender.sendURL("https://www.grinderscape.org/store")
        }
        for (boxType in MysteryBoxType.values()) {

            val spinMysteryBoxType = SpinMysteryBoxType.getItemIds()[boxType.id]

            onFirstInventoryAction(boxType.id) {

                if (player.getTimePlayed(TimeUnits.HOUR) < 1) {
                    player.message("You must have at least a play time of 1 hour to open the box.")
                    return@onFirstInventoryAction
                }

                if (boxType == MysteryBoxType.STAFF_PRESENT) {
                    if (player.inventory.getAmount(ItemID.COINS) > 1900000000) {
                        player.message("Note: The maximum amount of coins can be held in your inventory is 2147m! Exceeding that will make you lose any extra coins.")
                        return@onFirstInventoryAction
                    }
                }

                if (spinMysteryBoxType != null)
                    SpinMysteryBox.open(player, spinMysteryBoxType)
            }
        }
    }

    /**
     * Claiming the reward
     *
     * @param player
     * the player
     */
    fun openCasket(player: Player, boxType: MysteryBoxType) {

        player.randomItemReward = Item(boxType.id)

        val boxItem = player.randomItemReward?:return

        if (!player.hasItemInInventory(boxItem))
            return

        val boxType = MysteryBoxType.forId(boxItem.id) ?: return

        if (boxType.itemRewards.isEmpty()) {
            return
        }

        val odds = Misc.getRandomDouble(100.0)

        val boxRewardList =  boxType.itemRewards.filter { it.chance > odds }.toList()

        if (boxRewardList.isEmpty()) {
            openCasket(player, boxType)
            return
        }

        val rewardItem = boxRewardList.random().reward
        if (player.removeInventoryItem(boxItem, -1)){

            // Achievements
            AchievementManager.processFor(AchievementType.CURIOSITY, player)
            AchievementManager.processFor(AchievementType.SUPER_CURIOUS, player)

            val boxName = "@dre@" + boxItem.name() +"</col>"
            val rewardText = rewardItem.nameAndQuantity()
            player.message("<img=767> You open the $boxName and received $rewardText!")

            if (!player.addInventoryItem(rewardItem)) {
                player.message("The reward has been dropped beneath you do to a full inventory.")
                ItemContainerUtil.dropUnder(player, rewardItem.id, rewardItem.amount)
            }

            // Rare reward jinglebit
            if (rewardItem.isLargeAmount() || rewardItem.isHighValued()) {
                player.packetSender.sendJinglebitMusic(234, 0)
                player.markTime(Attribute.LAST_MYSTERY_BOX_OPENING)
            }
        }
    }
}