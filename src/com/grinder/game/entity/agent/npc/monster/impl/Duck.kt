package com.grinder.game.entity.agent.npc.monster.impl

import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.npc.monster.Monster
import com.grinder.game.entity.agent.npc.monster.MonsterEvents
import com.grinder.game.entity.agent.npc.monster.onEventEvery
import com.grinder.game.model.Position
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Misc
import com.grinder.util.NpcID
import java.util.function.Consumer

/**
 * @author L E G E N D
 * Date: 2/4/2021
 * Time: 10:53 PM
 * Discord: "L E G E N D#4380"
 */
class Duck(id: Int, position: Position) : Monster(id, position) {

    init {
        onEventEvery(30..50, MonsterEvents.PRE_SEQUENCE) {
            quack()
        }
    }

    private fun quack() {
        if (combat.isInCombat)
            return
        if (Misc.random(10) % 5 == 0) {
            say(if (isDuckling()) "Eep!" else "Quack!")
            val boundaries = position.createSquareBoundary(7)
            boundaries.forEach(Consumer {
                for (player in AreaManager.getPlayers(boundaries)) {
                    player.packetSender.sendSound(
                        if (isDuckling()) Sounds.DUCKLING_QUACK else Sounds.DUCK_QUACK,
                        0,
                        1,
                        0
                    )
                }
            })
        }
    }

    private fun isDuckling() = getId() == NpcID.DUCKLING && getId() == NpcID.DUCKLINGS

    override fun attackRange(type: AttackType) = 0
}