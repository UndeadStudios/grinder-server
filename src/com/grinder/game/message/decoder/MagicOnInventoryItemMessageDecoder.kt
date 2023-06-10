package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.game.message.impl.MagicOnInventoryItemMessage
import com.grinder.net.packet.Packet
import com.grinder.net.packet.PacketReader

/**
 * A [MessageDecoder] for [MagicOnInventoryItemMessage] messages.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class MagicOnInventoryItemMessageDecoder: MessageDecoder<MagicOnInventoryItemMessage>() {

	override fun decode(packet: Packet) : MagicOnInventoryItemMessage {
		val reader = PacketReader(packet)
		val slot: Int = reader.readShort().toInt()
		val itemId: Int = reader.readShortA().toInt()
		val childId: Int = reader.readShort().toInt()
		val spellId: Int = reader.readShortA().toInt()
		return MagicOnInventoryItemMessage(slot, itemId, childId, spellId)
	}
}
