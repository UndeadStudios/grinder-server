package com.grinder.game.content.miscellaneous.voting

import com.grinder.game.content.points.PremiumPoints
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.markTimeFromNow
import com.grinder.game.entity.setBoolean
import com.grinder.game.model.attribute.Attribute
import org.apache.commons.lang.WordUtils
import java.time.Duration

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   13/05/2020
 * @version 1.0
 *
 * @param requiredStreak    the amount of points a player has consecutively voted
 * @param identifier        a name identifying this reward
 * @param description       a description of this reward
 * @param repeat            can this streak be obtained multiple times
 */
enum class VoteStreakType(private val requiredStreak: Double,
                          private val identifier: String,
                          private val description: String,
                          private val repeat: Boolean) {

    MAX_HIT_LOOKUP(
            requiredStreak      = 1.0,
            identifier          = "Max Hit lookup",
            description         = "Allows you to view your maximum damage in the equipment tab.",
            repeat              = false
    ) {
        override fun onReceived(player: Player) {
            player.setBoolean(Attribute.CAN_LOOKUP_MAX_HIT, true)
        }
        override fun onPenaltyReceived(player: Player) {
            player.setBoolean(Attribute.CAN_LOOKUP_MAX_HIT, false)
        }
        override fun onPenaltyLifted(player: Player) {
            player.setBoolean(Attribute.CAN_LOOKUP_MAX_HIT, true)
        }
    },
    CLUE_RE_ROLL(
            requiredStreak      = 5.0,
            identifier          = "Clue Reward re-rolls",
            description         = "Provides you with 2 re-rolls for Clue scroll rewards.",
            repeat              = false
    ) {

    },
    FREE_MEMBER_DAYS(
            requiredStreak      = 12.0,
            identifier          = "Two free Ruby Member days",
            description         = "Provides you with two free member days.",
            repeat              = true
    ) {
        override fun onReceived(player: Player) {
            player.markTimeFromNow(Attribute.FREE_RUBY_MEMBER_RANK, Duration.ofDays(2))
        }
    },
    PREMIUM_POINTS(
            requiredStreak      = 31.0,
            identifier          = "5K Premium points",
            description         = "Rewards you with 5k Premium points.",
            repeat              = true
    ) {
        override fun onReceived(player: Player) {
            PremiumPoints.rewardPremiumPoints(player, 5_000, "Vote Streaks")
        }
    };

    open fun onReceived(player: Player){}
    open fun onPenaltyReceived(player: Player){}
    open fun onPenaltyLifted(player: Player){}

    fun applies(oldStreakPoints: Double, streakPoints: Double): Boolean {

        if(streakPoints > requiredStreak){
            if(repeat){
                val oldTimes = (oldStreakPoints / requiredStreak).toInt()
                val newTimes = (streakPoints / requiredStreak).toInt()
                return newTimes > oldTimes
            }
        }

        if(oldStreakPoints < requiredStreak && streakPoints >= requiredStreak)
            return true

        return false
    }

    fun getText(): Array<String> {
        val list = ArrayList<String>()

        list.add(getFormattedName())

        if(repeat){
            list.add("@blu@Every ${requiredStreak.toInt()} streak points</col>")
        } else
            list.add("@blu@At ${requiredStreak.toInt()} streak points</col>")

        val wrapped = WordUtils.wrap(description, 25, System.lineSeparator(), false)
                .split(System.lineSeparator())
                .toTypedArray()

        list.addAll(wrapped)

        return list.toTypedArray()
    }

    fun getFormattedName() = "@cya@$name</col>"
}