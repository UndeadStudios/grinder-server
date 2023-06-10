package com.grinder.net.packet

import com.grinder.net.ByteBufUtils
import io.netty.buffer.ByteBuf

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   20/11/2019
 * @version 1.0
 */
class PacketReader(val packet: Packet) {

    private val buffer: ByteBuf = packet.payload

    fun readableBytes() = buffer.readableBytes()

    /**
     * Read an unsigned byte from the packet.
     *
     * @return The unsigned byte.
     */
    fun readByte(): Byte {
        var b: Byte = 0
        try {
            b = buffer.readByte()
        } catch (e: Exception) {

        }

        return b
    }


    /**
     * Reads an inverse (negative) unsigned byte from the packet.
     *
     * @return readByte()
     */
    fun readByteC(): Byte {
        return (-readByte()).toByte()
    }

    /**
     * Reads a packetType-S byte from the packet.
     *
     * @return 128 - the unsigned byte value.
     */
    fun readByteS(): Byte {
        return (128 - readByte()).toByte()
    }

    /**
     * Reads an unsigned byte.
     *
     * @return The unsigned byte value read from the packet.
     */
    fun readUnsignedByte(): Int {
        return buffer.readUnsignedByte().toInt()
    }

    /**
     * Reads a short value.
     *
     * @return The short value read from the packet.
     */
    fun readShort(): Short {
        return buffer.readShort()
    }

    /**
     * Reads a short packetType-A from the packet.
     *
     * @return The short packetType-A value.
     */
    fun readShortA(): Short {
        val value = readByte().toInt() and 0xFF shl 8 or (readByte() - 128 and 0xFF)
        return (if (value > 32767) value - 0x10000 else value).toShort()
    }

    /**
     * Reads a little-endian short from the packet.
     *
     * @return The little-endian short value.
     */
    fun readLEShort(): Short {
        val value = readByte().toInt() and 0xFF or (readByte().toInt() and 0xFF shl 8)
        return (if (value > 32767) value - 0x10000 else value).toShort()
    }

    /**
     * Reads a little-endian packetType-A short from the packet.
     *
     * @return The little-endian packetType-A short value.
     */
    fun readLEShortA(): Short {
        val value = readByte() - 128 and 0xFF or (readByte().toInt() and 0xFF shl 8)
        return (if (value > 32767) value - 0x10000 else value).toShort()
    }

    /**
     * Reads the unsigned short value from the packet.
     *
     * @return The unsigned short value.
     */
    fun readUnsignedShort(): Int {
        return buffer.readUnsignedShort()
    }

    /**
     * Reads the unsigned short value packetType-A from the packet.
     *
     * @return The unsigned short packetType-A value.
     */
    fun readUnsignedShortA(): Int {
        var value = 0
        value = value or (readUnsignedByte() shl 8)
        value = value or (readByte() - 128 and 0xff)
        return value
    }

    /**
     * Reads an int value from the packet.
     *
     * @return The int value.
     */
    fun readInt(): Int {
        return buffer.readInt()
    }

    /**
     * Reads the long value from the packet.
     *
     * @return The long value.
     */
    fun readLong(): Long {
        return buffer.readLong()
    }

    /**
     * Reads the string value from the packet.
     *
     * @return The string value.
     */
    fun readString() = ByteBufUtils.readString(buffer)

}