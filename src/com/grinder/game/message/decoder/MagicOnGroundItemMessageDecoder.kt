package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.game.message.impl.MagicOnGroundItemMessage
import com.grinder.net.packet.Packet
import com.grinder.net.packet.PacketReader

/**
 * A [MessageDecoder] for [MagicOnGroundItemMessage] messages.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   21/07/2021
 * @version 1.0
 */
class MagicOnGroundItemMessageDecoder: MessageDecoder<MagicOnGroundItemMessage>() {

	override fun decode(packet: Packet) : MagicOnGroundItemMessage {
		val reader = PacketReader(packet)
		val itemY = reader.readShort().toInt()
		val itemId = reader.readShort().toInt()
		val itemX = reader.readShort().toInt()
		val spellId = reader.readShort().toInt()
		return MagicOnGroundItemMessage(itemId, spellId, itemX, itemY)
	}
}
