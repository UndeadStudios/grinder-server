package com.grinder.game.entity.agent.combat.event

/**
 * Represents an event that can fire during combat processes.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   02/07/2020
 * @version 1.0
 */
interface CombatEvent {

    /**
     * Identifying string for this event,
     * used for debugging of combat.
     */
    fun debugIdentifier(): String = this::class.java.simpleName

}