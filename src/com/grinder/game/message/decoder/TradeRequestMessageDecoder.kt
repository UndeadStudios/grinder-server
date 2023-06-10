package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.TradeRequestMessage
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class TradeRequestMessageDecoder: MessageDecoder<TradeRequestMessage>() {

	override fun decode(packet: Packet) : TradeRequestMessage {
		val reader = PacketReader(packet)
		val index: Int = reader.readLEShort().toInt()
		return TradeRequestMessage(index)
	}
}
