package com.grinder.net.packet.impl

import com.grinder.net.packet.DataType
import com.grinder.net.packet.GamePacketReader
import com.grinder.net.packet.Packet
import com.grinder.net.packet.PacketType
import io.netty.buffer.Unpooled
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@ExperimentalStdlibApi
internal class BotDetectionPacketListenerTest {

    /**
     * x domain = [-32, 31]
     * y domain = [-32, 31]
     * unchangedCoordsTick domain = [0, 7]
     */
    @Test
    fun testShort(){
        val actualDeltaX = -31
        val actualDeltaY = -30
        val deltaX = actualDeltaX + 32
        val deltaY = actualDeltaY + 32
        val unchangedCoordsTick = 2
    val toPack = deltaY + (deltaX shl 6) + (unchangedCoordsTick shl 12)
    val buffer = byteArrayOf(
            (toPack shr 8).toByte(),
            toPack.toByte())
    Assertions.assertEquals(0, buffer[0])

        val packet = Packet(59, PacketType.VARIABLE_BYTE, Unpooled.wrappedBuffer(buffer))
        val reader = GamePacketReader(packet)

        val packed = reader.getUnsigned(DataType.SHORT)

        val firstByte = buffer[0]
        Assertions.assertEquals(0, firstByte)

        val decodedDeltaX = ((packed shr 6) and 63).toInt() - 32
        Assertions.assertEquals(actualDeltaX, decodedDeltaX)

        val decodedDeltaY = (packed and 63).toInt() - 32
        Assertions.assertEquals(actualDeltaY, decodedDeltaY)

        val decodedUnchangedCoordsTick = (packed shr 12).toInt()
        Assertions.assertEquals(unchangedCoordsTick, decodedUnchangedCoordsTick)
    }

    /**
     * x domain = [0, 764]
     * y domain = [0, 502]
     * unchangedCoordsTick domain = [0, 7]
     */
    @Test
    fun testTryByte(){
        val x = 0
        val y = 0
        val coordinates = x + y * 765
        val unchangedCoordsTick = 0
        val toEncode = (unchangedCoordsTick shl 19) + 0x800000 + coordinates
        val buffer = byteArrayOf(
                (toEncode shr 16).toByte(),
                (toEncode shr 8).toByte(),
                toEncode.toByte())

        val packet = Packet(59, PacketType.VARIABLE_BYTE, Unpooled.wrappedBuffer(buffer))
        val reader = GamePacketReader(packet)

        val packed = reader.getUnsigned(DataType.TRI_BYTE)

        Assertions.assertEquals(0x8, buffer[0])

        val decodedCoordinates = (packed and 0x7FFFF).toInt()
        Assertions.assertEquals(coordinates, decodedCoordinates)

        val decodedX = (decodedCoordinates) % 765
        Assertions.assertEquals(x, decodedX)

        val decodedY = (decodedCoordinates-decodedX) / 765
        Assertions.assertEquals(y, decodedY)

        val decodedUnchangedCoordsTick = ((packed shr 19) and 0xf).toInt()
        Assertions.assertEquals(unchangedCoordsTick, decodedUnchangedCoordsTick)
    }

    /**
     * x domain = [0, 764]
     * y domain = [0, 502]
     * unchangedCoordsTick domain = [0, 2046]
     */
    @Test
    fun testInt(){
        val x = 6
        val y = 2
        val coordinates = x + y * 765
        val unchangedCoordsTick = 8

        val toEncode = (unchangedCoordsTick shl 19) - 0x40000000 + coordinates
        val buffer = byteArrayOf(
                (toEncode shr 24).toByte(),
                (toEncode shr 16).toByte(),
                (toEncode shr 8).toByte(),
                toEncode.toByte())

        val packet = Packet(59, PacketType.VARIABLE_BYTE, Unpooled.wrappedBuffer(buffer))
        val reader = GamePacketReader(packet)

        val packed = reader.getUnsigned(DataType.INT)

        Assertions.assertEquals(0xc, buffer[0])

        val decodedCoordinates = (packed and 0x7FFFF).toInt()
        Assertions.assertEquals(coordinates, decodedCoordinates)

        val decodedX = (decodedCoordinates) % 765
        Assertions.assertEquals(x, decodedX)

        val decodedY = (decodedCoordinates-decodedX) / 765
        Assertions.assertEquals(y, decodedY)

        val decodedUnchangedCoordsTick = ((packed shr 19) and 0x7FF).toInt()
        Assertions.assertEquals(unchangedCoordsTick, decodedUnchangedCoordsTick)
    }

}