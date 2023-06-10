package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.game.message.impl.InterfaceActionClickMessage
import com.grinder.net.packet.Packet
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   28/11/2019
 * @version 1.0
 */
class InterfaceActionClickMessageDecoder : MessageDecoder<InterfaceActionClickMessage>() {

    override fun decode(packet: Packet): InterfaceActionClickMessage {
        val reader = PacketReader(packet)
        val interfaceId = reader.readInt()
        val action = reader.readByte().toInt()
        return InterfaceActionClickMessage(interfaceId, action)
    }
}