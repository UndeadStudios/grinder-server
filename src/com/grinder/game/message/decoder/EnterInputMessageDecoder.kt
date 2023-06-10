package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.game.message.impl.EnterInputMessage
import com.grinder.net.packet.Packet
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class EnterInputMessageDecoder: MessageDecoder<EnterInputMessage>() {

	override fun decode(packet: Packet) : EnterInputMessage {
		val reader = PacketReader(packet)
		val name: String = reader.readString()
		val amount: Int = reader.readInt()
		return EnterInputMessage(name, amount)
	}
}
