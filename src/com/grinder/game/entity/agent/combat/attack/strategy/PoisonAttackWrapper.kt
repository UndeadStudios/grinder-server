package com.grinder.game.entity.agent.combat.attack.strategy

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonEffect
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.npc.NPC
import kotlin.random.Random

/**
 * Attack Strategy wrapper for poison attacks.
 */
class PoisonAttackWrapper(val attackType: AttackType, val chance:Double, val poisonType: PoisonType) : AttackStrategy<NPC> {
    val strategy: AttackStrategy<Agent> by lazy {
        when(attackType) {
            AttackType.MAGIC -> MagicAttackStrategy.INSTANCE
            AttackType.MELEE -> MeleeAttackStrategy.INSTANCE
            AttackType.RANGED -> RangedAttackStrategy.INSTANCE
            else -> MeleeAttackStrategy.INSTANCE
        }
    }

    override fun animate(actor: NPC) = strategy.animate(actor)

    override fun sequence(actor: NPC, target: Agent) = strategy.sequence(actor, target)

    override fun postHitAction(actor: NPC, target: Agent) = strategy.postHitAction(actor, target)

    override fun postHitEffect(hit: Hit) {
        if (!hit.target.isPoisoned && Random.nextDouble(100.0) <= chance)
            PoisonEffect.applyPoisonTo(hit.target, poisonType)
        super.postHitEffect(hit)
    }

    override fun postIncomingHitEffect(hit: Hit) = strategy.postIncomingHitEffect(hit)

    override fun canAttack(actor: NPC, target: Agent) = strategy.canAttack(actor, target)

    override fun duration(actor: NPC) = strategy.duration(actor)

    override fun requiredDistance(actor: Agent) = strategy.requiredDistance(actor)

    override fun createHits(actor: NPC, target: Agent) = strategy.createHits(actor, target)

    override fun type() = strategy.type()
}