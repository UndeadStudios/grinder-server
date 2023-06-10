package com.grinder.game.entity.agent.npc.monster.boss.impl.god

import com.grinder.game.entity.agent.npc.monster.MonsterEvents
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinion
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinionPolicy
import com.grinder.game.model.Position

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
open class GodMinion<R : God>(bossNPC: R, id: Int, position: Position)
    : BossMinion<R>(bossNPC, id, position,
        BossMinionPolicy.NO_RESPAWN,
        BossMinionPolicy.ATTACK_PREFERRED_OPPONENT),
        Religious
{
    init {
        movementCoordinator.radius = 5
        onEvent {
            if(it == MonsterEvents.ADDED){
                if(this.position != spawnPosition)
                    this.moveTo(spawnPosition)
            }
        }
    }

    override fun getRetreatPolicy() = MonsterRetreatPolicy.NEVER
    override fun chamber() = bossNPC.chamber()
}