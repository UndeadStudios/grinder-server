package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.MagicOnPlayerMessage
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class MagicOnPlayerMessageDecoder: MessageDecoder<MagicOnPlayerMessage>() {

	override fun decode(packet: Packet) : MagicOnPlayerMessage {
		val reader = PacketReader(packet)
		var playerIndex: Int = reader.readShortA().toInt()
		var spellId: Int = reader.readLEShort().toInt()
		return MagicOnPlayerMessage(playerIndex, spellId)
	}
}
