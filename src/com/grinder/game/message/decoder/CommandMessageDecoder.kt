package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.CommandMessage
import com.grinder.net.packet.GamePacketReader
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class CommandMessageDecoder: MessageDecoder<CommandMessage>() {

	override fun decode(packet: Packet) : CommandMessage {
		val reader = GamePacketReader(packet)
		val command = reader.readOsString()
		return CommandMessage(command)
	}
}
