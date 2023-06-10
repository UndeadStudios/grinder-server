package com.grinder.net.channel

import com.grinder.net.NetworkConstants
import com.grinder.net.codec.handshake.HandshakeDecoder
import com.grinder.net.codec.handshake.HandshakeEncoder
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.timeout.IdleStateHandler
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.traffic.ChannelTrafficShapingHandler
import io.netty.handler.traffic.GlobalTrafficShapingHandler
import java.math.BigInteger
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * The [ChannelInitializer] that will determine how channels will be
 * initialized when registered to the event loop group.
 *
 * @author  Tom <rspsmods></rspsmods>@gmail.com>
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   20/11/2019
 * @version 1.0
 */
class ClientChannelInitializer(
        private val handler: ChannelInboundHandlerAdapter,
        private val serverRevision: Int,
        private val rsaExponent: BigInteger,
        private val rsaModulus: BigInteger
) : ChannelInitializer<SocketChannel>() {

    /**
     * A global traffic handler that limits the amount of bandwidth all channels
     * can take up at once.
     */
    private val globalTrafficHandler = GlobalTrafficShapingHandler(Executors.newSingleThreadScheduledExecutor(), 0, 0, 600)

    @Throws(Exception::class)
    override fun initChannel(channel: SocketChannel) {
        val pipeline = channel.pipeline()
        pipeline.addLast("global_traffic", globalTrafficHandler)
        pipeline.addLast("channel_traffic", ChannelTrafficShapingHandler(0, 1024 * 5, 600))
        pipeline.addLast("timeout",  IdleStateHandler(NetworkConstants.SECONDS_TILL_IDLE_DISCONNECT, 0, 0))
        pipeline.addLast("handshake_encoder", HandshakeEncoder())
        pipeline.addLast("handshake_decoder", HandshakeDecoder(serverRevision, rsaExponent, rsaModulus))
        pipeline.addLast("handler", handler)
    }
}
