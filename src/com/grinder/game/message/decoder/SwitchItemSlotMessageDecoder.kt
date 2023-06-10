package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.SwitchItemSlotMessage
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.item.container.bank.Bank
import com.grinder.game.model.item.container.bank.BankConstants
import com.grinder.game.model.item.container.player.Inventory
import com.grinder.net.packet.PacketListener
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class SwitchItemSlotMessageDecoder: MessageDecoder<SwitchItemSlotMessage>() {

	override fun decode(packet: Packet) : SwitchItemSlotMessage {
		val reader = PacketReader(packet)
		var interfaceId: Int = reader.readInt()
		var fromSlot: Int = reader.readLEShortA().toInt()
		var toSlot: Int = reader.readLEShort().toInt()
		return SwitchItemSlotMessage(interfaceId, fromSlot, toSlot)
	}
}
