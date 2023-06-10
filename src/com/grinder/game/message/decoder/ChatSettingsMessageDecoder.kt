package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.ChatSettingsMessage
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class ChatSettingsMessageDecoder: MessageDecoder<ChatSettingsMessage>() {

	override fun decode(packet: Packet) : ChatSettingsMessage {
		val reader = PacketReader(packet)
		var publicMode: Int = reader.readByte().toInt()
		var privateMode: Int = reader.readByte().toInt()
		var tradeMode: Int = reader.readByte().toInt()
		return ChatSettingsMessage(publicMode, privateMode, tradeMode)
	}
}
