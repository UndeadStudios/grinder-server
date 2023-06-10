package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.model.areas.MapBuilder
import com.grinder.game.model.areas.instanced.CerberusArea
import com.grinder.game.model.areas.instanced.VorkathArea
import com.grinder.game.model.areas.instanced.ZulrahShrine
import com.grinder.game.model.commands.DeveloperCommand
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
 * @since   16/10/2019
 * @version 1.0
 */
class BossCommand : DeveloperCommand() {

    override fun getSyntax() = ""

    override fun getDescription() = "Teleports you to a list of instanced bosses."

    override fun execute(player: Player, command: String, parts: Array<out String>) {
        DialogueBuilder(DialogueType.OPTION)
                .firstOption("Cerberus") {
                    CerberusArea.handleCaveEntrance(it)
                }
                .secondOption("Vorkath") {
                    MapBuilder.buildVorkathMap(it)
                }
                .thirdOption("Zulrah") {
                    ZulrahShrine.handleBoatFairing(it)
                }
                .addCancel()
                .start(player)
    }
    override fun canUse(player: Player) = player.rights.anyMatch(PlayerRights.DEVELOPER, PlayerRights.OWNER)
}