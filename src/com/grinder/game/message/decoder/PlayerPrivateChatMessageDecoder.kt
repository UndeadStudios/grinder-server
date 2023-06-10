package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.game.message.impl.PlayerPrivateChatMessage
import com.grinder.net.packet.Packet
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class PlayerPrivateChatMessageDecoder: MessageDecoder<PlayerPrivateChatMessage>() {

	override fun decode(packet: Packet) : PlayerPrivateChatMessage {
		val reader = PacketReader(packet)
		val username: Long = reader.readLong()
		val message: String = reader.readString()
		return PlayerPrivateChatMessage(username, message.capitalize())
	}
}
