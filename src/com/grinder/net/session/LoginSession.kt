package com.grinder.net.session

import com.grinder.Config
import com.grinder.game.service.ServiceManager
import com.grinder.game.service.host.HostLookupServiceRequest
import com.grinder.game.service.login.LoginServiceRequest
import com.grinder.net.codec.login.LoginRequest
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   24/11/2019
 * @version 1.0
 */
class LoginSession(channel: Channel?) : Session(channel) {
    override fun receiveMessage(ctx: ChannelHandlerContext, msg: Any) {
        if(msg is LoginRequest){
            if(Config.block_proxy_vpn_tor)
                ServiceManager.hostLookUpService.addHostLookupRequest(HostLookupServiceRequest(msg))
            else
                ServiceManager.loginService.addLoginRequest(LoginServiceRequest(msg))
        }
    }

    override fun onChannelInactive() {

    }

}