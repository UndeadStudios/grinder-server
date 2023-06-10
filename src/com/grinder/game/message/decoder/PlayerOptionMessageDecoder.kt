package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.game.message.impl.PlayerOptionMessage
import com.grinder.net.packet.Packet
import com.grinder.net.packet.PacketConstants
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class PlayerOptionMessageDecoder: MessageDecoder<PlayerOptionMessage>() {

	override fun decode(packet: Packet) : PlayerOptionMessage {
		val reader = PacketReader(packet)
		var index = -1

		when (packet.opcode) {
			PacketConstants.FIRST_PLAYER_ACTION -> {
				index = reader.readShort().toInt() and 0xFFFF
			}
			PacketConstants.SECOND_PLAYER_ACTION ->{
				index = reader.readLEShort().toInt()
			}
			PacketConstants.PLAYER_OPTION_2_OPCODE -> {
				index = reader.readShort().toInt() and 0xFFFF
			}
			PacketConstants.PLAYER_OPTION_3_OPCODE -> {
				index = reader.readLEShortA().toInt() and 0xFFFF
			}
		}
		return PlayerOptionMessage(index, packet.opcode)
	}
}
