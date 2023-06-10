package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.game.message.impl.RecolorItemMessage
import com.grinder.net.packet.Packet
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class RecolorItemMessageDecoder : MessageDecoder<RecolorItemMessage>(){

    override fun decode(packet: Packet): RecolorItemMessage {
        val reader = PacketReader(packet)
        val itemId = reader.readInt()
        val colorsLength = reader.readByte().toInt()
        val colors = IntArray(colorsLength) {
            reader.readInt()
        }
        return RecolorItemMessage(itemId, colors)
    }

    companion object {
        fun decode(reader: PacketReader): RecolorItemMessage {
            val itemId = reader.readInt()
            val colorsLength = reader.readByte().toInt()
            val colors = IntArray(colorsLength) {
                reader.readInt()
            }
            return RecolorItemMessage(itemId, colors)
        }
    }
}