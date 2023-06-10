package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.TeleportMessage
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.PlayerStatus
import com.grinder.net.packet.PacketListener
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class TeleportMessageDecoder: MessageDecoder<TeleportMessage>() {

	override fun decode(packet: Packet) : TeleportMessage {
		val reader = PacketReader(packet)
		var type: Int = reader.readByte().toInt()
		var index: Int = reader.readByte().toInt()
		return TeleportMessage(type, index)
	}
}
