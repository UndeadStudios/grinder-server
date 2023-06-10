package com.grinder.game.content.pvm.contract

import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import kotlin.math.max

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   04/04/2020
 * @version 1.0
 */
class MonsterContract(val playerName: String, val bounty: MonsterBounty, var startTime: Long = -1L) {

    fun completedInTime(duration: Long) = duration <= bounty.expirationTime

    fun secondsRemaining() = if(started())
        max(0, bounty.expirationTime - (System.currentTimeMillis() - startTime)) / 1000
    else
        bounty.expirationTime.toLong() / 1000

    fun isExpired() = started() && System.currentTimeMillis() - startTime > bounty.expirationTime

    fun ownedBy(player: Player) = player.username == playerName

    fun isTarget(npc: NPC) = npc.id == bounty.npcId

    fun started() = startTime != -1L

    fun start() {
        startTime = System.currentTimeMillis()
    }

}