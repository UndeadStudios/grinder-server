package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.DropItemMessage
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class DropItemMessageDecoder: MessageDecoder<DropItemMessage>() {

	override fun decode(packet: Packet) : DropItemMessage {
		val reader = PacketReader(packet)
		val id: Int = reader.readUnsignedShortA()
		val interfaceId: Int = reader.readUnsignedShort()
		val itemSlot: Int = reader.readUnsignedShortA()
		return DropItemMessage(id, interfaceId, itemSlot)
	}
}
