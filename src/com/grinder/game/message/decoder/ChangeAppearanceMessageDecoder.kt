package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.ChangeAppearanceMessage
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class ChangeAppearanceMessageDecoder: MessageDecoder<ChangeAppearanceMessage>() {

	override fun decode(packet: Packet) : ChangeAppearanceMessage {
		val reader = PacketReader(packet)
		val gender: Int = reader.readByte().toInt()
		val style = IntArray(7) {
			reader.readByte().toInt()
		}
		val color = IntArray(5) {
			reader.readByte().toInt()
		}
		return ChangeAppearanceMessage(gender, style, color)
	}
}
