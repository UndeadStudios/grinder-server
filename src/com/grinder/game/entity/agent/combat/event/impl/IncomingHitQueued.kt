package com.grinder.game.entity.agent.combat.event.impl

import com.grinder.game.entity.agent.combat.event.CombatEvent
import com.grinder.game.entity.agent.combat.hit.Hit

/**
 * Represents a [CombatEvent] fired whenever an incoming [hit]
 * is queued for some agent.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   04/09/2020
 */
class IncomingHitQueued(val hit: Hit) : CombatEvent