package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.game.message.impl.ObjectActionMessage
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
class ObjectActionMessageDecoder: MessageDecoder<ObjectActionMessage>() {

	override fun decode(packet: Packet) : ObjectActionMessage {
		val reader = PacketReader(packet)
		return decode(packet.opcode, reader)
	}

	companion object {
		fun decode(opcode: Int, reader: PacketReader) : ObjectActionMessage {
			var x = -1
			var id = -1
			var y = -1
			when (opcode) {
				PacketConstants.OBJECT_FIRST_CLICK_OPCODE -> {
					x = reader.readLEShortA().toInt()
					id = reader.readInt()
					y = reader.readUnsignedShortA()
				}
				PacketConstants.OBJECT_SECOND_CLICK_OPCODE -> {
					id = reader.readInt()
					y = reader.readLEShort().toInt()
					x = reader.readUnsignedShortA()
				}
				PacketConstants.OBJECT_THIRD_CLICK_OPCODE -> {
					x = reader.readLEShort().toInt()
					y = reader.readShort().toInt()
					id = reader.readInt()
				}
				PacketConstants.OBJECT_FOURTH_CLICK_OPCODE -> {
					x = reader.readLEShortA().toInt()
					id = reader.readInt()
					y = reader.readLEShortA().toInt()
				}
				PacketConstants.OBJECT_FIFTH_CLICK_OPCODE -> {
					id = reader.readInt()
					x = reader.readUnsignedShortA()
					y = reader.readUnsignedShort()
				}
			}
			return ObjectActionMessage(id, x, y, opcode)
		}
	}
}
