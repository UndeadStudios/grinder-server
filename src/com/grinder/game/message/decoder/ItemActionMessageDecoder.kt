package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.game.message.impl.ItemActionMessage
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
class ItemActionMessageDecoder: MessageDecoder<ItemActionMessage>() {

	override fun decode(packet: Packet) : ItemActionMessage {
		val reader = PacketReader(packet)
		return decode(packet.opcode, reader)
	}

	companion object {
		fun decode(opcode: Int, reader: PacketReader) : ItemActionMessage{
			var interfaceId = -1
			var slot = -1
			var itemId = -1
			when(opcode) {
				PacketConstants.SECOND_ITEM_ACTION_OPCODE -> {
					interfaceId = reader.readLEShortA().toInt()
					slot = reader.readLEShort().toInt()
					itemId = reader.readShortA().toInt()
				}
				PacketConstants.FIRST_ITEM_ACTION_OPCODE -> {
					interfaceId = reader.readUnsignedShort()
					itemId = reader.readShort().toInt()
					slot = reader.readShort().toInt()
				}
				PacketConstants.THIRD_ITEM_ACTION_OPCODE -> {
					itemId = reader.readShortA().toInt()
					slot = reader.readLEShortA().toInt()
					interfaceId = reader.readLEShortA().toInt()
				}
			}
			return ItemActionMessage(itemId, slot, interfaceId, opcode)
		}
	}
}
