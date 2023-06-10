package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.ButtonClickMessage
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class ButtonClickMessageDecoder: MessageDecoder<ButtonClickMessage>() {

	override fun decode(packet: Packet) : ButtonClickMessage {
		val reader = PacketReader(packet)
		val button = reader.readInt()
		return ButtonClickMessage(button)
	}
}
