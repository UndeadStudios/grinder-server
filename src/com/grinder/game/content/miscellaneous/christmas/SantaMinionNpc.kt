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
class SantaMinionNpc(id: Int, position: Position?) : NPC(id, position) {

    init {
        movementCoordinator.radius = 4
    }



}