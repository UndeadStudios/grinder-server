package com.grinder.game.model

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.event.PlayerEvent
import kotlin.reflect.KClass


/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   22/11/2020
 */
object PlayerActions {

    val playerActions = HashMap<KClass<*>, MutableList<(PlayerAction<PlayerEvent>) -> Unit>>()

    fun onAnyEvent(vararg events: KClass<out PlayerEvent>, function: PlayerAction<*>.() -> Unit){
        events.forEach {
            playerActions.putIfAbsent(it, ArrayList())
            playerActions[it]!!.add(function)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : PlayerEvent> onEvent(event: KClass<T>, function: PlayerAction<T>.() -> Unit){
        playerActions.putIfAbsent(event, ArrayList())
        playerActions[event]!!.add(function as (PlayerAction<PlayerEvent>) -> Unit)
    }

    fun handleEvent(player: Player, event: PlayerEvent) : Boolean {
        val actions = playerActions[event::class]?:return false
        for(action in actions)
            action.invoke(PlayerAction(event, player))
        return true
    }

    class PlayerAction<out T : PlayerEvent>(val event: T, val player: Player)
}