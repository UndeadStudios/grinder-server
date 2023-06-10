package com.grinder.game.entity.agent.npc.monster.impl

import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.npc.monster.Monster
import com.grinder.game.entity.agent.npc.monster.MonsterEvents
import com.grinder.game.entity.agent.npc.monster.onEventEvery
import com.grinder.game.entity.agent.player.playAreaSound
import com.grinder.game.model.Position
import com.grinder.game.model.areas.AreaManager
import com.grinder.util.Misc
import java.util.function.Consumer

/**
 * @author L E G E N D
 * Date: 2/4/2021
 * Time: 11:18 PM
 * Discord: "L E G E N D#4380"
 */
class Sheep(id: Int, position: Position) : Monster(id, position) {

    init {
        onEventEvery(30..50, MonsterEvents.PRE_SEQUENCE) {
            bea()
        }
    }

    private fun bea() {
        if (Misc.random(10) % 5 == 0) {
            say("Bea!")
            val boundaries = position.createSquareBoundary(7)
            boundaries.forEach(Consumer {
                for (player in AreaManager.getPlayers(boundaries)) {
                    player.playAreaSound(2053, 7, 1, 0)
                }
            })
        }
    }

    override fun attackRange(type: AttackType) = 0
}