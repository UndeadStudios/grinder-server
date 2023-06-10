package com.grinder.net.session

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext

/**
 * Represents a session that handler this [channel].
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   24/11/2019
 * @version 1.0
 */
abstract class Session(open val channel: Channel?) {

    /**
     * Fired upon reading a message in the channel handler.
     */
    abstract fun receiveMessage(ctx: ChannelHandlerContext, msg: Any)

    /**
     * Fired upon channel inactive invocation by the channel handler.
     */
    abstract fun onChannelInactive()

}