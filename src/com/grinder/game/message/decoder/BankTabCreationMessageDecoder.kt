package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.BankTabCreationMessage
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class BankTabCreationMessageDecoder: MessageDecoder<BankTabCreationMessage>() {

	override fun decode(packet: Packet) : BankTabCreationMessage {
		val reader = PacketReader(packet)
		val interfaceId = reader.readInt()
		val sourceSlot = reader.readShort().toInt()
		val targetTab = reader.readLEShort().toInt()
		return BankTabCreationMessage(interfaceId, sourceSlot, targetTab)
	}
}
