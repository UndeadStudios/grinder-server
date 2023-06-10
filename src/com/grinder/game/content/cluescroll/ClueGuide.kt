package com.grinder.game.content.cluescroll

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.interfaces.IntefaceID

/**
 * Represents a guide to complete a clue scroll
 *
 * @author Pb600
 */
class ClueGuide {
    val interfaceID: Int
    val description: Array<String>?

    constructor(vararg description: String) {
        interfaceID = IntefaceID.SCROLL_TEXT_INTERFACE
        this.description = arrayOf(*description)
    }

    constructor(interfaceID: Int) {
        this.interfaceID = interfaceID
        description = null
    }

    fun display(player: Player) {
        val packetSender = player.packetSender
        if (description != null) {
            packetSender.clearInterfaceText(6968, 6976)
            var child = 6971
            for (line in description)
                packetSender.sendString(child++, line)
        }
        if (interfaceID == 7221 || interfaceID == 9196) {
            packetSender.sendString(4268, "")
            packetSender.sendString(4269, "")
            packetSender.sendString(358, "")
        }
        packetSender.sendInterface(interfaceID)
    }

    override fun toString(): String {
        return "ClueGuide [interfaceID=$interfaceID, description=$description]"
    }

    companion object {

        @JvmStatic
		fun renderText(player: Player, vararg description: String) {
            val packetSender = player.packetSender
            packetSender.clearInterfaceText(6968, 6976)
            var child = 6971
            for (line in description)
                packetSender.sendString(child++, line)
            packetSender.sendInterface(IntefaceID.SCROLL_TEXT_INTERFACE)
        }
    }
}