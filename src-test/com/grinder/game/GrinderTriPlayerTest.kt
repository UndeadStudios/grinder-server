package com.grinder.game

import com.grinder.GrinderTest
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces
import com.grinder.game.entity.agent.player.bot.BotPlayer
import com.grinder.game.model.Position
import com.grinder.game.model.areas.AreaManager
import org.junit.jupiter.api.AfterEach

open class GrinderTriPlayerTest(
        name: String,
        printMessage: Boolean = false,
        position: Position = GameConstants.DEFAULT_POSITION.clone()
) : GrinderTest() {

    val player1 = BotPlayer("$name-1", position).also {
        initPlayer(it, printMessage)
    }

    val player2 = BotPlayer("$name-2", position).also {
        initPlayer(it, printMessage)
    }

    val player3 = BotPlayer("$name-3", position).also {
        initPlayer(it, printMessage)
    }

    private fun initPlayer(player: BotPlayer, printMessage: Boolean) {
        player.isLoggedIn = true
        if (printMessage)
            player.isPrintMessages = true
        player.hostAddress = "127.0.0.1"
        player.achievements.progress[AchievementType.SELF_SECURE.ordinal] = 1
        WeaponInterfaces.INSTANCE.assign(player)
        World.players.add(player)
        AreaManager.sequence(player)
    }

    @AfterEach
    fun postTest(){
        World.remove(player1)
        World.remove(player2)
        World.remove(player3)
    }
}