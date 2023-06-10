package com.grinder.game.entity.agent.npc.monster.impl

import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.npc.monster.Monster
import com.grinder.game.entity.agent.npc.monster.MonsterEvents
import com.grinder.game.entity.agent.npc.monster.onEventEvery
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Misc
import java.util.function.Consumer

/**
 * @author L E G E N D
 */
class Cow(id: Int, position: Position?) : Monster(id, position!!) {

    init {
        onEventEvery(30..50, MonsterEvents.PRE_SEQUENCE) {
            moo()
        }
    }

    fun moo() {
        if (combat.isInCombat)
            return
        if (Misc.randomChance(20F)) {
            say("Moo")
            performAnimation(Animation(5854, 0))
            val boundaries = position.createSquareBoundary(7)
            boundaries.forEach(Consumer {
                for (player in AreaManager.getPlayers(boundaries)) {
                    player.packetSender.sendSound(Sounds.COW_YAK_MOO, 0, 1, 0)
                }
            })
        }
    }

    override fun attackRange(type: AttackType) = 1
}