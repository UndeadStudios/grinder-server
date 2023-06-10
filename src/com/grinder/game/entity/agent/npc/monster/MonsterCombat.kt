package com.grinder.game.entity.agent.npc.monster

import com.grinder.game.entity.agent.combat.NPCCombat
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   15/10/2019
 * @version 1.0
 */
open class MonsterCombat(private val monster: Monster) : NPCCombat(monster) {

    override fun sequence() {
        if (monster.skipNextCombatSequence()) {
            monster.debug("Skipping combat!")
            return
        }
        monster.debug("Sequencing combat")

        // If monster left alone then retreat to full hp in some circumstances
        if (monster.hitpoints < monster.fetchDefinition().hitpoints) {

            if (monster.retreatPolicy == MonsterRetreatPolicy.RETREAT_IF_OUT_OF_COMBAT) {

                monster.combat.outOfCombatCount.incrementAndGet()
                if (monster.combat.outOfCombatCount.get() >= 100) { // 60 seconds
                    monster.combat.reset(true)
                    monster.regenerateFullHealth()
                    monster.combat.outOfCombatCount.set(0)
                }
            }
        }
        super.sequence()
    }
}