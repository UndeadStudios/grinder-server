package com.grinder.net.update

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class OnDemandInfoEncoder : MessageToByteEncoder<OnDemandInfoResponse>() {

    @Throws(Exception::class)
    override fun encode(ctx: ChannelHandlerContext, msg: OnDemandInfoResponse, buf: ByteBuf) {
        buf.writeByte(msg.response)
    }
}