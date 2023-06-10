package com.grinder.net.codec.handshake

import com.grinder.Config
import com.grinder.game.World
import com.grinder.game.service.ServiceManager
import com.grinder.net.codec.filestore.OnDemandDecoder
import com.grinder.net.codec.filestore.OnDemandEncoder
import com.grinder.net.codec.login.LoginDecoder
import com.grinder.net.codec.login.LoginEncoder
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.math.BigInteger
import java.security.SecureRandom
import java.util.*

/**
 * A [ByteToMessageDecoder] implementation which is responsible for handling
 * the initial handshake signal from the client. The implementation is dependant
 * on the network module.
 *
 * @author Tom <rspsmods@gmail.com>
 */
class HandshakeDecoder(
        private val serverRevision: Int,
        private val rsaExponent: BigInteger,
        private val rsaModulus: BigInteger
) : ByteToMessageDecoder() {

    private val logger: Logger = LogManager.getLogger(HandshakeDecoder::class.java)!!

    /**
     * Generates random numbers via secure cryptography. Generates the session
     * key for packet encryption.
     */
    private val random: Random = SecureRandom()

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if (!buf.isReadable) {
            return
        }

        val opcode = buf.readByte().toInt()
        val handshake = HandshakeType.values.firstOrNull { it.id == opcode }
        when (handshake) {
            HandshakeType.FILESTORE -> {
                val p = ctx.pipeline()
                p.addFirst("filestore_encoder", OnDemandEncoder())
                p.addAfter("handshake_decoder", "filestore_decoder", OnDemandDecoder())
            }
            HandshakeType.LOGIN -> {
                val p = ctx.pipeline()
                if(Config.enable_captcha && !ServiceManager.captchaService.hasCompletedCaptcha(ctx.channel())){
                    ctx.writeAndFlush(ctx.alloc().buffer(1).writeByte(69))
                } else {
                    val serverSeed = random.nextLong()
                    p.addAfter("handshake_decoder", "login_decoder", LoginDecoder(serverRevision, serverSeed, rsaExponent, rsaModulus))
                    p.addAfter("login_decoder", "login_encoder", LoginEncoder())
                    ctx.writeAndFlush(ctx.alloc().buffer(1).writeByte(0))
                    ctx.writeAndFlush(ctx.alloc().buffer(8).writeLong(serverSeed))
                }
            }
            /*       else -> {
                       *//*
                 * If the handshake type is not handled, we want to log it and
                 * make sure we read any bytes from the buffer.
                 *//*
                buf.readBytes(buf.readableBytes())
                logger.warn("Unhandled handshake type {$opcode} requested by {${ctx.channel()}}.")
                return
            }*/
        }
        /*
         * This decoder is no longer needed for this context, so we discard it.
         */
        ctx.pipeline().remove(this)
        if (handshake != null) {
            out.add(HandshakeMessage(handshake.id))
        }
    }
}
