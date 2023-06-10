package com.grinder

import com.grinder.game.GameConstants
import com.grinder.game.World
import com.grinder.game.entity.agent.player.bot.BotPlayer
import com.grinder.game.model.Position
import org.junit.jupiter.api.AfterEach

open class GrinderPlayerTest(
        name: String,
        printMessage: Boolean = false,
        position: Position = GameConstants.DEFAULT_POSITION.clone()
) : GrinderTest() {

    val player = BotPlayer(name, position).also {
        it.isLoggedIn = true
        if(printMessage)
            it.isPrintMessages = true
        World.addPlayer(it)
    }

    @AfterEach
    fun postTest(){
        World.remove(player)
    }
}