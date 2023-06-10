package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.model.commands.Command
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.interfaces.dialogue.secondOption
import com.grinder.game.model.interfaces.dialogue.thirdOption
import com.grinder.game.model.interfaces.dialogue.fourthOption
import com.grinder.game.model.interfaces.dialogue.fifthOption

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/10/2019
 * @version 1.0
 */
class SendPacketCommand : Command {

    override fun getSyntax() = ""

    override fun getDescription() = "Sends update player packet."

    override fun canUse(player: Player) = player.rights.isStaff(PlayerRights.DEVELOPER)

    override fun execute(player: Player, command: String?, parts: Array<out String>?) {
        DialogueBuilder(DialogueType.OPTION)
                .firstOption("update-1") {

                }
    }
}