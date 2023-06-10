package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.game.message.impl.ChatMessage
import com.grinder.net.packet.Packet
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class ChatMessageDecoder: MessageDecoder<ChatMessage>() {

	override fun decode(packet: Packet) : ChatMessage {
		val reader = PacketReader(packet)
		val clanMessage: String = reader.readString()
		val color: Int = reader.readByte().toInt()
		val effect: Int = reader.readByte().toInt()
		val chatMessage: String = reader.readString()
		return ChatMessage(clanMessage, color, effect, chatMessage)
	}
}
