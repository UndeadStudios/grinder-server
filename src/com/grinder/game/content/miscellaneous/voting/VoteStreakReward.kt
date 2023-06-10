package com.grinder.game.content.miscellaneous.voting

import org.apache.commons.lang.WordUtils

/**
 * A vote streak can give extra rewards to players who vote
 * between an interval of 12-16 or 16-24 hours for the server.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   13/05/2020
 * @version 1.0
 *
 * @param requiredStreak    the amount of points a player has consecutively voted
 * @param name              a name identifying this reward
 * @param description       a description of this reward
 * @param repeat            can this streak be obtained multiple times
 * @param usableWithPenalty can this streak be used when penalized
 */
data class VoteStreakReward(
        private val requiredStreak: Double,
        private val name: String,
        private val description: String,
        private val repeat: Boolean,
        private val usableWithPenalty: Boolean
) {

    fun applies(penalized: Boolean, oldStreakPoints: Double, streakPoints: Double): Boolean {

        if(penalized && !usableWithPenalty)
            return false

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