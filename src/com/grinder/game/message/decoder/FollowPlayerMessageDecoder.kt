package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.FollowPlayerMessage
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class FollowPlayerMessageDecoder: MessageDecoder<FollowPlayerMessage>() {

	override fun decode(packet: Packet) : FollowPlayerMessage {
		val reader = PacketReader(packet)
		var otherPlayersIndex: Int = reader.readLEShort().toInt()
		return FollowPlayerMessage(otherPlayersIndex)
	}
}
