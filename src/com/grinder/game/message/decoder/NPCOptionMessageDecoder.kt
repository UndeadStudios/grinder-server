package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.game.message.impl.NPCOptionMessage
import com.grinder.net.packet.Packet
import com.grinder.net.packet.PacketConstants
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class NPCOptionMessageDecoder : MessageDecoder<NPCOptionMessage>(){

    override fun decode(packet: Packet): NPCOptionMessage {
        val reader = PacketReader(packet)
        var index = -1
        when (packet.opcode) {
            PacketConstants.ATTACK_NPC_OPCODE -> index = reader.readShortA().toInt()
            PacketConstants.FIRST_CLICK_NPC_OPCODE -> index = reader.readLEShort().toInt()
            PacketConstants.SECOND_CLICK_NPC_OPCODE -> index = reader.readLEShortA().toInt()
            PacketConstants.THIRD_CLICK_NPC_OPCODE -> index = reader.readShort().toInt()
            PacketConstants.FOURTH_CLICK_NPC_OPCODE -> index = reader.readLEShort().toInt()
        }
        return NPCOptionMessage(index, packet.opcode)
    }

    companion object {
        fun decode(opcode: Int, reader: PacketReader) : NPCOptionMessage {
            var index = -1
            when (opcode) {
                PacketConstants.ATTACK_NPC_OPCODE -> index = reader.readShortA().toInt()
                PacketConstants.FIRST_CLICK_NPC_OPCODE -> index = reader.readLEShort().toInt()
                PacketConstants.SECOND_CLICK_NPC_OPCODE -> index = reader.readLEShortA().toInt()
                PacketConstants.THIRD_CLICK_NPC_OPCODE -> index = reader.readShort().toInt()
                PacketConstants.FOURTH_CLICK_NPC_OPCODE -> index = reader.readLEShort().toInt()
            }
            return NPCOptionMessage(index, opcode)
        }
    }

}