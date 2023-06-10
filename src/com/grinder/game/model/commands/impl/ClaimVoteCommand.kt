package com.grinder.game.model.commands.impl

import com.grinder.game.content.miscellaneous.voting.Voting
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.commands.Command
import com.grinder.net.codec.database.SQLManager
import com.grinder.net.codec.database.impl.LookUpPlayerVote

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   06/04/2020
 * @version 1.0
 */
class ClaimVoteCommand : Command {

    override fun getDescription() = "Used to claim vote points"

    override fun canUse(player: Player?) = true

    override fun execute(player: Player, command: String?, parts: Array<out String>?) {
        Voting.requestVoteLookup(player)
    }

    override fun getSyntax() = "claimvote or redeemvote"
}