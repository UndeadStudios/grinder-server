package com.grinder.net.channel

import com.grinder.net.NetworkConstants
import com.grinder.net.codec.handshake.HandshakeMessage
import com.grinder.net.codec.handshake.HandshakeType
import com.grinder.net.session.FilestoreSession
import com.grinder.net.session.LoginSession
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.ReadTimeoutException
import org.apache.logging.log4j.LogManager

/**
 * The part of the pipeline that handles exceptions caught,
 * channels being read, in-active channels, and channel triggered events.
 *
 * @author Professor Oak
 * @author Stan van der Bend
 */
@Sharable
class GrinderHandler : SimpleChannelInboundHandler<Any>() {

    private val logger = LogManager.getLogger(GrinderHandler::class.java)

    override fun channelRead0(ctx: ChannelHandlerContext, msg: Any) {
        try {

            val attribute = ctx.channel().attr(NetworkConstants.SESSION_KEY)
            val session = attribute.get()

            if(session != null)
                session.receiveMessage(ctx, msg)
            else if(msg is HandshakeMessage)
            {
                when (msg.id) {
                    HandshakeType.FILESTORE.id -> attribute.set(FilestoreSession(ctx.channel()))
                    HandshakeType.LOGIN.id -> attribute.set(LoginSession(ctx.channel()))
                }
            }
        } catch (e: Exception) {
            logger.error("Failed to read Channel: ${ctx.channel()}: ${e.message}", e)
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        ctx.channel()
                .attr(NetworkConstants.SESSION_KEY)
                .getAndSet(null)
                ?.onChannelInactive()
        ctx.channel().close()
    }

    /**
     * Occurs when the player exits the client through exit button, or task-manager.
     */
    private val clientExitedErrorMessage = "An existing connection was forcibly closed by the remote host"

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {

        if (cause is ReadTimeoutException) {
            logger.info("Channel disconnected due to read timeout: {${ctx.channel()}}")
        } else if (!cause.message.equals(clientExitedErrorMessage)){
            logger.error("Channel threw an exception: ${ctx.channel()}: ${cause.message}", cause)
        }

        try {
            ctx.channel().close()
        } catch (e: Exception) {
            logger.error("Failed to close Channel: ${ctx.channel()}: ${e.message}", e)
        }
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }
}
