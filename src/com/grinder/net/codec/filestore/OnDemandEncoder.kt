package com.grinder.net.codec.filestore

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

/**
 * @author Tom <rspsmods@gmail.com>
 */
class OnDemandEncoder : MessageToByteEncoder<OnDemandResponse>() {

    override fun encode(ctx: ChannelHandlerContext, msg: OnDemandResponse, out: ByteBuf) {
        out.writeByte(msg.index)
        out.writeShort(msg.archive)

        msg.data.forEach { data ->
            if (out.writerIndex() % 512 == 0) {
                out.writeByte(-1)
            }
            out.writeByte(data.toInt())
        }
    }
}