package com.grinder.game.entity.agent.combat.attack.special

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MeleeAttackStrategy

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
abstract class MeleeSpecialAttack(provider: SpecialAttackProvider)
    : SpecialAttack(provider) {

    override fun type() = AttackType.MELEE

    override fun requiredDistance(actor: Agent) =
            MeleeAttackStrategy.INSTANCE.requiredDistance(actor)

    override fun canAttack(actor: Agent, target: Agent)=
            MeleeAttackStrategy.INSTANCE.canAttack(actor, target)
}