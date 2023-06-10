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
 * @author Rachael eLou
 */
class RedJapan(id: Int, position: Position?) : Monster(id, position!!) {

    init {
        onEventEvery(10..30, MonsterEvents.PRE_SEQUENCE) {
            wave()
        }
    }

    fun wave() {
        if (Misc.randomChance(90F)) {
            performAnimation(Animation(863, 0))
        }
    }

    override fun attackRange(type: AttackType) = 1
}