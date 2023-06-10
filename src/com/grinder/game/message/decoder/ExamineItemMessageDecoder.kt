package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.ExamineItemMessage
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class ExamineItemMessageDecoder: MessageDecoder<ExamineItemMessage>() {

	override fun decode(packet: Packet) : ExamineItemMessage {
		val reader = PacketReader(packet)
		val itemId: Int = reader.readShort().toInt()
		val interfaceId: Int = reader.readShort().toInt()
		return ExamineItemMessage(itemId, interfaceId)
	}
}
