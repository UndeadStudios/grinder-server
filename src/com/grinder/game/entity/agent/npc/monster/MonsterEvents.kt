package com.grinder.game.entity.agent.npc.monster

/**
 * Represents events that are fired whenever
 * an action denoted by the event name occurs.
 *
 * @see MonsterEventListener for a listener of these events
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
enum class MonsterEvents : MonsterEvent {

    /**
     * Fired whenever a [Monster] [dies][Monster.appendDeath].
     */
    DYING,

    /**
     * Fired when a [Monster] is added to the game world.
     */
    ADDED,

    /**
     * Fired when a [Monster] is removed from the game world.
     */
    REMOVED,

    /**
     * Fired when a [Monster] its [position][Monster.position] changes.
     */
    MOVED,

    /**
     * Fired at the start of [monster sequencing][Monster.sequence].
     */
    PRE_SEQUENCE,

    /**
     * Fired at the end of [monster sequencing][Monster.sequence].
     */
    POST_SEQUENCE

}