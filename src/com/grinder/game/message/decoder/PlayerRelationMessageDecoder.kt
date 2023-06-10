package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.game.message.impl.PlayerRelationMessage
import com.grinder.net.packet.Packet
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class PlayerRelationMessageDecoder: MessageDecoder<PlayerRelationMessage>() {

	override fun decode(packet: Packet) : PlayerRelationMessage {
		val reader = PacketReader(packet)
		val username: Long = reader.readLong()
		return PlayerRelationMessage(username, packet.opcode)
	}
}
