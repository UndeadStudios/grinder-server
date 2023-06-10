package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.game.message.impl.MagicOnObjectMessage
import com.grinder.net.packet.Packet
import com.grinder.game.message.impl.MagicOnPlayerMessage
import com.grinder.net.packet.PacketReader

class MagicOnObjectMessageDecoder: MessageDecoder<MagicOnObjectMessage>() {

	override fun decode(packet: Packet) : MagicOnObjectMessage {
		val reader = PacketReader(packet)
		var objectIndex: Int = reader.readLEShort().toInt()
		var objectX: Int = reader.readInt()
		var objectY: Int = reader.readInt()
		var spellId: Int = reader.readLEShort().toInt()
		return MagicOnObjectMessage(objectIndex, objectX, objectY, spellId)
	}
}
