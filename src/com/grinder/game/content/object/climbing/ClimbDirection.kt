package com.grinder.game.content.`object`.climbing

import com.grinder.game.definition.ObjectDefinition

/**
 * Represents climbable directions (in height levels)
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since 19/01/2021
 */
internal enum class ClimbDirection {

    UP, DOWN;

    fun formatted() = name.toLowerCase()

    companion object {
        @JvmStatic
        operator fun get(objectType: ClimbableObjectType, definition: ObjectDefinition, optionIndex: Int): ClimbDirection? {
            if (objectType === ClimbableObjectType.ROPE)
                return UP

            val actions = definition.actions
            if (actions != null && actions.size > optionIndex){
                val action = actions[optionIndex]?.toLowerCase()?:return null
                if (action.contains("up"))
                    return UP
                else if (action.contains("down") || action.contains("push"))
                    return DOWN
            }
           return null
        }
    }
}