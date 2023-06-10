package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.ChangePasswordMessage
import com.grinder.game.entity.agent.player.Player
import com.grinder.net.packet.PacketListener
import com.grinder.net.packet.PacketReader
import com.grinder.util.Logging

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class ChangePasswordMessageDecoder: MessageDecoder<ChangePasswordMessage>() {

	override fun decode(packet: Packet) : ChangePasswordMessage {
		val reader = PacketReader(packet)
		var enteredPassword: String = reader.readString()
		var newPassword: String = reader.readString()
		var confirmationPassword: String = reader.readString()
		return ChangePasswordMessage(enteredPassword, newPassword, confirmationPassword)
	}
}
