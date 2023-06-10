package com.grinder.game.message.impl
import com.google.common.base.Preconditions
import com.grinder.game.message.Message
import com.grinder.game.model.Position

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
data class MovementMessage(
        val teleport: Boolean,
        val running: Boolean,
        val steps: Array<Position>
) : Message {

    init {
        Preconditions.checkArgument(steps.size >= 0, "Number of steps can't be negative.");
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MovementMessage) return false

        if (teleport != other.teleport) return false
        if (running != other.running) return false
        if (!steps.contentEquals(other.steps)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = teleport.hashCode()
        result = 31 * result + running.hashCode()
        result = 31 * result + steps.contentHashCode()
        return result
    }


}