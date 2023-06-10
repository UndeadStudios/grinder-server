package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.ClanChatMessageMessage
import com.grinder.game.content.clan.GlobalClanChatManager
import com.grinder.game.entity.agent.player.Player
import com.grinder.net.packet.PacketListener
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class ClanChatMessageMessageDecoder: MessageDecoder<ClanChatMessageMessage>() {

	override fun decode(packet: Packet) : ClanChatMessageMessage {
		val reader = PacketReader(packet)
		var crowns: String = reader.readString()
		var message: String = reader.readString()
		return ClanChatMessageMessage(crowns, message)
	}
}
