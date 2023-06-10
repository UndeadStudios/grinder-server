package com.grinder.net.security

import com.grinder.net.security.tor.ExitNodeCheck
import com.grinder.net.security.vpn.VPNDetection

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   05/01/2020
 * @version 1.0
 */
object CheckConnectionUtil {

    fun evaluateHost(hostAddress: String) = when {
        ExitNodeCheck.isExitNodeInOnlineList(hostAddress) -> HostType.TOR_EXIT_NODE
        VPNDetection().getResponse(hostAddress).hostip -> HostType.VPN
        else -> HostType.UNKNOWN
    }

    enum class HostType {
        UNKNOWN,
        TOR_EXIT_NODE,
        VPN
    }

}