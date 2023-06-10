package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.game.message.impl.ItemContainerActionMessage
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
class ItemContainerActionMessageDecoder: MessageDecoder<ItemContainerActionMessage>() {

	override fun decode(packet: Packet) : ItemContainerActionMessage {
		val reader = PacketReader(packet)
		return decode(packet.opcode, reader)
	}

	companion object {
		fun decode(packetOpcode: Int, reader: PacketReader): ItemContainerActionMessage{
			
			var interfaceId = -1
			var slot = -1
			var id = -1
			when (packetOpcode) {
				PacketConstants.FIRST_ITEM_CONTAINER_ACTION_OPCODE -> {
					interfaceId = reader.readInt()
					slot = reader.readShortA().toInt()
					id = reader.readShortA().toInt()
				}
				PacketConstants.SECOND_ITEM_CONTAINER_ACTION_OPCODE -> {
					interfaceId = reader.readInt()
					id = reader.readLEShortA().toInt()
					slot = reader.readLEShort().toInt()
				}
				PacketConstants.THIRD_ITEM_CONTAINER_ACTION_OPCODE -> {
					interfaceId = reader.readInt()
					id = reader.readShortA().toInt()
					slot = reader.readShortA().toInt()
				}
				PacketConstants.FOURTH_ITEM_CONTAINER_ACTION_OPCODE -> {
					slot = reader.readShortA().toInt()
					interfaceId = reader.readInt()
					id = reader.readShortA().toInt()
				}
				PacketConstants.FIFTH_ITEM_CONTAINER_ACTION_OPCODE -> {
					interfaceId = reader.readInt()
					slot = reader.readLEShort().toInt()
					id = reader.readLEShort().toInt()
				}
				PacketConstants.SIXTH_ITEM_CONTAINER_ACTION_OPCODE -> {
					interfaceId = reader.readInt()
					slot = reader.readLEShort().toInt()
					id = reader.readLEShort().toInt()
				}
				PacketConstants.SEVENTH_ITEM_CONTAINER_ACTION_OPCODE -> {
					interfaceId = reader.readInt()
					slot = reader.readLEShort().toInt()
					id = reader.readLEShort().toInt()
				}
				PacketConstants.EIGTH_ITEM_CONTAINER_ACTION_OPCODE -> {
					interfaceId = reader.readInt()
					slot = reader.readLEShort().toInt()
					id = reader.readLEShort().toInt()
				}
			}

			return ItemContainerActionMessage(id, slot, interfaceId, packetOpcode)
		}
	}
}
