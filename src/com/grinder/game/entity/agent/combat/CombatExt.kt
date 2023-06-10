package com.grinder.game.entity.agent.combat

import com.grinder.game.entity.agent.combat.event.CombatEvent
import com.grinder.game.entity.agent.combat.event.CombatEventListener
import com.grinder.game.entity.agent.combat.event.CombatState
import com.grinder.game.entity.agent.combat.event.impl.IncomingHitApplied
import com.grinder.game.entity.agent.combat.event.impl.IncomingHitQueued
import com.grinder.game.entity.agent.combat.event.impl.OutgoingHitApplied
import com.grinder.game.entity.agent.combat.event.impl.OutgoingHitQueued
import com.grinder.game.entity.agent.combat.hit.Hit

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/08/2020
 */

fun Combat<*>.subscribe(handler: (CombatEvent) -> Boolean) {
    subscribe(object : CombatEventListener {
        override fun on(event: CombatEvent): Boolean {
            return handler.invoke(event)
        }
    })
}

fun Combat<*>.onState(state: CombatState, function: () -> Unit){
    subscribe {
        if(it == state)
            function.invoke()
        return@subscribe false
    }
}

fun Combat<*>.onIncomingHitQueued(function: Hit.() -> Unit){
    subscribe {
        if (it is IncomingHitQueued)
            function.invoke(it.hit)
        return@subscribe false
    }
}

fun Combat<*>.onIncomingHitApplied(function: Hit.() -> Unit){
    subscribe {
        if (it is IncomingHitApplied)
            function.invoke(it.hit)
        return@subscribe false
    }
}

fun Combat<*>.onOutgoingHitQueued(function: Hit.() -> Unit){
    subscribe {
        if (it is OutgoingHitQueued)
            function.invoke(it.hit)
        return@subscribe false
    }
}

fun Combat<*>.onOutgoingHitApplied(function: Hit.() -> Unit){
    subscribe {
        if (it is OutgoingHitApplied)
            function.invoke(it.hit)
        return@subscribe false
    }
}