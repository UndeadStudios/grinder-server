package com.grinder.game.entity.updating.block

import com.google.gson.annotations.Expose
import com.grinder.game.entity.updating.UpdateBlock

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   24/03/2020
 * @version 1.0
 */
data class ColorItemBlock(
        @Expose val colors: Array<IntArray>
) : UpdateBlock() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ColorItemBlock) return false

        if (!colors.contentDeepEquals(other.colors)) return false

        return true
    }

    override fun hashCode(): Int {
        return colors.contentDeepHashCode()
    }
}