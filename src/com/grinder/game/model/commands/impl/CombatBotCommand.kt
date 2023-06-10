package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.bot.BotManager
import com.grinder.game.entity.agent.player.bot.CombatBotPlayer
import com.grinder.game.model.commands.DeveloperCommand
import java.util.concurrent.atomic.AtomicInteger

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   16/10/2019
 * @version 1.0
 */
class CombatBotCommand : DeveloperCommand() {

    private val atomicCount = AtomicInteger(0)

    override fun getSyntax() = "[amount]"

    override fun getDescription() = "Adds combat bot."

    override fun execute(player: Player, command: String, parts: Array<out String>) {

        val combatBot = CombatBotPlayer("random-"+atomicCount.getAndIncrement(), player.position.clone().addY(1))
        BotManager.startScript(combatBot, "afkscript")
    }
    override fun canUse(player: Player) = player.rights.anyMatch(PlayerRights.DEVELOPER, PlayerRights.OWNER)
}