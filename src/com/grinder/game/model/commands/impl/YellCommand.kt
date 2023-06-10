package com.grinder.game.model.commands.impl

import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.decInt
import com.grinder.game.entity.getInt
import com.grinder.game.entity.passedTime
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.attribute.AttributeManager
import com.grinder.game.model.commands.Command
import com.grinder.util.Misc
import java.util.concurrent.TimeUnit

class YellCommand : Command {

    override fun getSyntax() = "[message]"

    override fun getDescription() = "Sends a message to the global yell chat."

    override fun execute(player: Player, command: String, parts: Array<String>) {

        if (player.isAccountFlagged)
            return

        if (player.BLOCK_ALL_BUT_TALKING)
            return

        if (player.isMuted) {
            player.statement("You're not allowed to yell when your account is muted.")
            return
        }
        val staff = PlayerUtil.isStaff(player)

        val message = command.toLowerCase().substringAfter("yell").trim()

        if (message.isBlank()){
            player.statement("Your yell message cannot be blank.")
            return
        }

        if (message.toLowerCase().contains("<img")) {
            return;
        }

        if (!staff && Misc.blockedWord(message)) {
            player.statement("A word was blocked in your sentence. Please do not repeat it!")
            return
        }

        val requireYellCredits = !staff && !PlayerUtil.hasMemberYellPriviliges(player)

        if (requireYellCredits){
            val yellCredits = player.getInt(Attribute.YELL_CREDITS)
            if (yellCredits <= 0) {
                player.sendMessage("<img=745> The yell feature is only available to the Ruby ranked members or higher.");
                return
            }
        }

        if (staff || player.passedTime(Attribute.YELL_TIMER, 15, TimeUnit.SECONDS, message = true)){
            if (requireYellCredits) {
                val newAmount = player.decInt(Attribute.YELL_CREDITS, 1)
                player.message("You have $newAmount yell credits left.")
            }
            player.progressAchievement(AchievementType.YELL_MANIA)
            player.points.increase(AttributeManager.Points.YELLS_YELLED, 1) // Increase points

            PlayerUtil.broadcastSpecialMessage(player.username, 10, format(player, message))
        }
    }

    override fun canUse(player: Player) = true

    companion object {

        @JvmStatic
		fun format(player: Player, yellMessage: String?): String {
            val colors = player.yellColors
            return "<col=" + String.format("%06X", 0xFFFFFF and colors[0]) + ">" +
                    "<shad=" + colors[1] + ">" +
                    "[" +
                    "<col=" + String.format("%06X", 0xFFFFFF and colors[2]) + ">" +
                    "<shad=" + colors[3] + ">" +
                    player.yellTitle +
                    "<col=" + String.format("%06X", 0xFFFFFF and colors[0]) + ">" +
                    "<shad=" + colors[1] + ">" +
                    "]" +
                    " " +
                    PlayerUtil.getImages(player) +
                    "<col=" + String.format("%06X", 0xFFFFFF and colors[4]) + ">" +
                    "<shad=" + colors[5] + ">" +
                    player.username + ": " +
                    "<col=" + String.format("%06X", 0xFFFFFF and colors[6]) + ">" +
                    "<shad=" + colors[7] + ">" +
                    Misc.capitalize(yellMessage)
        }
    }
}