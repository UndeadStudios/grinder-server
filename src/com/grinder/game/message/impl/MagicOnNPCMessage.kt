package com.grinder.game.message.impl

import com.grinder.net.packet.PacketConstants

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   28/11/2019
 * @version 1.0
 */
data class MagicOnNPCMessage(val npcIndex: Int, val spellId: Int)
    : NPCOptionMessage(npcIndex, PacketConstants.MAGE_NPC_OPCODE)