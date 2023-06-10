package com.grinder.game.content.points

import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.miscellaneous.FrogPrinceEvent
import com.grinder.game.content.miscellaneous.MysteriousManEvent
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.attribute.AttributeManager.Points
import com.grinder.util.Misc
import com.grinder.util.TaskFunctions.delayBy

object ParticipationPoints {

    @JvmStatic
	fun addPoints(player: Player, participationPoints: Int, rewardSource: String) {

        player.message("<img=779> You have received @dre@$participationPoints</col> participation points @dre@$rewardSource")
        player.points.increase(Points.PARTICIPATION_POINTS, participationPoints)
        player.points.increase(Points.TOTAL_PARTICIPATION_POINTS_RECEIVED, participationPoints)

        //val amount = (participationPoints * 200f).toInt()

        //if (!player.gameMode.isSpawn) {
        if (!rewardSource.contains("@dre@from store shopping</col>.")) {

            // Reward players blood money or cash for receiving participation points
            /*if (player.inventory.countFreeSlots() > 1) {
                player.message("You have received @dre@" + NumberFormat.getIntegerInstance().format(amount.toLong()) + "</col> Blood money as a reward!")
                player.inventory.add(ItemID.BLOOD_MONEY, amount)
            } else {
                BankUtil.addToBank(player, Item(ItemID.BLOOD_MONEY, amount))
                player.message("You have received @dre@" + NumberFormat.getIntegerInstance().format(amount.toLong()) + "</col> Blood money as a reward! The reward " + (if (player.gameMode.isUltimate) "is dropped under you" else "was sent to your bank") + ".")
            }*/

            // Announce players who receive participation points

            if (participationPoints > 2 && rewardSource != "@dre@from completing achievements</col>.") {
                if (!(participationPoints == 10 && rewardSource == "@dre@from Minigames</col>.")) { // Remove winning minigames for non winner
                    PlayerUtil.broadcastMessage("<img=758> " + PlayerUtil.getImages(player) + "" + player.username + " has just received @dre@" + participationPoints + "</col> participation points " + rewardSource + "")
                }
            }
            if (participationPoints >= 12 && rewardSource == "@dre@from completing achievements</col>.") {
                PlayerUtil.broadcastMessage("<img=758> " + PlayerUtil.getImages(player) + "" + player.username +" has just received @dre@" + participationPoints + "</col> participation points " + rewardSource + "")
            }
        }
        //}

        // Process achievement
        AchievementManager.processFor(AchievementType.STRAIGHT_UP, participationPoints, player)


        // Weird pre-caution and trigger new anti botting
        if (Misc.random(25) == 1) {
            // Trigger the event
            delayBy(7) {
                MysteriousManEvent.trigger(player)
            }
        } else if (Misc.random(25) == 1) {
            // Trigger the event
            delayBy(7) {
                FrogPrinceEvent.trigger(player)
            }
        }


    }
}