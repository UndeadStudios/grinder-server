package com.grinder.game.message.impl

import com.grinder.game.message.Message

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
data class ChangeAppearanceMessage(
        var gender: Int,
        val style: IntArray,
        val color: IntArray
) : Message {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChangeAppearanceMessage) return false

        if (gender != other.gender) return false
        if (!style.contentEquals(other.style)) return false
        if (!color.contentEquals(other.color)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = gender
        result = 31 * result + style.contentHashCode()
        result = 31 * result + color.contentHashCode()
        return result
    }
}
