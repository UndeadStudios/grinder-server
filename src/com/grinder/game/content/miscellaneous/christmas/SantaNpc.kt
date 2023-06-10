package com.grinder.game.content.miscellaneous.christmas

import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.model.Position

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   20/12/2019
 * @version 1.0
 */
class SantaNpc(id: Int, position: Position?) : NPC(id, position) {

    init {
        movementCoordinator.radius = 1
    }

    var cycle = 0

    private val shouts = arrayOf("*Hik*", "25 now is! *Hik*")

    override fun sequence() {
        super.sequence()

        if(++cycle % 15 == 0)
            say(shouts.random())

        if(cycle >= Integer.MAX_VALUE)
            cycle = 0
    }

    override fun getSize() = 3
}