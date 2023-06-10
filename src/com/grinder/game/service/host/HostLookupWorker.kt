package com.grinder.game.service.host

import com.grinder.Config
import com.grinder.game.model.commands.impl.ToggleVPNCommand
import com.grinder.game.service.ServiceManager
import com.grinder.game.service.login.LoginServiceRequest
import com.grinder.net.codec.login.LoginRequest
import com.grinder.net.codec.login.LoginResultType
import com.grinder.net.security.CheckConnectionUtil
import com.grinder.util.DiscordBot
import com.maxmind.geoip2.DatabaseReader
import io.netty.channel.ChannelFutureListener
import org.apache.logging.log4j.LogManager
import java.io.File
import java.net.InetAddress

/**
 * A worker for looking up the host of incoming connections.
 *
 * If [Config.block_proxy_vpn_tor] is set to true (see [ToggleVPNCommand]
 * and the host is flagged as being a VPN or tor exit node,
 * the connection is refused and staff will be notified.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   24/11/2019
 * @version 1.0
 */
class HostLookupWorker(val boss: HostLookupService) : Runnable {

    private val logger = LogManager.getLogger(HostLookupWorker::javaClass.name)!!

    private val dbCountryPath = "data/GeoLite2/GeoLite2-Country.mmdb"

    override fun run() {
        while (true) {

            val serviceRequest = boss.requests.take()

            try {
                val loginRequest = serviceRequest.login
                val host = loginRequest.host
                var type = CheckConnectionUtil.HostType.UNKNOWN

                try {
                    type = CheckConnectionUtil.evaluateHost(host)
                }  catch (e : Exception){
                    logger.info("Failed to perform vpn or tor check for $host -> ${e.localizedMessage}")
                }
                if(type == CheckConnectionUtil.HostType.UNKNOWN) {
                    try {
                        val dbFile = File(dbCountryPath)
                        val reader: DatabaseReader = DatabaseReader.Builder(dbFile).build()
                        val ipAddress = InetAddress.getByName(host)
                        val country = reader.country(ipAddress)

                        if (country != null) {
                            val mac = loginRequest.macAddress
                            val isoCode = country.country.isoCode
                            boss.macCountryMap.putIfAbsent(mac, HashSet())
                            val previousCountries = boss.macCountryMap[mac]!!
                            if (previousCountries.isNotEmpty()) {
                                if (!previousCountries.contains(isoCode)) {
                                    type = CheckConnectionUtil.HostType.VPN
                                }
                            } else {
                                previousCountries.add(isoCode)
                            }
                        }
                    } catch (e : Exception){
                        logger.info("Did not find country data for $host -> ${e.localizedMessage}")
                    }
                }
                if(type != CheckConnectionUtil.HostType.UNKNOWN){
                    denyRequest(host, type, loginRequest)
                } else
                    ServiceManager.loginService.addLoginRequest(LoginServiceRequest(loginRequest))
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    private fun denyRequest(host: String?, type: CheckConnectionUtil.HostType, loginRequest: LoginRequest) {
        DiscordBot.INSTANCE.sendModMessage("Detected login request by ${loginRequest.username} from $host of type $type")
        val channel = loginRequest.channel
        val response = LoginResultType.LOGIN_DISABLED_COMPUTER
        channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
        logger.info("User '{${loginRequest.username}}' login denied with code {$response} and channel {${channel}}.")
    }

}