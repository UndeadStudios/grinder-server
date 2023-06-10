package com.grinder.net.channel

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import io.netty.handler.codec.http.cors.CorsConfigBuilder
import io.netty.handler.codec.http.cors.CorsHandler


/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   06/12/2019
 * @version 1.0
 */
class WebChannelInitializer : ChannelInitializer<SocketChannel>() {

    override fun initChannel(ch: SocketChannel) {
        val corsConfig = CorsConfigBuilder.forAnyOrigin().build()
        val pipeline = ch.pipeline()
        pipeline.addLast("encoder", HttpResponseEncoder())
        pipeline.addLast("decoder", HttpRequestDecoder())
        pipeline.addLast("aggregator", HttpObjectAggregator(8_388_608))
        pipeline.addLast("cors", CorsHandler(corsConfig))
        pipeline.addLast("handler", HttpStaticFileServerHandler(true))
    }
}