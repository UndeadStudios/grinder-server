package com.grinder.game.model

import com.grinder.game.entity.agent.combat.Combat
import com.grinder.game.entity.agent.combat.event.CombatEvent
import com.grinder.game.entity.agent.player.Player
import kotlin.reflect.KClass

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   09/09/2020
 */
object CombatActions {

    val combatActions = HashMap<KClass<*>, MutableList<(CombatAction<CombatEvent>) -> Unit>>()

    fun onAnyEvent(vararg events: KClass<out CombatEvent>, function: CombatAction<*>.() -> Unit){
       events.forEach {
           combatActions.putIfAbsent(it, ArrayList())
           combatActions[it]!!.add(function)
       }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : CombatEvent> onEvent(event: KClass<T>, function: CombatAction<T>.() -> Unit){
        combatActions.putIfAbsent(event, ArrayList())
        combatActions[event]!!.add(function as (CombatAction<CombatEvent>) -> Unit)
    }

    fun handleEvent(combat: Combat<*>, event: CombatEvent) : Boolean {
        val actions = combatActions[event::class]?:return false
        for(action in actions)
            action.invoke(CombatAction(event, combat))
        return true
    }

    class CombatAction<out T : CombatEvent>(val combatEvent: T, val combat: Combat<*>) {
        fun ifActorIsPlayer(function: (Player) -> Unit){
            val actor = combat.actor
            if(actor is Player)
                function.invoke(actor)
        }
    }
}