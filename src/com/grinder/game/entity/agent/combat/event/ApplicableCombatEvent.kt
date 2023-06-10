package com.grinder.game.entity.agent.combat.event

import com.grinder.game.entity.agent.Agent

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/08/2020
 */
interface ApplicableCombatEvent : CombatEvent {

    fun isApplicableTo(agent: Agent): Boolean

    fun applyTo(agent: Agent)
}