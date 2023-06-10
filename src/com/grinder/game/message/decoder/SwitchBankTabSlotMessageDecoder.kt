package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.SwitchBankTabSlotMessage
import com.grinder.game.entity.agent.player.Player
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
class SwitchBankTabSlotMessageDecoder: MessageDecoder<SwitchBankTabSlotMessage>() {

	override fun decode(packet: Packet) : SwitchBankTabSlotMessage {
		val reader = PacketReader(packet)
		var fromTab: Int = reader.readInt()
		var toTab: Int = reader.readInt()
		return SwitchBankTabSlotMessage(fromTab, toTab)
	}
}
