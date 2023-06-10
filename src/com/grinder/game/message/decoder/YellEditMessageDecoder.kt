package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.YellEditMessage
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class YellEditMessageDecoder: MessageDecoder<YellEditMessage>() {

	override fun decode(packet: Packet) : YellEditMessage {
		val reader = PacketReader(packet)
		var title: String = reader.readString()
		var color: Int = reader.readInt()
		return YellEditMessage(title, color)
	}
}
