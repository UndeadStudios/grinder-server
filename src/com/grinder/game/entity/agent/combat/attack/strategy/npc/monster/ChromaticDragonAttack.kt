package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.strategy.MeleeAttackStrategy
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.util.Misc

/**
 * Handles Chromatic Dragon attacks. Such as Green, Red, Black dragons.
 */
class ChromaticDragonAttack : AttackStrategy<NPC> {

    // Always start with a melee attack
    private var currentAttackType : AttackStrategy<Agent> = MeleeAttackStrategy.INSTANCE

    override fun postHitEffect(hit: Hit) {}

    override fun animate(actor: NPC) {
        currentAttackType.animate(actor)
    }

    override fun postHitAction(actor: NPC, target: Agent) {
        currentAttackType.postHitAction(actor, target)
        currentAttackType = if (Misc.randomInclusive(0, 2) == 0) {
            DragonFireAttack.closeRange
        } else {
            MeleeAttackStrategy.INSTANCE
        }
    }

    override fun duration(actor: NPC) = currentAttackType.duration(actor)

    override fun requiredDistance(actor: Agent) = currentAttackType.requiredDistance(actor)

    override fun createHits(actor: NPC, target: Agent) = currentAttackType.createHits(actor, target)

    override fun type() = currentAttackType.type()
}