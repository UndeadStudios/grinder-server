package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.SecondItemOnGroundOptionMessage
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class SecondItemOnGroundOptionMessageDecoder: MessageDecoder<SecondItemOnGroundOptionMessage>() {

	override fun decode(packet: Packet) : SecondItemOnGroundOptionMessage {
		val reader = PacketReader(packet)
		var y: Int = reader.readLEShort().toInt()
		var itemId: Int = reader.readShort().toInt()
		var x: Int = reader.readLEShort().toInt()
		return SecondItemOnGroundOptionMessage(y, itemId, x)
	}
}
