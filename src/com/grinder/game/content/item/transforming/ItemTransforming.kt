package com.grinder.game.content.item.transforming

import com.grinder.game.entity.agent.player.Player
import java.util.*

/**
 * A class that handles item transformations such as dismantling, restoring or
 * reverting of an item.
 *
 * @author Blake
 */
object ItemTransforming {

    /**
     * A hash collection of the opcodes of the `Transformable` items.
     */
    private val OP_CODES: MutableMap<Int, Int> = HashMap()

    /**
     * A hash collection of all the `Transformable` items.
     */
    private val ITEMS: MutableMap<Int, Transformable> = HashMap()

    /**
     * Handles the item transformation.
     */
	@JvmStatic
	fun handle(player: Player?, itemId: Int, opcode: Int): Boolean {
        val targetOpcode = OP_CODES.getOrDefault(itemId, -1)
        if (opcode != -1 && targetOpcode == opcode) {
            val data = ITEMS[itemId]
            data!!.start(player!!)
            return true
        }
        return false
    }

    init {
        Transformable.values().forEach {
            OP_CODES[it.product.id] = it.opCode
            ITEMS[it.product.id] = it
        }
    }
}