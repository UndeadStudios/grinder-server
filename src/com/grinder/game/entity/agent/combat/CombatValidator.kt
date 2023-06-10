package com.grinder.game.entity.agent.combat

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.event.impl.TargetIsOutOfReach

/**
 * TODO: should probably integrate this into [Combat]
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   22/11/2019
 * @version 1.0
 */
class CombatValidator<T : Agent> {

    fun evaluateTarget(combat: Combat<T>) : Boolean {

        val attacker = combat.actor?:return false
        var target = combat.target?:return false

        if(target.hasPendingTeleportUpdate()){
            combat.clearOpponent(target)
            if (!combat.retaliateAutomatically()) {
                combat.setTarget(null)
                return false
            }
            combat.opponent?.let { newTarget ->
                target = newTarget
                combat.target(newTarget)
            }?:return false
        }

        if(!target.combat.canBeAttackedBy(attacker, true)){
            combat.reset(false)
            return false
        }

        if(!combat.isInReachForAttack(target, true)){
            if (target.motion.completed())
                combat.submit(TargetIsOutOfReach(target))
            return false
        }

        return true
    }


}