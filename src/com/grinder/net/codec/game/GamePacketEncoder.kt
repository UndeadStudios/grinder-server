package com.grinder.net.codec.game

import com.grinder.net.packet.Packet
import com.grinder.net.packet.PacketType.VARIABLE_BYTE
import com.grinder.net.packet.PacketType.VARIABLE_SHORT
import com.grinder.net.security.IsaacRandom
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import org.apache.logging.log4j.LogManager
import java.text.DecimalFormat

/**
 * TODO: add documentation
 *
 * @author  Tom <rspsmods@gmail.com>
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   20/11/2019
 * @version 1.0
 */
class GamePacketEncoder(private val random: IsaacRandom) : MessageToByteEncoder<Packet>() {

    private val logger = LogManager.getLogger(GamePacketEncoder::javaClass.name)

    override fun encode(ctx: ChannelHandlerContext, msg: Packet, out: ByteBuf) {
        val type = msg.type

        if (type == VARIABLE_BYTE && msg.length >= 256) {
            logger.error("Message[${msg.opcode}] length {${DecimalFormat().format(msg.length)}} too long for 'variable-byte' packet on channel {${ctx.channel()}}.")
            return
        } else if (type == VARIABLE_SHORT && msg.length >= 65536) {
            logger.error("Message[${msg.opcode}] length {${DecimalFormat().format(msg.length)}} too long for 'variable-short' packet on channel {${ctx.channel()}}.")
            return
        }

        out.writeByte((msg.opcode + random.nextInt()) and 0xFF)
        when (type) {
            VARIABLE_BYTE -> out.writeByte(msg.length)
            VARIABLE_SHORT -> out.writeShort(msg.length)
            else -> {}
        }
        out.writeBytes(msg.payload)
//        if(msg.opcode != 65 && msg.opcode != 81) {
//            if (Config.enable_debug_messages) {
//                logger.info("Sending packet {$msg}")
//            }
//        }
        msg.payload.release()
    }

}