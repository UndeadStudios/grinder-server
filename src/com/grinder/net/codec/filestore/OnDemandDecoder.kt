package com.grinder.net.codec.filestore

import com.grinder.net.codec.StatefulFrameDecoder
import com.grinder.net.update.OnDemandInfo
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import org.apache.logging.log4j.LogManager

/**
 * @author Tom <rspsmods@gmail.com>
 */
class OnDemandDecoder : StatefulFrameDecoder<OnDemandDecoderState>(OnDemandDecoderState.REVISION_REQUEST) {

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>, state: OnDemandDecoderState) {
        when (state) {
            OnDemandDecoderState.REVISION_REQUEST -> decodeRevisionRequest(buf, out)
            OnDemandDecoderState.ARCHIVE_REQUEST -> decodeArchiveRequest(buf, out)
        }
    }

    private fun decodeRevisionRequest(buf: ByteBuf, out: MutableList<Any>) {
        if (buf.readableBytes() >= 4) {
            val revision = buf.readUnsignedInt()
            out.add(OnDemandInfo(revision))
            setState(OnDemandDecoderState.ARCHIVE_REQUEST)
        }
    }

    private fun decodeArchiveRequest(buf: ByteBuf, out: MutableList<Any>) {
        if (!buf.isReadable) {
            return
        }
        buf.markReaderIndex()
        when (val opcode = buf.readByte().toInt()) {
            CLIENT_INIT_GAME, CLIENT_LOAD_SCREEN, CLIENT_INIT_OPCODE -> {
                // hmm
                buf.skipBytes(3)
            }
            CLIENT_PING_REQUEST -> {
                val someRandomValue = buf.readByte()
                buf.skipBytes(2)
                logger.warn("Received ping request from client, rando = $someRandomValue")
            }
            ARCHIVE_REQUEST_NEUTRAL, ARCHIVE_REQUEST_URGENT -> {
                if (buf.readableBytes() >= 3) {
                    val index = buf.readUnsignedByte().toInt()
                    val archive = buf.readUnsignedShort()
                    val request = OnDemandRequest(index = index, archive = archive, priority = OnDemandPriority.valueOf(opcode))
                    out.add(request)
                } else {
                    buf.resetReaderIndex()
                }
            }
            else -> {
                logger.error("Unhandled opcode: $opcode")
            }
        }
    }

    companion object  {
        private const val ARCHIVE_REQUEST_URGENT = 0
        private const val ARCHIVE_REQUEST_NEUTRAL = 1
        private const val CLIENT_INIT_GAME = 2
        private const val CLIENT_LOAD_SCREEN = 3
        private const val CLIENT_PING_REQUEST = 4
        private const val CLIENT_INIT_OPCODE = 6

        val logger = LogManager.getLogger(OnDemandDecoder::class.java.name)!!
    }
}
