package com.grinder.game.entity.agent.combat.event.impl

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.event.CombatEvent

/**
 * A [CombatEvent] fired when the [failedTarget] is already under attack.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/08/2020
 */
class TargetAlreadyUnderAttack(val failedTarget: Agent) : CombatEvent