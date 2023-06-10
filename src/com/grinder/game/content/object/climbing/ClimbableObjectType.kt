package com.grinder.game.content.`object`.climbing

import com.grinder.game.definition.ObjectDefinition
import com.grinder.game.model.Animation
import com.grinder.util.ObjectID

/**
 * Represents climbable object types.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since 19/01/2021
 */
internal enum class ClimbableObjectType {

    PIPE,
    STAIRS,
    STAIRCASE,
    SPIKEY_CHAIN,
    LADDER,
    VINE,
    ROPE,
    MEMORIAL;

    fun moveAnimation(direction: ClimbDirection): Animation? {
        if (this == VINE || this == STAIRCASE || this == STAIRS || this == PIPE)
            return null
        return when (direction) {
            ClimbDirection.UP -> Animation(828, 25)
            ClimbDirection.DOWN -> Animation(827, 25)
        }
    }

    fun moveToDelay() = if (this == STAIRCASE || this == STAIRS || this == PIPE) 1 else 2

    fun sendClimbMessage() = this != STAIRCASE && this != STAIRS && this != PIPE

    fun formatted() = name.toLowerCase()

    companion object {
        @JvmStatic
        fun forDefinition(definition: ObjectDefinition): ClimbableObjectType? {
            if (definition.id == ObjectID.OBSTACLE_PIPE_5) return PIPE
            if (definition.id == ObjectID.MEMORIAL) return MEMORIAL
            if (definition.getName() != null) {
                val formatted = definition.getName().toLowerCase()
                if (formatted.contains("bridge"))
                    return null
                when {
                    formatted.contains("rope") && !formatted.contains("ropeswing") && !formatted.contains("balancing") -> return ROPE
                    formatted.contains("vine") -> return VINE
                    formatted.contains("ladder") -> return LADDER
                    formatted.contains("spikey chain") -> return SPIKEY_CHAIN
                    formatted.contains("staircase") -> return STAIRCASE
                    formatted.contains("stairs") -> return STAIRS
                }
            }
            return null
        }
    }
}