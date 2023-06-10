package com.grinder.game.entity.agent.combat.event.impl

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.event.CombatEvent

/**
 * This event firs when the [target] is out of reach for the attacker
 * but was previously selected as combat target.
 *
 * This event differs from [TargetIsNotReachable] in that it already
 * had the target selected for combat.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/08/2020
 */
class TargetIsOutOfReach(val target: Agent) : CombatEvent