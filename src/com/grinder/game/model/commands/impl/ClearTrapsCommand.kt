package com.grinder.game.model.commands.impl

import com.grinder.game.content.skill.skillable.impl.hunter.HunterTrapState
import com.grinder.game.content.skill.skillable.impl.hunter.HunterTraps
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.model.commands.Command

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   06/01/2020
 * @version 1.0
 */
class ClearTrapsCommand : Command {

    override fun getDescription() = "Force remove all hunter traps"
    override fun canUse(player: Player) = player.rights.isAdvancedStaff
    override fun execute(player: Player, command: String?, parts: Array<out String>?) {
        var totalRemoved = 0
        HunterTraps.PLAYER_TRAPS.forEach { playerName, playerTraps ->
            var removalCount = 0
            val iterator = playerTraps.iterator()
            while (iterator.hasNext()){
                val trap = iterator.next()
                iterator.remove()
                trap.deSpawn(true)
                removalCount++
            }
            if(removalCount > 0) {
                player.sendMessage("Removed $removalCount traps for player $playerName")
                totalRemoved+=removalCount
            }
        }
        PlayerUtil.broadcastPlayerMediumStaffMessage(player.username+" removed a total of $totalRemoved traps.")
    }
    override fun getSyntax() = "cleartraps"
}