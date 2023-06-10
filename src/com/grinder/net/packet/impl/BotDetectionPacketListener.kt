package com.grinder.net.packet.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.net.packet.DataType
import com.grinder.net.packet.GamePacketReader
import com.grinder.net.packet.PacketListener
import com.grinder.net.packet.PacketReader

class BotDetectionPacketListener : PacketListener{

    override fun handleMessage(player: Player, packetReader: PacketReader, packetOpcode: Int) {
        val packet = packetReader.packet
        val reader = GamePacketReader(packet)
        when(packetOpcode){
            59 -> {
//                val leftmostBit =
//                when(reader.getUnsigned(DataType.BYTE).toInt()){
//                    0 -> {
//                        val values = reader.getUnsigned(DataType.SHORT)
//                        val unchangedCoordsTick = (values shr 12).toInt()
//                        val deltaX = (((values shr 6) and 63) - 32).toInt()
//                        val deltaY = ((values and 63) - 32).toInt()
//                        val movement = MouseMovement(unchangedCoordsTick, deltaX, deltaY)
//                    }
//                    1 -> {
//
//                    }
//                }
            }
        }
    }

    class MouseMovement(stationaryTicks: Int, deltaX: Int, deltaY: Int)

}