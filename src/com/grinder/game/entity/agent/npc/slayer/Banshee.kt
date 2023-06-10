package com.grinder.game.entity.agent.npc.slayer

import com.grinder.game.entity.agent.combat.attack.strategy.npc.monster.BansheeAttack
import com.grinder.game.entity.agent.combat.onIncomingHitApplied
import com.grinder.game.model.Position

/**
 * Banshee has a unique mechanic that it will heal a lifepoint everytime it is hit regardless if it was damaged.
 */
class Banshee(id:Int, pos:Position): SlayerMonster(id, pos) {

    init {
        attackStrategy = BansheeAttack()
        combat.onIncomingHitApplied {
            if (totalDamage < hitpoints)
                heal(1)
        }
    }
}