package com.grinder.game.content.skill.skillable.impl.hunter

/**
 * Represents different states a [HunterTrap] can be in.
 *
 * This state is serialised so make sure not to rename it.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   29/11/2019
 * @version 1.0
 */
enum class HunterTrapState {

    /**
     * The trap is active, waiting for prey to get caught in it.
     */
    WAITING,

    /**
     * The trap has failed to catch a prey.
     */
    FAILED,

    /**
     * The trap caught a prey and can be looted.
     */
    SUCCESS,

    /**
     * The trap after the caught prey has been looted.
     *
     * This is a temporary state that prevents player from looting the trap multiple times,
     * whilst the trap game object is awaiting removal.
     */
    LOOTED

}