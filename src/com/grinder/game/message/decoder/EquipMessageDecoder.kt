package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.game.message.impl.EquipMessage
import com.grinder.net.packet.Packet
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class EquipMessageDecoder: MessageDecoder<EquipMessage>() {

	override fun decode(packet: Packet) : EquipMessage {
		val reader = PacketReader(packet)
		val id: Int = reader.readShort().toInt()
		val slot: Int = reader.readShortA().toInt()
		val interfaceId: Int = reader.readShortA().toInt()
		return EquipMessage(id, slot, interfaceId)
	}
}
