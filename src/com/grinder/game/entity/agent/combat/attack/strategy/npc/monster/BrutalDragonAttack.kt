package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.attack.strategy.MeleeAttackStrategy
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.util.DistanceUtil
import com.grinder.util.random.RandomUtil

/**
 * BrutalDragonAttack
 *
 * Magic Attack
 * Melee Attack
 * DragonFire Attack
 */
class BrutalDragonAttack : AttackStrategy<NPC> {

    var attackType: AttackStrategy<Agent> = MeleeAttackStrategy.INSTANCE

    override fun animate(actor: NPC) {
        attackType.animate(actor)
    }

    override fun postHitAction(actor: NPC, target: Agent) {
        attackType.postHitAction(actor, target)
    }

    override fun postHitEffect(hit: Hit) {
       attackType.postHitEffect(hit)
    }

    override fun duration(actor: NPC) = 4

    fun getMaxHit(type: AttackType): Int { // TODO: ADD SUPPORT FOR THIS TO WORK
        return when (type) {
            AttackType.MELEE -> 18
            AttackType.MAGIC -> 18
            else -> 50
        }
    }

    override fun sequence(actor: NPC, target: Agent) {
        attackType.sequence(actor, target)
    }

    override fun requiredDistance(actor: Agent) = 7

    // We must decide what attack to perform.
    override fun canAttack(actor: NPC, target: Agent): Boolean {
        if (DistanceUtil.isWithinDistance(actor, target, 1) && RandomUtil.RANDOM.nextInt(3) != 0)
            attackType = MeleeAttackStrategy.INSTANCE
        else
            when(RandomUtil.RANDOM.nextInt(4)) {
                0-> attackType=DragonFireAttack.variableDragonfireStrategy(actor, target)
                3->attackType=MagicAttackStrategy.INSTANCE
            }
        return attackType.canAttack(actor, target)
    }

    override fun createHits(actor: NPC, target: Agent) = attackType.createHits(actor, target)

    override fun type() = attackType.type()
}