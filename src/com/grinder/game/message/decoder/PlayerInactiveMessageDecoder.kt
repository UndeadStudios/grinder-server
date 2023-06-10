package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.PlayerInactiveMessage
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class PlayerInactiveMessageDecoder: MessageDecoder<PlayerInactiveMessage>() {

	override fun decode(packet: Packet) : PlayerInactiveMessage {
		val reader = PacketReader(packet)
		return PlayerInactiveMessage()
	}
}
