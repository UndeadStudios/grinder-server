package com.grinder.game.model.areas

import com.grinder.game.content.clan.ClanChat
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.Boundary
import com.grinder.game.model.areas.impl.BossInstances
import com.grinder.util.TaskFunctions
import org.joda.time.DateTime
import org.joda.time.Duration

class ClanInstancedBossArea(
        boss: Boss,
        boundaries: Boundary,
        val clan : ClanChat,
        val instanceType : BossInstances,
        val startTime: DateTime = DateTime.now(),
        val endTime : DateTime = DateTime.now().plusMinutes(6)) :
    UntypedInstancedBossArea(boss, boundaries) {

    /**
     * False when the instance has expired
     */
    var active = true;

    init {
        // Run every minute
        TaskFunctions.repeatDelayed(75) { checkExpired() }
    }

    /**
     * Destroy the instance after 1 hour.
     */
    fun checkExpired() {

        if(endTime.isBeforeNow) {
            /*players.forEach { player ->
                player.message("The instance you were in has expired and have been teleported home.")
                player.moveTo(Position(3089, 3492))
            }*/

            destroy()
            active = false
        }

        if(DateTime.now().plusMinutes(5).isAfter(endTime)) {
            val minsOff = Duration(DateTime.now(), endTime).standardMinutes

            players.forEach { player ->
                if(minsOff > 0) {
                    player.message("The instance you are in will expire in $minsOff minutes.")
                } else {
                    player.message("The instance you are in will expire in less than a minute.")
                }
            }
        }
    }
}