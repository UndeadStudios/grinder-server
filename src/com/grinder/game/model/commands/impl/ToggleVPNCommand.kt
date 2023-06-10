package com.grinder.game.model.commands.impl

import com.grinder.Config
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.model.commands.Command

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   05/01/2020
 * @version 1.0
 */
class ToggleVPNCommand : Command {

    override fun getDescription() = "Temporary block out malicious vpn players."

    override fun canUse(player: Player) = player.rights.isAdvancedStaff

    override fun execute(player: Player, command: String?, parts: Array<out String>?) {
        Config.block_proxy_vpn_tor = !Config.block_proxy_vpn_tor
        player.sendMessage("You set block_proxy_vpn_tor to ${Config.block_proxy_vpn_tor}")
    }

    override fun getSyntax() = "togglevpn"
}