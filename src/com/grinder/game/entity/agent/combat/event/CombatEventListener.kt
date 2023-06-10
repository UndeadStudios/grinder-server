package com.grinder.game.entity.agent.combat.event

/**
 * Represents a listener that listens to [CombatState]s.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   22/11/2019
 * @version 1.0
 */
@FunctionalInterface
interface CombatEventListener {

    /**
     * This method is invoked whenever the [Combat] system
     * is notified of a new [CombatState].
     *
     * @param event the [CombatState] that was fired
     *
     * @return 'true' if this listener should be removed,
     *          'false' if not.
     */
    fun on(event: CombatEvent) : Boolean

}