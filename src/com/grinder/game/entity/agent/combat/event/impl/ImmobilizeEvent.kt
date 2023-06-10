package com.grinder.game.entity.agent.combat.event.impl

import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.event.ApplicableCombatEvent
import com.grinder.game.model.EffectTimer
import com.grinder.util.time.TimeUtil
import com.grinder.util.timing.TimerKey

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/08/2020
 */
open class ImmobilizeEvent(
    private var effectDuration: Int,
    private val immunityDuration: Int = 5,
    private val forced: Boolean,
    private val message: String = "@red@You have been frozen!"
) : ApplicableCombatEvent {

    override fun isApplicableTo(agent: Agent): Boolean {

        if (!forced && (agent.timerRepository.has(TimerKey.FREEZE)
                    || agent.timerRepository.has(TimerKey.FREEZE_IMMUNITY)))
            return false

        return agent.size <= 2
    }

    override fun applyTo(agent: Agent) {
        agent.timerRepository.register(TimerKey.FREEZE, effectDuration)
        agent.timerRepository.register(TimerKey.FREEZE_IMMUNITY, immunityDuration)
        agent.motion.clearSteps()
        agent.ifPlayer {
            it.sendMessage(message)
            it.packetSender.sendEffectTimer(
                TimeUtil.GAME_CYCLES.toSeconds(effectDuration.toLong()),
                EffectTimer.FREEZE
            )
        }
    }
}