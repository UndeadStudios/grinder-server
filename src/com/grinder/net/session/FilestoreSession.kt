package com.grinder.net.session

import com.grinder.game.WorldConstants
import com.grinder.game.service.ServiceManager
import com.grinder.net.codec.filestore.OnDemandDecoder
import com.grinder.net.codec.filestore.OnDemandRequest
import com.grinder.net.codec.login.LoginResultType
import com.grinder.net.update.OnDemandInfo
import io.netty.channel.Channel
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import net.runelite.cache.fs.Store
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * A [Session] responsible for sending decoded [Store] data to the [channel].
 *
 * @author  Tom <rspsmods@gmail.com>
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/02/2020
 * @version 1.0
 */
class FilestoreSession(channel: Channel) : Session(channel) {

    private var handshakeComplete = false

    override fun receiveMessage(ctx: ChannelHandlerContext, msg: Any) {

        if (msg is OnDemandRequest) {
            if(handshakeComplete)
                ServiceManager.updateService.dispatcher.dispatch(ctx.channel(), msg)
        } else if(msg is OnDemandInfo){

            val status = if (msg.releaseNumber != WorldConstants.REVISION) {
                OnDemandDecoder.logger.info(
                        "Revision mismatch for channel {${ctx.channel()}}" +
                                " with client revision {${msg.releaseNumber}}" +
                                " when expecting {${WorldConstants.REVISION}}.")
                LoginResultType.REVISION_MISMATCH
            } else {
                LoginResultType.ACCEPTABLE
            }

            val future = channel!!.write(status)

            if (status == LoginResultType.ACCEPTABLE) {
                handshakeComplete = true
            } else {
                future.addListener(ChannelFutureListener.CLOSE)
            }
        } else {
            throw IllegalArgumentException("Unknown message type.")
        }
    }

    override fun onChannelInactive() {
        logger.info("Terminating session for channel {$channel}")
    }

    companion object {
        val logger: Logger = LogManager.getLogger(FilestoreSession::class.java)
    }
}