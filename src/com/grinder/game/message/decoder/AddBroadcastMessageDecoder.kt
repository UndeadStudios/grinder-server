package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.game.message.impl.AddBroadcastMessage
import com.grinder.net.packet.Packet
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class AddBroadcastMessageDecoder: MessageDecoder<AddBroadcastMessage>() {

	override fun decode(packet: Packet) : AddBroadcastMessage {
		val reader = PacketReader(packet)
		val duration: Int = reader.readInt()
		val text: String = reader.readString()
		val link: String = reader.readString()
		return AddBroadcastMessage(duration, text, link)
	}
}
