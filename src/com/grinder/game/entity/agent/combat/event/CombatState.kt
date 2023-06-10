package com.grinder.game.entity.agent.combat.event

/**
 * Represents a type of [CombatEvent] that is a constant
 *
 * @see CombatEventListener for listening to these events.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   22/11/2019
 * @version 1.0
 */
enum class CombatState : CombatEvent {

    SEQUENCED_COMBAT,

    SEQUENCED_ATTACK,

    STARTING_ATTACK,

    FINISHED_ATTACK,

    ATTACKING_TARGET,

    /**
     * This event is fired when a [Combat] system re-targets,
     * note that this event also fires if the new target
     * is the same as the old target.
     */
    LOCKED_TARGET,
}