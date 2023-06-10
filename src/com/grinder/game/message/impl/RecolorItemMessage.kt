package com.grinder.game.message.impl

import com.grinder.game.message.Message

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
data class RecolorItemMessage(val itemId: Int, val colors: IntArray): Message {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RecolorItemMessage) return false

        if (itemId != other.itemId) return false
        if (!colors.contentEquals(other.colors)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = itemId
        result = 31 * result + colors.contentHashCode()
        return result
    }
}