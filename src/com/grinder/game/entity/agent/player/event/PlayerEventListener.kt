package com.grinder.game.entity.agent.player.event

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
@FunctionalInterface
interface PlayerEventListener {

    fun on(event: PlayerEvent) : Boolean

}