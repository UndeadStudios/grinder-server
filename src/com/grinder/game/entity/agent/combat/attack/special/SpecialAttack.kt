package com.grinder.game.entity.agent.combat.attack.special

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.Attack
import com.grinder.game.entity.agent.combat.attack.AttackContext

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   19/04/2020
 * @version 1.0
 */
abstract class SpecialAttack(
        open val provider: SpecialAttackProvider
) : Attack<Agent>(provider) {

    var cancelDrain = false

    abstract fun special(): SpecialAttackType

    open fun secondaryAccuracyModifier(context: AttackContext): Double = 1.0
    open fun tertiaryAccuracyModifier(context: AttackContext): Double = 1.0

    open fun secondaryDamageModifier(context: AttackContext): Double = 1.0
    open fun tertiaryDamageModifier(context: AttackContext): Double = 1.0

    open fun onActivated(actor: Agent) {}

    open fun onHit(actor: Agent, target: Agent) {}

    open fun drainAmount(actor: Agent) = special().customDrainAmount

    final override fun postHitAction(actor: Agent, target: Agent) {
        cancelDrain = false
        onHit(actor, target)
        if (!cancelDrain)
            SpecialAttackType.drain(actor, drainAmount(actor))
    }
}