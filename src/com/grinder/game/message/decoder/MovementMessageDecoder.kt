package com.grinder.game.message.decoder

import com.grinder.game.message.MessageDecoder
import com.grinder.game.message.impl.MovementMessage
import com.grinder.game.model.Position
import com.grinder.net.packet.Packet
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class MovementMessageDecoder: MessageDecoder<MovementMessage>() {

    override fun decode(packet: Packet) : MovementMessage {
        val reader = PacketReader(packet)

        val steps = (packet.length - 5) / 2
        val path = Array(steps) { IntArray(2) }

        val shiftTeleport = reader.readByte().toInt() == 1

        val x = reader.readLEShortA().toInt()
        for (i in 0 until steps) {
            path[i][0] = reader.readByte().toInt()
            path[i][1] = reader.readByte().toInt()
        }
        val y = reader.readLEShort().toInt()
        val run = reader.readByteC().toInt() == 1

        val positions = Array(steps + 1) {
            if(it == 0)
                Position(x, y)
            else
                Position(path[it-1][0] + x, path[it-1][1] + y)
        }

        return MovementMessage(shiftTeleport, run, positions)
    }

    companion object {
        fun decode(reader: PacketReader) : MovementMessage{
            val steps = (reader.readableBytes() - 5) / 2
            val path = Array(steps) { IntArray(2) }

            val shiftTeleport = reader.readByte().toInt() == 1

            val x = reader.readLEShortA().toInt()
            for (i in 0 until steps) {
                path[i][0] = reader.readByte().toInt()
                path[i][1] = reader.readByte().toInt()
            }
            val y = reader.readLEShort().toInt()
            val run = reader.readByteC().toInt() == 1

            val positions = Array(steps + 1) {
                if(it == 0)
                    Position(x, y)
                else
                    Position(path[it-1][0] + x, path[it-1][1] + y)
            }

            return MovementMessage(shiftTeleport, run, positions)
        }
    }
}
