package com.grinder.game.entity.agent.npc.monster

/**
 * Represents a listener for [monster events][MonsterEvents].
 *
 * @see Monster.onEvent for usage
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
@FunctionalInterface
interface MonsterEventListener {

    /**
     * Do some action when the argued [event] is fired.
     */
    fun on(event: MonsterEvents)

}