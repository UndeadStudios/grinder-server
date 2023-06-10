package com.grinder.game.service.login

import com.grinder.Server
import com.grinder.game.World
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.net.session.PlayerSession
import com.grinder.net.codec.login.LoginResponse
import com.grinder.net.codec.login.LoginResponses
import com.grinder.net.codec.login.LoginResultType
import io.netty.channel.ChannelFutureListener
import org.apache.logging.log4j.LogManager
import java.lang.Exception

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   24/11/2019
 * @version 1.0
 */
class LoginWorker(val boss: LoginService) : Runnable {

    private val logger = LogManager.getLogger(LoginWorker::class.java)!!

    override fun run() {
        while (true) {

            val serviceRequest = boss.requests.take()

            try {
                val request = serviceRequest.login
                val channel =  request.channel
                val session = PlayerSession(channel)
                val player = session.player

                player.username = request.username
                player.longUsername = request.username.encodeLong()
                player.password = request.password
                player.hostAddress = request.host
                player.uid = request.uid.toString()
                player.macAddress = request.macAddress
                player.snAddress = request.snAddress
                player.hdSerialNumber = request.hdSerialNumber

                val response = LoginResponses.getResponse(player)!!

                if (response == LoginResultType.LOGIN_SUCCESSFUL) {
                    World.submitGameThreadJob {

                        val interceptedLoginResult = interceptLoginResult(player.username, player.hostAddress, player.macAddress)
                        val loginResult: LoginResultType = interceptedLoginResult
                                ?: if (player.register()) {
                                    LoginResultType.LOGIN_SUCCESSFUL
                                } else
                                    LoginResultType.LOGIN_REJECT_SESSION
                        if (loginResult == LoginResultType.LOGIN_SUCCESSFUL) {
                            channel.write(LoginResponse(response.id, player.rights.ordinal))
                            boss.successfulLogin(player.session, request.encryptor, request.decryptor)
                        } else {
                            channel.writeAndFlush(loginResult).addListener(ChannelFutureListener.CLOSE)
                            logger.info("User '{${player.username}}' login denied with code {$loginResult}.")
                        }
                    }
                } else {
                    channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
                    logger.info("User '{${player.username}}' login denied with code {$response} and channel {${player.session.channel}}.")
                }
            } catch (e: Exception){
                logger.error("Error when handling request from ${serviceRequest.login.channel}.", e)
            }
        }
    }
    private fun interceptLoginResult(username: String, ip: String, mac: String): LoginResultType? {
        return when {
            !Server.loaded.get() || Server.isUpdating() -> LoginResultType.LOGIN_GAME_UPDATE
            World.players.isFull -> LoginResultType.LOGIN_WORLD_FULL
            else -> {

                val validMac = PlayerUtil.isValidMacAddress(mac)
                var sameComputerCount = 0

                for(player in World.botPlayerLoginQueue){
                    if(player != null){
                        if(player.username == username)
                            return LoginResultType.LOGIN_ACCOUNT_ONLINE
                        if(checkSameMachineOrNetwork(player, ip, validMac, mac))
                            sameComputerCount++
                    }
                }

                for(player in World.players){
                    if(player != null){
                        if(player.username == username)
                            return LoginResultType.LOGIN_ACCOUNT_ONLINE
                        if(checkSameMachineOrNetwork(player, ip, validMac, mac))
                            sameComputerCount++
                    }
                }

                return if(sameComputerCount >= 5)
                    LoginResultType.LOGIN_CONNECTION_LIMIT
                else
                    null
            }
        }
    }

    private fun checkSameMachineOrNetwork(player: Player, ip: String, validMac: Boolean, mac: String): Boolean {
        if (player.hostAddress == ip) {
            if (validMac && PlayerUtil.hasValidMacAddress(player)) {
                if (player.macAddress == mac)
                    return true
            } else
                return true
        }
        return false
    }

    private fun String.encodeLong(): Long{
        var l = 0L
        run {
            var i = 0
            while (i < length && i < 12) {
                val c: Char = get(i)
                l *= 37L
                when (c) {
                    in 'A'..'Z' -> l += 1 + c.toInt() - 65.toLong()
                    in 'a'..'z' -> l += 1 + c.toInt() - 97.toLong()
                    in '0'..'9' -> l += 27 + c.toInt() - 48.toLong()
                }
                i++
            }
        }
        while (l % 37L == 0L && l != 0L) l /= 37L
        return l
    }
}