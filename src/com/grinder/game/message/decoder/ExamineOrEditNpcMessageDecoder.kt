package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.game.message.impl.ExamineOrEditNpcMessage
import com.grinder.net.packet.Packet
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   28/11/2019
 * @version 1.0
 */
class ExamineOrEditNpcMessageDecoder : MessageDecoder<ExamineOrEditNpcMessage>() {

    override fun decode(packet: Packet): ExamineOrEditNpcMessage {
        val reader = PacketReader(packet)
        val index = reader.readShort().toInt()
        return ExamineOrEditNpcMessage(index)
    }
}