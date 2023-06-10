package com.grinder.game.entity.agent.combat.event.impl

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.weapon.melee.BloodSacrificeEventTask
import com.grinder.game.entity.agent.combat.event.ApplicableCombatEvent
import com.grinder.game.model.EffectTimer
import com.grinder.game.task.TaskManager
import com.grinder.util.time.TimeUtil
import com.grinder.util.timing.TimerKey

/**
 * @author R-Y-M-R
 * @date 5/25/2022
 * @see [RuneServer](https://www.rune-server.ee/members/necrotic/)
 */
open class BloodSacrificeEvent(
    private val effectDuration: Int,
    private val forced: Boolean,
    private val message: String = "@red@You have been struck by Blood Sacrifice!"
) : ApplicableCombatEvent {

    override fun isApplicableTo(agent: Agent): Boolean {
        if (!forced && agent.timerRepository.has(TimerKey.BLOOD_SACRIFICE_TIMER))
            return false
        return true
    }

    override fun applyTo(agent: Agent) {
        agent.timerRepository.register(TimerKey.BLOOD_SACRIFICE_TIMER, effectDuration)
        // apply Orange GFX to Agent here
        agent.ifPlayer {
            it.sendMessage(message)
        }
        TaskManager.submit(BloodSacrificeEventTask(agent))
    }
}