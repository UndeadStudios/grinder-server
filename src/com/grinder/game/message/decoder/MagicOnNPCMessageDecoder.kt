package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.MagicOnNPCMessage
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class MagicOnNPCMessageDecoder: MessageDecoder<MagicOnNPCMessage>() {

	override fun decode(packet: Packet) : MagicOnNPCMessage {
		val reader = PacketReader(packet)
		val index: Int = reader.readLEShortA().toInt()
		val spellId: Int = reader.readShortA().toInt()
		return MagicOnNPCMessage(index, spellId)
	}

	companion object {
		fun decode(reader: PacketReader) : MagicOnNPCMessage {
			val index: Int = reader.readLEShortA().toInt()
			val spellId: Int = reader.readShortA().toInt()
			return MagicOnNPCMessage(index, spellId)
		}
	}
}
