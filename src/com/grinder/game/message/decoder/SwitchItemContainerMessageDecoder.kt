package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.SwitchItemContainerMessage
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.item.Item
import com.grinder.game.entity.agent.player.PlayerStatus
import com.grinder.game.model.item.container.bank.Bank
import com.grinder.game.model.item.container.bank.BankConstants
import com.grinder.net.packet.PacketListener
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class SwitchItemContainerMessageDecoder: MessageDecoder<SwitchItemContainerMessage>() {

	override fun decode(packet: Packet) : SwitchItemContainerMessage {
		val reader = PacketReader(packet)
		var fromContainerId: Int = reader.readInt()
		var toContainerId: Int = reader.readInt()
		var fromSlot: Int = reader.readLEShortA().toInt()
		var toSlot: Int = reader.readLEShort().toInt()
		return SwitchItemContainerMessage(fromContainerId, toContainerId, fromSlot, toSlot)
	}
}
