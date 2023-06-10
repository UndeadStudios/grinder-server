package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.content.minigame.MinigameManager
import com.grinder.game.content.minigame.impl.WeaponMinigame
import com.grinder.game.entity.agent.player.PlayerRights

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   11/04/2020
 * @version 1.0
 */
class ToggleWeaponsGame : DebugCommand() {

    override fun execute(player: Player, command: String, parts: Array<out String>) {
        WeaponMinigame.ENABLED = !WeaponMinigame.ENABLED
        val message = if(WeaponMinigame.ENABLED) "enabled" else "disabled"
        player.sendMessage("You $message weapons game")
        if(!WeaponMinigame.ENABLED){
            player.sendMessage("Attempting to rescue players...")
            MinigameManager.WEAPON_GAME.players.forEach {
                MinigameManager.WEAPON_GAME.leave(it)
            }
        }
    }
    override fun canUse(player: Player): Boolean {
        return player.rights.isHighStaff
    }
}