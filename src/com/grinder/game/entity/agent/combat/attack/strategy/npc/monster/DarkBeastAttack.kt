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
 * DarkBeastAttack. They attack with both melee & Magic, but only magic from a distance, or if they were attacked first.
 * This is super hard to confirm, so we will just use magic at a distance.
 */
class DarkBeastAttack: AttackStrategy<NPC> {
    var attackType: AttackStrategy<Agent> = MeleeAttackStrategy.INSTANCE

    override fun animate(actor: NPC) = attackType.animate(actor)

    override fun sequence(actor: NPC, target: Agent) = attackType.sequence(actor, target)

    override fun postHitAction(actor: NPC, target: Agent) = attackType.postHitAction(actor, target)

    override fun postHitEffect(hit: Hit) = attackType.postHitEffect(hit)

    override fun postIncomingHitEffect(hit: Hit)= attackType.postIncomingHitEffect(hit)

    override fun duration(actor: NPC) = 4

    override fun requiredDistance(actor: Agent) = attackType.requiredDistance(actor)

    override fun createHits(actor: NPC, target: Agent) = attackType.createHits(actor, target)

    override fun type() = attackType.type()

    override fun canAttack(actor: NPC, target: Agent): Boolean {
        attackType = if (!DistanceUtil.isWithinDistance(actor, target, 1))
            MagicAttackStrategy.INSTANCE
        else MeleeAttackStrategy.INSTANCE
        return true
    }

}