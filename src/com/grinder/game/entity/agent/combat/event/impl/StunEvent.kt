package com.grinder.game.entity.agent.combat.event.impl

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.event.ApplicableCombatEvent
import com.grinder.game.model.Graphics
import com.grinder.util.Misc
import com.grinder.util.timing.TimerKey

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/08/2020
 */
class StunEvent(val duration: Int, val forced: Boolean,
                val resetCombat: Boolean = true,
                val resetMotion: Boolean = true
) : ApplicableCombatEvent {

    override fun isApplicableTo(agent: Agent): Boolean {
        return !(!forced && agent.timerRepository.has(TimerKey.STUN))
    }

    override fun applyTo(agent: Agent) {
        agent.messageIfPlayer("You've been stunned!", 1000)
        agent.timerRepository.register(TimerKey.STUN, Misc.getTicks(duration))
        if (resetMotion)
            agent.motion.clearSteps()
        agent.performGraphic(Graphics.STUNNED_GRAPHIC)
        if (resetCombat)
            agent.combat.reset(false)
    }
}