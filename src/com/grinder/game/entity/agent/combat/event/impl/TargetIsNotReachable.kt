package com.grinder.game.entity.agent.combat.event.impl

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.event.CombatEvent

/**
 * This event fires whenever a selected for [target] is not reachable,
 * this is not the same as [TargetIsOutOfReach], as this fires
 * before any combat evaluation is done with respect to the [target].
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/08/2020
 */
class TargetIsNotReachable(val target: Agent) : CombatEvent