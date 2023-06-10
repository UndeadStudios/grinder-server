package com.grinder.net.codec.database

import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.entity.agent.player.bot.BotPlayer
import com.grinder.game.entity.getBoolean
import com.grinder.game.model.Position
import com.grinder.game.model.attribute.Attribute
import com.grinder.net.codec.database.SQLManager.Companion.INSTANCE
import com.grinder.net.codec.database.impl.UpdateHighscorePlayers
import com.grinder.net.codec.database.impl.UpdateOnlinePlayers

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   07/04/2020
 * @version 1.0
 */

fun main() {
    INSTANCE.init()
    val con = INSTANCE.getConnection(SQLDataSource.WEBSITE)
    println(con)

    val dummy = BotPlayer("test", Position(0,0,0))

//    testPlayersOnline(dummy)
//    testHighScore(dummy)


}

private fun testHighScore(dummy: BotPlayer) {
    UpdateHighscorePlayers(
            INSTANCE,
            dummy.username,
            dummy.gameMode,
            dummy.skillManager
    ).schedule(dummy)
}

private fun testPlayersOnline(dummy: BotPlayer) {
    UpdateOnlinePlayers(
            INSTANCE,
            dummy.username,
            if (PlayerUtil.isStaff(dummy)) "staff" else if (dummy.getBoolean(Attribute.MIDDLEMAN)) "middleman" else "",
            true,
            dummy.rights
    ).schedule(dummy)
}