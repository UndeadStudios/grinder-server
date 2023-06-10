package com.grinder

import com.grinder.game.GameConstants
import com.grinder.game.World
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.entity.agent.player.bot.BotPlayer
import com.grinder.game.model.Position
import com.grinder.game.model.areas.AreaManager
import org.junit.jupiter.api.AfterEach

open class GrinderBiPlayerTest(
        name: String,
        printMessage: Boolean = false,
        position: Position = GameConstants.DEFAULT_POSITION.clone()
) : GrinderTest() {

    val player1 = BotPlayer("$name-1", position).also {
        it.isLoggedIn = true
        if(printMessage)
            it.isPrintMessages = true
        it.hostAddress = "127.0.0.1"
        it.achievements.progress[AchievementType.SELF_SECURE.ordinal] = 1
        World.players.add(it)
        AreaManager.sequence(it)
    }

    val player2 = BotPlayer("$name-2", position).also {
        it.isLoggedIn = true
        if(printMessage)
            it.isPrintMessages = true
        it.hostAddress = "127.0.0.1"
        it.achievements.progress[AchievementType.SELF_SECURE.ordinal] = 1
        World.players.add(it)
        AreaManager.sequence(it)
    }

    @AfterEach
    fun postTest(){
        World.remove(player1)
        World.remove(player2)
    }
}