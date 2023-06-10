package com.grinder.net.codec.login

import com.grinder.net.codec.StatefulFrameDecoder
import com.grinder.net.security.IsaacRandom
import com.grinder.util.Misc
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import org.apache.logging.log4j.LogManager
import java.math.BigInteger
import java.net.InetSocketAddress

/**
 * Represents a [StatefulFrameDecoder] that decodes incoming login packets.
 *
 * @author Tom <rspsmods@gmail.com> (from rs-mod)
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   25/11/2019
 * @version 1.0
 */
class LoginDecoder(private val serverRevision: Int,
                   private val serverSeed: Long,
                   private val rsaExponent: BigInteger,
                   private val rsaModulus: BigInteger
) : StatefulFrameDecoder<LoginDecoderState>(LoginDecoderState.HANDSHAKE) {

    private val logger = LogManager.getLogger(LoginDecoder::javaClass.name)

    private var payloadLength = -1
    private var reconnecting = false

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>, state: LoginDecoderState) {
       buf.markReaderIndex()
        when(state) {
            LoginDecoderState.HANDSHAKE -> decodeHandshake(ctx, buf)
            LoginDecoderState.HEADER -> decodeHeader(ctx, buf, out)
        }
    }

    private fun decodeHandshake(ctx: ChannelHandlerContext, buf: ByteBuf) {
        if (buf.isReadable) {
            val opcode = buf.readByte().toInt()
            if (opcode == LOGIN_OPCODE || opcode == RECONNECT_OPCODE) {
                reconnecting = opcode == RECONNECT_OPCODE
                setState(LoginDecoderState.HEADER)
            } else {
                ctx.writeResponse(LoginResultType.LOGIN_BAD_SESSION_ID)
            }
        }
    }

    private fun decodeHeader(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if (buf.readableBytes() >= 3) {
            val size = buf.readUnsignedByte()
            if (buf.readableBytes() >= size) {
                val magicId = buf.readUnsignedByte().toInt()
                val memory = buf.readByte().toInt()

                if(magicId != 0xFF || (memory != 0 && memory != 1)) {
                    ctx.writeResponse(LoginResultType.LOGIN_REJECT_SESSION)
                } else {
                    payloadLength = size - (Byte.SIZE_BYTES + Byte.SIZE_BYTES)
                    decodePayload(ctx, buf, out)
                }
            } else {
                buf.resetReaderIndex()
            }
        }
    }
    private fun decodePayload(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if (buf.readableBytes() >= payloadLength) {

            buf.markReaderIndex()

            val secureBuf: ByteBuf = run {
                val secureBufLength = buf.readUnsignedByte().toInt()
                val secureBufData = ByteArray(secureBufLength)
                buf.readBytes(secureBufData)
                val rsaValue = BigInteger(secureBufData).modPow(rsaExponent, rsaModulus)
                Unpooled.wrappedBuffer(rsaValue.toByteArray())
            }
            val securityId: Int = secureBuf.readByte().toInt()

            val successfulEncryption = securityId == 10
            if (!successfulEncryption) {
                buf.resetReaderIndex()
                buf.skipBytes(payloadLength)
                logger.info("Channel '{${ctx.channel()}}' login request rejected.")
                ctx.writeResponse(LoginResultType.LOGIN_REJECT_SESSION)
                return
            }

            val clientSeed: Long = secureBuf.readLong()
            val reportedSeed: Long = secureBuf.readLong()

            val seed = intArrayOf(
                    (clientSeed shr 32).toInt(),
                    clientSeed.toInt(),
                    (reportedSeed shr 32).toInt(),
                    reportedSeed.toInt()
            )
            val encryptionKey = IsaacRandom(seed)
            for (i in seed.indices) {
                seed[i] += 50
            }

            val clientUID = secureBuf.readInt()
            val userUID = 0L//secureBuf.readLong()
            if (clientUID != serverRevision) {
                ctx.writeResponse(LoginResultType.OLD_CLIENT_VERSION)
               return
           }

            val macAddress = secureBuf.readString()
            val snAddress = secureBuf.readString()
            val hdSerialNumber = secureBuf.readString()
            val username = secureBuf.readString().format()
            val password = secureBuf.readString()

            if(reportedSeed != serverSeed){
                logger.info("Channel '{${ctx.channel()}}' reported wrong seed {$reportedSeed} != {${serverSeed}}.")
                ctx.writeResponse(LoginResultType.LOGIN_REJECT_SESSION)
                return
            }

            if (isValid(username, password)) {
                ctx.writeResponse(LoginResultType.INVALID_CREDENTIALS_COMBINATION)
                return
            }

            val channel = ctx.channel()
            val hostAddress = channel.getHostAddress()

            val request = LoginRequest(
                    channel,
                    username,
                    password,
                    hostAddress,
                    macAddress,
                    snAddress,
                    hdSerialNumber,
                    userUID,
                    IsaacRandom(seed),
                    encryptionKey
            )
            out.add(request)
        }
    }

    private fun isValid(username: String, password: String) =
            username.length < 3 || username.length > 12 || password.length < 3 || password.length > 20 || username.startsWith(" ") || username.endsWith(" ") || !Misc.isValidName(username)

    private fun ChannelHandlerContext.writeResponse(result: LoginResultType) {
        val buf = channel().alloc().buffer(1)
        buf.writeByte(result.id)
        writeAndFlush(buf).addListener(ChannelFutureListener.CLOSE)
    }

    private fun ByteBuf.readString(): String {
        val builder = StringBuilder()
        while(isReadable){
            val temp = readByte()
            if(temp.toInt() == 10)
                break
            builder.append(temp.toChar())
        }
        return builder.toString()
    }

    private fun String.format(): String{
        return Misc.formatText(this.toLowerCase())
    }

    private fun Channel.getHostAddress(): String{
        return (remoteAddress() as InetSocketAddress).address.hostAddress
    }

    companion object {
        private const val LOGIN_OPCODE = 16
        private const val RECONNECT_OPCODE = 18
    }
}