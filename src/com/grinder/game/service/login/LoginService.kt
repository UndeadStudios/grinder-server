package com.grinder.game.service.login

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.grinder.game.World
import com.grinder.game.service.Service
import com.grinder.net.NetworkConstants
import com.grinder.net.codec.game.GameMessageEncoder
import com.grinder.net.codec.game.GamePacketDecoder
import com.grinder.net.codec.game.GamePacketEncoder
import com.grinder.net.security.IsaacRandom
import com.grinder.net.session.PlayerSession
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   24/11/2019
 * @version 1.0
 */
class LoginService: Service {

    private val logger: Logger = LogManager.getLogger(LoginService::class.java)
    val requests = LinkedBlockingQueue<LoginServiceRequest>()

    private var threadCount = 1

    override fun init() {
        val executorService = Executors
                .newFixedThreadPool(threadCount, ThreadFactoryBuilder()
                        .setNameFormat("login-worker")
                        .setUncaughtExceptionHandler { t, e ->
                            logger.error("Error with thread $t", e) }.build())
        for (i in 0 until threadCount)
            executorService.execute(LoginWorker(this))
    }

    override fun postLoad() {

    }

    override fun bindNet() {}

    override fun terminate() {}

    fun addLoginRequest(msg: LoginServiceRequest) {
        requests.offer(msg)
    }

    fun successfulLogin(session: PlayerSession, encoderIsaac: IsaacRandom, decoderIsaac: IsaacRandom){

        val channel = session.channel!!

        channel.attr(NetworkConstants.SESSION_KEY).set(session)

        val pipeline = channel.pipeline()

        if(channel.isActive){
            pipeline.remove("handshake_encoder")
            pipeline.remove("login_decoder")
            pipeline.remove("login_encoder")

            pipeline.addFirst("messageEncoder", GameMessageEncoder(World.packetMetaData))
            pipeline.addBefore("messageEncoder", "packet_encoder", GamePacketEncoder(encoderIsaac))
            pipeline.addBefore("handler", "packet_decoder",
                    GamePacketDecoder(decoderIsaac, session.packetMetaData))

            session.player.login()
            channel.flush()
        }
    }
}