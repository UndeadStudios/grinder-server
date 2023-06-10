package com.grinder.game.model.commands.impl

import com.grinder.game.content.miscellaneous.donating.Store
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.commands.Command

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   06/04/2020
 * @version 1.0
 */
class ClaimOrderCommand : Command {

    override fun getDescription() = "Claims your purchase order reward."

    override fun canUse(player: Player?) = true

    override fun execute(player: Player, command: String?, parts: Array<out String>?) {
        Store.requestPurchaseLookup(player)
    }

    override fun getSyntax() = "claim or claimorder or redeem"
}