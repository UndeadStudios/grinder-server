package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.DialogueMessage
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.interfaces.dialogue.DialogueManager
import com.grinder.net.packet.PacketListener
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class DialogueMessageDecoder: MessageDecoder<DialogueMessage>() {

	override fun decode(packet: Packet) : DialogueMessage {
		val reader = PacketReader(packet)
		return DialogueMessage()
	}
}
