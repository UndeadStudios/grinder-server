package com.grinder.game.entity.agent.combat.event.impl

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.event.ApplicableCombatEvent
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.misc.CombatPrayer

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/08/2020
 */
class VegeanceEvent(recipient: Agent, val hit: Hit) : ApplicableCombatEvent {

    override fun isApplicableTo(agent: Agent) = hit.totalDamage > 0

    override fun applyTo(agent: Agent) {
        hit.recoilPercentage = CombatPrayer.VENGEANCE_HIT_TO_DAMAGE_RATIO
        agent.say("Taste Vengeance!")
        agent.vengeanceEffect.stop()
    }
}