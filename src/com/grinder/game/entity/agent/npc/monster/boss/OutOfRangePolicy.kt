package com.grinder.game.entity.agent.npc.monster.boss

import com.grinder.game.entity.agent.npc.monster.Monster

/**
 * This enum represents a policy that can be overridden
 * to determine what a [Monster]'s preferred response is
 * given the situation where there is no target in range
 * for the selected [AttackType].
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   22/04/2020
 * @version 1.0
 */
enum class OutOfRangePolicy {

    /**
     * Makes the [Monster] walk to the target.
     */
    TRACE_TO_TARGET,

    /**
     * Exclude the [AttackType] and try to find a different one
     * that can be used instead.
     */
    EXCLUDE_TYPE
}