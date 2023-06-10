package com.grinder.net.codec.game

import com.grinder.net.codec.StatefulFrameDecoder
import com.grinder.net.codec.game.GameDecoderState.*
import com.grinder.net.packet.Packet
import com.grinder.net.packet.PacketConfiguration
import com.grinder.net.packet.PacketType
import com.grinder.net.security.IsaacRandom
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import org.apache.logging.log4j.LogManager

/**
 * Represents a [StatefulFrameDecoder] that decodes incoming packets.
 *
 * First the [Packet.opcode] is read then information
 * of the packet is retrieved from [packetConfiguration].
 *
 * If the [Packet.type] is equal to [PacketType.FIXED] then the [Packet.length] is fixed
 * and thus this [length] is set to the length retrieved form [packetConfiguration].
 *
 * If the [Packet.type] is equal to [PacketType.VARIABLE_BYTE] or [PacketType.VARIABLE_SHORT],
 * then the [Packet.length] is variable, and included within the packet.
 * Therefore first the [length] must be read from the [ByteBuf].
 *
 * Then a [ByteBuf] of this [length] is read from the channel [ByteBuf]
 * which represents the [Packet.payload], unless the packet is ignored,
 * then [length] bytes are skipped in the channel [ByteBuf].
 *
 * @author  Tom <rspsmods@gmail.com>
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   20/11/2019
 * @version 1.0
 *
 * @param isaacRandom       the [IsaacRandom] that is used to decrypt the packet opcode
 * @param packetConfiguration    the [PacketConfiguration] contains the lengths of each incoming packet
 */
class GamePacketDecoder(
        private val isaacRandom: IsaacRandom,
        private val packetConfiguration: PacketConfiguration
) : StatefulFrameDecoder<GameDecoderState>(OPCODE) {

    private val logger = LogManager.getLogger(GamePacketDecoder::javaClass.name)
    private var opcode = 0
    private var length = 0
    private var type = PacketType.FIXED
    private var ignore = false

    /**
     * Decodes (part of) the incoming packet depending on which [state] is set.
     */
    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>, state: GameDecoderState) {

        when (state) {
            OPCODE -> decodeOpcode(ctx, buf, out)
            LENGTH -> decodeLength(buf, out)
            PAYLOAD -> decodePayload(buf, out)
        }
    }

    /**
     * Decodes the [Packet.opcode] from the [ByteBuf]
     *
     */
    private fun decodeOpcode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if (buf.isReadable) {
            opcode = buf.readUnsignedByte().toInt() - isaacRandom.nextInt() and 0xFF

            val length = packetConfiguration.lengths.getOrNull(opcode)

            if (length == null) {
                logger.warn("Channel {${ctx.channel()}} sent message with no valid metadata: {$opcode}.")
                buf.skipBytes(buf.readableBytes())
                return
            }

            type = when(length) {
                -2 -> PacketType.VARIABLE_SHORT
                -1 -> PacketType.VARIABLE_BYTE
                else -> PacketType.FIXED
            }
            ignore = packetConfiguration.ignore(opcode)

            when (type) {
                PacketType.FIXED -> {
                    this.length = length
                    if (length > 0)
                        setState(PAYLOAD)
                    else if (!ignore) // ping packets
                        out.add(Packet(opcode, PacketType.FIXED, Unpooled.EMPTY_BUFFER))
                }
                PacketType.VARIABLE_BYTE,
                PacketType.VARIABLE_SHORT -> setState(LENGTH)
                else -> throw IllegalStateException("Unhandled packet type $type for opcode $opcode.")
            }
        }
    }

    /**
     * Reads the [Packet.length], this only applies for variable-length packets.
     */
    private fun decodeLength(buf: ByteBuf, out: MutableList<Any>) {
        if (buf.isReadable) {

            length = if (type == PacketType.VARIABLE_SHORT)
                buf.readUnsignedShort()
            else
                buf.readUnsignedByte().toInt()

            if (length > 0)
                setState(PAYLOAD)
            else {
                setState(OPCODE)
                if (!ignore)
                    out.add(Packet(opcode, type, Unpooled.EMPTY_BUFFER))
            }
        }
    }

    /**
     * Reads the [Packet.payload], that is a [ByteBuf] of this [length].
     */
    private fun decodePayload(buf: ByteBuf, out: MutableList<Any>) {
        if (buf.readableBytes() >= length) {

            // reset decoder state
            setState(OPCODE)

            /**
             * If the packet isn't flagged as being a packet we should ignore,
             * we queue it up for our game to process the packet.
             */
            if (!ignore) {
                val payload = buf.readBytes(length)
                out.add(Packet(opcode, type, payload))
            } else {
                buf.skipBytes(length)
            }
        }
    }
}