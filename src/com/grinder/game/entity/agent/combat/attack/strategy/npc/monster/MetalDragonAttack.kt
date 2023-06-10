package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MeleeAttackStrategy
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.util.DistanceUtil
import com.grinder.util.random.RandomUtil


/**
 * MetalDragon attack strategy.
 * Melee: 91, projectile: 54
 */
class MetalDragonAttack: AttackStrategy<NPC> {

    // Always start out with range attack.
    var attackType: AttackStrategy<Agent> = DragonFireAttack.LongRange

    override fun animate(actor: NPC) {
        attackType.animate(actor)
    }

    override fun postHitAction(actor: NPC, target: Agent) {
        attackType.postHitAction(actor, target)
    }

    override fun postHitEffect(hit: Hit) {
        attackType.postHitEffect(hit)
    }

    override fun duration(actor: NPC): Int {
        return attackType.duration(actor)
    }

    override fun sequence(actor: NPC, target: Agent) {
        attackType.sequence(actor, target)
    }

    // we have to put the max amount here, or it won't `reRoll` to fix if they walk away.
    override fun requiredDistance(actor: Agent) = 8

    override fun canAttack(actor: NPC, target: Agent): Boolean {
        attackType = if (!DistanceUtil.isWithinDistance(actor, target, 1) || RandomUtil.RANDOM.nextInt(3) == 0)
            DragonFireAttack.variableDragonfireStrategy(actor, target)
        else MeleeAttackStrategy.INSTANCE
        return true
    }

    override fun createHits(actor: NPC, target: Agent): Array<Hit> {
        return attackType.createHits(actor, target)
    }

    override fun type(): AttackType? {
        return attackType.type()
    }
}